/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */
package rootbeer.examples.quicksort;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;

public class QuickSort {

  public int[] backrdsArray(int len){
    int[] ret = new int[len];
    for(int i = 0; i < len; ++i){
      ret[i] = len - i;
    }
    return ret;
  }

  public void printArray(String title, int[] array){
    System.out.print(title+": [ ");
    for(int i = 0; i < 64; ++i){
      System.out.print(array[i]+" ");
    }
    System.out.println("]");
  }

  public void sortGPU(){
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      int len = 2000;
      int[] a = new int[len];
      int[] b = new int[len];
      int[] c = backrdsArray(len);
      QuickSortKernel kernel = new QuickSortKernel(a, b, c, 0, len);
      jobs.add(kernel);
    }
    Rootbeer rootbeer = new Rootbeer();
    rootbeer.runAll(jobs);

    QuickSortKernel job0 = (QuickSortKernel) jobs.get(0);
    int[] result = job0.getValues();
    printArray("gpu_result", result);
  }
	
  public void swap(int array[], int index1, int index2) {
    int temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;
  }

  public void quicksort(int array[], int start, int end){
    int i = start;                          // index of left-to-right scan
    int k = end;                            // index of right-to-left scan

    if (end - start >= 1){                  // check that there are at least two elements to sort
      int pivot = array[start];       // set the pivot as the first element in the partition

      while (k > i){                   // while the scan indices from left and right have not met,
        while (array[i] <= pivot && i <= end && k > i){  // from the left, look for the first
          i++;                                    // element greater than the pivot
        }
        while (array[k] > pivot && k >= start && k >= i){ // from the right, look for the first
          k--;                                        // element not greater than the pivot
	}
        if (k > i){                                       // if the left seekindex is still smaller than
          swap(array, i, k);                      // the right index, swap the corresponding elements
        }
      }
      swap(array, start, k);          // after the indices have crossed, swap the last element in
                                      // the left partition with the pivot 
      quicksort(array, start, k - 1); // quicksort the left partition
      quicksort(array, k + 1, end);   // quicksort the right partition
    } else {    // if there is only one element in the partition, do not do any sorting
      return;                     // the array is sorted, so exit
    }
  }

  public void sortCPU(){
    int[] result = null;
    for(int i = 0; i < 500; ++i){
      int len = 2000;
      int[] values = backrdsArray(len);
      quicksort(values, 0, len-1);
      if(result == null){
        result = values;
      }
    }
    printArray("cpu_result", result);
  }

  public void execTest(){
    Stopwatch watch1 = new Stopwatch();
    watch1.start();
    sortGPU();
    watch1.stop();

    Stopwatch watch2 = new Stopwatch();
    watch2.start();
    sortCPU();
    watch2.stop();

    System.out.println();
    System.out.println("gpu time: "+watch1.elapsedTimeMillis());
    System.out.println("cpu time: "+watch2.elapsedTimeMillis());
  }

  public static void main(String[] args){
    QuickSort app = new QuickSort();
    app.execTest();
  }
}
