/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

public class CompositeClass2 extends CompositeClass1 {

  private int m_Var1;
  protected int m_Shared;
  
  public CompositeClass2(int value){
    m_Var1 = value;
    m_Shared = 20;
  }

  @Override
  int go() {
    return m_Var1 + m_Shared + super.go();
  }
 
}
