/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringArrayTest1RunOnGpu implements Kernel {
  
  private String [] m_source; 
  private String [] m_ret; 
  private int m_index;
  
  public StringArrayTest1RunOnGpu (String [] src, String [] dst, int i){
    m_source = src; 
    m_ret = dst; 
    m_index = i;
  }
  
  public void gpuMethod(){
    String str = "york";
    for(int i = 0; i < m_source.length; ++i) {
      str = m_source[i]+ str;
    }
    m_ret[m_index] = str;
  }
  
  public String[] getResult()
  {
    return m_ret;
  }
}
