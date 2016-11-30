/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class SameClassUsedClass {

  private int m_Value1;
  private int m_Value2;
  
  public SameClassUsedClass(){
    m_Value1 = 10;
    m_Value2 = 20;
  }
  
  public int getValue1(){
    return m_Value1;
  }
  
  public int getValue2(){
    return m_Value2;
  }
}
