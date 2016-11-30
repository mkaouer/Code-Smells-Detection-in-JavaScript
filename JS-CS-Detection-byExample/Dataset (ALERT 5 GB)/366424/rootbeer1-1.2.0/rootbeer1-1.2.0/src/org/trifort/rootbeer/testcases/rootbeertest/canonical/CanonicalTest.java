package org.trifort.rootbeer.testcases.rootbeertest.canonical;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class CanonicalTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new CanonicalKernel());
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    CanonicalKernel lhs = (CanonicalKernel) original;
    CanonicalKernel rhs = (CanonicalKernel) from_heap;
    return lhs.compare(rhs);
  }

}
