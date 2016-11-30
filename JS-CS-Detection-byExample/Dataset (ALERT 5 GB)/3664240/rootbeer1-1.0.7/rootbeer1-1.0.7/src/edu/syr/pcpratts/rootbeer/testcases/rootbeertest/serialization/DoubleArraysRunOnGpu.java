/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class DoubleArraysRunOnGpu implements Kernel {

  private double element;
  private double[] element1;
  private double[][] element2;
  private double[][][] element3;

  public DoubleArraysRunOnGpu(){
    element = 10;
    element1 = new double[10];
    element2 = new double[10][10];
    element3 = new double[10][10][10];

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          element3[m][n][p] = (double) p;
          element2[n][p] = (double) p;
          element1[p] = (double) p;
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

  boolean compare(DoubleArraysRunOnGpu brhs) {

    if(element != brhs.element){
      System.out.println("first failed");
      System.out.println(element);
      System.out.println(brhs.element);
      return false;
    }

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          if(element1[p] != brhs.element1[p]){
            System.out.println("second failed");
            return false;
          }
          if(element2[n][p] != brhs.element2[n][p]){
            System.out.println("third failed");
            return false;
          }
          if(element3[m][n][p] != brhs.element3[m][n][p]){
            System.out.println("fourth failed");
            return false;
          }
        }
      }
    }

    return true;
  }
}
