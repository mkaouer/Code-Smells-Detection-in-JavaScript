/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.awt.Robot;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class BarrierRunOnGpu implements Kernel {

  private int[] m_array;
  private int m_threadId;
  
  public BarrierRunOnGpu(int[] array, int thread_id){
    m_array = array;
    m_threadId = thread_id;
  }
  
  public void gpuMethod() {
    if(RootbeerGpu.isOnGpu()){
      int value = m_array[m_threadId];
      RootbeerGpu.syncthreads();
      int len = m_array.length;
      m_array[len - m_threadId - 1] = value;
    }
  }

  public boolean compare(BarrierRunOnGpu rhs) {
    int[] array = rhs.m_array;
    
    for(int i = 0; i < array.length; ++i ){
      if(array[i] != i){
        System.out.println("failure at: "+i+" array[i]: "+array[i]);
        return false;
      }
    }
    
    return true;
  }
}
