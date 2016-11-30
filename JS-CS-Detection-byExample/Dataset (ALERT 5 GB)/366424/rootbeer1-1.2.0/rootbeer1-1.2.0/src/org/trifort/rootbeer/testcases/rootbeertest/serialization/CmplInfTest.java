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

public class CmplInfTest implements TestSerialization {

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    ret.add(new CmplInfRunOnGpu(Double.NEGATIVE_INFINITY));
    ret.add(new CmplInfRunOnGpu(Double.POSITIVE_INFINITY));
    ret.add(new CmplInfRunOnGpu(-10.0));
    ret.add(new CmplInfRunOnGpu(10.0));
    ret.add(new CmplInfRunOnGpu(0.0));
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    CmplInfRunOnGpu lhs = (CmplInfRunOnGpu) original;
    CmplInfRunOnGpu rhs = (CmplInfRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }

}
