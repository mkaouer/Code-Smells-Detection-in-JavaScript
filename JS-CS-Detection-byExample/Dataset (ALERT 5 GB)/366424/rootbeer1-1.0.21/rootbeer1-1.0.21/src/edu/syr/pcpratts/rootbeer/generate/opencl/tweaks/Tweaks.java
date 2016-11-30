/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.tweaks;

import java.util.List;

public abstract class Tweaks {

  private static Tweaks m_Instance;
  
  public static Tweaks v(){
    return m_Instance;
  }
  
  public static void setInstance(Tweaks instance){
    m_Instance = instance;
  }

  public abstract String getHeaderPath();
  public abstract String getGlobalAddressSpaceQualifier();
  public abstract String getGarbageCollectorPath();
  public abstract String getKernelPath();
  public abstract String getDeviceFunctionQualifier();
}
