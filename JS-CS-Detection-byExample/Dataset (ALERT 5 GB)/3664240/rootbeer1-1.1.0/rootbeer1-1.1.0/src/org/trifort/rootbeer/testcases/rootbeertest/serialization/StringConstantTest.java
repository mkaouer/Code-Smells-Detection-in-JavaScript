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

public class StringConstantTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 20; ++i){
      ret.add(new StringConstantKernel());
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    StringConstantKernel lhs = (StringConstantKernel) original;
    StringConstantKernel rhs = (StringConstantKernel) from_heap;
    return lhs.compare(rhs);
  }

}
