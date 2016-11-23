/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class CmplInfRunOnGpu implements Kernel {

  private double m_value;
  private int m_ret;
  
  public CmplInfRunOnGpu(double value) {
    m_value = value;
  }

  public void gpuMethod() {
    m_ret = 0;
    
    if(m_value == Double.NEGATIVE_INFINITY){
      m_ret += 1;
    }
    
    if(m_value > Double.NEGATIVE_INFINITY){
      m_ret += 4;
    } 
    
    if(m_value >= Double.NEGATIVE_INFINITY){
      m_ret += 8;
    } 
    
    if(m_value < Double.NEGATIVE_INFINITY){
      m_ret += 16;
    } 
    
    if(m_value <= Double.NEGATIVE_INFINITY){
      m_ret += 32;
    } 
    
    if(m_value == Double.POSITIVE_INFINITY){
      m_ret += 64;
    }
    
    if(m_value > Double.POSITIVE_INFINITY){
      m_ret += 128;
    } 
    
    if(m_value >= Double.POSITIVE_INFINITY){
      m_ret += 256;
    } 
    
    if(m_value < Double.POSITIVE_INFINITY){
      m_ret += 512;
    } 
    
    if(m_value <= Double.POSITIVE_INFINITY){
      m_ret += 1024;
    } 
    
  }

  public boolean compare(CmplInfRunOnGpu rhs) {
    if(m_ret != rhs.m_ret){
      System.out.println("m_ret");
      System.out.println("lhs: "+m_ret);
      System.out.println("rhs: "+rhs.m_ret);
      System.out.println("value: "+m_value);
      return false;
    }
    return true;
  }

}
