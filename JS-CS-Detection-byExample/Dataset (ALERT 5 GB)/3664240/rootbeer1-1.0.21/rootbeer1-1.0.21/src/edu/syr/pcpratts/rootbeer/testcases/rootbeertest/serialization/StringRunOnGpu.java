/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class StringRunOnGpu implements Kernel {
  private int index;
  private String str;
  
  public StringRunOnGpu(int index){
    this.index = index;
    str = "hello";
  }

  @Override
  public void gpuMethod() {
    str += " world";
    //for(int i = 0; i < 5; ++i){
    //  str += "!";
    //}
  }

  boolean compare(StringRunOnGpu brhs) {
    if(str.equals(brhs.str) == false){
      System.out.println(str);
      System.out.println(brhs.str);
      return false;
    }
    return true;
  }
}
