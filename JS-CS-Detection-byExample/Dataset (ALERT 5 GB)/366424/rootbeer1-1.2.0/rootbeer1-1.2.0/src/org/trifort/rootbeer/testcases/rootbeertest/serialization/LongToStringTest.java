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

public class LongToStringTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(long l = 0; l < 5; ++l){
      ret.add(new LongToStringRunOnGpu(l));
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    LongToStringRunOnGpu lhs = (LongToStringRunOnGpu) original;
    LongToStringRunOnGpu rhs = (LongToStringRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
