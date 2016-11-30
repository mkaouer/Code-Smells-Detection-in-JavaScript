/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph.PermissionGraph;
import edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph.PermissionGraphNode;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.util.Stack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

public class VisitorReadGenStatic extends AbstractVisitorGen {
  
  private Local m_mem;
  private List<RefType> m_orderedHistory;
  private Set<RefType> m_writeToHeapMethodsMade;
  private Set<String> m_attachedReaders;
  private StaticOffsets m_staticOffsets;
  
  public VisitorReadGenStatic(BytecodeLanguage bcl, FieldReadWriteInspector inspector){
    super(inspector);
    m_bcl.push(bcl);
    
    m_orderedHistory = RootbeerScene.v().getDfsInfo().getOrderedRefTypes();
    m_writeToHeapMethodsMade = new HashSet<RefType>();
    m_attachedReaders = new HashSet<String>();
    m_objSerializing = new Stack<Local>();
    m_staticOffsets = new StaticOffsets();
    m_fieldInspector = inspector;
    m_currMem = new Stack<Local>();
  }
  
  public void makeMethod(){
   
    BytecodeLanguage bcl = m_bcl.top();
    
    bcl.startMethod("doReadStaticsFromHeap", VoidType.v());

    m_thisRef = bcl.refThis();
    m_mem = bcl.refInstanceField(m_thisRef, "mMem"); 
    m_currMem.push(m_mem);
    m_gcObjVisitor.push(m_thisRef);
    
    PermissionGraph graph = new PermissionGraph();   
    List<PermissionGraphNode> roots = graph.getRoots();
    for(PermissionGraphNode node : roots){
      SootClass soot_class = node.getSootClass();
      if(FastWholeProgram.v().isApplicationClass(soot_class)){
        attachAndCallReader(soot_class, node.getChildren());
      } else {
        doReader(soot_class);
      }
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_currMem.pop();
    m_gcObjVisitor.pop();    
  }

  private String getReaderName(SootClass soot_class){
    return "edu_syr_pcpratts_readStaticsFromHeap"+JavaNameToOpenCL.convert(soot_class.getName())+OpenCLScene.v().getIdent();
  }
  
  private void attachReader(SootClass soot_class, List<SootClass> children){    
    String method_name = getReaderName(soot_class);
    if(m_attachedReaders.contains(method_name))
      return;
    m_attachedReaders.add(method_name);
    
    List<OpenCLField> static_fields = m_staticOffsets.getStaticFields(soot_class);
    
    BytecodeLanguage bcl = new BytecodeLanguage();
    m_bcl.push(bcl);
    bcl.openClass(soot_class);
    SootClass mem = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.startStaticMethod(method_name, VoidType.v(), mem.getType(), m_thisRef.getType());
    
    Local memory = bcl.refParameter(0);
    Local gc_visit = bcl.refParameter(1);
    m_gcObjVisitor.push(gc_visit);
    m_currMem.push(memory);
    
    BclMemory bcl_mem = new BclMemory(bcl, memory);
    for(OpenCLField field : static_fields){
      if(m_fieldInspector.fieldIsWrittenOnGpu(field) == false)
        continue;
      
      int index = m_staticOffsets.getIndex(field);
      bcl_mem.setAddress(LongConstant.v(index));
      if(field.getType().isRefType()){
        readRefField(field);
      } else {
        readNonRefField(field);
      }      
    }
    
    for(SootClass child : children){
      attachAndCallReader(child, new ArrayList<SootClass>());
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_gcObjVisitor.pop();
    m_currMem.pop();
    m_bcl.pop();
  }

  private void attachAndCallReader(SootClass soot_class, List<SootClass> children) {
    String class_name = soot_class.getName();
    if(m_classesToIgnore.contains(class_name))
      return;
    
    attachReader(soot_class, children);
    callReader(soot_class);
  }

  private void callReader(SootClass soot_class) {    
    BytecodeLanguage bcl = m_bcl.top();
    String method_name = getReaderName(soot_class);
    SootClass mem = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.pushMethod(soot_class, method_name, VoidType.v(), mem.getType(), m_thisRef.getType());
    bcl.invokeStaticMethodNoRet(m_currMem.top(), m_gcObjVisitor.top());
  }

  private void doReader(SootClass soot_class) {
    BytecodeLanguage bcl = m_bcl.top();
    Local memory = m_currMem.top();
    Local gc_visit = m_gcObjVisitor.top();
    
    List<OpenCLField> static_fields = m_staticOffsets.getStaticFields(soot_class);
    
    BclMemory bcl_mem = new BclMemory(bcl, memory);
    SootClass obj = Scene.v().getSootClass("java.lang.Object");
    for(OpenCLField field : static_fields){
      Local field_value;
      
      if(field.getType().isRefType()){
        bcl_mem.useStaticPointer();
        bcl_mem.setAddress(LongConstant.v(m_staticOffsets.getIndex(field)));
        Local ref = bcl_mem.readRef();
        bcl_mem.useInstancePointer();
        
        if(FastWholeProgram.v().isApplicationClass(soot_class)){
          bcl_mem.useStaticPointer();
          bcl_mem.setAddress(LongConstant.v(m_staticOffsets.getIndex(field)));
          field_value = bcl_mem.readVar(field.getType().getSootType());
          bcl_mem.useInstancePointer();
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
        
        bcl.pushMethod(m_thisRef, "readFromHeap", obj.getType(), obj.getType(), BooleanType.v(), LongType.v());
        field_value = bcl.invokeMethodRet(m_thisRef, field_value, IntConstant.v(0), ref);
      } else {
        bcl_mem.useStaticPointer();
        bcl_mem.setAddress(LongConstant.v(m_staticOffsets.getIndex(field)));
        field_value = bcl_mem.readVar(field.getType().getSootType());
        bcl_mem.useInstancePointer();
      }
      
      if(field.isFinal()){
        continue;
      }
      
      if(FastWholeProgram.v().isApplicationClass(soot_class)){
        bcl.setStaticField(field.getSootField(), field_value);
      } else {
        SootClass string = Scene.v().getSootClass("java.lang.String");
        SootClass cls = Scene.v().getSootClass("java.lang.Class");
        if(field.getType().isRefType()){
          bcl.pushMethod(gc_visit, "writeStaticField", VoidType.v(), cls.getType(), string.getType(), obj.getType());
          bcl.invokeMethodNoRet(gc_visit, ClassConstant.v(toConstant(soot_class.getName())), StringConstant.v(field.getName()), field_value);
        } else {
          bcl.pushMethod(gc_visit, "writeStatic"+field.getType().getCapitalName()+"Field", VoidType.v(), cls.getType(), string.getType(), field.getType().getSootType());
          bcl.invokeMethodNoRet(gc_visit, ClassConstant.v(toConstant(soot_class.getName())), StringConstant.v(field.getName()), field_value);
        }
      }
      
    } 
  }
}
