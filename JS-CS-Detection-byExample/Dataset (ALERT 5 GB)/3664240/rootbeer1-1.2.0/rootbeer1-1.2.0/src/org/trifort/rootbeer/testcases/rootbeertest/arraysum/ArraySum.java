/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.arraysum;

import java.util.List;
import java.util.ArrayList;

import org.trifort.rootbeer.runtime.Kernel;

public class ArraySum implements Kernel {
  
  private int[] source; 
  private int[] ret; 
  private int index;
  
  public ArraySum (int[] src, int[] dst, int i){
    source = src; ret = dst; index = i;
  }
  
  public void gpuMethod(){
    int sum = 0;
    for(int i = 0; i < source.length; ++i){
      sum += source[i];
    }
    ret[index] = sum;
  }
  
  public int[] getResult(){
    return ret;
  }
}
