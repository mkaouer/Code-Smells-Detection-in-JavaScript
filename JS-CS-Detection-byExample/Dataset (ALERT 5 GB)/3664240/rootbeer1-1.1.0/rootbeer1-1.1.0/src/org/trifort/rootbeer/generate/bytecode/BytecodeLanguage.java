/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import soot.*;
import soot.jimple.*;
import soot.rbclassload.ClassHierarchy;
import soot.rbclassload.HierarchyGraph;
import soot.rbclassload.MethodSignatureUtil;
import soot.rbclassload.RootbeerClassLoader;
import soot.rbclassload.TypeToString;

public class BytecodeLanguage {

  private Jimple jimple;
  private SootClass mCurrClass;

  //method fields
  private SootMethod mCurrMethod;
  private JimpleBody mCurrBody;
  private List<Type> mParameterTypes;
  private UnitAssembler mAssembler;

  private Stack<SootMethod> mMethodStack;

  public BytecodeLanguage(){
    jimple = Jimple.v();
    mMethodStack = new Stack<SootMethod>();
  }

  public SootClass makeClass(String name){
    SootClass ret = new SootClass(name, Modifier.PUBLIC);

    SootClass object_soot_class = Scene.v().getSootClass("java.lang.Object");
    ret.setSuperclass(object_soot_class);
    Scene.v().addClass(ret);
    ret.setApplicationClass();

    mCurrClass = ret;
    return ret;
  }

  public SootClass makeClass(String name, String parent){
    SootClass ret = new SootClass(name, Modifier.PUBLIC);

    //set superclass
    SootClass parent_class = Scene.v().getSootClass(parent);
    ret.setSuperclass(parent_class);

    Scene.v().addClass(ret);
    ret.setApplicationClass();

    mCurrClass = ret;
    return ret;
  }

  public void addFieldToClass(Local local){
    SootField field = new SootField(local.getName(), local.getType(), Modifier.PUBLIC);
    mCurrClass.addField(field);
  }
  
  public void addFieldToClass(Local local, String name){
    SootField field = new SootField(name, local.getType(), Modifier.PUBLIC);
    mCurrClass.addField(field);
  }


  public void openClass(String name){
    mCurrClass = Scene.v().getSootClass(name);
  }

  public void openClass(SootClass soot_class){
    mCurrClass = soot_class;
  }

  public void startMethod(String method_name, Type return_type, Type... arg_types){
    doStartMethod(method_name, return_type, Modifier.PUBLIC, arg_types);
  }
  
  private void doStartMethod(String method_name, Type return_type, int modifiers, Type... arg_types){
    mAssembler = new UnitAssembler();

    mParameterTypes = convertTypeArrayToList(arg_types);
    mCurrMethod = new SootMethod(method_name, mParameterTypes, return_type, modifiers);
    mCurrMethod.setDeclaringClass(mCurrClass);

    mCurrBody = jimple.newBody(mCurrMethod);
    mCurrMethod.setActiveBody(mCurrBody);
    mCurrClass.addMethod(mCurrMethod);
    
    RootbeerClassLoader.v().addGeneratedMethod(mCurrMethod.getSignature());
  }
  
  public void startStaticMethod(String method_name, Type return_type, Type... arg_types){
    doStartMethod(method_name, return_type, Modifier.PUBLIC | Modifier.STATIC, arg_types);
  }

  public void continueMethod(UnitAssembler assembler){
    mAssembler = assembler;
  }

  public Local refThis(){
    String name = "this0";
    RefType type = mCurrClass.getType();
    Local thislocal = jimple.newLocal(name, type);
    Unit u = jimple.newIdentityStmt(thislocal, jimple.newThisRef(type));
    mAssembler.add(u);
    return thislocal;
  }

  public Local refParameter(int index){
    Type type = mParameterTypes.get(index);
    String name = "parameter"+Integer.toString(index);
    Local parameterI = jimple.newLocal(name, type);
    Unit u = jimple.newIdentityStmt(parameterI, jimple.newParameterRef(type, index));
    mAssembler.add(u);
    return parameterI;
  }

  public Local binOp(Value lhs, String op, Value rhs){
    Value binop = null;
    if(op.equals("*")){
      binop = jimple.newMulExpr(lhs, rhs);
    }

    Local ret = jimple.newLocal(getLocalName(), lhs.getType());
    Unit u = jimple.newAssignStmt(ret, binop);
    mAssembler.add(u);
    return ret;
  }

