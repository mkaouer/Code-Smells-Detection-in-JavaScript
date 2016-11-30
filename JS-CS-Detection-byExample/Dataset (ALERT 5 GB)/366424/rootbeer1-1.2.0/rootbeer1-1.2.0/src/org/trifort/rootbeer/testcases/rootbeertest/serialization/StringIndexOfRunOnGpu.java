package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringIndexOfRunOnGpu implements Kernel {

  private String m_start;
  private int m_index1;
  private int m_index2;
  
  public StringIndexOfRunOnGpu(){
    m_start = "string_index_of_test_string_index_of_test";
  }
  
  @Override
  public void gpuMethod() {
    m_index1 = m_start.indexOf("index_of");
    m_index2 = m_start.indexOf("index_of", 8);
  }

  public boolean compare(StringIndexOfRunOnGpu rhs) {
    if(m_index1 != rhs.m_index1){
      System.out.println("m_index1: ");
      System.out.println("lhs: "+m_index1);
      System.out.println("rhs: "+rhs.m_index1);
      return false;
    }
    if(m_index2 != rhs.m_index2){
      System.out.println("m_index2: ");
      System.out.println("lhs: "+m_index2);
      System.out.println("rhs: "+rhs.m_index2);
      return false;
    }
    return true;
  }
}
