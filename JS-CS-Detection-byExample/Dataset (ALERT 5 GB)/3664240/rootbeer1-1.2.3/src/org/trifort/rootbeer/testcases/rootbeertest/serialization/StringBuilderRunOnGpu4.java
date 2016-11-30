/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class StringBuilderRunOnGpu4 implements Kernel {

  private double[][] m_big_array;

  public StringBuilderRunOnGpu4(double[][] big_array) {
    this.m_big_array = big_array;
  }

  @Override
  public void gpuMethod() {
    if (RootbeerGpu.getThreadId() == 0) {
      // TODO CheckedFixedMemory.java:20
      // java.lang.RuntimeException: address out of range
      StringBuilder sb = new StringBuilder(
          "I will throw the java.lang.RuntimeException.");
    }

    // Some dummy operation on big_array
    m_big_array[RootbeerGpu.getBlockIdxx()][RootbeerGpu.getThreadIdxx()] = RootbeerGpu
        .getThreadId();
  }

  public boolean compare(StringBuilderRunOnGpu4 rhs) {
    return true;
  }
}
