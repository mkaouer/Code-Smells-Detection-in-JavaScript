/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.rbclassload.ClassTester;
import soot.rbclassload.HierarchySootClass;

public class TestCaseFollowTester implements ClassTester {

  private Set<String> m_testCaseInterfaces;
  
  public TestCaseFollowTester(){
    m_testCaseInterfaces = new HashSet<String>();
    m_testCaseInterfaces.add("org.trifort.rootbeer.test.TestApplication");
    m_testCaseInterfaces.add("org.trifort.rootbeer.test.TestException");
    m_testCaseInterfaces.add("org.trifort.rootbeer.test.TestKernelTemplate");
    m_testCaseInterfaces.add("org.trifort.rootbeer.test.TestSerialization");
  }
  
  public boolean test(HierarchySootClass hsc) {
    List<String> interfaces = hsc.getInterfaces();
    for(String iface : interfaces){
      if(m_testCaseInterfaces.contains(iface)){
        return true;
      }
    }
    return false;
  }
  
}
