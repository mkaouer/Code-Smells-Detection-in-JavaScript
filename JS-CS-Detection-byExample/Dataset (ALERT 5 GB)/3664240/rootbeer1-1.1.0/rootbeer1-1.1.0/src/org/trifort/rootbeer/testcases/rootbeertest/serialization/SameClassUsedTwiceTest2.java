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

public class SameClassUsedTwiceTest2 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      SameClassUsedTwiceRunOnGpu2 curr = new SameClassUsedTwiceRunOnGpu2();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SameClassUsedTwiceRunOnGpu2 blhs = (SameClassUsedTwiceRunOnGpu2) lhs;
    SameClassUsedTwiceRunOnGpu2 brhs = (SameClassUsedTwiceRunOnGpu2) rhs;
    return blhs.compare(brhs);
  }
}
