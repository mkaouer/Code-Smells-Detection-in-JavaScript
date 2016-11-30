/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.everything;

import org.trifort.rootbeer.runtime.Kernel;

class EverythingRunOnGpu implements Kernel {

  private EverythingSynch m_synch;
  
  public EverythingRunOnGpu(EverythingSynch synch) {
    m_synch = synch;
  }

  public void gpuMethod() {
    
    
  }
  
  

}
