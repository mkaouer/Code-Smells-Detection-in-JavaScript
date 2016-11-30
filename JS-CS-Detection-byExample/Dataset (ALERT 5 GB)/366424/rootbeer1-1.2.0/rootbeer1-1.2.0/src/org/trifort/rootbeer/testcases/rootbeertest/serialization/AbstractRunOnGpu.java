/* 
 * Copyright 2013 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class AbstractRunOnGpu implements Kernel {

  private int m_result;
  
  public void gpuMethod() {
    AbstractTestBaseClass base_class = new AbstractTestDerivedClass();
    m_result = base_class.add(10, 10);  
  }

  public boolean compare(AbstractRunOnGpu rhs) {
    if(m_result != rhs.m_result){
      System.out.println("m_result");
      return false;
    }
    return true;
  }
}
