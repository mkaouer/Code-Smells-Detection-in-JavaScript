/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class SynchronizedMethod2Test implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    SynchronizedMethod2Shared obj = new SynchronizedMethod2Shared();
    for(int i = 0; i < 32; ++i){
      SynchronizedMethod2RunOnGpu curr = new SynchronizedMethod2RunOnGpu(obj);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SynchronizedMethod2RunOnGpu glhs = (SynchronizedMethod2RunOnGpu) lhs;
    SynchronizedMethod2RunOnGpu grhs = (SynchronizedMethod2RunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
