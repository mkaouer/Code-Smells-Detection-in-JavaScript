/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.everything;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class EverythingTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    EverythingSynch synch = new EverythingSynch();
    for(int i = 0; i < 10; ++i){
      ret.add(new EverythingRunOnGpu(synch));
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
