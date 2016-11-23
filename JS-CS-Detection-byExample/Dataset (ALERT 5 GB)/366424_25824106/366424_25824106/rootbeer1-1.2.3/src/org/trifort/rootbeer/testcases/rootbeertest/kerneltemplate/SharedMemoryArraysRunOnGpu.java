package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class SharedMemoryArraysRunOnGpu implements Kernel {

  private int[][][] inputArray;
  private int[][][] outputArray;
  private int subArrayLength;
  
  public SharedMemoryArraysRunOnGpu(int[][][] inputArray, int[][][] outputArray,
      int subArrayLength){
    
    this.inputArray = inputArray;
    this.outputArray = outputArray;
    this.subArrayLength = subArrayLength;
  }
  
  @Override
  public void gpuMethod() {
    int blockIdxx = RootbeerGpu.getBlockIdxx();
    int threadIdxx = RootbeerGpu.getThreadIdxx();
    int[] localInputArray = inputArray[blockIdxx][threadIdxx];
    int[] localOutputArray = outputArray[blockIdxx][threadIdxx];
    
    //RootbeerGpu.createSharedIntArray(threadIdxx, subArrayLength);
    //RootbeerGpu.syncthreads();
    
    /*
     * 
     * / Allocate an array for this thread
        RootbeerGpu.createSharedIntArray(threadIdx, subArrayLength);

        RootbeerGpu.syncthreads();

        // Use all threads of the thread group to fetch data from global memory
        // into each of the shared memory arrays.
        // Each thread contributes to filling the shared array of all threads in the
        // current thread group
        for (int i = threadIdx; i < blkDim * subArrayLength; i += blkDim) {
            int sharedArrayIndex = i / subArrayLength;
            int[] sharedArray = RootbeerGpu.getSharedIntArray(sharedArrayIndex);
            int globalIdx = RootbeerGpu.getBlockIdxx() * blkDim + threadIdx + i;
            sharedArray[i % blkDim] = data[globalIdx];
        }

        RootbeerGpu.syncthreads();

        int[] threadArray = RootbeerGpu.getSharedIntArray(threadIdx);
        // Do something with threadArray locally

        RootbeerGpu.syncthreads();

        // Copy the data back into global memory.
        // Again, each thread contributes to copying the shared array of all threads in the
        // current thread group
        for (int i = threadIdx; i < blkDim * subArrayLength; i += blkDim) {
            int sharedArrayIndex = i / subArrayLength;
            int[] sharedArray = RootbeerGpu.getSharedIntArray(sharedArrayIndex);
            int globalIdx = RootbeerGpu.getBlockIdxx() * blkDim + threadIdx + i;
            data[globalIdx] = sharedArray[i % blkDim];
        }
     */
  }

  public boolean compare(SharedMemoryArraysRunOnGpu rhs) {
    for(int i = 0; i < outputArray.length; ++i){
      int[][] innerArray1a = outputArray[i];
      int[][] innerArray1b = rhs.outputArray[i];
      for(int j = 0; j < innerArray1a.length; ++j){
        int[] innerArray2a = innerArray1a[j];
        int[] innerArray2b = innerArray1b[j];
        for(int k = 0; k < innerArray2a.length; ++k){
          int value1 = innerArray2a[k];
          int value2 = innerArray2b[k];
          if(value1 != value2){
            System.out.println("i: "+i+" j: "+j+" k: "+k);
            System.out.println("value1: "+value1);
            System.out.println("value2: "+value2);
            return false;
          }
        }
      }
    }
    return true;
  }
}
