/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class SameClassUsedTwiceRunOnGpu2 implements Kernel {

  private int m_Int;

  public SameClassUsedTwiceRunOnGpu2(){
    m_Int = 6;
  }

  @Override
  public void gpuMethod() {
    SameClassUsedClass obj = new SameClassUsedClass();
    m_Int += obj.getValue2();
  }

  boolean compare(SameClassUsedTwiceRunOnGpu2 brhs) {
    if(m_Int != brhs.m_Int){
      System.out.println("m_Int");
      System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
      return false;
    }
    return true;
  }
}
