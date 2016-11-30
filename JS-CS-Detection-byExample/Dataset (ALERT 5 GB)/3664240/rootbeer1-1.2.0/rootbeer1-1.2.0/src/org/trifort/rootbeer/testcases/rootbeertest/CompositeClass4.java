/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest;

public class CompositeClass4 extends CompositeClass3 {
  
  public int m_Shared;

  public CompositeClass4(int value){
    super(value);
    m_Shared = value;
  }
  
  @Override
  public int go() {
    return m_Shared + super.go();
  }
}
