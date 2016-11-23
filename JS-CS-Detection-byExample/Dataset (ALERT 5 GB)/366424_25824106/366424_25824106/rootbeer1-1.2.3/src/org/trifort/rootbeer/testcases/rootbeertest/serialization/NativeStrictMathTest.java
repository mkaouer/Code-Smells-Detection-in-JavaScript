/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class NativeStrictMathTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 5; ++i){
      NativeStrictMathRunOnGpu curr = new NativeStrictMathRunOnGpu(i);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    NativeStrictMathRunOnGpu glhs = (NativeStrictMathRunOnGpu) lhs;
    NativeStrictMathRunOnGpu grhs = (NativeStrictMathRunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
