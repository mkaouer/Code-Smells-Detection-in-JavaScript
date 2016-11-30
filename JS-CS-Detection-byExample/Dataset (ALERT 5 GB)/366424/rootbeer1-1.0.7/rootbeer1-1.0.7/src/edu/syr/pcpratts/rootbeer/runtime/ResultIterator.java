/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.Iterator;
import java.util.List;

public class ResultIterator implements Iterator<Kernel> {

  private Iterator<Kernel> m_CurrIter;
  private Iterator<Kernel> m_JobsToEnqueue;
  private ParallelRuntime m_Runtime;

  public ResultIterator(PartiallyCompletedParallelJob partial, ParallelRuntime runtime){
    readPartial(partial);
    m_Runtime = runtime;
  }

  private void readPartial(PartiallyCompletedParallelJob partial){
    List<Kernel> active_jobs = partial.getActiveJobs();
    m_CurrIter = active_jobs.iterator();
    m_JobsToEnqueue = partial.getJobsToEnqueue();
  }

  public boolean hasNext() {
    if(m_CurrIter.hasNext())
      return true;
    if(m_JobsToEnqueue.hasNext() == false)
      return false;
    try {
      readPartial(m_Runtime.run(m_JobsToEnqueue));
    } catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
    return m_CurrIter.hasNext();
  }

  public Kernel next() {
    return m_CurrIter.next();
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