  public void setInstanceField(SootField field, Local field_instance, Value value){
    Value lhs;
    if(field.isStatic() == false)
      lhs = jimple.newInstanceFieldRef(field_instance, field.makeRef());
    else
      lhs = jimple.newStaticFieldRef(field.makeRef());
    Unit u = jimple.newAssignStmt(lhs, value);
    mAssembler.add(u);
  }

  public void setInstanceField(String field_name, Local field_instance, Value value){
    Type type = field_instance.getType();
    if(type instanceof RefType == false)
      throw new RuntimeException("How do we handle this case?");
    RefType ref_type = (RefType) type;
    SootClass soot_class = ref_type.getSootClass();
    SootField soot_field = soot_class.getFieldByName(field_name);
    setInstanceField(soot_field, field_instance, value);
  }

  public void setStaticField(SootField field, Value value) {
    Value lhs;
    lhs = jimple.newStaticFieldRef(field.makeRef());
    Unit u = jimple.newAssignStmt(lhs, value);
    mAssembler.add(u);
  }

  public void endMethod(){
    //System.out.println("Ending method: "+mCurrMethod.getName());
    mAssembler.assemble(mCurrBody);
    //System.out.println(mAssembler.toString());
  }

  private List<Type> convertTypeArrayToList(Type[] type_array){
    List<Type> ret = new ArrayList<Type>();
    for(int i = 0; i < type_array.length; ++i){
      ret.add(type_array[i]);
    }
    return ret;
  }

  public void pushMethod(Local class_instance, String method_name, Type return_type, Type... arg_types){
    String class_name = getTypeString(class_instance);
    pushMethod(class_name, method_name, return_type, arg_types);
  }
  
  public void pushMethod(SootClass soot_class, String method_name, Type return_type, Type... arg_types){
    String class_name = soot_class.getName();
    pushMethod(class_name, method_name, return_type, arg_types);    
  }

  public void pushMethod(String class_name, String method_name, Type return_type, Type... arg_types){
    SootClass soot_class = Scene.v().getSootClass(class_name);
    SootClass org_class = soot_class;
    
    TypeToString converter = new TypeToString();
    MethodSignatureUtil util = new MethodSignatureUtil();
    util.setClassName(class_name);
    util.setMethodName(method_name);
    util.setReturnType(converter.convert(return_type));
    List<String> parameter_types = new ArrayList<String>();
    for(Type arg_type : arg_types){
      parameter_types.add(converter.convert(arg_type));
    }
    util.setParameterTypes(parameter_types);
    SootMethod soot_method = util.getSootMethod();
    mMethodStack.push(soot_method);
  }

  private List<Value> convertValueArrayToList(Value[] local_array){
    List<Value> ret = new ArrayList<Value>();
    new ArrayList<Type>();
    for(int i = 0; i < local_array.length; ++i){
      ret.add(local_array[i]);
    }
    return ret;
  }

  public void invokeMethodNoRet(Local base, Value... args){
    SootMethod method = mMethodStack.pop();
    List<Value> args_list = convertValueArrayToList(args);
    Value invoke_expr;
    if(method.getName().equals("<init>")){
      invoke_expr = jimple.newSpecialInvokeExpr(base, method.makeRef(), args_list);
    } else {
      //I can't find any way to distinguish between an interface and non-interface
      //method.  let's just try both and use whatever works.
      try {
        invoke_expr = jimple.newVirtualInvokeExpr(base, method.makeRef(), args_list);
      } catch(RuntimeException ex){
        invoke_expr = jimple.newInterfaceInvokeExpr(base, method.makeRef(), args_list);
      }
    }

    Unit u = jimple.newInvokeStmt(invoke_expr);
    mAssembler.add(u);
  }
  
  public void invokeStaticMethodNoRet(Value... args){
    SootMethod method = mMethodStack.pop();
    List<Value> args_list = convertValueArrayToList(args);
    Value invoke_expr;
    
    invoke_expr = jimple.newStaticInvokeExpr(method.makeRef(), args_list);
    
    Unit u = jimple.newInvokeStmt(invoke_expr);
    mAssembler.add(u);
  }
  
