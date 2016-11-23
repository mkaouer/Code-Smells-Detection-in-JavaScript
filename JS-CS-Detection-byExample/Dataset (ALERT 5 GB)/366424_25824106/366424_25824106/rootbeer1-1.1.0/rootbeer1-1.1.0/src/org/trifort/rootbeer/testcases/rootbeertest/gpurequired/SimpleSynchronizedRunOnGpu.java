/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class SimpleSynchronizedRunOnGpu implements Kernel {

  private SimpleSynchronizedObject m_syncObj;
  
  public SimpleSynchronizedRunOnGpu(SimpleSynchronizedObject sync_obj){
    m_syncObj = sync_obj;
  }
  
  public void gpuMethod() {
    m_syncObj.inc();
  }
  
  public SimpleSynchronizedObject getSyncObj(){
    return m_syncObj;
  }
}
