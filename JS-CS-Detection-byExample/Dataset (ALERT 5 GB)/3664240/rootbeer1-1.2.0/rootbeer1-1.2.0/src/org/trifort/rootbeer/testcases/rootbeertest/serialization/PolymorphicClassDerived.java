package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class PolymorphicClassDerived extends PolymorphicClassBase {

  private int m_value2;
  
  public PolymorphicClassDerived(){
    m_value2 = 1;
  }
  
  public void init(){
    m_value2 = 2;
  }
  
  public int getValue2(){
    return m_value2;
  }
}
