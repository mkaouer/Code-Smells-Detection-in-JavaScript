/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

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
