/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.util.ArrayList;
import java.util.List;

public class ForcedFields {

  private List<String> m_fields;
  
  public ForcedFields(){
    m_fields = new ArrayList<String>();
    m_fields.add("<java.lang.Boolean: boolean value>");
    m_fields.add("<java.lang.Integer: int value>");
    m_fields.add("<java.lang.Long: long value>");
    m_fields.add("<java.lang.Float: float value>");
    m_fields.add("<java.lang.Double: double value>");
    m_fields.add("<java.lang.Class: java.lang.String name>");
    m_fields.add("<java.lang.AbstractStringBuilder: char[] value>");
    m_fields.add("<java.lang.AbstractStringBuilder: int count>");
    m_fields.add("<org.trifort.rootbeer.runtimegpu.GpuException: int m_arrayLength>");
    m_fields.add("<org.trifort.rootbeer.runtimegpu.GpuException: int m_arrayIndex>");
    m_fields.add("<org.trifort.rootbeer.runtimegpu.GpuException: int m_array>");
    m_fields.add("<org.trifort.rootbeer.runtime.GpuStopwatch: long m_start>");
    m_fields.add("<org.trifort.rootbeer.runtime.GpuStopwatch: long m_stop>");
  }
  
  public List<String> get(){
    return m_fields;
  }
}
