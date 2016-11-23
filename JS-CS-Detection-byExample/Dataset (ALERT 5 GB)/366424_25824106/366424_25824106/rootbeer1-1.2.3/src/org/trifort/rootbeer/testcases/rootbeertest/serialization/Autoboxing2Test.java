package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class Autoboxing2Test implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 1; ++i){
      ret.add(new Autoboxing2RunOnGpu());
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    Autoboxing2RunOnGpu lhs = (Autoboxing2RunOnGpu) original;
    Autoboxing2RunOnGpu rhs = (Autoboxing2RunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
  
}
