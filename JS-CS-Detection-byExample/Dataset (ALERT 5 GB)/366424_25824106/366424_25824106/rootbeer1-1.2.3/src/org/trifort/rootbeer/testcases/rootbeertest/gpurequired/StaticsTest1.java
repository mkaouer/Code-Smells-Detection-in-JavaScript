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

public class StaticsTest1 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 50; ++i){
      StaticsTest1RunOnGpu curr = new StaticsTest1RunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    StaticsTest1RunOnGpu blhs = (StaticsTest1RunOnGpu) lhs;
    StaticsTest1RunOnGpu brhs = (StaticsTest1RunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
