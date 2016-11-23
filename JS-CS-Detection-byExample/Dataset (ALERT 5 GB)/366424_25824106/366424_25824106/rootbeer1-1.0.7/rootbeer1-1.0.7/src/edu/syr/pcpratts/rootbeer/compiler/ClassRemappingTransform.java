/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.util.SignatureUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.*;
import soot.jimple.CastExpr;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

public class ClassRemappingTransform {

  private ClassRemapping m_classRemapping;
  private Map<SootField, List<FieldRef>> m_fieldsToFix; 
  private Set<String> m_modified;
  private String m_currClass;
  private boolean m_hasNext;
  private boolean m_appClass;
  
  public ClassRemappingTransform(boolean map_runtime){
    m_classRemapping = new ClassRemapping();
    if(!map_runtime){
      m_classRemapping.loadMap();
    }
    m_fieldsToFix = new HashMap<SootField, List<FieldRef>>();
    m_modified = new HashSet<String>();
    m_hasNext = true;
  }
  
  public void reset(){
    m_hasNext = false;
  }
  
  public void run(List<String> reachable_methods){
    Set<String> visited = new HashSet<String>();
    SignatureUtil sig_util = new SignatureUtil();
    for(String method_sig : reachable_methods){
      if(visited.contains(method_sig)){
        continue;
      }
      visited.add(method_sig);
      
      String cls_name = sig_util.classFromMethodSig(method_sig);
      SootClass soot_class = Scene.v().getSootClass(cls_name);
      m_appClass = FastWholeProgram.v().isApplicationClass(soot_class);
      m_currClass = cls_name;
      String sub_sig = sig_util.methodSubSigFromMethodSig(method_sig);
      SootMethod soot_method = soot_class.getMethod(sub_sig);
      visit(soot_method);
    }
  }
  
  public void run(String cls) {
    SootClass soot_class = Scene.v().getSootClass(cls);
    run(cls, FastWholeProgram.v().isApplicationClass(soot_class));
  }
  
