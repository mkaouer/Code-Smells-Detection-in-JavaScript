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

public class StringToIntegerTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 1; ++i) {
      ret.add(new StringToIntegerRunOnGpu(Integer.toString(i)));
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    StringToIntegerRunOnGpu lhs = (StringToIntegerRunOnGpu) original;
    StringToIntegerRunOnGpu rhs = (StringToIntegerRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
