/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.cpu;

import edu.syr.pcpratts.rootbeer.runtime.ParallelRuntime;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CpuRuntime implements ParallelRuntime {

  private static CpuRuntime mInstance = null;
  private List<CpuCore> m_Cores;

  public static CpuRuntime v(){
    if(mInstance == null)
      mInstance = new CpuRuntime();
    return mInstance;
  }

  private CpuRuntime(){
    m_Cores = new ArrayList<CpuCore>();
    int num_cores = Runtime.getRuntime().availableProcessors();
    for(int i = 0; i < num_cores; ++i){
      m_Cores.add(new CpuCore());
    }
  }

  public PartiallyCompletedParallelJob run(Iterator<Kernel> jobs) throws Exception {
    PartiallyCompletedParallelJob ret = new PartiallyCompletedParallelJob(jobs);
    int enqueued = 0;
    for(int i = 0; i < m_Cores.size(); ++i){
      if(jobs.hasNext()){
        Kernel job = jobs.next();
        m_Cores.get(i).enqueue(job);
        enqueued++;
      }
    }

    for(int i = 0; i < enqueued; ++i){
      Kernel curr = m_Cores.get(i).getResult();
      ret.enqueueJob(curr);
    }
    return ret;
  }

  public boolean isGpuPresent() {
    return true;
  }


}
