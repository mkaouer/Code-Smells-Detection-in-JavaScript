/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class LargeMemTestRunOnGpu implements Kernel {

  private int[] m_Mem;
  private long m_Sum;

  public LargeMemTestRunOnGpu(){
    m_Mem = new int[2*1024*1024];
  }

  @Override
  public void gpuMethod() {
    m_Sum = 0;
    for(int i = 0; i < m_Mem.length; ++i){
      m_Sum += m_Mem[i];
    }
  }

  boolean compare(LargeMemTestRunOnGpu brhs) {
    if(m_Sum != brhs.m_Sum){
      System.out.println("m_Sum");
      System.out.println("lhs: "+m_Sum+" rhs: "+brhs.m_Sum);
      return false;
    }
    return true;
  }
}
