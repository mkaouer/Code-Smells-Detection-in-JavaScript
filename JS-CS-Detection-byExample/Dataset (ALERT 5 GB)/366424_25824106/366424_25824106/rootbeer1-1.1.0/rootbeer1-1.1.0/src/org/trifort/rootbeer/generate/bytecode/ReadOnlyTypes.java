/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.trifort.rootbeer.compiler.FindMethodCalls;

import soot.Body;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;

public class ReadOnlyTypes {

  private SootClass m_RootClass;
  private Set<String> m_WrittenClasses;
  private Set<String> m_Inspected;
  
  public ReadOnlyTypes(SootMethod gpuMethod) {
    m_RootClass = gpuMethod.getDeclaringClass();
    m_WrittenClasses = new HashSet<String>();
    m_Inspected = new HashSet<String>();
    inspectMethod(gpuMethod);
  }

  public boolean isRootReadOnly(){
    return isReadOnly(m_RootClass);
  }
  
  public boolean isReadOnly(SootClass soot_class){
    String name = soot_class.getName();
    if(m_WrittenClasses.contains(name))
      return false;
    return true;
  }

  private void inspectMethod(SootMethod method) {
    String sig = method.getSignature();
    if(m_Inspected.contains(sig))
      return;
    m_Inspected.add(sig);
    
    if(method.isConcrete() == false){
      return;
    }
    if(method.hasActiveBody() == false){
      return;
    }
    Body body = method.getActiveBody();
    if(body == null)
      return;
    inspectBody(body);
    
    FindMethodCalls finder = new FindMethodCalls();
    Set<SootMethod> calls = finder.findForMethod(method);
    Iterator<SootMethod> iter = calls.iterator();
    while(iter.hasNext()){
      SootMethod curr = iter.next();
      inspectMethod(curr);
    }
  }

  private void inspectBody(Body body) {
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      if(curr instanceof AssignStmt == false)
        continue;
      
      AssignStmt assign = (AssignStmt) curr;
      Value lhs = assign.getLeftOp();
      
      if(lhs instanceof FieldRef == false)
        continue;
        
      FieldRef ref = (FieldRef) lhs;
      SootField field = ref.getField();
      String name = field.getDeclaringClass().getName();
      if(m_WrittenClasses.contains(name) == false)
        m_WrittenClasses.add(name);
    }
  }
}
