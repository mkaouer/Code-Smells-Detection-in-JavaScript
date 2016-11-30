/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.LinkedHashMap;
import java.util.Map;

import org.trifort.rootbeer.runtime.Kernel;

public class LinkedHashMapRunOnGpu implements Kernel {

  private Map<Integer, String> m_map;
  
  public void gpuMethod() {
    m_map = new LinkedHashMap<Integer, String>();
    m_map.put(0, "a");
    m_map.put(1, "b");
    m_map.put(2, "c");
    m_map.put(3, "d");
    m_map.put(4, "e");
    m_map.put(5, "f");
  }

  public boolean compare(LinkedHashMapRunOnGpu rhs) {
    if(m_map.size() != rhs.m_map.size()){
      System.out.println("size");
      return false;
    }
    for(Integer key : m_map.keySet()){
      String lhs_value = m_map.get(key);
      if(rhs.m_map.containsKey(key) == false){
        System.out.println("key not found");
        return false;
      }
      String rhs_value = rhs.m_map.get(key);
      if(lhs_value.equals(rhs_value) == false){
        System.out.println("value mismatch");
        return false;
      }
    }
    return true;
  }
}
