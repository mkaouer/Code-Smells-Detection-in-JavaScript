/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class NullPointer3RunOnGpu implements Kernel {

  private NullPointer3Object m_Obj;
  private int m_Result;
  
  public NullPointer3RunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    m_Result = m_Obj.m_Value;
  }
}
