/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;

public class PrintRunOnGpu implements Kernel {

  private int m_num;
  
  public void gpuMethod(){
    m_num = 10;  
  }

  boolean compare(PrintRunOnGpu rhs) {
    if(m_num == rhs.m_num == false){
      System.out.println("m_num: "+m_num);
      System.out.println("rhs.m_num: "+rhs.m_num);
      return false;
    }
    return true;
  }
}
