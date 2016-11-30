/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class GpuListTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      ret.add(new GpuListRunOnGpu());
    }
    return ret;    
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    GpuListRunOnGpu glhs = (GpuListRunOnGpu) lhs;
    GpuListRunOnGpu grhs = (GpuListRunOnGpu) rhs;
    
    if(glhs.getList().size() != glhs.getList().size())
      return false;
    for(int i = 0; i < glhs.getList().size(); ++i){
      Item first = glhs.getList().get(i);
      Item second = grhs.getList().get(i);
      if(first.getValue() != second.getValue())
        return false;
    }
    return true;    
  }

  
}
