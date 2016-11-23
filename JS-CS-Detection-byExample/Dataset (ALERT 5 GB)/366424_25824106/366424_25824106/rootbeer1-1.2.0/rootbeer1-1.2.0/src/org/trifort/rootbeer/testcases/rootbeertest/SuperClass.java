/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class SuperClass implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 1; ++i){
      SuperClassRunOnGpu curr = new SuperClassRunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SuperClassRunOnGpu blhs = (SuperClassRunOnGpu) lhs;
    SuperClassRunOnGpu brhs = (SuperClassRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
  
}
