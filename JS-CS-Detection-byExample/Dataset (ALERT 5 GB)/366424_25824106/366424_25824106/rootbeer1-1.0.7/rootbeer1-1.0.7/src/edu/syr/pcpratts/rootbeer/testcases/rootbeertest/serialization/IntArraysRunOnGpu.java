/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class IntArraysRunOnGpu implements Kernel {

  private int[][] array2;
  private int[][] array3;

  public IntArraysRunOnGpu(int value){
    array2 = new int[3][3];
  }

  public void initArray3(){
    array3 = new int[3][2];
    array3[0][0] = 0;
    array3[0][1] = 1;
    array3[1][0] = 2;
    array3[1][1] = 3;
    array3[2][0] = 4;
    array3[2][1] = 5;
  }

  public void gpuMethod() {

  }

  public boolean compare(IntArraysRunOnGpu rhs) {
    if(rhs.array3[0][0] != 0){
      System.out.println("0, 0 = "+rhs.array3[0][0]);
      return false;
    }
    if(rhs.array3[0][1] != 1){
      System.out.println("0, 1 = "+rhs.array3[0][1]);
      return false;
    }
    if(rhs.array3[1][0] != 2){
      System.out.println("1, 0 = "+rhs.array3[1][0]);
      return false;
    }
    if(rhs.array3[1][1] != 3){
      System.out.println("1, 1 = "+rhs.array3[1][1]);
      return false;
    }
    if(rhs.array3[2][0] != 4){
      System.out.println("2, 0 = "+rhs.array3[2][0]);
      return false;
    }
    if(rhs.array3[2][1] != 5){
      System.out.println("2, 1 = "+rhs.array3[2][1]);
      return false;
    }
    return true;
  }
}
