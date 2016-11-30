package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class StaticInitClass {

  static {
    m_value = 1;
  }
  
  private static int m_value;
 
  public int getValue(){
    return m_value;
  }
}
