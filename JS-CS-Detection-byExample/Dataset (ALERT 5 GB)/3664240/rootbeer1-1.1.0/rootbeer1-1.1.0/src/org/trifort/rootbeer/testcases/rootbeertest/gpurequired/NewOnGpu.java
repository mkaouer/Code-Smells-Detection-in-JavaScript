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

public class NewOnGpu implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 10; ++i){
      NewOnGpuRunOnGpu curr = new NewOnGpuRunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  public boolean compare(Kernel lhs, Kernel rhs) {
    NewOnGpuRunOnGpu blhs = (NewOnGpuRunOnGpu) lhs;
    NewOnGpuRunOnGpu brhs = (NewOnGpuRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
