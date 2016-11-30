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
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    AtomicLong along = new AtomicLong(0);
    Random random = new Random();
    int size = 500;
    for(int i = 0; i < size; ++i){
      AtomicLongRunOnGpu curr = new AtomicLongRunOnGpu(along, random);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    AtomicLongRunOnGpu glhs = (AtomicLongRunOnGpu) lhs;
    AtomicLongRunOnGpu grhs = (AtomicLongRunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
