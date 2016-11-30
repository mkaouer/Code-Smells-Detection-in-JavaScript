package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class StringSplitTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new StringSplitRunOnGpu("split this string"));
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    StringSplitRunOnGpu lhs = (StringSplitRunOnGpu) original;
    StringSplitRunOnGpu rhs = (StringSplitRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }

}
