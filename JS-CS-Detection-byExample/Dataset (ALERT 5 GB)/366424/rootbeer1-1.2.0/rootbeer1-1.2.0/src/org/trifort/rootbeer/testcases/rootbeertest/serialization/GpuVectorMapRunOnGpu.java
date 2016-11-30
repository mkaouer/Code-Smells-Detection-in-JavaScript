package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class GpuVectorMapRunOnGpu implements Kernel {

  private int m_n;
  private GpuVectorMap m_map;
  private int m_vectorLen;
  
  public GpuVectorMapRunOnGpu(int n){
    m_n = n;
    m_map = new GpuVectorMap(n);
  }
  
  @Override
  public void gpuMethod() {
    double[] vector1 = new double[m_n];
    for (int i = 0; i < m_n; i++) {
      vector1[i] = i;
    }
    m_map.put(0, vector1);

    double[] vector2 = m_map.get(0);
    m_vectorLen = vector2.length;
  }

  public boolean compare(GpuVectorMapRunOnGpu rhs) {
    double[] lhs_array = m_map.get(0);
    double[] rhs_array = rhs.m_map.get(0);
    if(lhs_array == null || rhs_array == null){
      System.out.println("array == null");
      return false;
    }
    if(lhs_array.length != rhs_array.length){
      System.out.println("length");
      return false;
    }
    for(int i = 0; i < lhs_array.length; ++i){
      double lhs_value = lhs_array[i];
      double rhs_value = rhs_array[i];
      if(lhs_value != rhs_value){
        System.out.println("value");
        return false;
      }
    }
    if(m_vectorLen != rhs.m_vectorLen){
      System.out.println("vectorLen");
      return false;
    }
    return true;
  }
}