  public void invokeSpecialNoRet(Local base, Value... args){
    SootMethod method = mMethodStack.pop();
    List<Value> args_list = convertValueArrayToList(args);
    Value invoke_expr;
    invoke_expr = jimple.newSpecialInvokeExpr(base, method.makeRef(), args_list);

    Unit u = jimple.newInvokeStmt(invoke_expr);
    mAssembler.add(u);
  }

  public Local invokeMethodRet(Local base, Value... args){
    SootMethod method = mMethodStack.pop();
    List<Value> args_list = convertValueArrayToList(args);
    Value invoke_expr;
    if(method.getName().equals("<init>")){
      invoke_expr = jimple.newSpecialInvokeExpr(base, method.makeRef(), args_list);
    } else {
      //I can't find any way to distinguish between an interface and non-interface
      //method.  let's just try both and use whatever works.
      try {
        invoke_expr = jimple.newVirtualInvokeExpr(base, method.makeRef(), args_list);
      } catch(RuntimeException ex){
        invoke_expr = jimple.newInterfaceInvokeExpr(base, method.makeRef(), args_list);
      }
    }

    String name = getLocalName();
    Local ret = jimple.newLocal(name, method.getReturnType());
    Unit u = jimple.newAssignStmt(ret, invoke_expr);
    mAssembler.add(u);
    return ret;
  }

  private String getLocalName(){
    return RegisterNamer.v().getName();
  }

  public Local local(Type type){
    Local ret = jimple.newLocal(getLocalName(), type);
    return ret;
  }

  public void ifStmt(Value lhs, String op, Value rhs, String target_label){
    Value condition;
    if(op.equals("==")){
      condition = jimple.newEqExpr(lhs, rhs);
    } else if(op.equals("!=")){
      condition = jimple.newNeExpr(lhs, rhs);
    } else if(op.equals(">=")){
      condition = jimple.newGeExpr(lhs, rhs);    
    } else {
      throw new UnsupportedOperationException();
    }
    mAssembler.addIf(condition, target_label);
  }

  public void ifInstanceOfStmt(Value lhs, Type rhs, String target){
    //Local lhs_instanceof_rhs_local = lhs instanceof rhs;
    Value lhs_instanceof_rhs;
    lhs_instanceof_rhs = jimple.newInstanceOfExpr(lhs, rhs);
    Local lhs_instance_of_rhs_local = jimple.newLocal(getLocalName(), BooleanType.v());
    Unit u1 = jimple.newAssignStmt(lhs_instance_of_rhs_local, lhs_instanceof_rhs);
    mAssembler.add(u1);

    Value eq = jimple.newEqExpr(lhs_instance_of_rhs_local, IntConstant.v(0));
    mAssembler.addIf(eq, target);
  }

  public void label(String label_string){
    mAssembler.addLabel(label_string);
  }

  public void returnVoid() {
    mAssembler.add(jimple.newReturnVoidStmt());
  }

  public void returnValue(Value value){
    Unit u = jimple.newReturnStmt(value);
    mAssembler.add(u);
  }

  public Local refInstanceField(Local base, String field_name){
    Type base_type = base.getType();
    SootClass base_class = Scene.v().getSootClass(base_type.toString());
    SootField field = getFieldByName(base_class, field_name);
    Local ret = jimple.newLocal(getLocalName(), field.getType());

    Value rhs = jimple.newInstanceFieldRef(base, field.makeRef());
    Unit u = jimple.newAssignStmt(ret, rhs);
    mAssembler.add(u);
    return ret;
  }

  Local refStaticField(Local base, String field_name) {
    Type base_type = base.getType();
    return refStaticField(base_type, field_name);
  }

  Local refStaticField(Type base_type, String field_name) {
    SootClass base_class = Scene.v().getSootClass(base_type.toString());
    SootField field = getFieldByName(base_class, field_name);
    Local ret = jimple.newLocal(getLocalName(), field.getType());

    Value rhs = jimple.newStaticFieldRef(field.makeRef());
    Unit u = jimple.newAssignStmt(ret, rhs);
    mAssembler.add(u);
    return ret;
  }

  public void refInstanceFieldToInput(Local base, String field_name, Local input){
    Type base_type = base.getType();
    SootClass base_class = Scene.v().getSootClass(base_type.toString());
    SootField field = getFieldByName(base_class, field_name);

    Value rhs = jimple.newInstanceFieldRef(base, field.makeRef());
    Unit u = jimple.newAssignStmt(input, rhs);
    mAssembler.add(u);
  }
  
