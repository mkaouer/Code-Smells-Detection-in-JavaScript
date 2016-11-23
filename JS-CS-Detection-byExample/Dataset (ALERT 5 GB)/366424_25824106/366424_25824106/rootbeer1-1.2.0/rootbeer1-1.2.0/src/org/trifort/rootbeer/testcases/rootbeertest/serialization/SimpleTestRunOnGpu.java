/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class SimpleTestRunOnGpu implements Kernel {

  private int m_value;

  SimpleTestRunOnGpu(int index) {
    m_value = index;
  }
  
  @Override
  public void gpuMethod() {
    m_value = 5;
  }
  
  public int getValue(){
    return m_value;
  }
}
