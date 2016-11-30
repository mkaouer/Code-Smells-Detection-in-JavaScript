package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ForceArrayNewRunOnGpu implements Kernel {

  private String m_stringArray;
  
  @Override
  public void gpuMethod() {
    m_stringArray = getStringArray();
  }

  private String getStringArray() {
    return null;
  }

  private String getStringArrayCPU(){
    return "testForceArrayNew";
  }
  
  public boolean compare(ForceArrayNewRunOnGpu rhs) {
    m_stringArray = getStringArrayCPU();
    if(m_stringArray.equals(rhs.m_stringArray) == false){
      System.out.println("m_stringArray.length");
      System.out.println("lhs: "+m_stringArray);
      System.out.println("rhs: "+rhs.m_stringArray);
      return false;
    }
    return true;
  }

}
