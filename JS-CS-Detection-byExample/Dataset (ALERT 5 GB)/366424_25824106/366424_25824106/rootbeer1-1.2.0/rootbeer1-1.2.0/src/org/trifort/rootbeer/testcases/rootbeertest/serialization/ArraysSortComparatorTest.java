package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class ArraysSortComparatorTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new ArraysSortComparatorRunOnGpu());
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    ArraysSortComparatorRunOnGpu lhs = (ArraysSortComparatorRunOnGpu) original;
    ArraysSortComparatorRunOnGpu rhs = (ArraysSortComparatorRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
