/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

public class SynchronizedMethodObject {

  public int value;

  public synchronized void increment(boolean recurse) {
    if(recurse){
      increment(false);
    } else {
      value++; 
    }
  }
}
