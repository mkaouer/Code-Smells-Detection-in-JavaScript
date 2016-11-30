/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class String2RunOnGpu implements Kernel {
  private String str;
  
  public String2RunOnGpu(){
    str = "hello";
  }

  @Override
  public void gpuMethod() {
    StringBuilder builder = new StringBuilder(str);
    builder.append(" world");
    builder.append("!");
    builder.append("!");
    builder.append("!");
    builder.append("!");
    builder.append("!");
    str = builder.toString();
  }

  boolean compare(String2RunOnGpu brhs) {
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
