/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class CovarientDerivedClass extends CovarientBaseClass {
  
  protected int m_derivedValue;
  
  public CovarientDerivedClass(int value){
    super(value);
    m_derivedValue = 10;
  }
  
  @Override
  public CovarientBaseClass copy(int value){
    return super.copy(value);
  }
  
  
  @Override 
  public boolean equals(Object other){
    if(other instanceof CovarientDerivedClass == false){
      return false;
    }
    CovarientDerivedClass rhs = (CovarientDerivedClass) other;
    if(m_derivedValue != rhs.m_derivedValue){
      return false;
    }
    return super.equals(other);
  }
}
