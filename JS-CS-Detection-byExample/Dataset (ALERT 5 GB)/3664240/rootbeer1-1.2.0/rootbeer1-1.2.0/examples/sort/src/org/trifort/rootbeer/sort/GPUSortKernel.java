package org.trifort.rootbeer.sort;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;


public class GPUSortKernel implements Kernel {

  private int[][] arrays;

  public GPUSortKernel(int[][] arrays){
    this.arrays = arrays;
  }

  @Override
  public void gpuMethod(){
    int[] array = arrays[RootbeerGpu.getBlockIdxx()];
    int index1a = RootbeerGpu.getThreadIdxx() << 1;
    int index1b = index1a + 1;
    int index2a = index1a - 1;
    int index2b = index1a;
    int index1a_shared = index1a << 2;
    int index1b_shared = index1b << 2;
    int index2a_shared = index2a << 2;
    int index2b_shared = index2b << 2;

    RootbeerGpu.setSharedInteger(index1a_shared, array[index1a]);
    RootbeerGpu.setSharedInteger(index1b_shared, array[index1b]);
    //outer pass
    int arrayLength = array.length >> 1;
    for(int i = 0; i < arrayLength; ++i){
      int value1 = RootbeerGpu.getSharedInteger(index1a_shared);
      int value2 = RootbeerGpu.getSharedInteger(index1b_shared);
      int shared_value = value1;
      if(value2 < value1){
        shared_value = value2;
        RootbeerGpu.setSharedInteger(index1a_shared, value2);
        RootbeerGpu.setSharedInteger(index1b_shared, value1);
      }
      RootbeerGpu.syncthreads();
      if(index2a >= 0){
        value1 = RootbeerGpu.getSharedInteger(index2a_shared);
        //value2 = RootbeerGpu.getSharedInteger(index2b_shared);
        value2 = shared_value;
        if(value2 < value1){
          RootbeerGpu.setSharedInteger(index2a_shared, value2);
          RootbeerGpu.setSharedInteger(index2b_shared, value1);
        }
      }
      RootbeerGpu.syncthreads();
    }
    array[index1a] = RootbeerGpu.getSharedInteger(index1a_shared);
    array[index1b] = RootbeerGpu.getSharedInteger(index1b_shared);
  }
}
