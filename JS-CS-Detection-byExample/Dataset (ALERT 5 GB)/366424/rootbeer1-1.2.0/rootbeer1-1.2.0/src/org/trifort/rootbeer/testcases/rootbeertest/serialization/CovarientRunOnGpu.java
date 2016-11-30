/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class CovarientRunOnGpu implements Kernel {

  private CovarientBaseClass m_base1;
  private CovarientBaseClass m_base2;
  private CovarientBaseClass m_base3;
  private CovarientDerivedClass m_derived1;
  
  public void gpuMethod() {
    m_base1 = new CovarientBaseClass(1);    
    m_derived1 = new CovarientDerivedClass(2);
    
    m_base2 = m_base1.copy(3);
    m_base3 = m_derived1.copy(4);
  }

  public boolean compare(CovarientRunOnGpu rhs) {
    if(m_base1.equals(rhs.m_base1) == false){
      System.out.println("base1");
      return false;
    }
    if(m_base2.equals(rhs.m_base2) == false){
      System.out.println("base2");
      return false;
    }
    if(m_base3.equals(rhs.m_base3) == false){
      System.out.println("base3");
      return false;
    }
    if(m_derived1.equals(rhs.m_derived1) == false){
      System.out.println("derived1");
      return false;
    }
    return true;
  }
  
}
