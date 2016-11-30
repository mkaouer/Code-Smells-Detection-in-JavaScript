/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;

public class BitwiseRunOnGpu implements Kernel {

  private int m_intAnd;
  private int m_intXor;
  private int m_intOr;
  private int m_intInv;
  
  public BitwiseRunOnGpu(){
    m_intAnd = 0xff;
    m_intXor = 0xff;
    m_intOr = 0xff;
    m_intInv = 0xff;
  }
  
  public void gpuMethod() {
    m_intAnd &= 0xaa;
    m_intXor ^= 0xaa;
    m_intOr |= 0xaa;
    m_intInv = ~m_intInv;
  }

  boolean compare(BitwiseRunOnGpu rhs) {
    if(m_intAnd != rhs.m_intAnd){
      System.out.println("m_intAnd");
      System.out.println("lhs: "+m_intAnd);
      System.out.println("rhs: "+rhs.m_intAnd);
      return false;
    }
    if(m_intXor != rhs.m_intXor){
      System.out.println("m_intXor");
      System.out.println("lhs: "+m_intXor);
      System.out.println("rhs: "+rhs.m_intXor);
      return false;
    }
    if(m_intOr != rhs.m_intOr){
      System.out.println("m_intOr");
      System.out.println("lhs: "+m_intOr);
      System.out.println("rhs: "+rhs.m_intOr);
      return false;
    }
    if(m_intInv != rhs.m_intInv){
      System.out.println("m_intInv");
      System.out.println("lhs: "+m_intInv);
      System.out.println("rhs: "+rhs.m_intInv);
      return false;
    }
    return true;
  }
}
