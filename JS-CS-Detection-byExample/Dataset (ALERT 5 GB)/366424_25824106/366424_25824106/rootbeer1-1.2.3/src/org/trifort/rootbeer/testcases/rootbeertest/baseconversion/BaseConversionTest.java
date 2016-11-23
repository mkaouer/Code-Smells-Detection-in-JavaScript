/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.baseconversion;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.entry.Aug4th2011PerformanceStudy;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.test.TestSerialization;

public class BaseConversionTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    int job_size = 5000000;
    int count = 1221;
    for(int i = 0; i < job_size; i += count){
      BaseConversionRunOnGpu curr = new BaseConversionRunOnGpu(i, count);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    BaseConversionRunOnGpu blhs = (BaseConversionRunOnGpu) lhs;
    BaseConversionRunOnGpu brhs = (BaseConversionRunOnGpu) rhs;
    if(blhs.getRet() == null){
      System.out.println("blhs.getRet() == nill");
      return false;
    }
    if(brhs.getRet() == null){
      System.out.println("brhs.getRet() == nill");
      return false;
    }
    for(int j = 0; j < blhs.getRet().size(); ++j){
      IntList lhs_list = (IntList) blhs.getRet().get(j);
      IntList rhs_list = (IntList) brhs.getRet().get(j);
      for(int i = 0; i < lhs_list.size(); ++i){
        int lhs_value = lhs_list.get(i);
        int rhs_value = rhs_list.get(i);
        if(lhs_value != rhs_value){
          System.out.println("i: "+i+" lhs: "+lhs_value+" rhs: "+rhs_value);
          return false;
        }
      }
    }
    return true;
  }
}
