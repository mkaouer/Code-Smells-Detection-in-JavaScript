
package rootbeer.examples.arraymult;

import java.util.List;
import java.util.ArrayList;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Rootbeer;

public class ArrayMultApp {

  public void multArray(int[] array){
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < array.length; ++i){
      jobs.add(new ArrayMult(array, i));
    }

    Rootbeer rootbeer = new Rootbeer();
    rootbeer.run(jobs);
  }
  
  public static void main(String[] args){
    ArrayMultApp app = new ArrayMultApp();
    int[] array = new int[10];
    for(int i = 0; i < array.length; ++i){
      array[i] = i;
    }
    for(int i = 0; i < array.length; ++i){
      System.out.println("start array["+i+"]: "+array[i]);
    }
    
    app.multArray(array);
    for(int i = 0; i < array.length; ++i){
      System.out.println("final array["+i+"]: "+array[i]);
    }
  }
}
