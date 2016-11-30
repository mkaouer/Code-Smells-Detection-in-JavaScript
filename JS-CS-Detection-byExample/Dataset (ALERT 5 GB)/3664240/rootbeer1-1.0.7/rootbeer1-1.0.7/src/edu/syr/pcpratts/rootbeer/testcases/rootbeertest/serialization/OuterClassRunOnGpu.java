/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class OuterClassRunOnGpu implements Kernel {

  private int m_Int;

  public OuterClassRunOnGpu(){
    m_Int = 0;
  }

  @Override
  public void gpuMethod() {
    m_Int++;
  }

  boolean compare(OuterClassRunOnGpu brhs) {
    if(m_Int != brhs.m_Int){
      System.out.println("m_Int");
      System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
      return false;
    }
    return true;
  }
  
  public class OuterClassRunOnGpu2 implements Kernel {
    private int m_Int;
    
    public OuterClassRunOnGpu2(){
      m_Int = 1;
    }
    
    @Override
    public void gpuMethod() {
      m_Int++;
    }
    
    public boolean compare(OuterClassRunOnGpu.OuterClassRunOnGpu2 brhs) {
      if(m_Int != brhs.m_Int){
        System.out.println("m_Int");
        System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
        return false;
      }
      return true;
    }
  }
}
