package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class AtomicGlobalsTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    int[] intArray = new int[3];
    long[] longArray = new long[3];
    float[] floatArray = new float[3];
    int[] intArray2 = new int[1];
    long[] longArray2 = new long[1];
    float[] floatArray2 = new float[1];
    
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 10; ++i){
      Kernel kernel = new AtomicGlobalsRunOnGpu(intArray, longArray, floatArray,
          intArray2, longArray2, floatArray2);
      ret.add(kernel);
    }
    return ret;
  }

  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    AtomicGlobalsRunOnGpu lhs = (AtomicGlobalsRunOnGpu) original;
    AtomicGlobalsRunOnGpu rhs = (AtomicGlobalsRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }

}
