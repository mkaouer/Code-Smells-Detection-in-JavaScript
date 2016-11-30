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

public class BruteForceFFTTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    int size = 114688;
    float[] a = new float[size];
    float[] b = new float[size];
    for(int i = 0; i < size; ++i){
      a[i] = (short) i;
    }
    List<Kernel> jobs = new ArrayList<Kernel>();
    int num = 256;
    for(int i = 0; i < size; i += num){
      int count = num;
      if(i + num > a.length){
        count = i + num - a.length;
        count--;
      }
      BruteForceFFTRunOnGpu curr = new BruteForceFFTRunOnGpu(a, b, i, count);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    BruteForceFFTRunOnGpu blhs = (BruteForceFFTRunOnGpu) lhs;
    BruteForceFFTRunOnGpu brhs = (BruteForceFFTRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
