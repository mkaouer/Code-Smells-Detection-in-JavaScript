/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class ExceptionBasicTest implements TestException {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    int size = 4096;
    size = 10;
    for(int i = 0; i < size; ++i){
      ExceptionBasicRunOnGpu curr = new ExceptionBasicRunOnGpu(i);
      jobs.add(curr);
    }
    return jobs;
  }
  
  @Override
  public boolean catchException(Throwable thrwbl) {
    if(thrwbl instanceof RuntimeException)
      return true;
    return false;
  }
}