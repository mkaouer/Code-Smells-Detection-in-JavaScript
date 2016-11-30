/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class RefTypeArraysRunOnGpu implements Kernel {

  private RefType1 element;
  private RefType1[] element1;
  private RefType1[][] element2;
  private RefType1[][][] element3;

  public RefTypeArraysRunOnGpu(){
    element = new RefType1((byte) 10);
    element1 = new RefType1[10];
    element2 = new RefType1[10][10];
    element3 = new RefType1[10][10][10];

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(byte p = 0; p < 10; ++p){
          element3[m][n][p] = new RefType1(p);
          element2[n][p] = new RefType1(p);
          element1[p] = new RefType1(p);
        }
      }
    }
  }

  @Override
  public void gpuMethod() {
    RefType1 el = element1[0];
    el.modify();
    element1[0] = el;
    
    element3[0][0][0].modify();
    element2[0][0].modify();
    element.modify();
  }

  boolean compare(RefTypeArraysRunOnGpu brhs) {

    if(element.equals(brhs.element) == false){
      System.out.println("1");
      return false;
    }

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          if(element1[p].equals(brhs.element1[p]) == false){
            System.out.println("2: m: "+m+" n: "+n+" p: "+p);
            return false;
          }
          if(element2[n][p].equals(brhs.element2[n][p]) == false){
            return false;
          }
          if(element3[m][n][p].equals(brhs.element3[m][n][p]) == false){
            return false;
          }
        }
      }
    }
    return true;
  }
}
