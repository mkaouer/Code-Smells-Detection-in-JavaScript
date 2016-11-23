/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.otherpackage2;

class StaticsTest2PrivateClass {
  
  private static int m_PrivateClassNumber;
  private static int m_PrivateClassNumber2;
  
  static {
    m_PrivateClassNumber = 50;
    m_PrivateClassNumber2 = 50;
  }
  
  static int getPrivateNumber(){
    return m_PrivateClassNumber;
  }
  
  static void setPrivateNumber2(int value){
    m_PrivateClassNumber2 = value;
  }
  
  static int getPrivateNumber2(){
    return m_PrivateClassNumber2;
  }
}
