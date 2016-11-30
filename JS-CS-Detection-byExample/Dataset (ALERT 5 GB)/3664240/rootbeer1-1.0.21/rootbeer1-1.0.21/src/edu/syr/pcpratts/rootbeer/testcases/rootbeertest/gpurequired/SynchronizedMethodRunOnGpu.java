/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class SynchronizedMethodRunOnGpu implements Kernel {

  private SynchronizedMethodObject m_Object;
  
  public SynchronizedMethodRunOnGpu(SynchronizedMethodObject obj){
    m_Object = obj;
  }

  @Override
  public void gpuMethod() {
    m_Object.increment(true);
  }
  
  boolean compare(SynchronizedMethodRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_Object.value != grhs.m_Object.value){
      System.out.println("value");
      System.out.println("lhs: "+m_Object.value);
      System.out.println("rhs: "+grhs.m_Object.value);
      return false;
    }
    return true;
  }
}