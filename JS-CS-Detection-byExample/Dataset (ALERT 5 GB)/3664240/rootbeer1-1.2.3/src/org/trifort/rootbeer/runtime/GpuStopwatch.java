/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public class GpuStopwatch {
  
  private long m_start;
  private long m_stop;
  
  public void start(){
  }
  
  public void stop(){ 
  }
  
  /**
   * @return the time in clock cycles 
   */
  public long getTime(){
    return m_stop - m_start;
  }
}
