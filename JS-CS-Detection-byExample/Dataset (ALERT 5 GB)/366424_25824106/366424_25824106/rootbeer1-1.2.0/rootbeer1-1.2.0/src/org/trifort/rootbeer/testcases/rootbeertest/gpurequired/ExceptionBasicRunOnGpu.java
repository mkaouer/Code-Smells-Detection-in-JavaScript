/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;

public class ExceptionBasicRunOnGpu implements Kernel {
  private int index;
  
  public ExceptionBasicRunOnGpu(int index){
    this.index = index;
  }

  @Override
  public void gpuMethod() {
    method2();
  }
  
  private void method2(){
    int[] arr = new int[] {10, 20, 30};
    try {
      try {
        method3();
      } catch(RuntimeException ex){
        index++;
        method3();
      }
    } catch(Throwable ex){
      throw new ExceptionTestException(ex);
    } 
  }

  private void method3(){
    throw new RuntimeException();
  }
}