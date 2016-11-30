/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.tweaks;

public class NativeCpuTweaks extends Tweaks {

  @Override
  public String getGlobalAddressSpaceQualifier() {
    return "";
  }

  @Override
  public String getUnixHeaderPath() {
    return "/org/trifort/rootbeer/generate/opencl/UnixNativeHeader.c";
  }
  
  @Override
  public String getWindowsHeaderPath() {
    return "/org/trifort/rootbeer/generate/opencl/WindowsNativeHeader.c";
  }
  
  @Override
  public String getBothHeaderPath() {
    return "/org/trifort/rootbeer/generate/opencl/BothNativeHeader.c";
  }
  
  @Override
  public String getBarrierPath() {
    return "/org/trifort/rootbeer/generate/opencl/BarrierNativeBoth.c";
  }

  @Override
  public String getGarbageCollectorPath() {
    return "/org/trifort/rootbeer/generate/opencl/GarbageCollector.c";
  }

  @Override
  public String getUnixKernelPath() {
    return "/org/trifort/rootbeer/generate/opencl/UnixNativeKernel.c";
  }

  @Override
  public String getWindowsKernelPath() {
    return "/org/trifort/rootbeer/generate/opencl/WindowsNativeKernel.c";
  }

  @Override
  public String getBothKernelPath() {
    return "/org/trifort/rootbeer/generate/opencl/BothNativeKernel.c";
  }

  @Override
  public String getDeviceFunctionQualifier() {
    return "";
  }  
}
