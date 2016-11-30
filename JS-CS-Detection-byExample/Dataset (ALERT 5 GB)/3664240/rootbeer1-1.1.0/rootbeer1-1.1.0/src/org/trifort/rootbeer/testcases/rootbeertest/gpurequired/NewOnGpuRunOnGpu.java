/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;

public class NewOnGpuRunOnGpu implements Kernel {

  private long[] element1;
  private NewOnGpuRefObject[] test;
  //private long[][] element2;
  private NewOnGpuRefObject obj;
  
  public NewOnGpuRunOnGpu(){
    element1 = new long[10];
    test = new NewOnGpuRefObject[20];
    //element2 = new long[10][10];
    obj = null;
  }

  @Override
  public void gpuMethod() {
    element1 = new long[11];
    test = new NewOnGpuRefObject[44];
    for(int i = 0; i < 44; ++i){
      test[i] = new NewOnGpuRefObject();
    }
    //element2 = new long[50][50];
    obj = new NewOnGpuRefObject();
    
    int x = 20;
    switch(x){
      case 10:
        x = 40;
        break;
      case 20:
        x = 30;
        break;
    }
  }

  boolean compare(NewOnGpuRunOnGpu brhs) {
    if(brhs.obj == null){
      System.out.println("brhs.obj == null");
      return false;
    }
    if(brhs.element1 == null){
      System.out.println("brhs.element1 == null");
      return false;
    }
    if(brhs.test == null){
      System.out.println("brhs.test == null");
      return false;
    }
    if(obj.value != brhs.obj.value){
      System.out.println("obj.value mismatch");
      System.out.println("lhs: "+obj.value);
      System.out.println("rhs: "+brhs.obj.value);
      return false;
    }
    if(element1.length != brhs.element1.length){
      System.out.println("element1.length mismatch");
      System.out.println("lhs: "+element1.length);
      System.out.println("rhs: "+brhs.element1.length);
      return false;
    }
    if(test.length != brhs.test.length){
      System.out.println("test.length mismatch");
      System.out.println("lhs: "+test.length);
      System.out.println("rhs: "+brhs.test.length);
      return false;
    }
    for(int i = 0; i < test.length; ++i){
      NewOnGpuRefObject left = test[i];
      NewOnGpuRefObject right = brhs.test[i];
      if(left.value != right.value){
        System.out.println("test value mismatch");
        System.out.println("lhs: "+left.value);
        System.out.println("rhs: "+right.value);
        System.out.println("index: "+i);
        return false;
      }
    }
    return true;
  }
}
