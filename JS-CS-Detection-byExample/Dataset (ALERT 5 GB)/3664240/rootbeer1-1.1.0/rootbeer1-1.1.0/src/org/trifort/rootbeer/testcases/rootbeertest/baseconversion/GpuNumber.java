/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.baseconversion;

public class GpuNumber {

  private int m_DoubleValue;
  
  public GpuNumber(int value){
    m_DoubleValue = value;
  }
  
  public void setDouble(int value){
    m_DoubleValue = value;
  }
  
  public int getDouble(){
    return m_DoubleValue;
  }
}
