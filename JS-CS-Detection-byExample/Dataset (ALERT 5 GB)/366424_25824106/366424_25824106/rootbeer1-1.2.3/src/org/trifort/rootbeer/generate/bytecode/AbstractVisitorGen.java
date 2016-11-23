/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.generate.opencl.OpenCLClass;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.fields.OpenCLField;
import org.trifort.rootbeer.util.Stack;

import soot.*;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;

public class AbstractVisitorGen {
  
  protected Local m_thisRef;
  protected Stack<Local> m_currThisRef;
  private int m_labelIndex;
  protected Stack<BytecodeLanguage> m_bcl;
  protected Stack<Local> m_gcObjVisitor;
  protected Stack<Local> m_currMem;
  protected Stack<Local> m_objSerializing;
  protected List<String> m_classesToIgnore;
  
  public AbstractVisitorGen(){
    m_labelIndex = 0; 
    m_bcl = new Stack<BytecodeLanguage>();
    m_gcObjVisitor = new Stack<Local>();
    m_currMem = new Stack<Local>();
    m_currThisRef = new Stack<Local>();
    m_objSerializing = new Stack<Local>();
    m_classesToIgnore = new ArrayList<String>();
    m_classesToIgnore.add("org.trifort.rootbeer.runtime.RootbeerGpu");
    m_classesToIgnore.add("org.trifort.rootbeer.runtime.Sentinal");
    m_classesToIgnore.add("org.trifort.rootbeer.runtimegpu.GpuException");
  }
  
  protected boolean differentPackageAndPrivate(RefType ref_inspecting) {
    RefType ref_type = (RefType) m_thisRef.getType();
    SootClass this_class = getClassForType(ref_type);
    SootClass class_inspecting = getClassForType(ref_inspecting);
    if(this_class.getPackageName().equals(class_inspecting.getPackageName()))
      return false;
    if(class_inspecting.isPublic() == false)
      return true;
    return false;
  }

  protected SootClass getClassForType(RefType ref_type){   
    SootClass soot_class = ref_type.getSootClass();
    soot_class = Scene.v().getSootClass(soot_class.getName()); 
    return soot_class;
  }
  
  protected String getTypeString(SootField soot_field){
    Type type = soot_field.getType();
    String name = type.toString();
    char[] name_array = name.toCharArray();
    name_array[0] = Character.toUpperCase(name_array[0]);
    return new String(name_array);
  }
  
