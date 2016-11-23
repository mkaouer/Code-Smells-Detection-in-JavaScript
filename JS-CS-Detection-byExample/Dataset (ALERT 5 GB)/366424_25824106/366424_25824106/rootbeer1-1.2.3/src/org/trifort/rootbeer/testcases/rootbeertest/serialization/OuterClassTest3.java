/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class OuterClassTest3 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      OuterClass3RunOnGpu curr = new OuterClass3RunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    OuterClass3RunOnGpu blhs = (OuterClass3RunOnGpu) lhs;
    OuterClass3RunOnGpu brhs = (OuterClass3RunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