  private void run(String cls, boolean app_class){
    m_currClass = cls;
    SootClass soot_class = Scene.v().getSootClass(cls);
    m_appClass = app_class;
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      visit(method);
    }
  }
  
  public void finishClone(){
    List<String> cloned = m_classRemapping.getCloned();
    for(String cls : cloned){
      run(cls, true);
    }
    fixFields();
  }
  
  public Set<String> getModifiedClasses(){
    Set<String> ret = new HashSet<String>();
    ret.addAll(m_modified);
    ret.addAll(m_classRemapping.getValues());
    return ret;
  }
  
  public void setModified(){
    m_modified.add(m_currClass);
  }
  
  public void fixFields(){
    Iterator<SootField> iter = m_fieldsToFix.keySet().iterator();
    while(iter.hasNext()){
      SootField curr = iter.next();
      SootField orig = curr;
      SootClass field_cls = curr.getDeclaringClass();
      if(shouldMap(field_cls)){
        SootClass new_field_cls = getMapping(field_cls);
        curr = new_field_cls.getFieldByName(curr.getName());        
      }
      Type type = curr.getType();
      if(type instanceof RefType){
        RefType ref_type = (RefType) type;
        if(shouldMap(ref_type.getSootClass())){
          SootClass new_class = getMapping(ref_type.getSootClass());
          curr.setType(new_class.getType());
        }
      } else if(type instanceof ArrayType){
        ArrayType array_type = (ArrayType) type;
        if(array_type.baseType instanceof RefType == false){
          continue;
        }
        RefType ref_type = (RefType) array_type.baseType;
        if(shouldMap(ref_type.getSootClass())){
          SootClass new_class = getMapping(ref_type.getSootClass());
          ArrayType new_type = ArrayType.v(new_class.getType(), array_type.numDimensions);
          curr.setType(new_type);
        }
      }
      List<FieldRef> refs = m_fieldsToFix.get(orig);
      for(FieldRef ref : refs){
        ref.setFieldRef(curr.makeRef());
      }
    }
  }
  
  private void visit(SootMethod method) { 
    SootClass soot_class = method.getDeclaringClass();
    if(m_classRemapping.containsKey(soot_class.getName())){
      return;
    }
    Body body = method.retrieveActiveBody();
    if(body == null)
      return;
    fixArguments(method);
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      List<ValueBox> boxes = curr.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        value = mutate(value);
        box.setValue(value);
      }
    }
  }

  private Value mutate(Value value) {
    if(value instanceof FieldRef){
      FieldRef ref = (FieldRef) value;  
      SootField field = ref.getField();
      Type type = field.getType();
      if(type instanceof RefType){
        RefType ref_type = (RefType) type;
        SootClass soot_class = ref_type.getSootClass();
        if(shouldMap(soot_class)){
          addField(field, ref);
        }
      } else if(type instanceof ArrayType){
        ArrayType array_type = (ArrayType) type;
        Type base_type = array_type.baseType;
        if(base_type instanceof RefType){
          RefType ref_type = (RefType) base_type;
          SootClass soot_class = ref_type.getSootClass();
          if(shouldMap(soot_class)){
            addField(field, ref);
          }
        }
      } 
      SootClass soot_class = field.getDeclaringClass();
      if(shouldMap(soot_class)){
        addField(field, ref); 
      }      
      return value;
    } else if(value instanceof InvokeExpr){
      InvokeExpr expr = (InvokeExpr) value;
      SootMethodRef ref = expr.getMethodRef();
      SootClass soot_class = ref.declaringClass();
      if(shouldMap(soot_class)){
        SootClass new_class = getMapping(soot_class);
        SootMethod new_method = new_class.getMethod(ref.getSubSignature());
        fixArguments(new_method);
        expr.setMethodRef(new_method.makeRef());
      } else {
        if(soot_class.declaresMethod(ref.getSubSignature())){
          SootMethod method = soot_class.getMethod(ref.getSubSignature());
          fixArguments(method);
        }     
      }
      ref = remapRef(ref);
      try {
        if(shouldMap(soot_class)){
          soot_class = getMapping(soot_class);
        }
        SootMethod method = soot_class.getMethod(ref.getSubSignature());
        expr.setMethodRef(method.makeRef());
      } catch(Exception ex){
        //ex.printStackTrace();
      }
      return value;
    } else if(value instanceof NewExpr){
      NewExpr expr = (NewExpr) value;
      RefType base_type = expr.getBaseType();
      SootClass soot_class = base_type.getSootClass();
      if(shouldMap(soot_class)){
        SootClass new_class = getMapping(soot_class);
        expr.setBaseType(new_class.getType());
      }
      return value;
    } else if(value instanceof NewArrayExpr){
      NewArrayExpr expr = (NewArrayExpr) value;
      Type base_type = expr.getBaseType();
      base_type = fixType(base_type);
      expr.setBaseType(base_type);
      return value;      
    } else if(value instanceof NewMultiArrayExpr){
      NewMultiArrayExpr expr = (NewMultiArrayExpr) value;
      ArrayType array_type = expr.getBaseType();
      Type base_type = array_type.baseType;
      if(base_type instanceof RefType){
        RefType ref_type = (RefType) base_type;
        SootClass soot_class = ref_type.getSootClass();
        if(shouldMap(soot_class)){
          SootClass new_class = getMapping(soot_class);
          ArrayType new_type = ArrayType.v(new_class.getType(), array_type.numDimensions);
          expr.setBaseType(new_type);
        }
      }
      return value;
    } else if(value instanceof CastExpr){
      CastExpr expr = (CastExpr) value;
      Type cast_type = expr.getCastType();
      cast_type = fixType(cast_type);
      expr.setCastType(cast_type);
      return value;
    } else if(value instanceof ParameterRef){
      ParameterRef ref = (ParameterRef) value;
      Type new_type = fixType(ref.getType());
      return new ParameterRef(new_type, ref.getIndex());
    } else if(value instanceof ThisRef){
      ThisRef ref = (ThisRef) value;
      Type new_type = fixType(ref.getType());
      return new ThisRef((RefType) new_type);
    }else if(value instanceof Local){
      Local local = (Local) value;
      Type type = local.getType();
      local.setType(fixType(type));
      return value;
    } else {
      return value;
    }
  }

  private boolean shouldMap(SootClass soot_class) {
    if(m_appClass){
      if(m_classRemapping.containsKey(soot_class.getName())){
        setModified();
        return true;
      } else {
        return false;
      }
    } else {
      if(m_classRemapping.containsKey(soot_class.getName()) && !m_classRemapping.cloned(m_currClass)){
        m_hasNext = true;
        m_classRemapping.cloneClass(m_currClass);
      }
      return false;
    }
  }
  
  private SootMethodRef remapRef(SootMethodRef ref) {
    Type return_type = fixType(ref.returnType());
    List params = fixParameterList(ref.parameterTypes());
    int modifiers = Modifier.PUBLIC;
    if(ref.isStatic()){
      modifiers += Modifier.STATIC;
    }
    SootMethod method = new SootMethod(ref.name(), params, return_type, modifiers);
    SootClass decl_class = ref.declaringClass();
    if(shouldMap(decl_class)){
      decl_class = getMapping(decl_class);
    }
    method.setDeclaringClass(decl_class);
    return method.makeRef();
  }
  
  public ClassRemapping getClassRemapping(){
    return m_classRemapping;
  }

  private SootClass getMapping(SootClass soot_class) {
    String new_class = m_classRemapping.get(soot_class.getName());
    return Scene.v().getSootClass(new_class);
  }

  private void addField(SootField field, FieldRef ref) {
    List<FieldRef> refs;
    if(m_fieldsToFix.containsKey(field)){
      refs = m_fieldsToFix.get(field);
    } else {
      refs = new ArrayList<FieldRef>();
      m_fieldsToFix.put(field, refs);
    }
    refs.add(ref);
  }

  private void fixArguments(SootMethod method) {
    Type ret_type = method.getReturnType();
    method.setReturnType(fixType(ret_type));
    List param_types = method.getParameterTypes();
    List new_types = fixParameterList(param_types);
    method.setParameterTypes(new_types);    
  }
  
  private List fixParameterList(List param_types){
    List ret = new ArrayList();
    for(int i = 0; i < param_types.size(); ++i){
      Type type = (Type) param_types.get(i);
      ret.add(fixType(type));
    }  
    return ret;
  }
  
  private Type fixType(Type type){
    if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      SootClass soot_class = ref_type.getSootClass();
      if(shouldMap(soot_class)){
        SootClass new_class = getMapping(soot_class);
        return new_class.getType();
      } else {
        return type;
      }
    } else if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      Type base = fixType(array_type.baseType);
      return ArrayType.v(base, array_type.numDimensions);
    } else {
      return type;
    }
  }

  public boolean hasNext() {
    return m_hasNext;
  }
/*
  private String remapMethodSig(String method_sig) {
    SignatureUtil util = new SignatureUtil();
    String cls = util.classFromMethodSig(method_sig);
    String method_name = util.methodName(method_sig);
    String return_type = util.getReturnType(method_sig);
    List<String> params = util.getMethodParams(method_sig);
    cls = stringRemap(cls);
    for(int i = 0; i < params.size(); ++i){
      params.set(i, stringRemap(params.get(i)));
    }
    String new_sig = util.buildSignature(cls, method_name, return_type, params);
  }

  private String stringRemap(String type){
    if(m_classRemapping.containsKey(type)){
      m_hasNext = true;
      return m_classRemapping.get(type);
    }
    return type;
  }
  */
}
