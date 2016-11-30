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

public class NullPointer2Test implements TestException {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 4096; ++i){
      NullPointer2RunOnGpu curr = new NullPointer2RunOnGpu();
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
