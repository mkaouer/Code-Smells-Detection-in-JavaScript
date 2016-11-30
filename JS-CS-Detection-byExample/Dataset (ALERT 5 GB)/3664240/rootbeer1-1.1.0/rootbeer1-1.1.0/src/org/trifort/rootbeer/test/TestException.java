/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;

public interface TestException {
  
  List<Kernel> create();
  boolean catchException(Throwable ex);
  
}
