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

public class SameClassUsedTwiceTest1 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      SameClassUsedTwiceRunOnGpu1 curr = new SameClassUsedTwiceRunOnGpu1();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    SameClassUsedTwiceRunOnGpu1 blhs = (SameClassUsedTwiceRunOnGpu1) lhs;
    SameClassUsedTwiceRunOnGpu1 brhs = (SameClassUsedTwiceRunOnGpu1) rhs;
    return blhs.compare(brhs);
  }
}
