/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public class RootbeerAtomicInt {

  private int m_value;
  
  public RootbeerAtomicInt(){
    m_value = 0;
  }
  
  public int atomicAdd(int value){
    synchronized(this){
      int ret = m_value;
      ++m_value;
      return ret;
    }
  }

  @Override
  public String toString() {
    return Integer.toString(m_value);
  }
}
