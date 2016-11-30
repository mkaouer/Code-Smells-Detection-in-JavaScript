/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.memory;

public interface DeviceMemory {

  public void read(byte[] curr_block, long index, int size);
  public void write(byte[] curr_block, long index, int size);
  
}
