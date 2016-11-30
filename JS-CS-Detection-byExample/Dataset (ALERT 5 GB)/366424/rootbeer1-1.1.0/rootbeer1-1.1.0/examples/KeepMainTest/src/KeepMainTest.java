import java.util.List;
import java.util.ArrayList;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class KeepMainTest {
  
  public static void main(String[] args){
    KeepMainTest app = new KeepMainTest();
    int[] array = new int[10];

    for(int i = 0; i < array.length; ++i){
      array[i] = i;
    }

    for(int i = 0; i < array.length; ++i){
      System.out.println("array["+i+"] = " + array[i]);
    }

    app.MultiplyArray(array, 5);

    for(int i = 0; i < array.length; ++i){
      System.out.println("array["+i+"] * 5 = " + array[i]);
    }
  }

  public void MultiplyArray(int[] array, int width){
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < array.length; ++i){
      jobs.add(new Multiply(array, i, width));
    }

    Rootbeer rootbeer = new Rootbeer();
    rootbeer.runAll(jobs);
  }
}
