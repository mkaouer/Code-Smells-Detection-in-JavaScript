/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class MMultRunOnGpu implements Kernel {

  private int[] a;
  private int[] b;
  private int[] c;
  private int index;
  private int size;
  
  public MMultRunOnGpu(int[] a, int[] b, int[] c, int index, int size){
    this.a = a;
    this.b = b;
    this.c = c;
    this.index = index;
    this.size = size;
  }

  @Override
  public void gpuMethod() {
    int len = a.length;
    int lsize = size;
    int lindex = index;
    int[] la = a;
    int[] lb = b;
    int[] lc = c;
    for(int j = 0; j < lsize; ++j){
      int sum = 0;
      for(int k = 0; k < lsize; ++k){
        sum += (la[lindex*lsize+j]*lb[j*lsize+k]);
      }
      lc[lindex*lsize+j] = sum + len;
    }
  }

  boolean compare(MMultRunOnGpu brhs) {
    if(c.length != brhs.c.length){
      System.out.println("len failed");
      System.out.println("c.length: "+c.length);
      System.out.println("brhs.c.length: "+brhs.c.length);
      return false;
    }
    for(int i = index*size; i < (index+1)*size; ++i){
      int lhs = c[i];
      int rhs = brhs.c[i];
      if(lhs != rhs){
        System.out.println("c value failed");
        System.out.println("i: "+i);
        System.out.println("lhs: "+lhs+" rhs: "+rhs);
        return false;
      }
    }
    return true;
  }
}
