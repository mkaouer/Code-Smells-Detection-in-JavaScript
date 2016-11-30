/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestException;
import java.util.ArrayList;
import java.util.List;

public class NullPointer1Test implements TestException {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    int[] a = new int[4096];
    for(int i = 0; i < 4096; ++i){
      NullPointer1RunOnGpu curr = new NullPointer1RunOnGpu(a, i);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean catchException(Throwable thrwbl) {
    if(thrwbl instanceof NullPointerException){
      return true; 
    }
    return false;
  }
  
}
