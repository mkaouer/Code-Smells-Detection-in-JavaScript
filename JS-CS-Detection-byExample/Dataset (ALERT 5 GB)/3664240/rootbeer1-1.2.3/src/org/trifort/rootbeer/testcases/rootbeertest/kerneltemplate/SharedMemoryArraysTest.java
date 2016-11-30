package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.test.TestKernelTemplate;

public class SharedMemoryArraysTest implements TestKernelTemplate {

  private int threadSize;
  private int blockSize;
  
  public SharedMemoryArraysTest(){
    threadSize = 32;
    blockSize = 2;
  }
  
  @Override
  public Kernel create() {
    int subArraySize = 255;
    int[][][] inputArray = new int[blockSize][threadSize][subArraySize];
    for(int i = 0; i < blockSize; ++i){
      for(int j = 0; j < threadSize; ++j){
        for(int k = 0; k < subArraySize; ++k){
          inputArray[i][j][k] = k;
        }
      }
    }
    int[][][] outputArray = new int[blockSize][threadSize][subArraySize];
    return new SharedMemoryArraysRunOnGpu(inputArray, outputArray, subArraySize);
  }

  @Override
  public ThreadConfig getThreadConfig() {
    return new ThreadConfig(threadSize, 1, 1, blockSize, 1, threadSize * blockSize);
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    SharedMemoryArraysRunOnGpu lhs = (SharedMemoryArraysRunOnGpu) original;
    SharedMemoryArraysRunOnGpu rhs = (SharedMemoryArraysRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
