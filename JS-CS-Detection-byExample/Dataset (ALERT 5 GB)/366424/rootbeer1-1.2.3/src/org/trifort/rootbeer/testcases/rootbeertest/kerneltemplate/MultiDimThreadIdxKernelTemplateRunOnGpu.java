package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class MultiDimThreadIdxKernelTemplateRunOnGpu implements Kernel {

  private int[] results;
  
  public MultiDimThreadIdxKernelTemplateRunOnGpu(int[] results){
    this.results = results;
  }
  
  @Override
  public void gpuMethod() {
    int index = RootbeerGpu.getThreadId();
    results[index] = index;
  }

  public boolean compare() {
    boolean pass = true;
    for(int i = 0; i < results.length; ++i){
      if(results[i] != i){
        System.out.println("fail index: "+i);
        pass = false;
      }
    }
    return pass;
  }
}
