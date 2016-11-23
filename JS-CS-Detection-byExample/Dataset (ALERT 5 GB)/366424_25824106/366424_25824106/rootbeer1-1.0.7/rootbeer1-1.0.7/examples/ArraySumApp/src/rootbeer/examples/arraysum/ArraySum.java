
package rootbeer.examples.arraysum;

import java.util.List;
import java.util.ArrayList;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

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
}
