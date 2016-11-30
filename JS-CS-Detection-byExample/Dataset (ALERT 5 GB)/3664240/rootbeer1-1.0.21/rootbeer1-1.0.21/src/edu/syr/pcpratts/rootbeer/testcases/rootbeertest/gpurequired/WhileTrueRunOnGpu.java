/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.concurrent.atomic.AtomicLong;

public class WhileTrueRunOnGpu implements Kernel {

  private AtomicLong m_along;
  private volatile long m_threadId;
  
  public WhileTrueRunOnGpu(AtomicLong along, long id){
    m_along = along;
    m_threadId = id;
  }
  
  public void gpuMethod() {
    int count = 0;
    while(count < 100){
      count++;
      if(count > 95 && m_threadId != -1){
        count = 0;
      }
      long last = m_along.get();
      if(m_along.compareAndSet(last, last + 1)){
        count = 200;
      } 
    }
  }
  
  public AtomicLong get(){
    return m_along;
  }
  
}
