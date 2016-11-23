package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class Autoboxing2RunOnGpu implements Kernel {
  private Integer m_int0;
  private Integer m_int1;
  
  public void gpuMethod() {
    m_int0 = returnInteger0();
    m_int1 = returnInteger1();
  }
  
  private int returnInteger0() {
    return -30; 
  }
  
  private int returnInteger1() {
    return 1; 
  }
  
  public int getInteger0(){
    return m_int0;
  }

  public int getInteger1(){
    return m_int1;
  }
  
  public boolean compare(Autoboxing2RunOnGpu rhs) {
    if(getInteger0() != rhs.getInteger0()){
      System.out.println("m_int0");
      System.out.println("lhs: "+getInteger0());
      System.out.println("rhs: "+rhs.getInteger0());
      return false;
    }    
    if(getInteger1() != rhs.getInteger1()){
      System.out.println("m_int1");
      System.out.println("lhs: "+getInteger1());
      System.out.println("rhs: "+rhs.getInteger1());
      return false;
    }
    return true;
  }
}
