/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.util.Stack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;

public class VisitorReadGen extends AbstractVisitorGen {

  private Stack<Local> m_CurrObj;
  
  private Local m_Param0;
  private Local m_Param1;
  private Local m_RefParam;
  private Local m_Mem;
  private Local m_TextureMem;
  
  private Map<Type, Local> m_ReadFromHeapMethodsMade;
  private List<Type> m_OrderedHistory;
  private Set<String> m_VisitedReader;
  
  public VisitorReadGen(List<Type> ordered_history, String class_name, 
    BytecodeLanguage bcl, FieldReadWriteInspector inspector){
    super(inspector);
    
    m_ReadFromHeapMethodsMade = new HashMap<Type, Local>();
    m_OrderedHistory = ordered_history;
    m_VisitedReader = new HashSet<String>();
    m_bcl = new Stack<BytecodeLanguage>();
    m_bcl.push(bcl);
    m_gcObjVisitor = new Stack<Local>();
    m_fieldInspector = inspector;
    m_objSerializing = new Stack<Local>();
    m_currMem = new Stack<Local>();
    m_CurrObj = new Stack<Local>();
  }
  
  public void makeReadFromHeapMethod() {
    BytecodeLanguage bcl = m_bcl.top();
    SootClass obj_cls = Scene.v().getSootClass("java.lang.Object");
    bcl.startMethod("doReadFromHeap", obj_cls.getType(), obj_cls.getType(), BooleanType.v(), LongType.v());
    m_thisRef = bcl.refThis();
    m_gcObjVisitor.push(m_thisRef);
    m_Param0 = bcl.refParameter(0);
    m_objSerializing.push(m_Param0);
    m_Param1 = bcl.refParameter(1);
    m_RefParam = bcl.refParameter(2);
    m_Mem = bcl.refInstanceField(m_thisRef, "mMem");
    m_TextureMem = bcl.refInstanceField(m_thisRef, "mTextureMem");

    String dont_return_null_label = getNextLabel();
    bcl.ifStmt(m_RefParam, "!=", LongConstant.v(-1), dont_return_null_label);
    //mBcl.println("returning null");
    bcl.returnValue(NullConstant.v());
    bcl.label(dont_return_null_label);    
    
    String dont_fetch_obj = getNextLabel();
    
    bcl.ifStmt(m_Param0, "!=", NullConstant.v(), dont_fetch_obj);
    bcl.pushMethod(m_thisRef, "writeCacheFetch", obj_cls.getType(), LongType.v());
    Local written_to = bcl.invokeMethodRet(m_thisRef, m_RefParam);
    bcl.assign(m_Param0, written_to);
    bcl.label(dont_fetch_obj);
    
    Local mem = bcl.local(m_Mem.getType());
    
    bcl.assign(mem, m_Mem);      
    m_currMem.push(mem);
    
    BclMemory bcl_mem = new BclMemory(m_bcl.top(), mem);
    bcl_mem.setAddress(m_RefParam);
    
    String null_readers = getNextLabel();
    bcl.ifStmt(m_Param0, "==", NullConstant.v(), null_readers);    
    
    for(Type type : m_OrderedHistory){
      makeReadFromHeapMethodForType(type, false);
    }

    bcl.label(null_readers);
    makeReadForNull();
    
    RefType obj = RefType.v("java.lang.Object");
    makeReadFromHeapMethodForType(obj, true);
    
    bcl.returnValue(NullConstant.v());
    bcl.endMethod();
  }
   
  private void makeReadFromHeapMethodForType(Type type, boolean doing_object)
  {
    if(m_ReadFromHeapMethodsMade.containsKey(type))
      return;
    
    if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      if(ref_type.getClassName().equals("java.lang.Object")){
        if(!doing_object){
          return;
        }
      }
      if(differentPackageAndPrivate(ref_type)){
        return;  
      }
    }
    
    String label = getNextLabel();
    BytecodeLanguage bcl = m_bcl.top();
    bcl.ifInstanceOfStmt(m_Param0, type, label);
    
    //mBcl.println("reading: "+type.toString());
    //BclMemory bcl_mem = new BclMemory(mBcl, mMem);
    //Local ptr = bcl_mem.getPointer();
    //mBcl.println(ptr);
    
