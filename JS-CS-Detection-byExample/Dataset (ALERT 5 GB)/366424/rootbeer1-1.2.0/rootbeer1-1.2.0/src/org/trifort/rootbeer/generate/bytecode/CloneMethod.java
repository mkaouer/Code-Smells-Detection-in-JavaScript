/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.Iterator;
import soot.Local;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.util.Chain;

public class CloneMethod {

  public SootMethod clone(SootMethod method, String new_name){
    SootMethod ret = new SootMethod(new_name, method.getParameterTypes(), method.getReturnType());

    //clone body
    JimpleBody body = Jimple.v().newBody(ret);
    UnitAssembler assembler = new UnitAssembler();
    PatchingChain<Unit> unit_chain = method.getActiveBody().getUnits();
    Iterator<Unit> iter = unit_chain.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      assembler.add(next);
    }
    assembler.assemble(body);
    ret.setActiveBody(body);
    return ret;
  }
}
