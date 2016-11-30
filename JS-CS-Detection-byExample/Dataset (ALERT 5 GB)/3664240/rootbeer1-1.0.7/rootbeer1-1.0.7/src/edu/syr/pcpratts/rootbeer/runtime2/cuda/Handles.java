/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

public class Handles {
  
  private long m_CpuAddr;
  private long m_GpuAddr;
  
  public Handles(long cpu_base_address, long gpu_base_address){
    m_CpuAddr = cpu_base_address;
    m_GpuAddr = gpu_base_address;
    setup(cpu_base_address, gpu_base_address);
  }

  public void activate(){
    setup(m_CpuAddr, m_GpuAddr);
  }
  
  private native void setup(long cpu_base_address, long gpu_base_address);
  
  public native void resetPointer();
  public native void writeLong(long value);
  public native long readLong();
}
