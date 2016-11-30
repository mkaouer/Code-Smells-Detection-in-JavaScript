/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph.PermissionGraph;
import edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph.PermissionGraphNode;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

public class VisitorWriteGenStatic extends AbstractVisitorGen {
  
  private Local m_Mem;
  private StaticOffsets m_StaticOffsets;
  private Set<String> m_AttachedWriters;
    
  public VisitorWriteGenStatic(BytecodeLanguage bcl, FieldReadWriteInspector inspector){
    super(inspector);
    
    m_Bcl.push(bcl);
    m_StaticOffsets = new StaticOffsets();
    m_AttachedWriters = new HashSet<String>();
  }
  
  public void makeMethod(){
    BytecodeLanguage bcl = m_Bcl.top();    
    bcl.startMethod("doWriteStaticsToHeap", VoidType.v());
    
    m_ThisRef = bcl.refThis();
    m_GcObjVisitor.push(m_ThisRef);
    m_Mem = bcl.refInstanceField(m_ThisRef, "mMem");  
    m_CurrMem.push(m_Mem);
    
    BclMemory bcl_mem = new BclMemory(bcl, m_Mem);
    bcl_mem.useInstancePointer();
    bcl_mem.mallocWithSize(IntConstant.v(m_StaticOffsets.getEndIndex()));
    PermissionGraph graph = new PermissionGraph();   
    List<PermissionGraphNode> roots = graph.getRoots();
    for(PermissionGraphNode node : roots){
      SootClass soot_class = node.getSootClass();
      if(FastWholeProgram.v().isApplicationClass(soot_class)){
        attachAndCallWriter(soot_class, node.getChildren());
      } else {
        doWriter(soot_class);
      }
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
    
    m_GcObjVisitor.pop();
  }
  
  private void attachAndCallWriter(SootClass soot_class, List<SootClass> children){    
    String class_name = soot_class.getName();
    if(m_ClassesToIgnore.contains(class_name))
      return;
    
    attachWriter(soot_class, children);
    callWriter(soot_class);
  }
  
  private void callWriter(SootClass soot_class){    
    BytecodeLanguage bcl = m_Bcl.top();
    String method_name = getWriterName(soot_class);
    SootClass mem = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.pushMethod(soot_class, method_name, VoidType.v(), mem.getType(), m_GcObjVisitor.top().getType());
    bcl.invokeStaticMethodNoRet(m_CurrMem.top(), m_GcObjVisitor.top());
  }
  
  private String getWriterName(SootClass soot_class){
    return "edu_syr_pcpratts_writeStaticsToHeap"+JavaNameToOpenCL.convert(soot_class.getName())+OpenCLScene.v().getIdent();
  }

  private void attachWriter(SootClass soot_class, List<SootClass> children){    
        
    String method_name = getWriterName(soot_class);
    if(m_AttachedWriters.contains(method_name))
      return;
    m_AttachedWriters.add(method_name);
        
    BytecodeLanguage bcl = new BytecodeLanguage();
    m_Bcl.push(bcl);
    bcl.openClass(soot_class);
    SootClass mem = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.startStaticMethod(method_name, VoidType.v(), mem.getType(), m_GcObjVisitor.top().getType());
    
    Local memory = bcl.refParameter(0);
    Local gc_visit = bcl.refParameter(1);
    m_CurrMem.push(memory);
    m_GcObjVisitor.push(gc_visit);
    
    doWriter(soot_class);
    
    for(SootClass child : children){
      attachAndCallWriter(child, new ArrayList<SootClass>());
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_Bcl.pop();
    m_GcObjVisitor.pop();
    m_CurrMem.pop();
  }
  
  private void doWriter(SootClass soot_class){  
    BytecodeLanguage bcl = m_Bcl.top();
    Local memory = m_CurrMem.top();
    Local gc_visit = m_GcObjVisitor.top();
    
    List<OpenCLField> static_fields = m_StaticOffsets.getStaticFields(soot_class);
    
    BclMemory bcl_mem = new BclMemory(bcl, memory);
    SootClass obj = Scene.v().getSootClass("java.lang.Object");
    for(OpenCLField field : static_fields){
      Local field_value;
      if(FastWholeProgram.v().isApplicationClass(soot_class)){
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
  }
}
