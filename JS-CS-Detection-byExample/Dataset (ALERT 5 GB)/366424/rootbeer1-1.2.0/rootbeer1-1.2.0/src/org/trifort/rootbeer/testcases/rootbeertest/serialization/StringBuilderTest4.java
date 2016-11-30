/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class StringBuilderTest4 implements TestSerialization {
  public static final int N = 16500; // exception occurs only if N > 16500
  public static final int M = 1000;

  public List<Kernel> create() {
    // Prepare big array
    Random rand = new Random();
    System.out.println("init big_array: double[" + N + "][" + M + "]");
    double[][] big_array = new double[N][M];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < M; j++) {
        int choice = rand.nextInt(2);
        if (choice == 0) { // 50% chance
          big_array[i][j] = rand.nextDouble();
        }
      }
    }

    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new StringBuilderRunOnGpu4(big_array));
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    StringBuilderRunOnGpu4 lhs = (StringBuilderRunOnGpu4) original;
    StringBuilderRunOnGpu4 rhs = (StringBuilderRunOnGpu4) from_heap;
    return lhs.compare(rhs);
  }

}
