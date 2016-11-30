package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.Arrays;

import org.trifort.rootbeer.runtime.Kernel;

/**
 * See https://github.com/pcpratts/rootbeer1/issues/112
 * @author pcpratts
 *
 */
public class ArraysSortComparatorRunOnGpu implements Kernel {

  private Integer[] m_array;
  
  public ArraysSortComparatorRunOnGpu(){
    m_array = new Integer[8];
    for(int i = 0; i < m_array.length; ++i){
      m_array[i] = i;
    }
  }
  
  @Override
  public void gpuMethod() {
    Arrays.sort(m_array, new DescendingComparator());
  }

  public boolean compare(ArraysSortComparatorRunOnGpu rhs) {
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
