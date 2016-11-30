package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class GpuParametersRunOnGpu implements Kernel {

  private int[] m_threadIds;
  private int[] m_threadIdxxs;
  private int[] m_blockIdxxs;
  private int[] m_blockDims;
  private long[] m_gridDims;
  
  public GpuParametersRunOnGpu(int thread_count){
    m_threadIds = new int[thread_count];
    m_threadIdxxs = new int[thread_count];
    m_blockIdxxs = new int[thread_count];
    m_blockDims = new int[thread_count];
    m_gridDims = new long[thread_count];
  }
  
  @Override
  public void gpuMethod() {
    int thread_id = RootbeerGpu.getThreadId();
    m_threadIds[thread_id] = thread_id;
    m_threadIdxxs[thread_id] = RootbeerGpu.getThreadIdxx();
    m_blockIdxxs[thread_id] = RootbeerGpu.getBlockIdxx();
    m_blockDims[thread_id] = RootbeerGpu.getBlockDimx();
    m_gridDims[thread_id] = RootbeerGpu.getGridDimx();
  }

  public boolean compare(GpuParametersRunOnGpu rhs) {
    if(m_threadIds.length != rhs.m_threadIds.length){
      return false;
    }    
    if(m_threadIdxxs.length != rhs.m_threadIdxxs.length){
      return false;
    }
    if(m_blockIdxxs.length != rhs.m_blockIdxxs.length){
      return false;
    }
    if(m_blockDims.length != rhs.m_blockDims.length){
      return false;
    }
    if(m_gridDims.length != rhs.m_gridDims.length){
      return false;
    }
    for(int i = 0; i < m_threadIds.length; ++i){
      int lhs_value = m_threadIds[i];
      int rhs_value = rhs.m_threadIds[i];
      if(lhs_value != rhs_value){
        return false;
      }
    }
    for(int i = 0; i < m_threadIdxxs.length; ++i){
      int lhs_value = m_threadIdxxs[i];
      int rhs_value = rhs.m_threadIdxxs[i];
      if(lhs_value != rhs_value){
        return false;
      }
    }
    for(int i = 0; i < m_blockIdxxs.length; ++i){
      int lhs_value = m_blockIdxxs[i];
      int rhs_value = rhs.m_blockIdxxs[i];
      if(lhs_value != rhs_value){
        return false;
      }
    }
    for(int i = 0; i < m_blockDims.length; ++i){
      int lhs_value = m_blockDims[i];
      int rhs_value = rhs.m_blockDims[i];
      if(lhs_value != rhs_value){
        return false;
      }
    }
    for(int i = 0; i < m_gridDims.length; ++i){
      long lhs_value = m_gridDims[i];
      long rhs_value = rhs.m_gridDims[i];
      if(lhs_value != rhs_value){
        return false;
      }
    }
    return true;
  }
}
