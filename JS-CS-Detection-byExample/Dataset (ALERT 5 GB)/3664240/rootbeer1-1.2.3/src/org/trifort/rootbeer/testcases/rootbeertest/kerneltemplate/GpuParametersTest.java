package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.test.TestKernelTemplate;
import org.trifort.rootbeer.test.TestSerialization;

public class GpuParametersTest implements TestKernelTemplate {

  @Override
  public Kernel create() {
    return new GpuParametersRunOnGpu(120);
  }

  @Override
  public ThreadConfig getThreadConfig() {
    return new ThreadConfig(8, 1, 1, 16, 1, 120);
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    GpuParametersRunOnGpu lhs = (GpuParametersRunOnGpu) original;
    GpuParametersRunOnGpu rhs = (GpuParametersRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }

}
