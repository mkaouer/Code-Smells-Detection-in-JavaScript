/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.HashMap;
import java.util.Map;
import soot.Type;

public class ClassConstantNumbers {
  
  private Map<Type, Integer> m_map;
  private int m_offset;
  
  public ClassConstantNumbers(){
    m_map = new HashMap<Type, Integer>();
    m_offset = 0;
  }
  
  public int get(Type type){
    if(m_map.containsKey(type)){
      return m_map.get(type);
    } else {
      m_map.put(type, m_offset);
      int ret = m_offset;
      ++m_offset;
      return ret;
    }
  }
}
