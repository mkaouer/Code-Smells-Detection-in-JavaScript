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


public class FastMatrixTest implements TestKernelTemplate {

  private int m_blockSize;
  private int m_gridSize;
  public FastMatrixTest(){ 
    m_blockSize = 64;
    m_gridSize = 64*14;
  }

  public Kernel create() {
    int[] a = new int[m_blockSize*m_blockSize];
    int[] b = new int[m_blockSize*m_blockSize*m_gridSize];
    int[] c = new int[m_blockSize*m_blockSize*m_gridSize];

    for(int i = 0; i < a.length; ++i){
      a[i] = i;
    }

    for(int i = 0; i < b.length; ++i){
      b[i] = i;
    }
    Kernel ret = new MatrixKernel(a, b, c, m_blockSize, m_gridSize);
    return ret;
  }

  public ThreadConfig getThreadConfig() {
    ThreadConfig ret = new ThreadConfig(m_blockSize, m_gridSize, m_blockSize * m_gridSize);
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    MatrixKernel lhs = (MatrixKernel) original;
    MatrixKernel rhs = (MatrixKernel) from_heap;
    return lhs.compare(rhs);
  }

}
