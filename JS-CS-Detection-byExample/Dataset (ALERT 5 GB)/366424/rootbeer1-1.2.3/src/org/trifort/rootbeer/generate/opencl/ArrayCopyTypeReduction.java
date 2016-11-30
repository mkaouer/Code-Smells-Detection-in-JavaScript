/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.StaticInvokeExpr;

public class ArrayCopyTypeReduction {

  public Set<OpenCLArrayType> run(Set<OpenCLArrayType> array_types, MethodHierarchies hierarchies) {
    Set<OpenCLArrayType> ret = new HashSet<OpenCLArrayType>();
    for(OpenCLMethod method : hierarchies.getMethods()){
      SootMethod soot_method = method.getSootMethod();
      if(soot_method.isConcrete()){
        Body body = soot_method.retrieveActiveBody();
        ret.addAll(findTypes(body, array_types));
      }
    }
    return ret;
  }

  private Set<OpenCLArrayType> findTypes(Body body, Set<OpenCLArrayType> array_types) {
    Set<OpenCLArrayType> ret = new HashSet<OpenCLArrayType>();
    PatchingChain<Unit> units = body.getUnits();
    Unit curr = units.getFirst();
    while(curr != null){
      List<ValueBox> boxes = curr.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        if(value instanceof StaticInvokeExpr == false){
          continue;
        }
        StaticInvokeExpr expr = (StaticInvokeExpr) value;
        String method_sig = expr.getMethodRef().getSignature();
        String arraycopy_sig = "<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>";
        if(method_sig.equals(arraycopy_sig) == false){
          continue;
        }
        //now we have a method call to arraycopy
        Value src = expr.getArg(0);
        Value dest = expr.getArg(2);
        Type src_type = src.getType();
        Type dest_type = dest.getType();
        ret.add(findType(src_type, array_types));
        ret.add(findType(dest_type, array_types));
      }
      curr = units.getSuccOf(curr);
    }
    return ret;
  }

  private OpenCLArrayType findType(Type type, Set<OpenCLArrayType> array_types) {
    for(OpenCLArrayType ocl_type : array_types){
      ArrayType array_type = ocl_type.getArrayType();
      if(type.equals(array_type)){
        return ocl_type;
      }
    }
    return null;
  }

}
