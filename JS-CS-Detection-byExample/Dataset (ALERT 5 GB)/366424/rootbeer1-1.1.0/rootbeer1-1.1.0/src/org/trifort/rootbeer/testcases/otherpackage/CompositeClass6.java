/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.otherpackage;

public class CompositeClass6 extends CompositeClass5 {

  private long m_Shared;
  private int m_Modified;
  private String m_HelloStrings;
  
  public CompositeClass6(){
    m_Shared = 100;
    m_Modified = 50;
    m_HelloStrings = "hello world";
  }

  @Override
  public int go() {
    m_Shared = 50;
    m_Modified = 40;
    m_HelloStrings += "hello";
    //return super.go() + m_HelloStrings.length();
    return super.go();
  }
  
  @Override
  public int getModified(){
    return m_Modified + super.getModified();
  }
}