  public void refInstanceFieldFromInput(Local base, String field_name, Local input){
    Type base_type = base.getType();
    SootClass base_class = Scene.v().getSootClass(base_type.toString());
    SootField field = getFieldByName(base_class, field_name);

    Value rhs = jimple.newInstanceFieldRef(base, field.makeRef());
    Unit u = jimple.newAssignStmt(rhs, input);
    mAssembler.add(u);
  }

  public String getTypeString(Local local){
    Type type = local.getType();
    return type.toString();
  }

  public Local cast(Type type, Local rhs){
    Local ret = jimple.newLocal(getLocalName(), type);
    Value rhs_value = jimple.newCastExpr(rhs, type);
    Unit u = jimple.newAssignStmt(ret, rhs_value);
    mAssembler.add(u);
    return ret;
  }

  public Local newInstance(String mClassName, Value... params) {
    SootClass soot_class = Scene.v().getSootClass(mClassName);
    Local u1_lhs = jimple.newLocal(getLocalName(), soot_class.getType());
    Value u1_rhs = jimple.newNewExpr(soot_class.getType());
    Unit u1 = jimple.newAssignStmt(u1_lhs, u1_rhs);
    mAssembler.add(u1);

    Type[] arg_types = new Type[params.length];
    for(int i = 0; i < params.length; ++i){
      arg_types[i] = params[i].getType();
    }
    pushMethod(u1_lhs, "<init>", VoidType.v(), arg_types);
    invokeMethodNoRet(u1_lhs, params);

    Local u2_lhs = jimple.newLocal(getLocalName(), soot_class.getType());
    Unit u2 = jimple.newAssignStmt(u2_lhs, u1_lhs);
    mAssembler.add(u2);
    return u2_lhs;
  }

  public Local newInstanceValueOf(String mClassName, Value param) {
    // Generate mClassName.valueOf(param)
    SootClass soot_class = Scene.v().getSootClass(mClassName);
    Local l_res = jimple.newLocal(getLocalName(), soot_class.getType());
    SootMethodRef classForNameRef = soot.Scene.v().makeMethodRef(soot_class,
        "valueOf", Arrays.asList(param.getType()), soot_class.getType(), true);
    Unit u1 = jimple.newAssignStmt(l_res, jimple.newStaticInvokeExpr(classForNameRef, Arrays.asList(new Value[]{param})));
    mAssembler.add(u1);
    return l_res;
  }

  public Value newArray(Type type, Value size) {
    ArrayType atype = (ArrayType) type;
    if(atype.numDimensions == 1)
      return jimple.newNewArrayExpr(atype.baseType, size);
    else {
      ArrayType to_create = ArrayType.v(atype.baseType, atype.numDimensions-1);
      return jimple.newNewArrayExpr(to_create, size);
    }
  }

  public void assign(Value lhs, Value rhs) {
    Unit u = jimple.newAssignStmt(lhs, rhs);
    mAssembler.add(u);
  }

  public Unit getLastUnitCreated(){
    return mAssembler.getLastUnitCreated();
  }

  public void gotoLabel(String label2) {
    mAssembler.addGoto(label2);
  }

  public void makeVoidCtor() {
    startMethod("<init>", VoidType.v());
    SootClass super_soot_class = mCurrClass.getSuperclass();
    Local thisref = refThis();
    pushMethod(super_soot_class.getName(), "<init>", VoidType.v());
    invokeMethodNoRet(thisref);
    returnVoid();
    endMethod();
  }

  public UnitAssembler getAssembler() {
    return mAssembler;
  }

  Local lengthof(Local object_to_write_from) {
    Value rhs = jimple.newLengthExpr(object_to_write_from);
    Local lhs = jimple.newLocal(getLocalName(), IntType.v());
    Unit u = jimple.newAssignStmt(lhs, rhs);
    mAssembler.add(u);
    return lhs;
  }

  Local indexArray(Local base, Value i) {
    Value rhs = jimple.newArrayRef(base, i);
    Type type = base.getType();
    if(type instanceof ArrayType == false)
      throw new RuntimeException("How do we handle this case?");
    ArrayType atype = (ArrayType) type;

    Local lhs;
    if(atype.numDimensions == 1){
      lhs = jimple.newLocal(getLocalName(), atype.baseType);
    } else {
      Type lhs_type = ArrayType.v(atype.baseType, atype.numDimensions-1);
      lhs = jimple.newLocal(getLocalName(), lhs_type);
    }
    Unit u = jimple.newAssignStmt(lhs, rhs);
    mAssembler.add(u);
    return lhs;
  }
  
