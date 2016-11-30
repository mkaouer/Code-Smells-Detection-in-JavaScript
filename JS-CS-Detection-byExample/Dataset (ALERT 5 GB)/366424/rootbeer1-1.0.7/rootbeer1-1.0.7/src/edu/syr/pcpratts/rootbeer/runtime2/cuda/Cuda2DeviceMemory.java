/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.memory.DeviceMemory;

public class Cuda2DeviceMemory implements DeviceMemory {

  public Cuda2DeviceMemory(long cpu_addr, long gpu_addr){
    setup(cpu_addr, gpu_addr);
  }
  
  public native void read(byte[] curr_block, long index, int size);
  public native void write(byte[] curr_block, long index, int size);
  private native void setup(long cpu_addr, long gpu_addr);
}
