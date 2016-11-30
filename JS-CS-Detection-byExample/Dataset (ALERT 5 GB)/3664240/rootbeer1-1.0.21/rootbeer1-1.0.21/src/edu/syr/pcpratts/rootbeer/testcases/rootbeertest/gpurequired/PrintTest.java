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

public class PrintTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    System.out.println("hello world");
    for(int i = 0; i < 3; ++i){
      ret.add(new PrintRunOnGpu());
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    PrintRunOnGpu lhs = (PrintRunOnGpu) original;
    PrintRunOnGpu rhs = (PrintRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }

}
