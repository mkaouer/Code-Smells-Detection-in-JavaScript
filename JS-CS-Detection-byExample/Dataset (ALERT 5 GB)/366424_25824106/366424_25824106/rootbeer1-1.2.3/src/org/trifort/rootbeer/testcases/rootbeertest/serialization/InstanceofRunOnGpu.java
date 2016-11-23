/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class InstanceofRunOnGpu implements Kernel {
  
  int m_ret;
  private InstanceofOther2 m_other;
  
  public InstanceofRunOnGpu(){
    m_other = new InstanceofOther(); 
  }
  
  public void gpuMethod(){
    if(this instanceof Object){
      if(m_other instanceof InstanceofOther){
        if(m_other instanceof InstanceofOther2){
          int[][] array = new int[1][1];
          if(array instanceof Object){
            m_ret = 1; 
          } else {
            m_ret = 2; 
          }
        } else {
          m_ret = 2;
        }
      } else {
        m_ret = 2; 
      }
    } else {
      m_ret = 2;
    }
    
    
  }

  boolean compare(InstanceofRunOnGpu rhs) {
    if(m_ret != rhs.m_ret){
      System.out.println("m_ret error.");
      System.out.println("lhs: "+m_ret);
      System.out.println("rhs: "+rhs.m_ret);
      return false;
    }
    return true;
  }
}

