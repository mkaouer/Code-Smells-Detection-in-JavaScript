/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class NullPointer1RunOnGpu implements Kernel {

  private int index;
  private int[] a;
  private int[] b;
  private short[][] kx = {{-1,-2,-1},{0,0,0},{1,2,1}};
  private short[][] ky = {{-1,0,1}, {-2,0,2},{-1,0,1}};
  
  public NullPointer1RunOnGpu(int[] a, int index){
    this.a = a; 
    this.index = index;
  }
  
  @Override
  public void gpuMethod() {    
    int sx = convolve(index, kx);
    int sy = convolve(index, ky);

    if(sx*sx+sy*sy > 100*100){
      b[index] = 0; 
    } else {
      b[index] = 255;
    }
  }
  
  private int convolve(int index, short[][] kern) {
    int xx;
    int yy;
    int ret = 0;
    for(xx = -1; xx <= 1; xx++){
      for(yy = -1; yy <=1; yy++){
        int i = index+xx+yy;
        if(i >= a.length){
          return 0;
        }
        if(i < 0){
          return 0;
        } 
        int value = a[i];
        ret += value * kern[xx+1][yy+1];
      }
    }
    return ret;
  }
}
