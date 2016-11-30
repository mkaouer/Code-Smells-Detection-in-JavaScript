package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import java.util.Arrays;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.test.TestKernelTemplate;

public class GpuVectorMapTest2 implements TestKernelTemplate {
  public static final int blockSize = 1;
  public static final int gridSize = 2;
  public static final boolean isDebugging = true;

  @Override
  public Kernel create() {

    GpuVectorMap2 vectorMap = new GpuVectorMap2(gridSize);
    if (isDebugging) {
      System.out.println("input: ");
    }
    for (int i = 0; i < gridSize; i++) {
      double[] vector = new double[blockSize];
      for (int j = 0; j < blockSize; j++) {
        vector[j] = (i * gridSize) + j;
      }
      vectorMap.put(i, vector);
      if (isDebugging) {
        System.out.println("(" + i + "," + Arrays.toString(vector) + ")");
      }
    }
    return new GpuVectorMapRunOnGpu2(vectorMap);
  }

  @Override
  public ThreadConfig getThreadConfig() {
    return new ThreadConfig(blockSize, gridSize, blockSize * gridSize);
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    GpuVectorMapRunOnGpu2 lhs = (GpuVectorMapRunOnGpu2) original;
    GpuVectorMapRunOnGpu2 rhs = (GpuVectorMapRunOnGpu2) from_heap;

    System.out.println("verify lhs: ");
    for (int i = 0; i < gridSize; i++) {
      double[] v = lhs.m_map.get(i);
      System.out.println("(" + i + "," + Arrays.toString(v) + ")");
      for (int j = 0; j < blockSize; j++) {
        double value = v[j];
        double expectedValue = (i * gridSize) + j + 1;
        if (value != expectedValue) {
          System.out.println("Error at position: " + j + " expectedValue: "
              + expectedValue + " != " + value);
          return false;
        }
      }
    }

    System.out.println("verify rhs: ");
    for (int i = 0; i < gridSize; i++) {
      double[] v = rhs.m_map.get(i);
      System.out.println("(" + i + "," + Arrays.toString(v) + ")");
      for (int j = 0; j < blockSize; j++) {
        double value = v[j];
        double expectedValue = (i * gridSize) + j + 1;
        if (value != expectedValue) {
          System.out.println("Error at position: " + j + " expectedValue: "
              + expectedValue + " != " + value);
          return false;
        }
      }
    }

    return true;
  }
}
