/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestException;

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
    if(thrwbl instanceof ExceptionTestException)
      return true;
    return false;
  }
}