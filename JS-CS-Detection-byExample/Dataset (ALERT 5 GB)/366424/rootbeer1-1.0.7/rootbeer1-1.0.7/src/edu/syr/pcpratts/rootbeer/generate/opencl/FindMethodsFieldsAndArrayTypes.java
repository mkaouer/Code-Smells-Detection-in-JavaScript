/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.HashSet;
import java.util.Set;
import soot.*;
import soot.jimple.CastExpr;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;

public class FindMethodsFieldsAndArrayTypes {

  private static Set<SootMethod> mSootMethodsVisited;

  public static void reset(){
    mSootMethodsVisited = new HashSet<SootMethod>();
  }

  public static void methods(Value value){
    SootMethod soot_method = null;
    if(value instanceof InvokeExpr){
      InvokeExpr invoke_expr = (InvokeExpr) value;
      soot_method = invoke_expr.getMethod();
    } else if(value instanceof NewMultiArrayExpr){
      NewMultiArrayExpr expr = (NewMultiArrayExpr) value;
      ArrayType array_type = expr.getBaseType();
      OpenCLScene.v().addType(array_type);
      OpenCLScene.v().addNewMultiArray(expr);
      return;
    } else if(value instanceof NewExpr){
      NewExpr new_expr = (NewExpr) value;
      RefType ref_type = new_expr.getBaseType();
      OpenCLScene.v().addType(ref_type);
      return;
    } else if(value instanceof NewArrayExpr){
      NewArrayExpr new_expr = (NewArrayExpr) value;
      Type base = new_expr.getType();
      OpenCLScene.v().addType(base);
      return;
    } else if(value instanceof CastExpr){
      CastExpr cast_expr = (CastExpr) value;
      Type type = cast_expr.getCastType();
      if(type instanceof RefType || type instanceof ArrayType)
        OpenCLScene.v().addType(type);
      return;
    } else if(value instanceof FieldRef){
      FieldRef ref = (FieldRef) value;
      Type type = ref.getField().getType();
      if(type instanceof RefType || type instanceof ArrayType)
        OpenCLScene.v().addType(type);
      return;
    } else {
      return;
    }

    SootClass soot_class = soot_method.getDeclaringClass();

    if(mSootMethodsVisited.contains(soot_method))
      return;
    mSootMethodsVisited.add(soot_method);
    
    OpenCLScene.v().addMethod(soot_method);
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);

    ocl_method.findAllUsedMethodsAndFields();
  }

  public static void fields(Value value){

    if(value instanceof FieldRef == false)
      return;
    FieldRef field_ref = (FieldRef) value;
    
    //make sure SootField is what is really in the Scene
    SootField field = field_ref.getField();
    SootClass soot_class = field.getDeclaringClass();
    soot_class = Scene.v().getSootClass(soot_class.getName());
    field = soot_class.getField(field.getSubSignature());
    
    OpenCLScene.v().addField(field);
  }

  public static void arrayTypes(Value value) {
    Type type = value.getType();
    if(type instanceof ArrayType == false)
      return;
    OpenCLScene.v().addArrayType(new OpenCLArrayType((ArrayType) type));
  }
}