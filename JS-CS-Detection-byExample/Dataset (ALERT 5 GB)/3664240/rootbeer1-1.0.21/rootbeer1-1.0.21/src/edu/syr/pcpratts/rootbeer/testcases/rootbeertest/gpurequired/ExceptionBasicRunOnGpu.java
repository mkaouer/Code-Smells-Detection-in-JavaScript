/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

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
      throw new RuntimeException(ex);
    } 
  }

  private void method3(){
    throw new RuntimeException();
  }
}