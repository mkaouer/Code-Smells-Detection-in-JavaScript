/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class SimpleTestRunOnGpu implements Kernel {

  private int m_Value;

  SimpleTestRunOnGpu(int index) {
    m_Value = index;
  }
  
  @Override
  public void gpuMethod() {
    m_Value = 5;
  }
  
  public int getValue(){
    return m_Value;
  }
}
