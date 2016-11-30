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

public class SynchronizedMethodTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    SynchronizedMethodObject obj = new SynchronizedMethodObject();
    for(int i = 0; i < 32; ++i){
      SynchronizedMethodRunOnGpu curr = new SynchronizedMethodRunOnGpu(obj);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SynchronizedMethodRunOnGpu glhs = (SynchronizedMethodRunOnGpu) lhs;
    SynchronizedMethodRunOnGpu grhs = (SynchronizedMethodRunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
