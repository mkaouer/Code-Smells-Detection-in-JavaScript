/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class MultiArrayRunOnGpu implements Kernel {

  private int[][] m_Ret;
  private int[][][] m_Ret2;
    
  public MultiArrayRunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    int size = 30;
    m_Ret = new int[size][size];
    m_Ret2 = new int[size][size][size];
    for(int i = 0; i < size; ++i){
      for(int j = 0; j < size; ++j){ 
        m_Ret[i][j] = i+j;
        for(int k = 0; k < size; ++k){
          m_Ret2[i][j][k] = i+j+k;
        }
      }
    }
  }
  
  boolean compare(MultiArrayRunOnGpu brhs) {
    if(m_Ret.length != brhs.m_Ret.length){
      System.out.println("outer length failed");
      return false;
    }
    for(int i = 0; i < m_Ret.length; ++i){
      if(m_Ret[i].length != brhs.m_Ret[i].length){
        System.out.println("inner length failed");
        return false;
      }
      for(int j = 0; j < m_Ret[i].length; ++j){
        int lhs = m_Ret[i][j];
        int rhs = brhs.m_Ret[i][j];
        if(lhs != rhs){
          System.out.println("value failed");
          return false;
        }
      }
    }
    if(m_Ret2.length != brhs.m_Ret2.length){
      System.out.println("outer length failed ret2");
      return false;
    }
    for(int i = 0; i < m_Ret2.length; ++i){
      if(m_Ret2[i].length != brhs.m_Ret2[i].length){        
        System.out.println("inner1 length failed ret2");
        return false;
      }
      for(int j = 0; j < m_Ret2[i].length; ++j){
        if(m_Ret2[i][j].length != brhs.m_Ret2[i][j].length){
          System.out.println("inner2 length failed ret2");
          return false;
        }
        for(int k = 0; k < m_Ret2[i][j].length; ++k){        
          int lhs = m_Ret2[i][j][k];
          int rhs = brhs.m_Ret2[i][j][k];
          if(lhs != rhs){
            System.out.println("value failed");
            return false;
          }
        }
      }
    }
    return true;
  }

}
