/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class FloatArraysRunOnGpu implements Kernel {

  private float element;
  private float[] element1;
  private float[][] element2;
  private float[][][] element3;

  public FloatArraysRunOnGpu(){
    element = 10;
    element1 = new float[10];
    element2 = new float[10][10];
    element3 = new float[10][10][10];

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          element3[m][n][p] = (float) p;
          element2[n][p] = (float) p;
          element1[p] = (float) p;
        }
      }
    }
  }

  public void gpuMethod() {
    element++;
    element1[0]++;
    element2[0][0]++;
    element3[0][0][0]++;
  }

  boolean compare(FloatArraysRunOnGpu brhs) {

    if(element != brhs.element){
      return false;
    }

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          if(element1[p] != brhs.element1[p])
            return false;
          if(element2[n][p] != brhs.element2[n][p])
            return false;
          if(element3[m][n][p] != brhs.element3[m][n][p])
            return false;
        }
      }
    }

    return true;
  }
}
