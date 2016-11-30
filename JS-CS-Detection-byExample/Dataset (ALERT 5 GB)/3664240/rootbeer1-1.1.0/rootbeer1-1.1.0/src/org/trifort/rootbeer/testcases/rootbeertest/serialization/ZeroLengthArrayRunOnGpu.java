/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ZeroLengthArrayRunOnGpu implements Kernel {

  private char[] m_ret;
  
  public void gpuMethod() {
    m_ret = new char[0];  
  }
  
  public boolean compare(ZeroLengthArrayRunOnGpu rhs){
    if(rhs.m_ret == null){
      System.out.println("rhs.m_ret == null");
      return false;
    }
    if(m_ret.length != rhs.m_ret.length){
      System.out.println("length");
      System.out.println("lhs: "+m_ret.length);
      System.out.println("rhs: "+rhs.m_ret.length);
      return false;
    }
    return true;
  }
}
