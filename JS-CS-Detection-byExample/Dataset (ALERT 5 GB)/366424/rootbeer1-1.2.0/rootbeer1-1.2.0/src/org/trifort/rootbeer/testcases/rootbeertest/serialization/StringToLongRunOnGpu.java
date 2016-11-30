/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringToLongRunOnGpu implements Kernel {

  private String m_strValue;
  private long m_toLong;
  
  public StringToLongRunOnGpu(String value) {
    m_strValue = value;
  }
  
  public void gpuMethod() {
    m_toLong = Long.parseLong(m_strValue);
  }

  public boolean compare(StringToLongRunOnGpu rhs) {
    if(rhs.m_toLong != m_toLong) {
      System.out.println("m_toLong");
      System.out.println("  lhs: "+m_toLong);
      System.out.println("  rhs: "+rhs.m_toLong);
      return false;
    }
    return true;
  }
}
