/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.StaticInvokeExpr;

public abstract class UnitsSuitableForGpu {
  public abstract boolean isSuitable(Unit u);

  public boolean isStaticMathMethod(StaticInvokeExpr sinvoke_expr){
    SootMethod soot_method = sinvoke_expr.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.getName().equals("java.lang.Math") == false)
      return false;
    return true;
  }
}
