package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class ByteToStringTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new ByteToStringRunOnGpu());
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    ByteToStringRunOnGpu lhs = (ByteToStringRunOnGpu) original;
    ByteToStringRunOnGpu rhs = (ByteToStringRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }

}
