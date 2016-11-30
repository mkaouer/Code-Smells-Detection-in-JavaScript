package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class BooleanToStringRunOnGpu implements Kernel {

  private boolean m_bool1;
  private boolean m_bool2;
  private String m_string1;
  private String m_string2;
  
  public BooleanToStringRunOnGpu(){
    m_bool1 = true;
    m_bool2 = true;
  }
  
  @Override
  public void gpuMethod() {
    m_string1 = Boolean.valueOf(m_bool1).toString();
    m_string2 = Boolean.valueOf(m_bool2).toString();
  }

  public boolean compare(BooleanToStringRunOnGpu rhs) {
    if(m_string1.equals(rhs.m_string1) == false){
      System.out.println("m_string1");
      return false;
    }    
    if(m_string2.equals(rhs.m_string2) == false){
      System.out.println("m_string2");
      return false;
    }
    return true;
  }
}
