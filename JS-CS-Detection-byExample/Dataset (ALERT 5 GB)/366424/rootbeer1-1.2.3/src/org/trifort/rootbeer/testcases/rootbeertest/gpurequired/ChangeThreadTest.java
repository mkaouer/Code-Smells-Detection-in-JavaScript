/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.test.TestSerialization;

public class ChangeThreadTest implements TestSerialization {

  private Rootbeer m_rootbeer;
  
  public List<Kernel> create() {
    CreateRootbeerThread creator = new CreateRootbeerThread();
    Thread t = new Thread(creator);
    t.start();
    try { 
      t.join();
    } catch(Exception ex){
      ex.printStackTrace();
    }
    m_rootbeer = creator.getRootbeer();
    
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 10; ++i){
      ret.add(new ChangeThreadRunOnGpu());
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    ChangeThreadRunOnGpu lhs = (ChangeThreadRunOnGpu) original;
    ChangeThreadRunOnGpu rhs = (ChangeThreadRunOnGpu) from_heap;
    return lhs.compare(rhs);
  }
  
  private class CreateRootbeerThread implements Runnable {
    private Rootbeer m_rootbeer;
    public void run() {
      m_rootbeer = new Rootbeer();
    }
    public Rootbeer getRootbeer(){
      return m_rootbeer;
    }
  }
}
