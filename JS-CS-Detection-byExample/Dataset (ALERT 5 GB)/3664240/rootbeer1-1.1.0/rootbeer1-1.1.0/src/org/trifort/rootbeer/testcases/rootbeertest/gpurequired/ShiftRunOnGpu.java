/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;

public class ShiftRunOnGpu implements Kernel {

  private byte m_byteRightSigned;
  private byte m_byteRightUnsigned;
  private byte m_byteLeft;
  
  private short m_shortRightSigned;
  private short m_shortRightUnsigned;
  private short m_shortLeft;
  
  private int m_intRightSigned;
  private int m_intRightUnsigned;
  private int m_intLeft;
  
  private long m_longRightSigned;
  private long m_longRightUnsigned;
  private long m_longLeft;
  
  public ShiftRunOnGpu(){
    m_byteRightSigned = (byte) -127;
    m_byteRightUnsigned = (byte) -127;
    m_byteLeft = (byte) -127;
    
    m_shortRightSigned = -32765;
    m_shortRightUnsigned = -32765;
    m_shortLeft = -32765;
    
    m_intRightSigned = -2147483647;
    m_intRightUnsigned = -2147483647;
    m_intLeft = -2147483647;
    
    m_longRightSigned = -9223372036854775808L;
    m_longRightUnsigned = -9223372036854775808L;
    m_longLeft = -9223372036854775808L;
  }
  
  public void gpuMethod() {
    m_byteRightSigned >>= 4;
    m_byteRightUnsigned >>= 4;
    m_byteLeft <<= 4;
    
    m_shortRightSigned >>= 8;
    m_shortRightUnsigned >>= 8;
    m_shortLeft <<= 8;
    
    m_intRightSigned >>= 16;
    m_intRightUnsigned >>= 16;
    m_intLeft <<= 16;
    
    m_longRightSigned >>= 32;
    m_longRightUnsigned >>= 32;
    m_longLeft <<= 32;
  }

  public boolean compare(ShiftRunOnGpu rhs) {
    if(m_byteRightSigned != rhs.m_byteRightSigned){
      System.out.println("m_byteRightSigned");
      System.out.println("lhs: "+m_byteRightSigned);
      System.out.println("rhs: "+rhs.m_byteRightSigned);
      return false;
    }
    if(m_byteRightSigned != rhs.m_byteRightSigned){
      System.out.println("m_byteRightSigned");
      System.out.println("lhs: "+m_byteRightSigned);
      System.out.println("rhs: "+rhs.m_byteRightSigned);
      return false;
    }
    if(m_byteLeft != rhs.m_byteLeft){
      System.out.println("m_byteLeft");
      System.out.println("lhs: "+m_byteLeft);
      System.out.println("rhs: "+rhs.m_byteLeft);
      return false;
    }
    if(m_shortRightSigned != rhs.m_shortRightSigned){
      System.out.println("m_shortRightSigned");
      System.out.println("lhs: "+m_shortRightSigned);
      System.out.println("rhs: "+rhs.m_shortRightSigned);
      return false;
    }
    if(m_shortRightSigned != rhs.m_shortRightSigned){
      System.out.println("m_shortRightSigned");
      System.out.println("lhs: "+m_shortRightSigned);
      System.out.println("rhs: "+rhs.m_shortRightSigned);
      return false;
    }
    if(m_shortLeft != rhs.m_shortLeft){
      System.out.println("m_shortLeft");
      System.out.println("lhs: "+m_shortLeft);
      System.out.println("rhs: "+rhs.m_shortLeft);
      return false;
    }
    if(m_intRightSigned != rhs.m_intRightSigned){
      System.out.println("m_intRightSigned");
      System.out.println("lhs: "+m_intRightSigned);
      System.out.println("rhs: "+rhs.m_intRightSigned);
      return false;
    }
    if(m_intRightSigned != rhs.m_intRightSigned){
      System.out.println("m_intRightSigned");
      System.out.println("lhs: "+m_intRightSigned);
      System.out.println("rhs: "+rhs.m_intRightSigned);
      return false;
    }
    if(m_intLeft != rhs.m_intLeft){
      System.out.println("m_intLeft");
      System.out.println("lhs: "+m_intLeft);
      System.out.println("rhs: "+rhs.m_intLeft);
      return false;
    }
    if(m_longRightSigned != rhs.m_longRightSigned){
      System.out.println("m_longRightSigned");
      System.out.println("lhs: "+m_longRightSigned);
      System.out.println("rhs: "+rhs.m_longRightSigned);
      return false;
    }
    if(m_longRightSigned != rhs.m_longRightSigned){
      System.out.println("m_longRightSigned");
      System.out.println("lhs: "+m_longRightSigned);
      System.out.println("rhs: "+rhs.m_longRightSigned);
      return false;
    }
    if(m_longLeft != rhs.m_longLeft){
      System.out.println("m_longLeft");
      System.out.println("lhs: "+m_longLeft);
      System.out.println("rhs: "+rhs.m_longLeft);
      return false;
    }
    return true;
  }
  
}
