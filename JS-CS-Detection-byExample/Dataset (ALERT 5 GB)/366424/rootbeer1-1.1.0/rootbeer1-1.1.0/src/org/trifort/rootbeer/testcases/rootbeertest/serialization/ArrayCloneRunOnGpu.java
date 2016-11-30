package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ArrayCloneRunOnGpu implements Kernel {

  private int[] m_array1;
  private int[] m_array2;
  private int[][] m_array3;
  private int[][] m_array4;
  
  public ArrayCloneRunOnGpu(){
    m_array1 = new int[10];
    for(int i = 0; i < 10; ++i){
      m_array1[i] = i;
    }
    
    m_array3 = new int[10][];
    for(int i = 0; i < 10; ++i){
      m_array3[i] = new int[10];
      for(int j = 0; j < 10; ++j){
        m_array3[i][j] = j;
      }
    }
  }
  
  public void gpuMethod(){
    m_array2 = m_array1.clone();
    m_array4 = m_array3.clone();
  }

  public boolean compare(ArrayCloneRunOnGpu rhs) {
    if(m_array2.length != rhs.m_array2.length){
      System.out.println("m_array2.length");
      return false;
    }
    for(int i = 0; i < m_array2.length; ++i){
      if(m_array2[i] != rhs.m_array2[i]){
        System.out.println("m_array2[i]");
        return false;
      }
    }
    if(m_array4.length != rhs.m_array4.length){
      System.out.println("m_array4.length");
      return false;
    }
    for(int i = 0; i < m_array4.length; ++i){
      int[] lhs_inner = m_array4[i];
      int[] rhs_inner = rhs.m_array4[i];
      if(lhs_inner.length != rhs_inner.length){
        System.out.println("m_array4[i].length");
        return false;
      }
      for(int j = 0; j < lhs_inner.length; ++j){
        if(lhs_inner[j] != rhs_inner[j]){
          System.out.println("m_array4[i][j]");
          return false;
        }
      }
    }
    
    return true;
  }
}
