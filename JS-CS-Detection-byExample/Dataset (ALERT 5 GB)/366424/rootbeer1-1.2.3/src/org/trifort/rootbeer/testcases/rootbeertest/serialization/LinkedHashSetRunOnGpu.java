/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.LinkedHashSet;
import java.util.Set;

import org.trifort.rootbeer.runtime.Kernel;

public class LinkedHashSetRunOnGpu implements Kernel {

  private Set<Integer> m_set;
  
  public void gpuMethod() {
    m_set = new LinkedHashSet<Integer>();
    for(int i = 0; i < 5; ++i){
      m_set.add(i);
    }
  }
  
  public boolean compare(LinkedHashSetRunOnGpu rhs) {
    if(m_set.size() != rhs.m_set.size()){
      System.out.println("size");
      return false;
    }
    for(Integer key : m_set){
      if(rhs.m_set.contains(key) == false){
        System.out.println("key");
        return false;
      }
    }
    return true;
  }
}
