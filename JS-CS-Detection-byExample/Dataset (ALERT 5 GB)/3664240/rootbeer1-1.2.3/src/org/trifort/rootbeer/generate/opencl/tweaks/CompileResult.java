/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.tweaks;

import java.util.List;

public class CompileResult {
  private final boolean m_32bit;
  private final byte[] m_binary;
  private final List<String> m_errors;
  
  public CompileResult(boolean m32, List<byte[]> binary, List<String> errors){
    m_32bit = m32;
    m_binary = convert(binary);
    m_errors = errors;
  }

  private byte[] convert(List<byte[]> binary) {
    if(binary == null){
      return null;
    }
    int size = 0;
    for(byte[] array : binary){
      size += array.length;
    }
    byte[] ret = new byte[size];
    int offset = 0;
    for(byte[] array : binary){
      System.arraycopy(array, 0, ret, offset, array.length);
      offset += array.length;
    }
    return ret;
  }

  public boolean is32Bit() {
    return m_32bit;
  }
  
  public byte[] getBinary(){
    return m_binary;
  }
  
  public List<String> getErrors(){
    return m_errors;
  }
}
