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
  private final List<byte[]> m_binary;
  private final List<String> m_errors;
  
  public CompileResult(boolean m32, List<byte[]> binary, List<String> errors){
    m_32bit = m32;
    m_binary = binary;
    m_errors = errors;
  }

  public boolean is32Bit() {
    return m_32bit;
  }
  
  public List<byte[]> getBinary(){
    return m_binary;
  }
  
  public List<String> getErrors(){
    return m_errors;
  }
}
