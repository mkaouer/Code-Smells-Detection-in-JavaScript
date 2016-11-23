package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class CharToStringRunOnGpu implements Kernel {

  private char m_char;
  private String m_string;
  
  public CharToStringRunOnGpu(){
    m_char = 'a';
  }
  
  @Override
  public void gpuMethod() {
    m_string = Character.valueOf(m_char).toString();
  }

  public boolean compare(CharToStringRunOnGpu rhs) {
    if(m_string.equals(rhs.m_string) == false){
      System.out.println("m_string");
      return false;
    }
    return true;
  }

}
