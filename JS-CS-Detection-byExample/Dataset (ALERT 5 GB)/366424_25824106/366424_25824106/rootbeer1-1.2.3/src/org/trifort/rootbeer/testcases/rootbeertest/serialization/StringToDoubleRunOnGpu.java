/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringToDoubleRunOnGpu implements Kernel {

  private String m_strValue;
  private double m_toDouble;
  
  public StringToDoubleRunOnGpu(String value) {
    m_strValue = value;
  }
  
  public void gpuMethod() {
    m_toDouble = Double.parseDouble(m_strValue);
  }

  public boolean compare(StringToDoubleRunOnGpu rhs) {
    if(rhs.m_toDouble != m_toDouble) {
      System.out.println("m_toDouble");
      System.out.println("  lhs: "+m_toDouble);
      System.out.println("  rhs: "+rhs.m_toDouble);
      return false;
    }
    return true;
  }
}
