package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class NestedMonitorRunOnGpu implements Kernel {

  private int m_value;
  
  public NestedMonitorRunOnGpu(){
    m_value = 10;
  }
  
  @Override
  public void gpuMethod() {
    /*
    Object object1 = new Object();
    synchronized (object1) {
      if(object1.toString().equals("")){
        return;
      }
      
      synchronized (object1) {
        if(m_value == 10){
          throw new RuntimeException();
        }
        try {
          m_value = 5;
        } finally {
          m_value = 10;
        }
      }
    }
    */
  }

  public boolean compare(NestedMonitorRunOnGpu rhs) {
    /*
    if(m_value != rhs.m_value){
      System.out.println("m_value");
      System.out.println("lhs: "+m_value);
      System.out.println("rhs: "+rhs.m_value);
      return false;
    }
    return true;
    */
    return false;
  }
}
