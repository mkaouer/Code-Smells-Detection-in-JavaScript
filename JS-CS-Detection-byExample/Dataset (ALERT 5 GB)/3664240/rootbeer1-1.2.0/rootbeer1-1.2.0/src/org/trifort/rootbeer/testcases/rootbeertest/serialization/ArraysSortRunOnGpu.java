package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.Arrays;

import org.trifort.rootbeer.runtime.Kernel;


public class ArraysSortRunOnGpu implements Kernel {

  private int[] m_array;
  
  public ArraysSortRunOnGpu(){
    m_array = new int[8];
    for(int i = 0; i < m_array.length; ++i){
      m_array[i] = m_array.length - i;
    }
  }
  
	@Override
	public void gpuMethod() {
	  Arrays.sort(m_array);
	}

  public boolean compare(ArraysSortRunOnGpu rhs) {
    if(m_array.length != rhs.m_array.length){
      System.out.println("m_array.length");
      return false;
    }
    for(int i = 0; i < m_array.length; ++i){
      int lhs_value = m_array[i];
      int rhs_value = rhs.m_array[i];
      if(lhs_value != rhs_value){
        System.out.println("value");
        return false;
      }
    }
    return true;
  }
}
