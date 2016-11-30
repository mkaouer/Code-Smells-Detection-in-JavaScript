/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class ByteArraysRunOnGpu implements Kernel {

  private byte element;
  private byte[] element1;
  private byte[][] element2;
  private byte[][][] element3;

  public ByteArraysRunOnGpu(){
    element = 10;
    element1 = new byte[10];
    element2 = new byte[10][10];
    element3 = new byte[10][10][10];

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          element3[m][n][p] = (byte) p;
          element2[n][p] = (byte) p;
          element1[p] = (byte) p;
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

  boolean compare(ByteArraysRunOnGpu brhs) {

    if(element != brhs.element){
      System.out.println("1");
      System.out.println("lhs: "+element+" rhs: "+brhs.element);
      return false;
    }

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          if(element1[p] != brhs.element1[p]){
            System.out.println("2");
            System.out.println("known good: "+element1[p]);
            System.out.println("gpu value: "+brhs.element1[p]);
            return false;
          }
          if(element2[n][p] != brhs.element2[n][p]){
            System.out.println("3");
            return false;
          }
          if(element3[m][n][p] != brhs.element3[m][n][p]){
            System.out.println("4");
            return false;
          }
        }
      }
    }

    return true;
  }

}
