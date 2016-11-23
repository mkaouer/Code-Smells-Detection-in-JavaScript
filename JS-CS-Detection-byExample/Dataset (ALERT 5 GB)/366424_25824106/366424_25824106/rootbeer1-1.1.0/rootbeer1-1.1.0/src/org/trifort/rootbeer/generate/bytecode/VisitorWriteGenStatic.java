/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.generate.bytecode.permissiongraph.PermissionGraph;
import org.trifort.rootbeer.generate.bytecode.permissiongraph.PermissionGraphNode;
import org.trifort.rootbeer.generate.opencl.ClassConstantNumbers;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.fields.OpenCLField;

import soot.*;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;
import soot.rbclassload.RootbeerClassLoader;

public class VisitorWriteGenStatic extends AbstractVisitorGen {
  
  private Local m_Mem;
  private StaticOffsets m_StaticOffsets;
  private Set<String> m_AttachedWriters;
    
  public VisitorWriteGenStatic(BytecodeLanguage bcl){   
    m_bcl.push(bcl);
    m_StaticOffsets = new StaticOffsets();
    m_AttachedWriters = new HashSet<String>();
  }
  
  public void makeMethod(){
    BytecodeLanguage bcl = m_bcl.top();    
    bcl.startMethod("doWriteStaticsToHeap", VoidType.v());
    
    m_thisRef = bcl.refThis();
    m_currThisRef.push(m_thisRef);
    m_gcObjVisitor.push(m_thisRef);
    m_Mem = bcl.refInstanceField(m_thisRef, "mMem");  
    m_currMem.push(m_Mem);
    
    BclMemory bcl_mem = new BclMemory(bcl, m_Mem);
    bcl_mem.useInstancePointer();
    bcl_mem.mallocWithSize(IntConstant.v(m_StaticOffsets.getEndIndex()));
    PermissionGraph graph = new PermissionGraph();   
    List<PermissionGraphNode> roots = graph.getRoots();
    for(PermissionGraphNode node : roots){
      SootClass soot_class = node.getSootClass();
      if(soot_class.isApplicationClass()){
        attachAndCallWriter(soot_class, node.getChildren());
      } else {
        //doWriter(soot_class, node.getChildren());
        doWriter(soot_class, new ArrayList<SootClass>());
      }
    }
    
    //write .class's for array types
    Set<ArrayType> array_types = RootbeerClassLoader.v().getDfsInfo().getArrayTypes();
    for(ArrayType type : array_types){
      writeType(type);
    }
    
    bcl_mem.useStaticPointer();
    bcl_mem.setAddress(LongConstant.v(m_StaticOffsets.getLockStart()));
    //write the lock objects for all the classes
    int count = m_StaticOffsets.getClassSize();
    for(int i = 0; i < count; ++i){
      bcl_mem.writeInt(-1);
    }
    int zeros = m_StaticOffsets.getZerosSize();
    for(int i = 0; i < zeros; ++i){
      bcl_mem.writeByte((byte) 0);
    }
    bcl_mem.useInstancePointer();
    
    bcl.returnVoid();    
    bcl.endMethod();
    
    m_gcObjVisitor.pop();
  }
  
  private void attachAndCallWriter(SootClass soot_class, List<SootClass> children){    
    String class_name = soot_class.getName();
    if(m_classesToIgnore.contains(class_name))
      return;
    
    attachWriter(soot_class, children);
    callWriter(soot_class);
  }
  
  private void callWriter(SootClass soot_class){    
    BytecodeLanguage bcl = m_bcl.top();
    String method_name = getWriterName(soot_class);
    SootClass mem = Scene.v().getSootClass("org.trifort.rootbeer.runtime.Memory");
    bcl.pushMethod(soot_class, method_name, VoidType.v(), mem.getType(), m_gcObjVisitor.top().getType());
    bcl.invokeStaticMethodNoRet(m_currMem.top(), m_gcObjVisitor.top());
  }
  
  private String getWriterName(SootClass soot_class){
    return "org_trifort_writeStaticsToHeap"+JavaNameToOpenCL.convert(soot_class.getName())+OpenCLScene.v().getIdent();
  }

