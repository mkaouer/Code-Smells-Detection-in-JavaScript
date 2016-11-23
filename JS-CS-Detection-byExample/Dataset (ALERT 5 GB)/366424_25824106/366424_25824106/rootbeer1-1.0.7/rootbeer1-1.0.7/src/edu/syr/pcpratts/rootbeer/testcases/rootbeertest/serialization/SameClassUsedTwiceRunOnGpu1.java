/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class SameClassUsedTwiceRunOnGpu1 implements Kernel {

  private int m_Int;

  public SameClassUsedTwiceRunOnGpu1(){
    m_Int = 5;
  }

  @Override
  public void gpuMethod() {
    SameClassUsedClass obj = new SameClassUsedClass();
    m_Int += obj.getValue1();
  }

  boolean compare(SameClassUsedTwiceRunOnGpu1 brhs) {
    if(m_Int != brhs.m_Int){
      System.out.println("m_Int");
      System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
      return false;
    }
    return true;
  }
}
