package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StaticInitRunOnGpu implements Kernel {

  private int m_value;
  
  @Override
  public void gpuMethod() {
    StaticInitClass object = new StaticInitClass();
    m_value = object.getValue();
  }

  public boolean compare(StaticInitRunOnGpu rhs) {
    if(m_value != rhs.m_value){
      System.out.println("m_value");
      System.out.println("lhs: "+m_value);
      System.out.println("rhs: "+rhs.m_value);
      return false;
    }
    return true;
  }

}
