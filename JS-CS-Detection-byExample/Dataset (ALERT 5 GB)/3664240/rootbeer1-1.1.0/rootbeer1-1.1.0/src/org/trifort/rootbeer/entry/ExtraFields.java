/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.util.ArrayList;
import java.util.List;
import soot.rbclassload.RootbeerClassLoader;

public class ExtraFields {
  
  private List<String> m_fields;
  
  public ExtraFields(){
    m_fields = new ArrayList<String>();
    m_fields.add("<java.lang.Class: java.lang.String name>");
    m_fields.add("<org.trifort.rootbeer.runtimegpu.GpuException: int m_arrayLength>");
    m_fields.add("<org.trifort.rootbeer.runtimegpu.GpuException: int m_arrayIndex>");
    m_fields.add("<org.trifort.rootbeer.runtimegpu.GpuException: int m_array>");
    m_fields.add("<java.lang.String: char[] value>");
    m_fields.add("<java.lang.String: int count>");
    m_fields.add("<java.lang.String: int offset>");
    m_fields.add("<java.lang.StringBuilder: char[] value>");
    m_fields.add("<java.lang.StringBuilder: int count>");
    m_fields.add("<org.trifort.rootbeer.runtime.GpuStopwatch: long m_start>");
    m_fields.add("<org.trifort.rootbeer.runtime.GpuStopwatch: long m_stop>");
    m_fields.add("<java.lang.Integer: int value>");
  }
  
  public List<String> get(){
    return m_fields;
  }
}
