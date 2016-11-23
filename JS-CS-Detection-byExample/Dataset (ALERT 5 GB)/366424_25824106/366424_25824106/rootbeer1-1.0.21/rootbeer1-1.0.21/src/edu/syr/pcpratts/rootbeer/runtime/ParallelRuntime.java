/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.Iterator;

public interface ParallelRuntime {

  public PartiallyCompletedParallelJob run(Iterator<Kernel> blocks) throws Exception;
  public boolean isGpuPresent();
}
