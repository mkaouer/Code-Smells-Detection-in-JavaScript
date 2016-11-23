/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringToFloatRunOnGpu implements Kernel {

  private String m_strValue;
  private float m_toFloat;
  
  public StringToFloatRunOnGpu(String value) {
    m_strValue = value;
  }
  
  public void gpuMethod() {
    m_toFloat = Float.parseFloat(m_strValue);
  }

  public boolean compare(StringToFloatRunOnGpu rhs) {
    if(rhs.m_toFloat != m_toFloat) {
      System.out.println("m_toFloat");
      System.out.println("  lhs: "+m_toFloat);
      System.out.println("  rhs: "+rhs.m_toFloat);
      return false;
    }
    return true;
  }
}
