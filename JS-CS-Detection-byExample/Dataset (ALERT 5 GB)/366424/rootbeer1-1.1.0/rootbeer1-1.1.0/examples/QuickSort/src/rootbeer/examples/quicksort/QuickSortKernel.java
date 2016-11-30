/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */
package rootbeer.examples.quicksort;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class QuickSortKernel implements Kernel {

  private int[] values;
  private int[] start;
  private int[] end;
  private int index;
  private int size;

  public QuickSortKernel(int[] a, int[] b, int[] c, int index, int size){
		
    this.start = a;
    this.end = b;
    this.values = c;
    this.index = index;
    this.size = size;

  }

  public int[] getValues(){
    return values;
  }

  @Override
  public void gpuMethod() {

    int pivot, L, R;
    start[index] = index;
    end[index] = size - 1;
    while (index >= 0) {
      L = start[index];
      R = end[index];
      if (L < R) {
        pivot = values[L];
        while (L < R) {
          while (values[R] >= pivot && L < R)
            R--;
          if(L < R)
            values[L++] = values[R];
          while (values[L] < pivot && L < R)
            L++;
          if (L < R)
            values[R--] = values[L];
        }
        values[L] = pivot;
        start[index + 1] = L + 1;
        end[index + 1] = end[index];
        end[index++] = L;
        if (end[index] - start[index] > end[index - 1] - start[index - 1]) {
          // swap start[idx] and start[idx-1]
          int tmp = start[index];
          start[index] = start[index - 1];
          start[index - 1] = tmp;

          // swap end[idx] and end[idx-1]
          tmp = end[index];
          end[index] = end[index - 1];
          end[index - 1] = tmp;
        }

      } else {
        index--;
      }		
    }
  }
}
