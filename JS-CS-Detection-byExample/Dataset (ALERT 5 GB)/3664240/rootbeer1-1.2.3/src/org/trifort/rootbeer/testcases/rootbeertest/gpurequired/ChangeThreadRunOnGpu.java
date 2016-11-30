/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;

public class ChangeThreadRunOnGpu implements Kernel {

  private int m_result;
  
  public void gpuMethod() {
    m_result = 10;
  }
  
  public int getResult(){
    return m_result;
  }

  public boolean compare(ChangeThreadRunOnGpu rhs) {
    if(m_result != rhs.m_result){
      System.out.println("m_result");
      System.out.println("lhs: "+m_result);
      System.out.println("rhs: "+rhs.m_result);
    }
    return true;
  }
}
