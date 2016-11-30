package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ByteToStringRunOnGpu implements Kernel {

  private byte m_byte;
  private String m_string;
  
  public ByteToStringRunOnGpu(){
    m_byte = 10;
  }
  
  @Override
  public void gpuMethod() {
    m_string = Byte.valueOf(m_byte).toString();
  }

  public boolean compare(ByteToStringRunOnGpu rhs) {
    if(m_string.equals(rhs.m_string) == false){
      System.out.println("m_string");
      return false;
    }
    return true;
  }

}
