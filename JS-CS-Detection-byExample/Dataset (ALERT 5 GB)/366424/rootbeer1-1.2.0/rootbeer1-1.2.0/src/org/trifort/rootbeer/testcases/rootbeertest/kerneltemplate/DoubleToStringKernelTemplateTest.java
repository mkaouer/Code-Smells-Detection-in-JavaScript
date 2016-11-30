package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.test.TestKernelTemplate;

public class DoubleToStringKernelTemplateTest implements TestKernelTemplate {
  
  private int m_blockSize;
  private int m_gridSize;
  
  public DoubleToStringKernelTemplateTest(){ 
    m_blockSize = 5;
    m_gridSize = 1;
  }
  
  @Override
  public Kernel create() {
    return new DoubleToStringKernelTemplateRunOnGpu(0.125, m_blockSize * m_gridSize);
  }
  
  @Override
  public ThreadConfig getThreadConfig() {
    return new ThreadConfig(m_blockSize, m_gridSize, m_blockSize * m_gridSize);
  }
  
  @Override
  public boolean compare(Kernel original, Kernel from_heap) {
    DoubleToStringKernelTemplateRunOnGpu lhs = (DoubleToStringKernelTemplateRunOnGpu) original;
    DoubleToStringKernelTemplateRunOnGpu rhs = (DoubleToStringKernelTemplateRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
}