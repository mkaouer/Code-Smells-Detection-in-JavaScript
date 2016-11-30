/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    AtomicLong along = new AtomicLong(0);
    Random random = new Random();
    
    //if I comment this out, the test case fails. this is because Math.random
    //initializes the static state of the random number generator.
    //when this is active, the first static pointer in memory is non null
    //going into the gpu. when it is not-active, it is null.
    //
    //also, making m_Num2 not used on the gpu makes the test case work
    System.out.println(Math.random());
    
    int size = 5;
    for(int i = 0; i < size; ++i){
      AtomicLongRunOnGpu curr = new AtomicLongRunOnGpu(along, random);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    AtomicLongRunOnGpu glhs = (AtomicLongRunOnGpu) lhs;
    AtomicLongRunOnGpu grhs = (AtomicLongRunOnGpu) rhs;
    return glhs.compare(grhs);
  }
}
