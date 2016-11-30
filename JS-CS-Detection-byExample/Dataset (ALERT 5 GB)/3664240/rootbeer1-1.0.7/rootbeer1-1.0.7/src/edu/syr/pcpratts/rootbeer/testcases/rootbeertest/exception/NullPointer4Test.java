/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

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
