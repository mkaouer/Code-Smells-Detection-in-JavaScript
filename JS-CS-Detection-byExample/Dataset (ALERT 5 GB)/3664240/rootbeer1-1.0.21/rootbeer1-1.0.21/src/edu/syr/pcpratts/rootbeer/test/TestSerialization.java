/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.test;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public interface TestSerialization {

  List<Kernel> create();
  boolean compare(Kernel original, Kernel from_heap);
  
}
