/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.arraysum;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class ArraySumTest implements TestSerialization {

  public List<Kernel> create() {
    List<int[]> arrays = new ArrayList<int[]>();
    
    //you want 1000s of threads to run on the GPU all at once for speedups
    for(int i = 0; i < 1024; ++i){
      int[] array = new int[512];
      for(int j = 0; j < array.length; ++j){
        array[j] = j;
      }
      arrays.add(array);
    }
    
    List<Kernel> jobs = new ArrayList<Kernel>();
    int[] ret = new int[arrays.size()];
    for(int i = 0; i < arrays.size(); ++i){
      jobs.add(new ArraySum(arrays.get(i), ret, i));
    }
    
    return jobs;
  }

  public boolean compare(Kernel original, Kernel from_heap) {
    ArraySum typed_original = (ArraySum) original;
    ArraySum typed_from_heap = (ArraySum) from_heap;
    
    int[] lhs = typed_original.getResult();
    int[] rhs = typed_from_heap.getResult();
    
    if(lhs.length != rhs.length){
      return false;
    }
    
    for(int i = 0; i < lhs.length; ++i){
      if(lhs[i] != rhs[i]){
        return false;
      }
    }
    return true;
  }
  
}
