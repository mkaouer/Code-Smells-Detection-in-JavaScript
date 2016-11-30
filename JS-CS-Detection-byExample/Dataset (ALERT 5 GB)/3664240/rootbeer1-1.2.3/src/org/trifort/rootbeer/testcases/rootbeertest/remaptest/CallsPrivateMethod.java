/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.remaptest;

public class CallsPrivateMethod {

  private int privateGetNumber(){
    return 5;
  }
  
  public int getNumber(){
    return privateGetNumber();
  }
}
