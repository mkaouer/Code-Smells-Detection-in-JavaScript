/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class SynchronizedMethod3Test implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    SynchronizedMethod3Object obj = new SynchronizedMethod3Object();
    for(int i = 0; i < 4096; ++i){
      SynchronizedMethod3RunOnGpu curr = new SynchronizedMethod3RunOnGpu(obj);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel rog, Kernel rog1) {
    SynchronizedMethod3RunOnGpu lhs = (SynchronizedMethod3RunOnGpu) rog;
    SynchronizedMethod3RunOnGpu rhs = (SynchronizedMethod3RunOnGpu) rog1;
    return lhs.compare(rhs);
  }

  
}
