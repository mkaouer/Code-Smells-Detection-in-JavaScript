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

public class ShortArrays implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      ShortArraysRunOnGpu curr = new ShortArraysRunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  public boolean compare(Kernel lhs, Kernel rhs) {
    ShortArraysRunOnGpu blhs = (ShortArraysRunOnGpu) lhs;
    ShortArraysRunOnGpu brhs = (ShortArraysRunOnGpu) rhs;
    return blhs.compare(brhs);
  }

}
