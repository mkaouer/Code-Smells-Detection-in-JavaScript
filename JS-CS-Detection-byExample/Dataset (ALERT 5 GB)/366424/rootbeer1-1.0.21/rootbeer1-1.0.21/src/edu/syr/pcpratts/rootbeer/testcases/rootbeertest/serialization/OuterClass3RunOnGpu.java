/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class OuterClass3RunOnGpu implements Kernel {

  private int m_Int;

  public OuterClass3RunOnGpu(){
    m_Int = 0;
  }

  @Override
  public void gpuMethod() {
    InnerClass inner = new InnerClass();
    m_Int += inner.getValue();
  }

  boolean compare(OuterClass3RunOnGpu brhs) {
    if(m_Int != brhs.m_Int){
      System.out.println("m_Int");
      System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
      return false;
    }
    return true;
  }
  
  private class InnerClass {
    
    private int m_Int;
    
    public InnerClass(){
      m_Int = 5;
    }
    
    public int getValue(){
      return m_Int + 10;
    }
  }
}
