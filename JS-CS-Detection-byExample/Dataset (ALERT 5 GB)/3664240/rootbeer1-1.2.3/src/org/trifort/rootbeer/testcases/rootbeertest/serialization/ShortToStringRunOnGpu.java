package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ShortToStringRunOnGpu implements Kernel {

  private short m_short;
  private String m_string;
  
  public ShortToStringRunOnGpu(){
    m_short = 10;
  }
  
  @Override
  public void gpuMethod() {
    m_string = Short.valueOf(m_short).toString();
  }

  public boolean compare(ShortToStringRunOnGpu rhs) {
    if(m_string.equals(rhs.m_string) == false){
      System.out.println("m_string");
      return false;
    }
    return true;
  }

}
