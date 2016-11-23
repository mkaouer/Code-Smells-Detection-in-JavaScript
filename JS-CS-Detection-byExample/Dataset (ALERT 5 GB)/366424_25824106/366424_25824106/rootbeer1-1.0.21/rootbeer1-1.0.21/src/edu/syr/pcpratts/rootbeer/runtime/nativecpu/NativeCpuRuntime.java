/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.nativecpu;

import edu.syr.pcpratts.rootbeer.runtime.ParallelRuntime;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.Iterator;

public class NativeCpuRuntime implements ParallelRuntime {

  private static NativeCpuRuntime m_Instance = null;
  
  public static NativeCpuRuntime v(){
    if(m_Instance == null)
      m_Instance = new NativeCpuRuntime();
    return m_Instance;
  }
  
  NativeCpuDevice m_Device;
  
  private NativeCpuRuntime(){
    m_Device = new NativeCpuDevice();
  }
  
  public PartiallyCompletedParallelJob run(Iterator<Kernel> blocks) {
    return m_Device.run(blocks);
  }

  public boolean isGpuPresent() {
    return true;
  }
  
}