    Local ret;
    if(type instanceof ArrayType){
      ret = makeReadFromHeapBodyForArrayType((ArrayType) type);
    }
    else {
      ret = makeReadFromHeapBodyForSootClass((RefType) type);
    }
    m_ReadFromHeapMethodsMade.put(type, ret);
    bcl.returnValue(ret);
    bcl.label(label);
  }

  private Local makeReadFromHeapBodyForArrayType(ArrayType type) {
    BytecodeLanguage bcl = m_bcl.top();
    Local object_to_read_from = bcl.cast(type, m_Param0);

    BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());   
    
    bcl_mem.incrementAddress(3);
    Local ctor_used = bcl_mem.readByte();
    
    bcl_mem.incrementAddress(4);
    
    Local size = bcl_mem.readInt();
    //m_Bcl.top().println("reading size: ");
    //m_Bcl.top().println(size);
    Local ret = bcl.local(type);

    //pad for 8 bytes
    bcl_mem.incrementAddress(4);
    
    Local previous_size = bcl.lengthof(object_to_read_from);
    
    String label_new_float = getNextLabel();
    String label_after_new_float = getNextLabel();
    
    bcl.ifStmt(ctor_used, "==", IntConstant.v(1), label_new_float);
    bcl.assign(ret, object_to_read_from);
    bcl.gotoLabel(label_after_new_float);
    bcl.label(label_new_float);
    bcl.assign(ret, bcl.newArray(type, size));
     
    bcl.label(label_after_new_float);
    
    SootClass obj_class = Scene.v().getSootClass("java.lang.Object");
    bcl.pushMethod(m_thisRef, "checkCache",obj_class.getType(), LongType.v(), obj_class.getType());
    ret = bcl.invokeMethodRet(m_thisRef, m_RefParam, ret);
    ret = bcl.cast(type, ret);

    if(type.baseType == IntType.v() && type.numDimensions == 1){
      bcl_mem.readIntArray(ret, size);
    } else {
      Local i = bcl.local(IntType.v());
      bcl.assign(i, IntConstant.v(0));

      String end_for_label = getNextLabel();
      String before_if_label = getNextLabel();
      bcl.label(before_if_label);
      bcl.ifStmt(i, "==", size, end_for_label);

      Local new_curr;

      if(type.numDimensions != 1){
        new_curr = readFromHeapArray(object_to_read_from, i, previous_size);
      } else if(type.baseType instanceof RefType){
        Local temp = readFromHeapArray(object_to_read_from, i, previous_size);
        new_curr = bcl.cast(type.baseType, temp);
      } else {
        new_curr = bcl_mem.readVar(type.baseType);
      }

      bcl.assignElementToArray(ret, new_curr, i);
      
      bcl.plus(i, 1);
      bcl.gotoLabel(before_if_label);
      bcl.label(end_for_label);
    }
    bcl_mem.finishReading();

    return ret;
  }

  private Local makeReadFromHeapBodyForSootClass(RefType type){
    
    BytecodeLanguage bcl = m_bcl.top();
    SootClass soot_class = type.getSootClass();
    soot_class = Scene.v().getSootClass(soot_class.getName()); 
    
    Local object_to_write_to = bcl.cast(type, m_Param0); 
    
    String label = getNextLabel();
    BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());
    
    //get to ctor flag
    bcl_mem.incrementAddress(3);
    Local ctor_used = bcl_mem.readByte();
    
    bcl.ifStmt(ctor_used, "==", IntConstant.v(0), label);
    String name = soot_class.getName();
    if(FastWholeProgram.v().isApplicationClass(soot_class) == false){    
      if(soot_class.declaresMethod("void <init>()")){
        Local new_object = bcl.newInstance(name);
        bcl.assign(object_to_write_to, new_object); 
      }
    } else {
      Local sentinal = bcl.newInstance("edu.syr.pcpratts.rootbeer.runtime.Sentinal");
      Local new_object = bcl.newInstance(name, sentinal);
      bcl.assign(object_to_write_to, new_object);
    }
    bcl.label(label);
        
    SootClass obj_class = Scene.v().getSootClass("java.lang.Object");
    bcl.pushMethod(m_thisRef, "checkCache",obj_class.getType(), LongType.v(), obj_class.getType());
    object_to_write_to = bcl.invokeMethodRet(m_thisRef, m_RefParam, object_to_write_to);
    object_to_write_to = bcl.cast(type, object_to_write_to);
    
    bcl_mem.incrementAddress(12);       
    
    m_CurrObj.push(object_to_write_to);
    m_objSerializing.push(object_to_write_to);
    readFields(soot_class, true);
    readFields(soot_class, false);
    m_CurrObj.pop();
    m_objSerializing.pop();
   
    bcl_mem.finishReading();

    return object_to_write_to;
  }
  
  private void readFields(SootClass curr_class, boolean ref_types){
    if(FastWholeProgram.v().isApplicationClass(curr_class)){
      attachReader(curr_class.getName(), ref_types);
      callBaseClassReader(curr_class.getName(), ref_types);
    } else {
      insertReader(curr_class.getName(), ref_types);
    }    
  }
    
  private Local readFromHeapArray(Local object_to_read_from, Local i, Local size) {
    
    BytecodeLanguage bcl = m_bcl.top();
    BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());   
    
    Local curr;
    
    String after_read = getNextLabel();
    String before_read_int = getNextLabel();
    
    bcl.ifStmt(i, ">=", size, after_read);
    curr = bcl.indexArray(object_to_read_from, i);
    bcl.gotoLabel(before_read_int);
    bcl.label(after_read);
    if(object_to_read_from.getType() instanceof RefLikeType)
      bcl.assign(curr, NullConstant.v());
    else
      bcl.assign(curr, IntConstant.v(0));
    bcl.label(before_read_int);
    Local curr_phi = bcl.local(object_to_read_from.getType());
    bcl.assign(curr_phi, curr);
    Local object_addr = bcl_mem.readRef();
    bcl_mem.pushAddress();
    SootClass obj_cls = Scene.v().getSootClass("java.lang.Object");
    bcl.pushMethod(m_thisRef, "readFromHeap", obj_cls.getType(), obj_cls.getType(), BooleanType.v(), LongType.v());
    Local ret = bcl.invokeMethodRet(m_thisRef, curr_phi, m_Param1, object_addr);
    bcl_mem.popAddress();

    return ret;
  }

  private void makeReadForNull() {
    BytecodeLanguage bcl = m_bcl.top();
    BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());   
    
    Local start = bcl_mem.getPointer();
    bcl_mem.incrementAddress(2);
    Local type_id = bcl_mem.readByte();
    bcl_mem.setAddress(start);
    
    //m_Bcl.top().println("searching null creators for:");
    //m_Bcl.top().println(type_id);
        
    for(Type type : m_OrderedHistory){
      makeReadForNullForType(type, type_id);
    }
  }
  
  private void makeReadForNullForType(Type type_to_create, Local type_id){
    
    if(type_to_create instanceof ArrayType == false && type_to_create instanceof RefType == false)
      return;
    
    if(type_to_create instanceof RefType){
      RefType ref_type = (RefType) type_to_create;
      
      SootClass soot_class = getClassForType(ref_type);
      if(FastWholeProgram.v().isApplicationClass(soot_class) == false){    
        if(soot_class.declaresMethod("void <init>()") == false){
          return;
        }
      }
    }
    
    int id = RootbeerScene.v().getDfsInfo().getClassNumber(type_to_create);
    String label_after = getNextLabel();
    BytecodeLanguage bcl = m_bcl.top();
    bcl.ifStmt(type_id, "!=", IntConstant.v(id), label_after);
        
    //mBcl.println("reading null value");
    //mBcl.println(type_id);
    
    Local ret_obj = null;
    Local new_object = null;
    if(type_to_create instanceof RefType){
      RefType ref_type = (RefType) type_to_create;
      
      SootClass soot_class = getClassForType(ref_type);
      String name = ref_type.getClassName(); 
      if(FastWholeProgram.v().isApplicationClass(soot_class) == false){    
        if(soot_class.declaresMethod("void <init>()")){
          new_object = bcl.newInstance(name);
        }
      } else {
        Local sentinal = bcl.newInstance("edu.syr.pcpratts.rootbeer.runtime.Sentinal");
        new_object = bcl.newInstance(name, sentinal);    
      }
      
    } else if(type_to_create instanceof ArrayType){      
      
      BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());   
      Local start = bcl_mem.getPointer();
      bcl_mem.incrementAddress(8);
      Local size = bcl_mem.readInt();
      bcl_mem.setAddress(start);
    
      ArrayType array_type = (ArrayType) type_to_create;
      
      Value value = bcl.newArray(array_type, size);
      new_object = bcl.local(array_type);
      bcl.assign(new_object, value);
    } else {
      throw new UnsupportedOperationException();
    }
    
    if(new_object != null){
      SootClass obj_cls = Scene.v().getSootClass("java.lang.Object");
      bcl.pushMethod(m_thisRef, "readFromHeap", obj_cls.getType(), obj_cls.getType(), BooleanType.v(), LongType.v());
      ret_obj = bcl.invokeMethodRet(m_thisRef, new_object, IntConstant.v(1), m_RefParam);
      bcl.returnValue(ret_obj);
    } else {
      bcl.returnValue(NullConstant.v());
    }
    
    bcl.label(label_after);
  }
  
  public void attachReader(String class_name, boolean ref_fields){
    
    String specialization;
    
    if(ref_fields){
      specialization = "RefFields";
    } else {
      specialization = "NonRefFields";
    }
    specialization += JavaNameToOpenCL.convert(class_name);
    specialization += OpenCLScene.v().getIdent();
    String visited_name = class_name + specialization;
    
    if(m_VisitedReader.contains(visited_name))
      return;
    m_VisitedReader.add(visited_name);  
        
    SootClass curr_class = Scene.v().getSootClass(class_name);
    SootClass parent = curr_class.getSuperclass();
    parent = Scene.v().getSootClass(parent.getName());
    if(FastWholeProgram.v().isApplicationClass(parent)){
      attachReader(parent.getName(), ref_fields);
    }
    
    BytecodeLanguage bcl = new BytecodeLanguage();
    Local gc_obj_visit = m_gcObjVisitor.top();
    m_bcl.push(bcl);
    bcl.openClass(class_name);
    SootClass mem = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.startMethod("edu_syr_pcpratts_readFromHeap"+specialization, VoidType.v(), mem.getType(), gc_obj_visit.getType());
    m_objSerializing.push(bcl.refThis());
    m_currMem.push(bcl.refParameter(0));
    m_gcObjVisitor.push(bcl.refParameter(1));
    
    doReader(class_name, ref_fields);
    
    if(parent.getName().equals("java.lang.Object") == false){
      if(FastWholeProgram.v().isApplicationClass(parent)){
        callBaseClassReader(parent.getName(), ref_fields);
      } else {
        insertReader(parent.getName(), ref_fields);
      }
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_objSerializing.pop();
    m_currMem.pop();
    m_gcObjVisitor.pop();
    m_bcl.pop();
  } 
  
  public void insertReader(String class_name, boolean ref_fields){
       
    doReader(class_name, ref_fields);
    
    SootClass curr_class = Scene.v().getSootClass(class_name);
    if(curr_class.hasSuperclass() == false)
      return;
    
    SootClass parent = curr_class.getSuperclass();
    parent = Scene.v().getSootClass(parent.getName());
    if(parent.getName().equals("java.lang.Object") == false){
      if(FastWholeProgram.v().isApplicationClass(parent)){
        attachReader(parent.getName(), ref_fields);
        callBaseClassReader(parent.getName(), ref_fields);
      } else {
        insertReader(parent.getName(), ref_fields);
      }
    }
  }
      
  public void doReader(String class_name, boolean do_ref_fields){
    BytecodeLanguage bcl = m_bcl.top();
    BclMemory bcl_mem = new BclMemory(bcl, m_currMem.top());
    SootClass soot_class = Scene.v().getSootClass(class_name);
    
    //read all the ref fields
    int inc_size = 0;
    if(do_ref_fields){
      List<OpenCLField> ref_fields = getRefFields(soot_class);
      for(OpenCLField ref_field : ref_fields){
        if(m_fieldInspector.fieldIsWrittenOnGpu(ref_field)){
          //increment the address to get to this location
          bcl_mem.incrementAddress(inc_size);
          inc_size = 0;

          //read the field
          readRefField(ref_field);
        } else {
          inc_size += ref_field.getSize();
        }
      }

      if(inc_size > 0){
        bcl_mem.incrementAddress(inc_size);
      }
    } else {
      List<OpenCLField> non_ref_fields = getNonRefFields(soot_class);
      for(OpenCLField non_ref_field : non_ref_fields){
        if(m_fieldInspector.fieldIsWrittenOnGpu(non_ref_field)){
          //increment the address to get to this location
          if(inc_size > 0){
            bcl_mem.incrementAddress(inc_size);
            inc_size = 0;
          }
          //read the field
          readNonRefField(non_ref_field);
        } else {
          inc_size += non_ref_field.getSize();
        }
      }
      if(inc_size > 0)
        bcl_mem.incrementAddress(inc_size);
    }
    bcl_mem.align();
  }  
  
  public void callBaseClassReader(String class_name, boolean ref_types) {
    String specialization;
    if(ref_types){
      specialization = "RefFields";
    } else {
      specialization = "NonRefFields";
    }
    specialization += JavaNameToOpenCL.convert(class_name);
    specialization += OpenCLScene.v().getIdent();
    SootClass mem = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    BytecodeLanguage bcl = m_bcl.top();
    Local gc_obj_visit = m_gcObjVisitor.top();
    bcl.pushMethod(class_name, "edu_syr_pcpratts_readFromHeap"+specialization, VoidType.v(), mem.getType(), gc_obj_visit.getType());
    bcl.invokeMethodNoRet(m_objSerializing.top(), m_currMem.top(), gc_obj_visit); 
  }

}
