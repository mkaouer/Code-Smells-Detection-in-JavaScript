/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class SynchronizedStaticMethodRunOnGpu implements Kernel {

  private SynchronizedStaticMethodShared m_Shared;
  private int value1;
  
  public SynchronizedStaticMethodRunOnGpu(SynchronizedStaticMethodShared obj){
    m_Shared = obj;
  }

  @Override
  public void gpuMethod() {
    try {
      increment();
    } catch(Throwable ex){
      value1++;
    }    
  }
  
  private void increment(){
    m_Shared.increment();
    throw new RuntimeException();
  }
  
  boolean compare(SynchronizedStaticMethodRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_Shared.m_Value != grhs.m_Shared.m_Value){
      System.out.println("m_Value");
      System.out.println("lhs: "+m_Shared.m_Value);
      System.out.println("rhs: "+grhs.m_Shared.m_Value);
      return false;
    }
    if(value1 != grhs.value1){
      System.out.println("value1");
      return false; 
    }
    return true;
  }
}