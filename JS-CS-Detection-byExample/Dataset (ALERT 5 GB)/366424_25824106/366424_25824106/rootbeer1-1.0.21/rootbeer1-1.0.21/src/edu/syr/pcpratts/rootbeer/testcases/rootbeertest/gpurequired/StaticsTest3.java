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

public class StaticsTest3 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 50; ++i){
      StaticsTest3RunOnGpu curr = new StaticsTest3RunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    StaticsTest3RunOnGpu blhs = (StaticsTest3RunOnGpu) lhs;
    StaticsTest3RunOnGpu brhs = (StaticsTest3RunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
