/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.otherpackage2;

public class StaticsTest2PublicClass {
  
  private static int m_IntValue;
  
  static {
    m_IntValue = 20;
  }
  
  public int getNumber(){
    return m_IntValue + StaticsTest2PrivateClass.getPrivateNumber();
  }
  
  public void setNumber2(int value){
    StaticsTest2PrivateClass.setPrivateNumber2(value);
  }
  
  public int getNumber2(){
    return StaticsTest2PrivateClass.getPrivateNumber2();
  }
}
