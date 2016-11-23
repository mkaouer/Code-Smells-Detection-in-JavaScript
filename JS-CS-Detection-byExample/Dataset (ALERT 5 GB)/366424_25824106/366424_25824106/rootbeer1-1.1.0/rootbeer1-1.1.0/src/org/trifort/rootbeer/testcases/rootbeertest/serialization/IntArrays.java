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

public class IntArrays implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      IntArraysRunOnGpu curr = new IntArraysRunOnGpu(i);
      curr.initArray3();
      jobs.add(curr);
    }
    return jobs;
  }

  public boolean compare(Kernel original, Kernel read) {
    IntArraysRunOnGpu lhs = (IntArraysRunOnGpu) original;
    IntArraysRunOnGpu rhs = (IntArraysRunOnGpu) read;

    return lhs.compare(rhs);
  }
}
