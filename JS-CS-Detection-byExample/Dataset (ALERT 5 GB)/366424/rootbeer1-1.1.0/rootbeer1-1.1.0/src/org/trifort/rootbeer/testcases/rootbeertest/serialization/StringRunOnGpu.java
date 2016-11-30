/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

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
      System.out.println("lhs str: ["+str.toCharArray()+"]");
      System.out.println("rhs str: ["+brhs.str.toCharArray()+"]");
      System.out.println("lhs length: "+str.length());
      System.out.println("rhs length: "+brhs.str.length());
      return false;
    }
    return true;
  }
}
