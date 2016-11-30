package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class GpuVectorMapRunOnGpu2 implements Kernel {

  public GpuVectorMap2 m_map;

  public GpuVectorMapRunOnGpu2(GpuVectorMap2 map) {
    this.m_map = map;
  }

  @Override
  public void gpuMethod() {
    int thread_idxx = RootbeerGpu.getThreadIdxx();
    int block_idxx = RootbeerGpu.getBlockIdxx();

    // Setup sharedMemory from Map
    if (thread_idxx == 0) {
      double[] vector = m_map.get(block_idxx);
      debug(block_idxx, vector); // TODO Error
      for (int i = 0; i < vector.length; i++) {
        RootbeerGpu.setSharedDouble(i * 8, vector[i]);
        // System.out.println(vector[i]);
      }
    }
    RootbeerGpu.syncthreads();

    // Each kernel increments one item
    double val = RootbeerGpu.getSharedDouble(thread_idxx * 8);
    RootbeerGpu.setSharedDouble(thread_idxx * 8, val + 1);

    RootbeerGpu.syncthreads();

    // Put sharedMemory back into Map
    if (thread_idxx == 0) {
      double[] vector = new double[RootbeerGpu.getBlockDimx()];
      for (int i = 0; i < vector.length; i++) {
        vector[i] = RootbeerGpu.getSharedDouble(i * 8);
      }
      m_map.put(block_idxx, vector);
    }
  }

  private synchronized void debug(int val, double[] arr) {
    int x = arr.length; // ERROR arr.length sets array values to 0
    // System.out.print("(");
    // System.out.print(val);
    // System.out.print(",");
    // if (arr != null) {
    // for (int i = 0; i < arr.length; i++) {
    // System.out.print(Double.toString(arr[i]));
    // if (i + 1 < arr.length) {
    // System.out.print(",");
    // }
    // }
    // }
    // System.out.println(")");
  }
}
