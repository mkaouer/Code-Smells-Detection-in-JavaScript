/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.tweaks;

import java.util.List;

public class CompileResult {
  private List<byte[]> m_binary;
  private List<String> m_errors;
  
  public CompileResult(List<byte[]> binary, List<String> errors){
    m_binary = binary;
    m_errors = errors;
  }
  
  public List<byte[]> getBinary(){
    return m_binary;
  }
  
  public List<String> getErrors(){
    return m_errors;
  }
}
