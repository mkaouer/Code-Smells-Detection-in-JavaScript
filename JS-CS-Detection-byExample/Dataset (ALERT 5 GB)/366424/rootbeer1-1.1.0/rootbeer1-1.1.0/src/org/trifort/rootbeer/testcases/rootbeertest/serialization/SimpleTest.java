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

public class SimpleTest implements TestSerialization {

  public SimpleTest() {
  }

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 1; ++i){
      ret.add(new SimpleTestRunOnGpu(i));
    }
    return ret;
  }

  @Override
  public boolean compare(Kernel rlhs, Kernel rrhs) {
    SimpleTestRunOnGpu lhs = (SimpleTestRunOnGpu) rlhs;
    SimpleTestRunOnGpu rhs = (SimpleTestRunOnGpu) rrhs;
    
    if(lhs.getValue() == rhs.getValue())
      return true;
    
    System.out.println("known good: "+lhs.getValue());
    System.out.println("gpu value: "+rhs.getValue());
    return false;
  }
  
}
