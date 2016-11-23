/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.gpu;

public class MultiArrayCount {

  private int m_AllocCount;
  private long m_AllocSize;
  
  public MultiArrayCount(long total_size, long max_alloc_size){
    int alignment = 8;
    long total_blocks = align(total_size, alignment);
    m_AllocSize = 256*1024*1024L;
    m_AllocCount = ((int) (total_blocks / m_AllocSize)) - 2;
  }
  
  private long align(long size, long alignment){
    long mod = size % alignment;
    return size - mod;
  }
  
  public int getAllocCount(){
    return m_AllocCount;
  }
  
  public long getAllocSize(){
    return m_AllocSize;
  }
}