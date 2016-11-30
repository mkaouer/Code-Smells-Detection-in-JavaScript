/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class StrictMathTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      ret.add(new StrictMatchRunOnGpu());
    }
    return ret;    
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    StrictMatchRunOnGpu glhs = (StrictMatchRunOnGpu) lhs;
    StrictMatchRunOnGpu grhs = (StrictMatchRunOnGpu) rhs;
    
    if(glhs.compare(grhs) == false){
      return false;
    }
    return true;    
  }

  
}
