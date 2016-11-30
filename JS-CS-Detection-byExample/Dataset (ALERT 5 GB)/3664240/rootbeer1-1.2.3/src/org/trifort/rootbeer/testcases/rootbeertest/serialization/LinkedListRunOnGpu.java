/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.LinkedList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;

public class LinkedListRunOnGpu implements Kernel {

  private List<Double> m_list;
  
  public LinkedListRunOnGpu() {
    m_list = new LinkedList<Double>();
  }

  public void gpuMethod() {
    for(int i = 0; i < 5; ++i){
      m_list.add((double) i);
    }
  }

  public boolean compare(LinkedListRunOnGpu rhs) {
    if(m_list.size() != rhs.m_list.size()){
      System.out.println("size");
      return false;
    }
    for(int i = 0; i < m_list.size(); ++i){
      double lhs_value = m_list.get(i);
      double rhs_value = rhs.m_list.get(i);
      if(lhs_value != rhs_value){
        System.out.println("value");
        return false;
      }
    }
    return true;
  }
  
}
