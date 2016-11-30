package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;


public class ArraysSortTest implements TestSerialization {

  @Override
	public List<Kernel> create() {
	  List<Kernel> ret = new ArrayList<Kernel>();
	  for(int i = 0; i < 5; ++i){
	    ret.add(new ArraysSortRunOnGpu());
	  }
	  return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    ArraysSortRunOnGpu lhs = (ArraysSortRunOnGpu) original;
    ArraysSortRunOnGpu rhs = (ArraysSortRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}
