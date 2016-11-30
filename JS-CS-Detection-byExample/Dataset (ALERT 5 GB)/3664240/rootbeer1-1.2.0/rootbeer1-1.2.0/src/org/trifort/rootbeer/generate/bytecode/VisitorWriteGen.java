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

import org.trifort.rootbeer.generate.opencl.OpenCLClass;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.OpenCLType;
import org.trifort.rootbeer.generate.opencl.fields.OpenCLField;
import org.trifort.rootbeer.util.Stack;

import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.rbclassload.RootbeerClassLoader;

public class VisitorWriteGen extends AbstractVisitorGen {
  
  private Stack<Local> m_CurrObj;
  private Stack<Local> m_CurrentMem;
  private Local m_Mem;
  private Local m_TextureMem;
  
  private Local m_Param0;
  private Local m_Param1;
  private Local m_RefParam;
  private Local m_ReadOnlyParam;
  private List<Type> m_OrderedHistory;
  private Set<Type> mWriteToHeapMethodsMade;
  private Set<String> m_VisitedWriter;
  private boolean m_ApplicationClass;
  private SootClass m_CurrClass;
  private List<Value> m_ValuesWritten;
  private Local m_Array;
  
  public VisitorWriteGen(List<Type> ordered_history, String class_name, 
    BytecodeLanguage bcl){
    
    m_bcl = new Stack<BytecodeLanguage>();
    m_CurrObj = new Stack<Local>();
    m_OrderedHistory = ordered_history;
    mWriteToHeapMethodsMade = new HashSet<Type>();    
    m_VisitedWriter = new HashSet<String>();
    m_ValuesWritten = new ArrayList<Value>();
    m_bcl.push(bcl);
    
    m_CurrentMem = new Stack<Local>();
    m_gcObjVisitor = new Stack<Local>();
  }
    
  public void makeWriteToHeapMethod() {
    SootClass obj_cls = Scene.v().getSootClass("java.lang.Object");
    BytecodeLanguage bcl = m_bcl.top();
    bcl.startMethod("doWriteToHeap", VoidType.v(), obj_cls.getType(), BooleanType.v(), LongType.v(), BooleanType.v());
    m_thisRef = bcl.refThis();
    m_gcObjVisitor.push(m_thisRef);
    m_Param0 = bcl.refParameter(0);
    m_Param1 = bcl.refParameter(1);
    m_RefParam = bcl.refParameter(2);
    m_ReadOnlyParam = bcl.refParameter(3);    
    
    m_Mem = bcl.refInstanceField(m_thisRef, "mMem");
    m_TextureMem = bcl.refInstanceField(m_thisRef, "mTextureMem");
    
    String label1 = getNextLabel();    
    String label2 = getNextLabel();
    
    Local mem = bcl.local(m_Mem.getType());
    bcl.ifStmt(m_ReadOnlyParam, "==", IntConstant.v(1), label1);
    bcl.assign(mem, m_Mem);
    bcl.gotoLabel(label2);
    bcl.label(label1);
    bcl.assign(mem, m_TextureMem);
    bcl.label(label2);        
    m_CurrentMem.push(mem);
        
    //make writers for java.lang.String and char[]
    SootClass string_class = Scene.v().getSootClass("java.lang.String");
    RefType string_type = string_class.getType();
    String label = getNextLabel();
    bcl.ifInstanceOfStmt(m_Param0, string_type, label);
    makeWriteToHeapBodyForString(string_type);
    mWriteToHeapMethodsMade.add(string_type);
    bcl.label(label);
    
    ArrayType char_array_type = ArrayType.v(CharType.v(), 1);
    label = getNextLabel();
    bcl.ifInstanceOfStmt(m_Param0, char_array_type, label);
    makeWriteToHeapBodyForArrayType(char_array_type);
    mWriteToHeapMethodsMade.add(char_array_type);
    bcl.label(label);
    
    for(Type type : m_OrderedHistory){
      makeWriteToHeapMethodForType(type);
    }

    bcl.returnVoid();
    bcl.endMethod();
  }
  
