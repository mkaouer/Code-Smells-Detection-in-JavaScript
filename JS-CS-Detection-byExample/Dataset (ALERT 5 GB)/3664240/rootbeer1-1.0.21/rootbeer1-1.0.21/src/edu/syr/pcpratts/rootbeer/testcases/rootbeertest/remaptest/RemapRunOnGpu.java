/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.remaptest;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class RemapRunOnGpu implements Kernel {

  private int m_ret;
  
  public void gpuMethod() {
    CallsPrivateMethod caller = new CallsPrivateMethod();
    m_ret = caller.getNumber();
  }

  public int getRet(){
    return m_ret;
  }
}
