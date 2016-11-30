/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.List;
import soot.*;
import soot.jimple.InvokeExpr;

public class FindKernelForTestCase {

  private List<String> m_kernels;
  
  public String get(String test_case, List<String> kernels){
    m_kernels = kernels;
    SootClass test_class = Scene.v().getSootClass(test_case);
    List<SootMethod> methods = test_class.getMethods();
    for(SootMethod method : methods){
      String kernel = searchMethod(method);
      if(kernel != null){
        return kernel;
      }
    }
    throw new RuntimeException("cannot find kernel for test case: "+test_case);
  }

  private String searchMethod(SootMethod method) {
    Body body = method.retrieveActiveBody();
    List<ValueBox> boxes = body.getUseAndDefBoxes();
    for(ValueBox box : boxes){
      Value value = box.getValue();
      if(value instanceof InvokeExpr){
        InvokeExpr expr = (InvokeExpr) value;
        SootClass to_call = expr.getMethodRef().declaringClass();
        if(m_kernels.contains(to_call.getName())){
          return to_call.getName();
        }
      }
    }
    return null;
  }
}
