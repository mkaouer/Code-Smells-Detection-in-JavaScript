/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.nativecpu;

import edu.syr.pcpratts.rootbeer.runtime.gpu.GcHeap;
import edu.syr.pcpratts.rootbeer.runtime.gpu.GpuDevice;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicMemory;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicSwappedMemory;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicUnswappedMemory;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import java.util.ArrayList;
import java.util.List;

public class NativeCpuGcHeap extends GcHeap {

  public NativeCpuGcHeap(GpuDevice device){
    super(device);  
    allocateMemory();
  }
  
  @Override
  protected void allocateMemory() {
    mBufferSize = 128*1024*1024L;
    mToSpaceMemory = new BasicSwappedMemory(mBufferSize);
    mTextureMemory = new BasicSwappedMemory(mBufferSize);
    mHandlesMemory = new BasicSwappedMemory(mBufferSize);
    mHeapEndPtrMemory = new BasicSwappedMemory(8);
    mGcInfoSpaceMemory = new BasicSwappedMemory(mGcInfoSpaceSize);
    mExceptionsMemory = new BasicSwappedMemory(mBufferSize);
  }

  @Override
  protected void makeSureReadyForUsingGarbageCollector() {
    
  }
 
  public List<Memory> getMemory(){
    List<Memory> ret = new ArrayList<Memory>();
    ret.add(mToSpaceMemory);
    ret.add(mHandlesMemory);
    ret.add(mHeapEndPtrMemory);
    ret.add(mGcInfoSpaceMemory);
    ret.add(mExceptionsMemory);
    return ret;
  }
}