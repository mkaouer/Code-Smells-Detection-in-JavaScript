/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.DoubleToStringKernelTemplateBuilderTest;
import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.DoubleToStringKernelTemplateTest;
import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.FastMatrixTest;
import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.GpuParametersTest;
import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.GpuVectorMapTest2;

public class KernelTemplateMain implements TestKernelTemplateFactory {

  public List<TestKernelTemplate> getProviders() {
    List<TestKernelTemplate> ret = new ArrayList<TestKernelTemplate>();
    //ret.add(new FastMatrixTest());
    ret.add(new DoubleToStringKernelTemplateTest());
    ret.add(new DoubleToStringKernelTemplateBuilderTest());
    ret.add(new GpuParametersTest());
    ret.add(new GpuVectorMapTest2());
    return ret;
  }

}
