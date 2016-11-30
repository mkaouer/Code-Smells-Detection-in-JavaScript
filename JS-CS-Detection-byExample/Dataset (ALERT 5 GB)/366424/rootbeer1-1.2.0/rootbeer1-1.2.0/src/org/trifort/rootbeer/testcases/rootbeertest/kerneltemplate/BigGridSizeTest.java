/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.test.TestKernelTemplate;


public class BigGridSizeTest implements TestKernelTemplate {

  private int m_blockSize;
  private int m_gridSize;
  public BigGridSizeTest(){ 
    m_blockSize = 1024;
    m_gridSize = 1024;
  }

  public Kernel create() {
    Kernel ret = new EmptyKernel();
    return ret;
  }

  public ThreadConfig getThreadConfig() {
    ThreadConfig ret = new ThreadConfig(m_blockSize, m_gridSize, m_blockSize * m_gridSize);
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    EmptyKernel lhs = (EmptyKernel) original;
    EmptyKernel rhs = (EmptyKernel) from_heap;
    return lhs.compare(rhs);
  }

}
