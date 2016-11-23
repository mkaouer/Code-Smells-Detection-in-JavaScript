/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.ArrayList;
import java.util.List;

public class CpuRunner {

  private int m_NumCores;
  private List<CpuRunnerCore> m_Cores;
  
  public CpuRunner(){   
    m_NumCores = Runtime.getRuntime().availableProcessors(); 
    m_Cores = new ArrayList<CpuRunnerCore>();
    for(int i = 0; i < m_NumCores; ++i){
      m_Cores.add(new CpuRunnerCore());
    }
  }

  public void run(List<Kernel> cpu_jobs) {
    int items_per = cpu_jobs.size() / m_NumCores;
    for(int i = 0; i < m_NumCores; ++i){
      int end_index;
      if(i == m_NumCores - 1){
        end_index = cpu_jobs.size();
      } else {
        end_index = (i+1)*items_per;
      }
      List<Kernel> jobs = cpu_jobs.subList(i*items_per, end_index);
      m_Cores.get(i).put(jobs);
    }
  }

  public void join() {
    for(int i = 0; i < m_NumCores; ++i){
      m_Cores.get(i).take();
    }
  }
}
