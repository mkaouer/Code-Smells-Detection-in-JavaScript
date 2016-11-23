package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringSplitRunOnGpu implements Kernel {

  private String m_start;
  private String[] m_end1;
  private String[] m_end2;
  
  public StringSplitRunOnGpu(String start){
    m_start = start;
  }
  
  @Override
  public void gpuMethod() {
    m_end1 = m_start.split(" ");
    m_end2 = m_start.split(" ", 2);
  }

  public boolean compare(StringSplitRunOnGpu rhs) {
    if(compareArrays(m_end1, rhs.m_end1) == false){
      System.out.println("m_end1");
      System.out.println("lhs: "+m_end1);
      System.out.println("rhs: "+rhs.m_end1);
      return false;
    }   
    if(compareArrays(m_end2, rhs.m_end2) == false){
      System.out.println("m_end2");
      System.out.println("lhs: "+m_end2);
      System.out.println("rhs: "+rhs.m_end2);
      return false;
    }
    return true;
  }

  private boolean compareArrays(String[] lhs, String[] rhs) {
    if(lhs.length != rhs.length){
      return false;
    }
    for(int i = 0; i < lhs.length; ++i){
      if(lhs[i].equals(rhs[i]) == false){
        return false;
      }
    }
    return true;
  }
}
