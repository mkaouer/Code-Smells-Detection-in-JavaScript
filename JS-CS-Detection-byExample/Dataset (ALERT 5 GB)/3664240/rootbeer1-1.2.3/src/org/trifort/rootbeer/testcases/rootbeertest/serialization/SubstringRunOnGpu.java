package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class SubstringRunOnGpu implements Kernel {

  private String m_start;
  private String m_end1;
  private String m_end2;
  
  public SubstringRunOnGpu(){
    m_start = "substring_test";
  }
  
  @Override
  public void gpuMethod() {
    m_end1 = m_start.substring(2);
    m_end2 = m_start.substring(2, 4);
  }

  public boolean compare(SubstringRunOnGpu rhs) {
    if(m_end1.equals(rhs.m_end1) == false){
      System.out.println("m_end1");
      System.out.println("lhs: "+m_end1);
      System.out.println("rhs: "+rhs.m_end1);
      return false;
    }
    if(m_end2.equals(rhs.m_end2) == false){
      System.out.println("m_end2");
      System.out.println("lhs: "+m_end2);
      System.out.println("rhs: "+rhs.m_end2);
      return false;
    }
    return true;
  }

}
