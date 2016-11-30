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

public class SynchronizedStaticMethodTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    SynchronizedStaticMethodShared obj = new SynchronizedStaticMethodShared();
    for(int i = 0; i < 32; ++i){
      SynchronizedStaticMethodRunOnGpu curr = new SynchronizedStaticMethodRunOnGpu(obj);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SynchronizedStaticMethodRunOnGpu glhs = (SynchronizedStaticMethodRunOnGpu) lhs;
    SynchronizedStaticMethodRunOnGpu grhs = (SynchronizedStaticMethodRunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
