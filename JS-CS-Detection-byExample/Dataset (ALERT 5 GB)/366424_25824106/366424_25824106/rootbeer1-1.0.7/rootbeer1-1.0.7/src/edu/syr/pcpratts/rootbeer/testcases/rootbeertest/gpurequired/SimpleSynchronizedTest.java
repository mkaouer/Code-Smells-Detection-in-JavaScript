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

public class SimpleSynchronizedTest implements TestSerialization {

  public List<Kernel> create() {
    SimpleSynchronizedObject sync_obj = new SimpleSynchronizedObject();
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 20; ++i){
      Kernel kernel = new SimpleSynchronizedRunOnGpu(sync_obj);
      ret.add(kernel);
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_gpu) {
    SimpleSynchronizedRunOnGpu lhs = (SimpleSynchronizedRunOnGpu) original;
    SimpleSynchronizedRunOnGpu rhs = (SimpleSynchronizedRunOnGpu) from_gpu;
    
    if(lhs.getSyncObj().get() != rhs.getSyncObj().get()){
      System.out.println("lhs: "+lhs.getSyncObj().get());
      System.out.println("rhs: "+rhs.getSyncObj().get());
      return false;
    }
    
    return true;
  }
  
}
