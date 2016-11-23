package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class EmptyKernel implements Kernel {

  public EmptyKernel() {
  }

  public void gpuMethod() {
  }

  public boolean compare(EmptyKernel rhs) {
    return true;
  }
}
