/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.gpu;

import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.Iterator;

public interface GpuDevice {

  public GcHeap CreateHeap();
  public long getMaxEnqueueSize();
  public long getNumBlocks();
  public void flushQueue();  
  public PartiallyCompletedParallelJob run(Iterator<Kernel> blocks);  
  public long getMaxMemoryAllocSize();
  public long getGlobalMemSize();
}
