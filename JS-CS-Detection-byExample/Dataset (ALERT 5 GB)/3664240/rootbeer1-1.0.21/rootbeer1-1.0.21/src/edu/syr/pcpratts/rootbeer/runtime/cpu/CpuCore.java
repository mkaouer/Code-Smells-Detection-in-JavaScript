/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.cpu;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

class CpuCore implements Runnable{

  private LinkedBlockingQueue<Kernel> m_InQueue;
  private LinkedBlockingQueue<Kernel> m_OutQueue;
  public CpuCore(){
    m_InQueue = new LinkedBlockingQueue<Kernel>();
    m_OutQueue = new LinkedBlockingQueue<Kernel>();
    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }

  public void run() {
    while(true){
      try {
        Kernel job = m_InQueue.take();
        job.gpuMethod();
        m_OutQueue.put(job);
      } catch(Exception ex){
        //ignore
      }
    }
  }

  void enqueue(Kernel job) {
    while(true){
      try {
        m_InQueue.put(job);
        return;
      } catch(Exception ex){
        //ignore
      }
    }
  }

  Kernel getResult() {
    while(true){
      try {
        return m_OutQueue.take();
      } catch(Exception ex){
        //ignore
      }
    }
  }
}