  private void makeWriteToHeapMethodForType(Type type){
    if(type instanceof ArrayType == false &&
       type instanceof RefType == false){
      return;
    }
    
    if(mWriteToHeapMethodsMade.contains(type))
      return;
    mWriteToHeapMethodsMade.add(type);
    
    if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      SootClass soot_class = ref_type.getSootClass();
      if(soot_class.getName().equals("java.lang.Object"))
        return; 
      if(differentPackageAndPrivate(ref_type)){
        return;  
      }
      if(soot_class.isInterface()){
        return;
      }
      if(m_classesToIgnore.contains(ref_type.getSootClass().getName())){
        return; 
      }              
    }
    
    if(typeIsPublic(type) == false)
      return;
    
    String label = getNextLabel();
    BytecodeLanguage bcl = m_bcl.top();
    bcl.ifInstanceOfStmt(m_Param0, type, label);
        
    if(type instanceof ArrayType){
      makeWriteToHeapBodyForArrayType((ArrayType) type);
    } else {
      RefType ref_type = (RefType) type;
      makeWriteToHeapBodyForRefType(ref_type);
    }
    bcl.label(label);
  }
  
  private void makeWriteToHeapBodyForArrayType(ArrayType type) {
    BytecodeLanguage bcl = m_bcl.top();
    Local object_to_write_from = bcl.cast(type, m_Param0);

    Local length = bcl.lengthof(object_to_write_from);
    int class_id = RootbeerClassLoader.v().getClassNumber(type.toString());

    BclMemory bcl_mem = new BclMemory(bcl, m_CurrentMem.top());
    bcl_mem.writeByte((byte) 0);      //ref_type count              [0]
    bcl_mem.writeByte((byte) 0);      //garabage collector color    [1]
    bcl_mem.writeByte((byte) 0);      //reserved                    [2]
    bcl_mem.writeByte((byte) 0);      //ctor used                   [3]
    bcl_mem.writeInt(class_id);       //class number                [4]

    Local size = bcl.local(IntType.v());
    bcl.assign(size, IntConstant.v(Constants.SizeGcInfo));
      
    Local element_size = bcl.local(IntType.v());
    if(type.numDimensions == 1){
      OpenCLType ocl_type = new OpenCLType(type.baseType);
      bcl.assign(element_size, IntConstant.v(ocl_type.getSize()));
    } else {
      bcl.assign(element_size, IntConstant.v(4));
    }
    bcl.mult(element_size, length);
    bcl.plus(size, element_size);
    bcl_mem.writeInt(size);           //object size                 [8]
    bcl_mem.writeInt(length);         //array length                [12]
    bcl_mem.writeInt(-1);             //monitor                     [16]
    bcl_mem.writeInt(0);              //reserved                    [20]
    bcl_mem.writeInt(0);              //reserved                    [24]
    bcl_mem.writeInt(0);              //reserved                    [28]

    //optimization for single-dimensional arrays of primitive types.
    //doesn't work for chars yet because they are still stored as ints on the gpu
    if(type.baseType instanceof PrimType && type.numDimensions == 1 &&
       type.baseType.equals(CharType.v()) == false){
      
      bcl.pushMethod(m_CurrentMem.top(), "writeArray", VoidType.v(), type);
      bcl.invokeMethodNoRet(m_CurrentMem.top(), object_to_write_from);
      bcl_mem.incrementAddress(element_size);
      bcl.returnVoid();
      return;
    }
    
    Local space_for_elements = bcl.local(IntType.v());
    bcl.assign(space_for_elements, length);
    bcl.mult(space_for_elements, IntConstant.v(4));

    if(type.numDimensions != 1 || type.baseType instanceof RefType){      
      bcl_mem.startIntegerList();
      bcl_mem.incrementAddress(space_for_elements);
    }

    Local i = bcl.local(IntType.v());
    bcl.assign(i, IntConstant.v(0));

    String end_for_label = getNextLabel();
    String before_if_label = getNextLabel();
    bcl.label(before_if_label);
    bcl.ifStmt(i, "==", length, end_for_label);
    Local curr = bcl.indexArray(object_to_write_from, i);

    if(type.numDimensions != 1){
      SootClass object_soot_class = Scene.v().getSootClass("java.lang.Object");
      bcl.pushMethod(m_thisRef, "writeToHeap", LongType.v(), object_soot_class.getType(), BooleanType.v());
      Local array_element = bcl.invokeMethodRet(m_thisRef, curr, m_Param1);
      bcl_mem.addIntegerToList(array_element);
    } else if(type.baseType instanceof RefType){
      SootClass object_soot_class = Scene.v().getSootClass("java.lang.Object");
      bcl.pushMethod(m_thisRef, "writeToHeap", LongType.v(), object_soot_class.getType(), BooleanType.v());
      Local array_element = bcl.invokeMethodRet(m_thisRef, curr, m_Param1);
      bcl_mem.addIntegerToList(array_element);
    } else {
      bcl_mem.writeVar(curr);
    }
    bcl.plus(i, 1);
    bcl.gotoLabel(before_if_label);
    bcl.label(end_for_label);

    if(type.numDimensions != 1 || type.baseType instanceof RefType){
      bcl_mem.endIntegerList();
    } 
    //return ret;
    bcl.returnVoid();
  }
  
  private void makeWriteToHeapBodyForString(RefType type){
    BytecodeLanguage bcl = m_bcl.top();
    BclMemory bcl_mem = new BclMemory(bcl, m_CurrentMem.top());
    int class_id = RootbeerClassLoader.v().getClassNumber(type.toString());
    SootClass soot_class = type.getSootClass();
    
    Local object_to_write_from = bcl.cast(type, m_Param0);
    
    int size = Constants.SizeGcInfo + 16;
    int gc_count = 1;
        
    bcl_mem.writeByte((byte) gc_count);      //ref_type count [0]
    bcl_mem.writeByte((byte) 0);             //garabage collector color [1]
    bcl_mem.writeByte((byte) 0);             //reserved [2]
    bcl_mem.writeByte((byte) 0);             //ctor used [3]
    bcl_mem.writeInt(class_id);              //class number [4]
    bcl_mem.writeInt(size);                  //object size [8]
    bcl_mem.writeInt(0);                     //reserved [12]
    bcl_mem.writeInt(-1);                    //monitor [16]
    
    int written_size = 1+1+1+1+4+4+4+4;
    
    bcl_mem.incrementAddress(Constants.SizeGcInfo - written_size);
    bcl_mem.pushAddress();
    
    int size_minus_gc_info = size - Constants.SizeGcInfo;
    bcl_mem.incrementAddress(size_minus_gc_info);
        
    m_CurrClass = soot_class;
    m_CurrObj.push(object_to_write_from);
    
    ArrayType char_array_type = ArrayType.v(CharType.v(), 1);
    bcl.pushMethod(object_to_write_from, "toCharArray", char_array_type);
    Local char_array = bcl.invokeMethodRet(object_to_write_from);
    
    SootClass object_class = Scene.v().getSootClass("java.lang.Object");
    bcl.pushMethod(m_gcObjVisitor.top(), "writeToHeap", LongType.v(), 
        object_class.getType(), BooleanType.v());
    Local char_array_address = bcl.invokeMethodRet(m_gcObjVisitor.top(), char_array, 
        IntConstant.v(1));
    m_ValuesWritten.add(char_array_address);
        
    Local after_array_write_address = bcl_mem.getPointer();
    bcl_mem.popAddress();
    
    invokeWriteRefs(m_CurrClass, m_CurrentMem.top());
    
    bcl_mem.align();
    
    bcl_mem.setAddress(after_array_write_address);    
    
    BclMemory bcl_mem0 = new BclMemory(bcl, m_Mem);
    bcl_mem0.align();
    BclMemory bcl_mem1 = new BclMemory(bcl, m_TextureMem);
    bcl_mem1.align();         

    bcl.returnVoid();
    
    m_CurrObj.pop();
  }
  
  private void makeWriteToHeapBodyForRefType(RefType type){
    BytecodeLanguage bcl = m_bcl.top();
    BclMemory bcl_mem = new BclMemory(bcl, m_CurrentMem.top());
    int class_id = RootbeerClassLoader.v().getClassNumber(type.toString());
    SootClass soot_class = type.getSootClass();
    
    Local object_to_write_from = bcl.cast(type, m_Param0);
    
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);
    int size = ocl_class.getSize();
    int gc_count = ocl_class.getRefFieldsSize();
    
    bcl_mem.writeByte((byte) gc_count);      //ref_type count [0]
    bcl_mem.writeByte((byte) 0);             //garabage collector color [1]
    bcl_mem.writeByte((byte) 0);             //reserved [2]
    bcl_mem.writeByte((byte) 0);             //ctor used [3]
    bcl_mem.writeInt(class_id);              //class number [4]
    bcl_mem.writeInt(size);                  //object size [8]
    bcl_mem.writeInt(0);                     //reserved [12]
    bcl_mem.writeInt(-1);                    //monitor [16]
    
    int written_size = 1+1+1+1+4+4+4+4;
    
    bcl_mem.incrementAddress(Constants.SizeGcInfo - written_size);
    
    bcl_mem.pushAddress();
    
    int size_minus_gc_info = size - Constants.SizeGcInfo;
    bcl_mem.incrementAddress(size_minus_gc_info);
        
    m_CurrClass = soot_class;
    m_CurrObj.push(object_to_write_from);
    
    writeFields(true);
    
    Local after_array_write_address = bcl_mem.getPointer();
    bcl_mem.popAddress();
    
    invokeWriteRefs(m_CurrClass, m_CurrentMem.top());
    
    bcl_mem.align();
    
    writeFields(false); 
    bcl_mem.setAddress(after_array_write_address);    
    
    BclMemory bcl_mem0 = new BclMemory(bcl, m_Mem);
    bcl_mem0.align();
    BclMemory bcl_mem1 = new BclMemory(bcl, m_TextureMem);
    bcl_mem1.align();         

    bcl.returnVoid();
    
    m_CurrObj.pop();
  }
  
  private void writeFields(boolean ref_fields){    
    if(m_CurrClass.isApplicationClass()){
      attachWriter(m_CurrClass.getName(), ref_fields);
      callBaseClassWriter(m_CurrClass.getName(), ref_fields);
    } else {
      insertWriter(m_CurrClass.getName(), ref_fields);
    }
  }
  
  public void invokeWriteRefs(SootClass curr_class, Local mem_local) {
    BytecodeLanguage bcl = m_bcl.top();
    if(curr_class.isApplicationClass()){
      SootClass mem = Scene.v().getSootClass("org.trifort.rootbeer.runtime.Memory");
      String specialization = JavaNameToOpenCL.convert(curr_class.getName())+OpenCLScene.v().getIdent();
      bcl.pushMethod(m_CurrObj.top(), "org_trifort_writeRefs"+specialization, VoidType.v(), mem.getType());
      bcl.invokeMethodNoRet(m_CurrObj.top(), mem_local);
    } else {
      BclMemory bcl_mem = new BclMemory(bcl, mem_local);
      for(Value value : m_ValuesWritten){
        bcl_mem.writeRef(value);
      }
    }
    m_ValuesWritten.clear();
  }
  
  public void insertWriter(String class_name, boolean ref_fields){
    m_ApplicationClass = false;      
    
    boolean application_class = generatingCodeInApplicationClass();
    doWriter(class_name, ref_fields, application_class);
            
    SootClass curr_class = Scene.v().getSootClass(class_name);  
    if(curr_class.hasSuperclass() == false)
      return;
    
    SootClass parent = curr_class.getSuperclass();
    parent = Scene.v().getSootClass(parent.getName());
    if(parent.getName().equals("java.lang.Object") == false){
      if(parent.isApplicationClass()){
        attachWriter(parent.getName(), ref_fields);
        callBaseClassWriter(parent.getName(), ref_fields);
      } else {
        insertWriter(parent.getName(), ref_fields);
      }
    }    
  }
  
  private boolean generatingCodeInApplicationClass() {
    BytecodeLanguage bcl = m_bcl.top();
    SootClass soot_class = bcl.getSootClass();
    RefType gc_type = (RefType) m_gcObjVisitor.top().getType();
    
    if(soot_class.getName().equals(gc_type.getClassName())){
      return false;
    } else {
      return soot_class.isApplicationClass();
    }
  }

  private int sizeRefsArray(SootClass soot_class) {
    int ret = 0;
    while(true){
      ret += getRefFields(soot_class).size();
      soot_class = Scene.v().getSootClass(soot_class.getSuperclass().getName());
      if(soot_class.getName().equals("java.lang.Object"))
        return ret;
      if(soot_class.isApplicationClass())
        return ret;
    }
    
  }
  
  public void attachWriter(String class_name, boolean ref_fields){
    
    String specialization;
    
    if(ref_fields){
      specialization = "RefFields";
    } else {
      specialization = "NonRefFields";
    }
    specialization += JavaNameToOpenCL.convert(class_name)+OpenCLScene.v().getIdent();
    String visited_name = class_name + specialization;
    
    if(m_VisitedWriter.contains(visited_name))
      return;
    m_VisitedWriter.add(visited_name);
        
    SootClass curr_class = Scene.v().getSootClass(class_name);
    SootClass parent = curr_class.getSuperclass();
    parent = Scene.v().getSootClass(parent.getName());
    if(parent.isApplicationClass()){
      attachWriter(parent.getName(), ref_fields);
    }    
    
    BytecodeLanguage bcl = new BytecodeLanguage();
    m_bcl.push(bcl);
    bcl.openClass(class_name);    
    
    SootClass mem = Scene.v().getSootClass("org.trifort.rootbeer.runtime.Memory");
    bcl.startMethod("org_trifort_writeToHeap"+specialization, VoidType.v(), mem.getType(), m_gcObjVisitor.top().getType());
    m_CurrObj.push(bcl.refThis()); 
    m_CurrentMem.push(bcl.refParameter(0));
    m_gcObjVisitor.push(bcl.refParameter(1));  
    m_ApplicationClass = true;
    
    SootClass soot_class = Scene.v().getSootClass(class_name);
    if(ref_fields){
      ArrayType array = ArrayType.v(LongType.v(), 1);
      m_Array = bcl.local(array);
      bcl.addFieldToClass(m_Array, "org_trifort_refs_array"+OpenCLScene.v().getIdent());
      Value array_instance = bcl.newArray(array, IntConstant.v(sizeRefsArray(soot_class)));
      bcl.assign(m_Array, array_instance);
      bcl.refInstanceFieldFromInput(m_CurrObj.top(), "org_trifort_refs_array"+OpenCLScene.v().getIdent(), m_Array);
    }
    
    doWriter(class_name, ref_fields, true); 
    
    if(parent.getName().equals("java.lang.Object") == false){
      if(parent.isApplicationClass()){        
        callBaseClassWriter(parent.getName(), ref_fields);
      } else {
        insertWriter(parent.getName(), ref_fields);
      }
    }

    bcl.returnVoid();
    bcl.endMethod();
    m_CurrObj.pop();
    m_CurrentMem.pop();
    m_gcObjVisitor.pop();
    
    if(ref_fields){
      bcl.startMethod("org_trifort_writeRefs"+JavaNameToOpenCL.convert(class_name)+OpenCLScene.v().getIdent(), VoidType.v(), mem.getType());
      m_CurrObj.push(bcl.refThis());
      m_CurrentMem.push(bcl.refParameter(0));

      m_Array = bcl.refInstanceField(m_CurrObj.top(), "org_trifort_refs_array"+OpenCLScene.v().getIdent());
      BclMemory bcl_mem = new BclMemory(bcl, m_CurrentMem.top());
      for(int i = 0; i < sizeRefsArray(soot_class); ++i){
        Local ref = bcl.indexArray(m_Array, IntConstant.v(i));
        bcl_mem.writeRef(ref);
      }
      
      if(parent.isApplicationClass())
        invokeWriteRefs(parent, m_CurrentMem.top());
        
      bcl.returnVoid();
      bcl.endMethod();
      m_CurrObj.pop();
      m_CurrentMem.pop();
    }
    
    m_bcl.pop();
  }
      
  public void doWriter(String class_name, boolean do_ref_fields, boolean is_application){      
        
    BytecodeLanguage bcl = m_bcl.top();
    SootClass soot_class = Scene.v().getSootClass(class_name);
    List<OpenCLField> ref_fields = getRefFields(soot_class);
    List<OpenCLField> non_ref_fields = getNonRefFields(soot_class); 
    
    if(do_ref_fields){
      int count = 0;
      for(OpenCLField ref_field : ref_fields){
        int constant = 1;
        Local local;
        if(ref_field.isInstance()){
          local = writeRefField(ref_field, IntConstant.v(constant));
          m_ValuesWritten.add(local);
        } else {
          local = writeStaticRefField(ref_field, IntConstant.v(constant));
          m_ValuesWritten.add(local);
        }
        if(is_application){
          bcl.assignArray(m_Array, IntConstant.v(count), local);
          count++;
        }
      }
    } else {
      for(OpenCLField ocl_field : non_ref_fields){
        writeNonRefField(ocl_field);
      }
    }
  }
  
  private Local writeRefField(OpenCLField ref_field, Value copy_values){
    BytecodeLanguage bcl = m_bcl.top();
    SootField soot_field = ref_field.getSootField();
    SootClass obj = Scene.v().getSootClass("java.lang.Object");
    Local field_value;
    if(m_ApplicationClass){
      field_value = bcl.refInstanceField(m_CurrObj.top(), soot_field.getName());
    } else {
      SootClass string = Scene.v().getSootClass("java.lang.String");
      bcl.pushMethod(m_gcObjVisitor.top(), "readField", obj.getType(), obj.getType(), string.getType());       
      field_value = bcl.invokeMethodRet(m_gcObjVisitor.top(), m_CurrObj.top(), StringConstant.v(soot_field.getName()));
    }
    
    bcl.pushMethod(m_gcObjVisitor.top(), "writeToHeap", LongType.v(), obj.getType(), BooleanType.v());
    return bcl.invokeMethodRet(m_gcObjVisitor.top(), field_value, copy_values);
  }

  private Local writeStaticRefField(OpenCLField ref_field, IntConstant copy_values) {
    SootField soot_field = ref_field.getSootField();
    BytecodeLanguage bcl = m_bcl.top();
    
    //field = getField
    Local field_value = bcl.refStaticField(m_CurrObj.top(), soot_field.getName());
    SootClass object_soot_class = Scene.v().getSootClass("java.lang.Object");
    bcl.pushMethod(m_gcObjVisitor.top(), "writeToHeap", LongType.v(), object_soot_class.getType(), BooleanType.v());
    return bcl.invokeMethodRet(m_gcObjVisitor.top(), field_value, copy_values);
  }
  
  private void writeNonRefField(OpenCLField non_ref_field){
    SootField soot_field = non_ref_field.getSootField();
    String function_name = "write"+getTypeString(soot_field);
    BytecodeLanguage bcl = m_bcl.top();

    Local field_value;
    if(m_ApplicationClass){
      field_value = bcl.refInstanceField(m_CurrObj.top(), soot_field.getName());
    } else {
      SootClass obj = Scene.v().getSootClass("java.lang.Object");
      SootClass string = Scene.v().getSootClass("java.lang.String");
      String private_field_fun_name = "read"+getTypeString(soot_field);
      Local private_fields = bcl.newInstance("org.trifort.rootbeer.runtime.PrivateFields");
      bcl.pushMethod(private_fields, private_field_fun_name, soot_field.getType(), obj.getType(), string.getType(), string.getType());       
      field_value = bcl.invokeMethodRet(private_fields, m_CurrObj.top(), StringConstant.v(soot_field.getName()), StringConstant.v(soot_field.getDeclaringClass().getName()));
    }
    
    bcl.pushMethod(m_CurrentMem.top(), function_name, VoidType.v(), soot_field.getType());
    bcl.invokeMethodNoRet(m_CurrentMem.top(), field_value);
  }  
  
  public void callBaseClassWriter(String class_name, boolean ref_fields) {
    SootClass mem = Scene.v().getSootClass("org.trifort.rootbeer.runtime.Memory");
    String specialization;
    if(ref_fields){
      specialization = "RefFields";
    } else {
      specialization = "NonRefFields";
    }
    specialization += JavaNameToOpenCL.convert(class_name)+OpenCLScene.v().getIdent();
    BytecodeLanguage bcl = m_bcl.top();
    bcl.pushMethod(class_name, "org_trifort_writeToHeap"+specialization, VoidType.v(), mem.getType(), m_gcObjVisitor.top().getType());
    bcl.invokeMethodNoRet(m_CurrObj.top(), m_CurrentMem.top(), m_gcObjVisitor.top()); 
  } 
}
