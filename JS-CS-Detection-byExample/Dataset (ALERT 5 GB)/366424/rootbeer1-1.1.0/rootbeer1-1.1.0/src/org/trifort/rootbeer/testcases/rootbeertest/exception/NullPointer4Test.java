/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.exception;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestException;
import org.trifort.rootbeer.test.TestSerialization;

public class NullPointer4Test implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 4096; ++i){
      NullPointer4RunOnGpu curr = new NullPointer4RunOnGpu();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel rog, Kernel rog1) {
    NullPointer4RunOnGpu lhs = (NullPointer4RunOnGpu) rog;
    NullPointer4RunOnGpu rhs = (NullPointer4RunOnGpu) rog1;
    return lhs.compare(rhs);
  }

  
}
