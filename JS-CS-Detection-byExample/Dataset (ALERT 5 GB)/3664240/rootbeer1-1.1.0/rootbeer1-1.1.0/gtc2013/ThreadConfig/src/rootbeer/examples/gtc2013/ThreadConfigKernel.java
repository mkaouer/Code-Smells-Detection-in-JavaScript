package rootbeer.examples.gtc2013;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;

public class ThreadConfigKernel implements Kernel {

  private int m_threadIdxx;
  private int m_blockIdxx;
  private int m_blockDimx;
  private int m_threadId;

  public void gpuMethod(){
    m_threadIdxx = RootbeerGpu.getThreadIdxx();
    m_blockIdxx = RootbeerGpu.getBlockIdxx();
    m_blockDimx = RootbeerGpu.getBlockDimx();
    m_threadId = RootbeerGpu.getThreadId();
  }

  public void print(){
    System.out.println("thread_id: "+m_threadId);
    System.out.println("thread_idxx: "+m_threadIdxx);
    System.out.println("block_idxx: "+m_blockIdxx);
    System.out.println("block_dimx: "+m_blockDimx);
  }
}

