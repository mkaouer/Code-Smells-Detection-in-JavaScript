/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

public class SynchronizedMethod3Object {
  
  public int value;
  private SynchronizedMethod3Object m_NullRef;
  
  public synchronized void increment(){
    value++;
    m_NullRef.increment();
  }
  
  public synchronized void increment2(){
    value++;
    m_NullRef.value++;
  }
}
