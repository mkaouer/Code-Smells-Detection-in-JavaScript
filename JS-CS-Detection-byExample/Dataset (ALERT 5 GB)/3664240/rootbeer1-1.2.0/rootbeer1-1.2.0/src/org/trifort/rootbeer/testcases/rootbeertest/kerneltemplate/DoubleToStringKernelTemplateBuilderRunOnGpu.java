package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class DoubleToStringKernelTemplateBuilderRunOnGpu  implements Kernel {
  
  private String[] m_toString;
  private double[] m_value;
  
  public DoubleToStringKernelTemplateBuilderRunOnGpu(double value, int kernel_count){
    m_toString = new String[kernel_count];
    for (int i = 0; i < m_toString.length; i++) {
      m_toString[i] = "";
    }
    m_value = new double[kernel_count];
    for(int i = 0; i < kernel_count; ++i){
      m_value[i] = value;
    }
  }
  
  public void gpuMethod() {
    int thread_id = RootbeerGpu.getThreadId();
    double value = m_value[thread_id];
    StringBuilder builder = new StringBuilder();
    builder.append(value * value);
    m_toString[thread_id] = builder.toString();
  }
  
  public boolean compare(DoubleToStringKernelTemplateBuilderRunOnGpu rhs) {
    for (int i = 0; i < m_toString.length; i++) {
      if(rhs.m_toString[i] == null){
        System.out.println("rhs.m_toString["+i+"] == null");
        return false;
      }
      String lhs_str = m_toString[i];
      String rhs_str = trimZeros(rhs.m_toString[i]);
      //System.out.println("lhs_str: '"+lhs_str+"' == '"+rhs_str+"' rhs_str");
      if(rhs_str.equals(lhs_str) == false){
        System.out.println("m_toString["+i+"]");
        System.out.println("  lhs: "+lhs_str);
        System.out.println("  rhs: "+rhs_str);
        return false;
      }
    }
    return true;
  }

  private String trimZeros(String string) {
    while(string.endsWith("0") || string.endsWith(".")){
      string = string.substring(0, string.length()-1);
    }
    return string;
  }
}
