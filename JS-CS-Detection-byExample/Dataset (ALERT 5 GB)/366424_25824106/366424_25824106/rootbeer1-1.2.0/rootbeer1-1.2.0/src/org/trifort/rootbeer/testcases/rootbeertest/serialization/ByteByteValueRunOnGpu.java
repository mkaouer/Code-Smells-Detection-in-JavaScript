/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ByteByteValueRunOnGpu implements Kernel {

  private Byte m_byte;
  private byte m_ret;
  
  public ByteByteValueRunOnGpu(){
    m_byte = 20;
  }
  
  public void gpuMethod() {
    m_ret = m_byte;
  }

  public boolean compare(ByteByteValueRunOnGpu rhs) {
    if(m_ret != rhs.m_ret){
      System.out.println("m_ret");
      System.out.println("lhs: "+m_ret);
      System.out.println("rhs: "+rhs.m_ret);
      return false;
    }
    return true;
  }
}
