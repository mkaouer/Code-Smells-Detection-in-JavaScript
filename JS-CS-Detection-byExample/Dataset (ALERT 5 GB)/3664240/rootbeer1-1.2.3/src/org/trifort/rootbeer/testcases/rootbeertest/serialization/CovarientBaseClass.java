/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class CovarientBaseClass {

  protected int m_value;
  protected int m_baseValue;
  
  public CovarientBaseClass(int value){
    m_baseValue = 5;
    m_value = value;
  }
  
  public CovarientBaseClass copy(int value){
    return new CovarientBaseClass(value);
  }
  
  @Override 
  public boolean equals(Object other){
    if(other instanceof CovarientBaseClass == false){
      return false;
    }
    CovarientBaseClass rhs = (CovarientBaseClass) other;
    if(m_value != rhs.m_value){
      return false;
    }
    if(m_baseValue != rhs.m_baseValue){
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + this.m_value;
    hash = 31 * hash + this.m_baseValue;
    return hash;
  }
}
