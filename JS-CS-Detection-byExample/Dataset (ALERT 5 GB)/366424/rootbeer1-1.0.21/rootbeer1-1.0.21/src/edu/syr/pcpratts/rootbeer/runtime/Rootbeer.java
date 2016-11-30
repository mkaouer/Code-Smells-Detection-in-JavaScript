/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.Iterator;
import java.util.List;

public class Rootbeer implements IRootbeer {

  private IRootbeer m_Rootbeer;
  
  public Rootbeer(){
    RootbeerFactory factory = new RootbeerFactory();
    m_Rootbeer = factory.create();
  }

  public void runAll(List<Kernel> jobs) {
    if(jobs.isEmpty()){
      return;
    }
    if(jobs.get(0) instanceof CompiledKernel == false){
      for(Kernel job : jobs){
        job.gpuMethod();
      }
    } else {
      m_Rootbeer.runAll(jobs);
    }
  }

  public Iterator<Kernel> run(Iterator<Kernel> jobs) {
    return m_Rootbeer.run(jobs);
  }
  
  public long getExecutionTime() {
    return m_Rootbeer.getExecutionTime();  
  }
  
  public long getSerializationTime() {
    return m_Rootbeer.getSerializationTime();
  }
  
  public long getDeserializationTime() {
    return m_Rootbeer.getDeserializationTime();
  }
}