  private void attachWriter(SootClass soot_class, List<SootClass> children){    
        
    String method_name = getWriterName(soot_class);
    if(m_AttachedWriters.contains(method_name))
      return;
    m_AttachedWriters.add(method_name);
        
    BytecodeLanguage bcl = new BytecodeLanguage();
    m_bcl.push(bcl);
    bcl.openClass(soot_class);
    SootClass mem = Scene.v().getSootClass("org.trifort.rootbeer.runtime.Memory");
    bcl.startStaticMethod(method_name, VoidType.v(), mem.getType(), m_gcObjVisitor.top().getType());
    
    Local memory = bcl.refParameter(0);
    Local gc_visit = bcl.refParameter(1);
    m_currMem.push(memory);
    m_gcObjVisitor.push(gc_visit);
    
    doWriter(soot_class, children);
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_gcObjVisitor.pop();
    m_currMem.pop();
    m_bcl.pop();
  }
  
  private void doWriter(SootClass soot_class, List<SootClass> children){  
    BytecodeLanguage bcl = m_bcl.top();
    Local memory = m_currMem.top();
    Local gc_visit = m_gcObjVisitor.top();
    
    writeType(soot_class.getType());
    
    List<OpenCLField> static_fields = m_StaticOffsets.getStaticFields(soot_class);
    
    BclMemory bcl_mem = new BclMemory(bcl, memory);
    SootClass obj = Scene.v().getSootClass("java.lang.Object");
    for(OpenCLField field : static_fields){
      Local field_value;
      if(soot_class.isApplicationClass()){
        field_value = bcl.refStaticField(soot_class.getType(), field.getName());
      } else {
        SootClass string = Scene.v().getSootClass("java.lang.String");
        SootClass cls = Scene.v().getSootClass("java.lang.Class");
        bcl.pushMethod(gc_visit, "readStaticField", obj.getType(), cls.getType(), string.getType());
        Local obj_field_value = bcl.invokeMethodRet(gc_visit, ClassConstant.v(toConstant(soot_class.getName())), StringConstant.v(field.getName()));
        if(field.getType().isRefType()){
          field_value = obj_field_value;
        } else {
          Local capital_value = bcl.cast(field.getType().getCapitalType(), obj_field_value);
          bcl.pushMethod(capital_value, field.getType().getName()+"Value", field.getType().getSootType());
          field_value = bcl.invokeMethodRet(capital_value);
        }
      }
      if(field.getType().isRefType()){
        bcl.pushMethod(gc_visit, "writeToHeap", LongType.v(), obj.getType(), BooleanType.v());
        Local ref = bcl.invokeMethodRet(gc_visit, field_value, IntConstant.v(1));
        bcl_mem.useStaticPointer();
        bcl_mem.setAddress(LongConstant.v(m_StaticOffsets.getIndex(field)));
        bcl_mem.writeRef(ref);
        bcl_mem.useInstancePointer();
      } else {
        bcl_mem.useStaticPointer();
        bcl_mem.setAddress(LongConstant.v(m_StaticOffsets.getIndex(field)));
        bcl_mem.writeVar(field_value);
        bcl_mem.useInstancePointer();
      }
    } 
    
    for(SootClass child : children){
      attachAndCallWriter(child, new ArrayList<SootClass>());
    }
  }

  private boolean reachesJavaLangClass(){
    List<RefType> ref_types = RootbeerClassLoader.v().getDfsInfo().getOrderedRefTypes();  
    RefType java_lang_class = RefType.v("java.lang.Class");
    return ref_types.contains(java_lang_class);
  }
  
  private void writeType(Type type) {
    if(reachesJavaLangClass() == false){
      return;
    }
    int number = OpenCLScene.v().getClassConstantNumbers().get(type);
    Local gc_visit = m_gcObjVisitor.top();
    Local class_obj = null;
    
    if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      class_obj = m_bcl.top().classConstant(type);   
    } else {
      RefType ref_type = (RefType) type;
      class_obj = m_bcl.top().classConstant(type);   
    }
    
    //getName has to be called to load the name variable
    SootClass str_cls = Scene.v().getSootClass("java.lang.String");
    m_bcl.top().pushMethod(class_obj, "getName", str_cls.getType());
    m_bcl.top().invokeMethodRet(class_obj);
    
    SootClass obj_cls = Scene.v().getSootClass("java.lang.Object");
    m_bcl.top().pushMethod(gc_visit, "writeToHeap", LongType.v(), obj_cls.getType(), BooleanType.v());
    Local ref = m_bcl.top().invokeMethodRet(gc_visit, class_obj, IntConstant.v(1));
    
    m_bcl.top().pushMethod(gc_visit, "addClassRef", VoidType.v(), LongType.v(), IntType.v());
    m_bcl.top().invokeMethodNoRet(gc_visit, ref, IntConstant.v(number));
  }
}
