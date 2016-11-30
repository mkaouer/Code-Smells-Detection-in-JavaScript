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

public class StringToLongTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(long l = 0; l < 1; ++l) {
      ret.add(new StringToLongRunOnGpu(Long.toString(l)));
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    StringToLongRunOnGpu lhs = (StringToLongRunOnGpu) original;
    StringToLongRunOnGpu rhs = (StringToLongRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
