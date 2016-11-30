/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.compiler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

public class FindMethodCalls {
    
  public FindMethodCalls(){ 
  }
  
  public Set<SootMethod> findForBody(Body body) {
    Set<SootMethod> methods = new LinkedHashSet<SootMethod>();    
    PatchingChain<Unit> chain = body.getUnits();
    Iterator<Unit> iter = chain.iterator();
    while(iter.hasNext()){
      Unit unit = iter.next();
      List<ValueBox> vboxes = unit.getUseAndDefBoxes();
      for(ValueBox vbox : vboxes){
        Value value = vbox.getValue();
        if(value instanceof InvokeExpr == false)
          continue;
        InvokeExpr expr = (InvokeExpr) value;
        SootMethod method = expr.getMethod();
        if(methods.contains(method) == false)
          methods.add(method);
      }
    }
    return methods;
  }
  
  public Set<SootMethod> findForMethod(SootMethod method){
    if(method.isConcrete() == false){
      return new HashSet<SootMethod>();
    }
    Body body = method.getActiveBody();
    return findForBody(body);
  }
}
