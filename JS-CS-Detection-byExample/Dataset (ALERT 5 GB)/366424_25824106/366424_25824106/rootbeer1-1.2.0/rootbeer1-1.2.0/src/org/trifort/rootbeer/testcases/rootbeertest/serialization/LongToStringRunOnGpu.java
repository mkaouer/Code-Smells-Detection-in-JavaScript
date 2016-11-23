/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class LongToStringRunOnGpu implements Kernel {

  private String m_toString;
  private long m_value;
  
  public LongToStringRunOnGpu(long value){
    m_toString = "str";
    m_value = value;
  }
  
  public void gpuMethod() {
    m_toString = "" + m_value * m_value;
  }

  public boolean compare(LongToStringRunOnGpu rhs) {
    if(rhs.m_toString == null){
      System.out.println("rhs.m_toString == null");
      return false;
    }
    String lhs_str = m_toString;
    if(rhs.m_toString.equals(lhs_str) == false){
      System.out.println("m_toString");
      System.out.println("  lhs: "+m_toString);
      System.out.println("  rhs: "+rhs.m_toString);
      return false;
    }
    return true;
  }
}
