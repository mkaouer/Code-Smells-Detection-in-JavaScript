/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.exception;

import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;

public class NullPointer2RunOnGpu implements Kernel {

  private NullPointer2Object m_Obj;
  
  public NullPointer2RunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    m_Obj.m_Value = 10;
  }
}
