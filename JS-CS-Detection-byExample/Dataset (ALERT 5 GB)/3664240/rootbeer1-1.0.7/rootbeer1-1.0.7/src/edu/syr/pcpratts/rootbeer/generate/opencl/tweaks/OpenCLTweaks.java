/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.tweaks;

public class OpenCLTweaks extends Tweaks {

  @Override
  public String getGlobalAddressSpaceQualifier() {
    return "__global";
  }

  @Override
  public String getHeaderPath() {
    return "/edu/syr/pcpratts/rootbeer/generate/opencl/OpenCLHeader.c";
  }

  @Override
  public String getGarbageCollectorPath() {
    return "/edu/syr/pcpratts/rootbeer/generate/opencl/GarbageCollector.c";
  }

  @Override
  public String getKernelPath() {
    return "/edu/syr/pcpratts/rootbeer/generate/opencl/OpenCLKernel.c";
  }

  @Override
  public String getDeviceFunctionQualifier() {
    return "";
  }

}
