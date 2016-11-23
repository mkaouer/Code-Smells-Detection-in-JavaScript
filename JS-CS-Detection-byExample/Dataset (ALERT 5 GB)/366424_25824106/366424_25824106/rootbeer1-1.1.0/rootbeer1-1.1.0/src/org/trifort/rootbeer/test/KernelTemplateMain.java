/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.FastMatrixTest;

public class KernelTemplateMain implements TestKernelTemplateFactory {

  public List<TestKernelTemplate> getProviders() {
    List<TestKernelTemplate> ret = new ArrayList<TestKernelTemplate>();
    //ret.add(new FastMatrixTest());
    return ret;
  }

}
