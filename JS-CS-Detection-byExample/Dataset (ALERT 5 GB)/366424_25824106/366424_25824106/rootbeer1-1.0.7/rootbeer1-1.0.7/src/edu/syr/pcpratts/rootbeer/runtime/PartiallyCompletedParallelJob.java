/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PartiallyCompletedParallelJob {

  private Iterator<Kernel> m_RemainingJobs;
  private List<Kernel> m_ActiveJobs;
  private List<Kernel> m_NotWritten;

  public PartiallyCompletedParallelJob(Iterator<Kernel> remaining_jobs) {
    m_RemainingJobs = remaining_jobs;
    m_ActiveJobs = new LinkedList<Kernel>();
    m_NotWritten = new ArrayList<Kernel>();
  }

  public List<Kernel> getActiveJobs() {
    return m_ActiveJobs;
  }

  public Iterator<Kernel> getJobsToEnqueue(){
    return new CompositeIterator(m_NotWritten, m_RemainingJobs);
  }

  public void enqueueJob(Kernel job){
    m_ActiveJobs.add(job);
  }

  public void enqueueJobs(List<Kernel> items) {
    m_ActiveJobs.addAll(items);
  }

  public void addNotWritten(List<Kernel> not_written) {
    m_NotWritten = new ArrayList<Kernel>();
    m_NotWritten.addAll(not_written);
  }
  
  public class CompositeIterator implements Iterator<Kernel> {

    private Iterator<Kernel> m_NotWritten;
    private Iterator<Kernel> m_Remaining;
    
    private CompositeIterator(List<Kernel> not_written, Iterator<Kernel> remaining) {
      m_NotWritten = not_written.iterator();
      m_Remaining = remaining;
    }    

    public boolean hasNext() {
      if(m_NotWritten.hasNext())
        return true;
      if(m_Remaining.hasNext())
        return true;
      return false;
    }

    public Kernel next() {
      if(m_NotWritten.hasNext())
        return m_NotWritten.next();
      if(m_Remaining.hasNext())
        return m_Remaining.next();
      throw new RuntimeException("out of items");
    }

    public void remove() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    
  }
}
