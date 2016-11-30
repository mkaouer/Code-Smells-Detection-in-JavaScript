/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.math;

import java.math.BigDecimal;

import org.trifort.rootbeer.runtime.Kernel;

public class BigDecimalRunOnGpu implements Kernel {

  private BigDecimal m_bigDecimal;
  private int m_result;
  public BigDecimalRunOnGpu(int num){
    m_bigDecimal = new BigDecimal(num);
  }
  
  public void gpuMethod() {
    m_result = m_bigDecimal.intValue();
  }

  public boolean compare(BigDecimalRunOnGpu rhs) {
    if(m_result != rhs.m_result){
      System.out.println("m_result");
      System.out.println("  lhs: "+m_result);
      System.out.println("  rhs: "+rhs.m_result);
      return false;
    }
    return true;
  }
  
  
}
