package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class GpuVectorMapTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new GpuVectorMapRunOnGpu(1));
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    GpuVectorMapRunOnGpu lhs = (GpuVectorMapRunOnGpu) original;
    GpuVectorMapRunOnGpu rhs = (GpuVectorMapRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
