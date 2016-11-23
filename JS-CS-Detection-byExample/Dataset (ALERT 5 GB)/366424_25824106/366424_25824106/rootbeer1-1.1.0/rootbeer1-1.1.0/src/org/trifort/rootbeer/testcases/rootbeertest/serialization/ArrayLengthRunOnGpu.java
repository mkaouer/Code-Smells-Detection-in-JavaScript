/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ArrayLengthRunOnGpu implements Kernel {

  private int[] m_array;
  private int m_len;
  
  public ArrayLengthRunOnGpu(int[] array){
    m_array = array;
  }
  
  public void gpuMethod() {
    m_len = m_array.length;
  }

  public boolean compare(ArrayLengthRunOnGpu rhs) {
    if(m_len != rhs.m_len){
      System.out.println("m_len");
      System.out.println("lhs: "+m_len);
      System.out.println("rhs: "+rhs.m_len);
      return false;
    }
    return true;
  }

}
