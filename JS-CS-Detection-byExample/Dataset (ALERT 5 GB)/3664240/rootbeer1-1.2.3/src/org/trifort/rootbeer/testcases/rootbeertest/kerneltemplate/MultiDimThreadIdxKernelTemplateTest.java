package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.test.TestKernelTemplate;

public class MultiDimThreadIdxKernelTemplateTest implements TestKernelTemplate {

  @Override
  public Kernel create() {
    int[] results = new int[getThreadCount()];
    return new MultiDimThreadIdxKernelTemplateRunOnGpu(results);
  }

  private int getThreadCount() {
    return 8 * 8 * 8 * 8 * 8;
  }

  @Override
  public ThreadConfig getThreadConfig() {
    return new ThreadConfig(8, 8, 8, 8, 8, getThreadCount());
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    MultiDimThreadIdxKernelTemplateRunOnGpu onGpu = (MultiDimThreadIdxKernelTemplateRunOnGpu) from_heap;
    return onGpu.compare();
  }
}
