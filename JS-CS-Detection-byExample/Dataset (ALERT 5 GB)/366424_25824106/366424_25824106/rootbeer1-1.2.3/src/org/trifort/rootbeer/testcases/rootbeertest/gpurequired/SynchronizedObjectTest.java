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

public class SynchronizedObjectTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    SynchronizedObjectShared obj = new SynchronizedObjectShared();
    for(int i = 0; i < 32; ++i){
      SynchronizedObjectRunOnGpu curr = new SynchronizedObjectRunOnGpu(obj);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SynchronizedObjectRunOnGpu glhs = (SynchronizedObjectRunOnGpu) lhs;
    SynchronizedObjectRunOnGpu grhs = (SynchronizedObjectRunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
