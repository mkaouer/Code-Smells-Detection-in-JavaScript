/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class MMult implements TestSerialization {

  @Override
  public List<Kernel> create() {
    int size = 4096;
    int[] a = new int[size*size];
    int[] b = new int[size*size];
    int[] c = new int[size*size];
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < size; ++i){
      MMultRunOnGpu curr = new MMultRunOnGpu(a, b, c, i, size);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    MMultRunOnGpu blhs = (MMultRunOnGpu) lhs;
    MMultRunOnGpu brhs = (MMultRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
