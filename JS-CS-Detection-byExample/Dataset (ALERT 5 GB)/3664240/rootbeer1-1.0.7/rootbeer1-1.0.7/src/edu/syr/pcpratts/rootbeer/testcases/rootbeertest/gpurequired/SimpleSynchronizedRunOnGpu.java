/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import java.util.ArrayList;
import java.util.List;

public class SimpleSynchronizedRunOnGpu implements Kernel {

  private SimpleSynchronizedObject m_syncObj;
  private List<Integer> m_olds;
  
  public SimpleSynchronizedRunOnGpu(SimpleSynchronizedObject sync_obj){
    m_syncObj = sync_obj;
    m_olds = new ArrayList<Integer>();
  }
  
  public void gpuMethod() {
    m_syncObj.inc();
  }
  
  public SimpleSynchronizedObject getSyncObj(){
    return m_syncObj;
  }
}
