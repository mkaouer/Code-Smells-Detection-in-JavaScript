/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class LongArraysRunOnGpu implements Kernel {

  private long element;
  private long[] element1;
  private long[][] element2;
  private long[][][] element3;

  public LongArraysRunOnGpu(){
    int size = 11;
    element = 10;
    element1 = new long[size];
    element2 = new long[size][size];
    element3 = new long[size][size][size];

    for(int m = 0; m < size; m++){
      for(int n = 0; n < size; n++){
        for(int p = 0; p < size; ++p){
          element3[m][n][p] = (long) p;
          element2[n][p] = (long) p;
          element1[p] = (long) p;
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

  boolean compare(LongArraysRunOnGpu brhs) {

    if(element != brhs.element){
      System.out.println("element: "+element);
      System.out.println("rhs.element: "+brhs.element);
      return false;
    }

    int size = 11;
    for(int m = 0; m < size; m++){
      for(int n = 0; n < size; n++){
        for(int p = 0; p < size; ++p){
          if(element1[p] != brhs.element1[p]){
            System.out.println("p: "+p);
            System.out.println("element: "+element1[p]);
            System.out.println("rhs.element: "+brhs.element1[p]);
            return false;
          }
          if(element2[n][p] != brhs.element2[n][p]){
            System.out.println("p: "+p);
            System.out.println("n: "+n);
            System.out.println("element: "+element2[n][p]);
            System.out.println("rhs.element: "+brhs.element2[n][p]);
            return false;
          }
          if(element3[m][n][p] != brhs.element3[m][n][p]){
            System.out.println("p: "+p);
            System.out.println("n: "+n);
            System.out.println("m: "+m);
            System.out.println("element: "+element3[m][n][p]);
            System.out.println("rhs.element: "+brhs.element3[m][n][p]);
            return false;
          }
        }
      }
    }

    return true;
  }
}
