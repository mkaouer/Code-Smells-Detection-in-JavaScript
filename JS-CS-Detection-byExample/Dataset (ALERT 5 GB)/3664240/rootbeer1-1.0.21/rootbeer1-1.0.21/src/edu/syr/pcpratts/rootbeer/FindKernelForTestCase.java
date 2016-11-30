/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer;

import java.util.ArrayList;
import java.util.List;
import soot.*;
import soot.jimple.InvokeExpr;

public class FindKernelForTestCase {

  private List<SootClass> m_kernels;
  private List<String> m_testCasePackages;
  private String m_provider;
  
  public FindKernelForTestCase(){
    m_testCasePackages = new ArrayList<String>();
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.otherpackage.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.otherpackage2.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.arraysum.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.remaptest.");
    m_testCasePackages.add("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.");
  }
  
  public String getProvider(){
    return m_provider;
  }
  
  public SootClass get(String test_case, List<SootClass> kernels){
    m_kernels = kernels;
    
    if(test_case.contains(".") == false){
      String new_test_case = findTestCaseClass(test_case);
      if(new_test_case == null){
        System.out.println("cannot find test case class: "+test_case);
        System.exit(0);
      }
      test_case = new_test_case;
    }
    m_provider = test_case;
    
    SootClass test_class = Scene.v().getSootClass(test_case);
    List<SootMethod> methods = test_class.getMethods();
    for(SootMethod method : methods){
      SootClass kernel = searchMethod(method);
      if(kernel != null){
        return kernel;
      }
    }
    throw new RuntimeException("cannot find kernel for test case: "+test_case);
  }

  private SootClass searchMethod(SootMethod method) {
    Body body = method.retrieveActiveBody();
    List<ValueBox> boxes = body.getUseAndDefBoxes();
    for(ValueBox box : boxes){
      Value value = box.getValue();
      if(value instanceof InvokeExpr){
        InvokeExpr expr = (InvokeExpr) value;
        SootClass to_call = expr.getMethodRef().declaringClass();
        if(m_kernels.contains(to_call)){
          return to_call;
        }
      }
    }
    return null;
  }

  private String findTestCaseClass(String test_case) {
    for(String pkg : m_testCasePackages){
      String name = pkg + test_case;
      if(Scene.v().containsClass(name)){
        return name;
      }
    }
    return null;
  }
}
