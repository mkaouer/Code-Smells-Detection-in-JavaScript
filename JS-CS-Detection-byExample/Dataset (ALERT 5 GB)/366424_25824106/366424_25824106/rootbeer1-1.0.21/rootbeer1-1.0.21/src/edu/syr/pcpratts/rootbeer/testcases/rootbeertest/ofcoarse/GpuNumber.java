/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse;

public class GpuNumber {

  private double m_DoubleValue;
  
  public GpuNumber(double value){
    m_DoubleValue = value;
  }
  
  public void setDouble(double value){
    m_DoubleValue = value;
  }
  
  public double getDouble(){
    return m_DoubleValue;
  }
}
