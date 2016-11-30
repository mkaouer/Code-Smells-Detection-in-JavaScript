/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.List;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.SpecialInvokeExpr;
import soot.rbclassload.ClassHierarchy;
import soot.rbclassload.MethodSignatureUtil;
import soot.rbclassload.RootbeerClassLoader;

public class IsPolymorphic {
  
  private SootMethod m_baseMethod;
  private MethodSignatureUtil m_util;
  
  public IsPolymorphic(){
    m_util = new MethodSignatureUtil();
  }
  
  public boolean test(SootMethod soot_method){
    return test(soot_method, false);
  }
  
  public boolean test(SootMethod soot_method, boolean special_invoke){
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.isInterface()){
      m_baseMethod = soot_method;
      return true;
    }
    
    String signature = soot_method.getSignature();
    ClassHierarchy class_hierarchy = RootbeerClassLoader.v().getClassHierarchy();
    List<String> virtual_methods = class_hierarchy.getVirtualMethods(signature);
    
    String base_sig = virtual_methods.get(0);
    m_util.parse(base_sig);
    m_baseMethod = m_util.getSootMethod();
    
    if(virtual_methods.size() == 1 || m_baseMethod.isConstructor() || special_invoke){
      return false;
    } else {
      return true;
    }
  }

  public SootMethod getBaseMethod() {
    return m_baseMethod;
  }
}
