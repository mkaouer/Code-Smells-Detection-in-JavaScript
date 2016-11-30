/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class SharedMemSimpleRunOnGpu implements Kernel {

  private boolean m_boolean;
  private byte m_byte;
  private char m_char;
  private short m_short;
  private int m_integer;
  private long m_long;
  private float m_float;
  private double m_double;
  
  public void gpuMethod() {
    RootbeerGpu.setSharedBoolean(0, true);
    RootbeerGpu.setSharedByte(1, (byte) 2);
    RootbeerGpu.setSharedChar(2, (char) 3);
    RootbeerGpu.setSharedShort(4, (short) 4);
    RootbeerGpu.setSharedInteger(6, 5);
    RootbeerGpu.setSharedLong(10, 6);
    RootbeerGpu.setSharedFloat(18, 7.1f);
    RootbeerGpu.setSharedDouble(22, 8.2);
    
    RootbeerGpu.syncthreads();
    
    m_boolean = RootbeerGpu.getSharedBoolean(0);
    m_byte = RootbeerGpu.getSharedByte(1);
    m_char = RootbeerGpu.getSharedChar(2);
    m_short = RootbeerGpu.getSharedShort(4);
    m_integer = RootbeerGpu.getSharedInteger(6);
    m_long = RootbeerGpu.getSharedLong(10);
    m_float = RootbeerGpu.getSharedFloat(18);
    m_double = RootbeerGpu.getSharedDouble(22);
  }

  private String floatToString(float value){
    int bits = Float.floatToIntBits(value);
    String str = Integer.toBinaryString(bits);
    while(str.length() < 32){
      str = "0" + str;
    }
    return str;
  }
  
  private String doubleToString(double value){
    long bits = Double.doubleToLongBits(value);
    String str = Long.toBinaryString(bits);
    while(str.length() < 64){
      str = "0" + str;
    }
    return str;
  }
  
  private String byteToString(byte value){
    String str = Integer.toBinaryString(value);
    while(str.length() < 8){
      str = "0" + str;
    }
    return str;
  }
  
  private String longToString(long value){
    String str = Long.toBinaryString(value);
    while(str.length() < 64){
      str = "0" + str;
    }
    return str;
  }
  
  public boolean compare(SharedMemSimpleRunOnGpu rhs) {
    if(m_boolean != rhs.m_boolean){
      System.out.println("m_boolean");
      return false;
    }
    if(m_byte != rhs.m_byte){
      System.out.println("m_byte");
      return false;
    }
    if(m_char != rhs.m_char){
      System.out.println("m_char");
      return false;
    } 
    if(m_short != rhs.m_short){
      System.out.println("m_short");
      return false;
    }
    if(m_integer != rhs.m_integer){
      System.out.println("m_integer");
      return false;
    }
    if(m_long != rhs.m_long){
      System.out.println("m_long");
      System.out.println("org: "+longToString(6));
      System.out.println("lhs: "+longToString(m_long));
      System.out.println("rhs: "+longToString(rhs.m_long));
      System.out.println("  rhs byte 0: "+byteToString(RootbeerGpu.getSharedByte(10)));
      System.out.println("  rhs byte 7: "+byteToString(RootbeerGpu.getSharedByte(17)));
      return false;
    }
    if(m_float != rhs.m_float){
      System.out.println("m_float");
      System.out.println("org: "+floatToString(7.1f));
      System.out.println("lhs: "+floatToString(m_float));
      System.out.println("rhs: "+floatToString(rhs.m_float));
      return false;
    }
    if(m_double != rhs.m_double){
      System.out.println("m_double");
      System.out.println("org: "+doubleToString(8.2));
      System.out.println("lhs: "+doubleToString(m_double));
      System.out.println("  lhs byte 0: "+byteToString(RootbeerGpu.getSharedByte(22)));
      System.out.println("  lhs byte 1: "+byteToString(RootbeerGpu.getSharedByte(22+1)));
      System.out.println("  lhs byte 2: "+byteToString(RootbeerGpu.getSharedByte(22+2)));
      System.out.println("  lhs byte 3: "+byteToString(RootbeerGpu.getSharedByte(22+3)));
      System.out.println("  lhs byte 4: "+byteToString(RootbeerGpu.getSharedByte(22+4)));
      System.out.println("  lhs byte 5: "+byteToString(RootbeerGpu.getSharedByte(22+5)));
      System.out.println("  lhs byte 6: "+byteToString(RootbeerGpu.getSharedByte(22+6)));
      System.out.println("  lhs byte 7: "+byteToString(RootbeerGpu.getSharedByte(22+7)));
      System.out.println("rhs: "+doubleToString(rhs.m_double));
      return false;
    }
    return true;
  }
}
