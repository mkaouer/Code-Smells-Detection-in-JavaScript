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
  private String str;
  
  public StringRunOnGpu(){
    str = "hello";
  }

  @Override
  public void gpuMethod() {
    str += " world";
    for(int i = 0; i < 1; ++i){
      str += "!";
    }
  }

  boolean compare(StringRunOnGpu brhs) {
    if(str.equals(brhs.str) == false){
      System.out.println("lhs str: ["+str+"]");
      System.out.println("rhs str: ["+brhs.str+"]");
      System.out.println("lhs length: "+str.length());
      System.out.println("rhs length: "+brhs.str.length());
      return false;
    }
    return true;
  }
}
