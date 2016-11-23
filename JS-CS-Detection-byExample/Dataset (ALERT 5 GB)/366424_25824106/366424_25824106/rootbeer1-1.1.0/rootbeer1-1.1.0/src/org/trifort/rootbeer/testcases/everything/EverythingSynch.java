/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.everything;

public class EverythingSynch {

  private int m_value;
  
  public synchronized void increment(){
    ++m_value;
  }
}
