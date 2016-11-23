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
import org.trifort.rootbeer.testcases.rootbeertest.serialization.OuterClassRunOnGpu.OuterClassRunOnGpu2;

public class OuterClassTest2 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      OuterClassRunOnGpu curr_outer = new OuterClassRunOnGpu();
      OuterClassRunOnGpu.OuterClassRunOnGpu2 curr = curr_outer.new OuterClassRunOnGpu2();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    OuterClassRunOnGpu.OuterClassRunOnGpu2 blhs = (OuterClassRunOnGpu.OuterClassRunOnGpu2) lhs;
    OuterClassRunOnGpu.OuterClassRunOnGpu2 brhs = (OuterClassRunOnGpu.OuterClassRunOnGpu2) rhs;
    return blhs.compare(brhs);
  }
}