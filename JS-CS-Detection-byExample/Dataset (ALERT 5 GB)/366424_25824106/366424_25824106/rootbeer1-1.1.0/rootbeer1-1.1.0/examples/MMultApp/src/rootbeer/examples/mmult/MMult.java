/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package rootbeer.examples.mmult;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class MMult implements Kernel {

  private int[] a;
  private int[] b;
  private int[] c;
  private int index;
  private int size;
  
  public MMult(int[] a, int[] b, int[] c, int index, int size){
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
      lc[lindex*lsize+j] = sum;
    }
  }
}

