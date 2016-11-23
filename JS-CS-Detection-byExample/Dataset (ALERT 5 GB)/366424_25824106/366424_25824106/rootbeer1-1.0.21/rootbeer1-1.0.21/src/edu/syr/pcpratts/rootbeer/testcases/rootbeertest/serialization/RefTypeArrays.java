/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class RefTypeArrays implements TestSerialization{

  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 50; ++i){
      RefTypeArraysRunOnGpu curr = new RefTypeArraysRunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  public boolean compare(Kernel lhs, Kernel rhs) {
    RefTypeArraysRunOnGpu blhs = (RefTypeArraysRunOnGpu) lhs;
    RefTypeArraysRunOnGpu brhs = (RefTypeArraysRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