  protected List<OpenCLField> getNonRefFields(SootClass soot_class){
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);
    return ocl_class.getInstanceNonRefFields();
  }

  protected List<OpenCLField> getRefFields(SootClass soot_class){
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);
    if(ocl_class == null){
      System.out.println("ocl_class == null: "+soot_class.getName());
    }
    return ocl_class.getInstanceRefFields();
  }
  
  protected SootClass getGcVisitorClass(Local visitor){    
    RefType type = (RefType) visitor.getType();
    SootClass gc_visitor = Scene.v().getSootClass(type.getClassName());
    return gc_visitor;    
  }

  protected String getNextLabel(){
    String ret = "rblabel"+Integer.toString(m_labelIndex);
    m_labelIndex++;
    return ret;
  }
  
  protected boolean typeIsPublic(Type type){
    Type poss_ref_type;
    if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      poss_ref_type = array_type.baseType;
    } else {
      poss_ref_type = type;
    }
    
    if(poss_ref_type instanceof RefType){
      RefType ref_type = (RefType) poss_ref_type;
      SootClass soot_class = ref_type.getSootClass();
      return soot_class.isPublic();
    } else {
      return true;
    }
  }
    
  protected void readRefField(OpenCLField ref_field) {
    SootField soot_field = ref_field.getSootField();
    SootClass soot_class = Scene.v().getSootClass(soot_field.getDeclaringClass().getName());

    BytecodeLanguage bcl = m_bcl.top();
    Local gc_obj_visit = m_gcObjVisitor.top();
    BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());

    Local ref = bcl_mem.readRef();
    bcl_mem.useInstancePointer();
    bcl_mem.pushAddress();
    bcl_mem.setAddress(ref);

    //bcl.println("reading field: "+ref_field.getName());
    
    SootClass obj_class = Scene.v().getSootClass("java.lang.Object");
    SootClass string = Scene.v().getSootClass("java.lang.String");
    SootClass class_class = Scene.v().getSootClass("java.lang.Class");
    Local original_field_value;
    if(soot_class.isApplicationClass() == false){
      if(ref_field.isInstance()){
        bcl.pushMethod(gc_obj_visit, "readField", obj_class.getType(), obj_class.getType(), string.getType());       
        original_field_value = bcl.invokeMethodRet(gc_obj_visit, m_objSerializing.top(), StringConstant.v(soot_field.getName()));
      } else { 
        bcl.pushMethod(gc_obj_visit, "readStaticField", obj_class.getType(), class_class.getType(), string.getType());
        Local cls = bcl.classConstant(soot_field.getDeclaringClass().getType());
        original_field_value = bcl.invokeMethodRet(gc_obj_visit, cls, StringConstant.v(soot_field.getName()));
      }
    } else {
      if(ref_field.isInstance()){
        original_field_value = bcl.refInstanceField(m_objSerializing.top(), ref_field.getName());
      } else {
        original_field_value = bcl.refStaticField(soot_class.getType(), ref_field.getName());
      } 
    }
    bcl.pushMethod(gc_obj_visit, "readFromHeap", obj_class.getType(), obj_class.getType(), BooleanType.v(), LongType.v());
    int should_read = 1;
    Local ret_obj = bcl.invokeMethodRet(gc_obj_visit, original_field_value, IntConstant.v(should_read), ref);
    
    Type type = soot_field.getType();
    Local ret = bcl.cast(type, ret_obj);

    if(soot_class.isApplicationClass() == false){
      if(ref_field.isInstance()){
        bcl.pushMethod(gc_obj_visit, "writeField", VoidType.v(), obj_class.getType(), string.getType(), obj_class.getType());       
        bcl.invokeMethodNoRet(gc_obj_visit, m_objSerializing.top(), StringConstant.v(soot_field.getName()), ret);
      } else {
        bcl.pushMethod(gc_obj_visit, "writeStaticField", VoidType.v(), class_class.getType(), string.getType(), obj_class.getType());
        Local cls = bcl.classConstant(soot_field.getDeclaringClass().getType());
        bcl.invokeMethodNoRet(gc_obj_visit, cls, StringConstant.v(soot_field.getName()), ret);
      }
    } else {
      if(ref_field.isInstance()){
        bcl.setInstanceField(soot_field, m_objSerializing.top(), ret);
      } else {
        bcl.setStaticField(soot_field, ret);
      }
    }

    bcl_mem.popAddress();
  }
    
  protected void readNonRefField(OpenCLField field) {
    SootField soot_field = field.getSootField();
    String function_name = "read"+getTypeString(soot_field);

    BytecodeLanguage bcl = m_bcl.top();
    bcl.pushMethod(m_currMem.top(), function_name, soot_field.getType());
    Local data = bcl.invokeMethodRet(m_currMem.top());

    SootClass soot_class = Scene.v().getSootClass(soot_field.getDeclaringClass().getName());
    if(soot_class.isApplicationClass()){
      if(field.isInstance()){
        bcl.setInstanceField(soot_field, m_objSerializing.top(), data);
      } else {
        bcl.setStaticField(soot_field, data);
      }
    } else {
      SootClass obj = Scene.v().getSootClass("java.lang.Object");
      SootClass string = Scene.v().getSootClass("java.lang.String");
      String static_str;
      SootClass first_param_type;
      Value first_param;
      if(field.isInstance()){
        static_str = "";
        first_param_type = obj;
        first_param = m_objSerializing.top();
      } else {
        static_str = "Static";
        first_param_type = Scene.v().getSootClass("java.lang.Class");
        first_param = ClassConstant.v(soot_class.getName());
      }
      String private_field_fun_name = "write"+static_str+getTypeString(soot_field);
      Local private_fields = bcl.newInstance("org.trifort.rootbeer.runtime.PrivateFields");
      bcl.pushMethod(private_fields, private_field_fun_name, VoidType.v(), first_param_type.getType(), string.getType(), string.getType(), soot_field.getType());       
      bcl.invokeMethodNoRet(private_fields, first_param, StringConstant.v(soot_field.getName()), StringConstant.v(soot_field.getDeclaringClass().getName()), data);
    }
  }
  
  protected String toConstant(String name) {
    return name.replace(".", "/");
  }
  
}
