/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class CpuRunnerCore implements Runnable {
  
  private BlockingQueue<List<Kernel>> m_InputQueue;
  private BlockingQueue<List<Kernel>> m_OutputQueue;
  private Thread m_Thread;
  
  public CpuRunnerCore(){
    m_InputQueue = new BlockingQueue<List<Kernel>>();
    m_OutputQueue = new BlockingQueue<List<Kernel>>();
    m_Thread = new Thread(this);
    m_Thread.setDaemon(true);
    m_Thread.start();
  }
  
  public void put(List<Kernel> jobs) {
    m_InputQueue.put(jobs);
  }

  public void take() {
    m_OutputQueue.take();
  }

  public void run() {
    while(true){
      List<Kernel> jobs = m_InputQueue.take();
      runJobs(jobs);
      m_OutputQueue.put(jobs);
    }
  }

  private void runJobs(List<Kernel> jobs) {
    for(Kernel job : jobs){
      job.gpuMethod();
    }
  }  
  
}
