package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class ForceArrayNewTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 1; ++i){
      ret.add(new ForceArrayNewRunOnGpu());
    }
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    ForceArrayNewRunOnGpu lhs = (ForceArrayNewRunOnGpu) original;
    ForceArrayNewRunOnGpu rhs = (ForceArrayNewRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