  public void assignArray(Local base, Value i, Local value){
    Value lhs = jimple.newArrayRef(base, i);
    Unit u = jimple.newAssignStmt(lhs, value);
    mAssembler.add(u);
  }

  void plus(Local i, int add_value) {
    Value rhs = jimple.newAddExpr(i, IntConstant.v(add_value));
    Unit u = jimple.newAssignStmt(i, rhs);
    mAssembler.add(u);
  }

  void plus(Local i, Value add_value) {
    Value rhs = jimple.newAddExpr(i, add_value);
    Unit u = jimple.newAssignStmt(i, rhs);
    mAssembler.add(u);
  }

  void mult(Local lhs, Value mult_value){
    Value rhs = jimple.newMulExpr(lhs, mult_value);
    Unit u = jimple.newAssignStmt(lhs, rhs);
    mAssembler.add(u);
  }

  void noOp() {
    Unit u = jimple.newNopStmt();
    mAssembler.add(u);
  }
  
  void assignElementToArray(Local base, Value rhs, Value i) {
    Value lhs = jimple.newArrayRef(base, i);
    Unit u = jimple.newAssignStmt(lhs, rhs);
    mAssembler.add(u);
  }

  public void println(String message){
    Type system = RefType.v("java.lang.System");
    Local out = refStaticField(system, "out");
    Type string = RefType.v("java.lang.String");
    pushMethod(out, "println", VoidType.v(), string);
    invokeMethodNoRet(out, StringConstant.v(message));
  }

  void println(Local number) {
    Type system = RefType.v("java.lang.System");
    Local out = refStaticField(system, "out");
    pushMethod(out, "println", VoidType.v(), IntType.v());
    invokeMethodNoRet(out, number);
  }
  
  void printlnLong(Local number) {
    Type system = RefType.v("java.lang.System");
    Local out = refStaticField(system, "out");
    pushMethod(out, "println", VoidType.v(), LongType.v());
    invokeMethodNoRet(out, number);
  }

  SootClass getSootClass() {
    return mCurrClass;
  }

  Local classConstant(Type type) {
    String class_name = convertToConstant(type);
    Value curr = ClassConstant.v(class_name);
    Local ret = jimple.newLocal(getLocalName(), curr.getType());
    Unit u = jimple.newAssignStmt(ret, curr);
    mAssembler.add(u);
    return ret;
  }

  private String convertToConstant(Type type) {
    if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      String prefix = "";
      int dims = array_type.numDimensions;
      for(int i = 0; i < dims; ++i){
        prefix += "[";
      }
      String base_string = convertToConstant(array_type.baseType);
      if(array_type.baseType instanceof RefType){
        return prefix + "L" + base_string + ";";
      } else {
        return prefix + base_string;
      }
    } else if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      return ref_type.getSootClass().getName().replace(".", "/");
    } else if(type instanceof PrimType){
      if(type.equals(BooleanType.v())){
        return "Z";
      } else if(type.equals(ByteType.v())){
        return "B";
      } else if(type.equals(ShortType.v())){
        return "S";
      } else if(type.equals(CharType.v())){
        return "C";
      } else if(type.equals(IntType.v())){
        return "I";
      } else if(type.equals(LongType.v())){
        return "J";
      } else if(type.equals(FloatType.v())){
        return "F";
      } else if(type.equals(DoubleType.v())){
        return "D";
      }
    }
    
    throw new RuntimeException("please report bug in BytecodeLanguage.convertToConstant");
  }

  private SootField getFieldByName(SootClass base_class, String field_name) {
    List<String> queue = new LinkedList<String>();
    queue.add(base_class.getName());
    while(queue.isEmpty() == false){
      String curr_class = queue.get(0);
      queue.remove(0);
      SootClass soot_class = Scene.v().getSootClass(curr_class);
      if(soot_class.declaresFieldByName(field_name)){
        return soot_class.getFieldByName(field_name);
      }
      if(soot_class.hasSuperclass()){
        queue.add(soot_class.getSuperclass().getName());
      }
      if(soot_class.hasOuterClass()){
        queue.add(soot_class.getOuterClass().getName());
      }
    }
    throw new RuntimeException("cannot find field: "+field_name+" in "+base_class.getName());
  }
}
