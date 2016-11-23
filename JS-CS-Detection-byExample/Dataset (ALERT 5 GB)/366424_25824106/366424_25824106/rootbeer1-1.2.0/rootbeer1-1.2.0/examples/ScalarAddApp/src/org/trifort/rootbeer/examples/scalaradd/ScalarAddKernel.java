
package org.trifort.rootbeer.examples.scalaradd;

import org.trifort.rootbeer.runtime.Kernel;

public class ScalarAddKernel implements Kernel {

  private int[] array;
  private int index;

  public ScalarAddKernel(int[] array, int index){
    this.array = array;
    this.index = index;
  }

  public void gpuMethod(){
    array[index] += 1;
  }
}
