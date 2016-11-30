/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

public class CompositeClass1 extends CompositeClass0 {
  
  private int m_Shared;
  
  public CompositeClass1(){
    m_Shared = 900;
  }

  int go() {
    return m_Shared;
  }
  
}
