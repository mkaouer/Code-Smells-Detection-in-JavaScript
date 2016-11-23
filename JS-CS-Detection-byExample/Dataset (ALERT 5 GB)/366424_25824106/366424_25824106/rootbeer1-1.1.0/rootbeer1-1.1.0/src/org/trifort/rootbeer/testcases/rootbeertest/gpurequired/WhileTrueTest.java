/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class WhileTrueTest implements TestSerialization{

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    AtomicLong along = new AtomicLong();
    for(int i = 0; i < 5; ++i){
      Kernel kernel = new WhileTrueRunOnGpu(along, i);
      ret.add(kernel);
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_gpu) {
    WhileTrueRunOnGpu lhs = (WhileTrueRunOnGpu) original;
    WhileTrueRunOnGpu rhs = (WhileTrueRunOnGpu) from_gpu;
    if(lhs.get().get() != rhs.get().get()){
      System.out.println("lhs: "+lhs.get().get());
      System.out.println("rhs: "+rhs.get().get());
      return false;
    }
    return true;
  }
  
}
