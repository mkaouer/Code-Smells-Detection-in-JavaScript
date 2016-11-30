/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class NullPointer2RunOnGpu implements Kernel {

  private NullPointer2Object m_Obj;
  
  public NullPointer2RunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    m_Obj.m_Value = 10;
  }
}
