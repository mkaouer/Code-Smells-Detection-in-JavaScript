
package rootbeer.examples.mmult;

import java.util.List;
import java.util.ArrayList;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.runtime.util.Stopwatch;

public class MMultApp {

  public void multMatrices(int[] a, int[] b, int[] c, int size){
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < size; ++i){
      jobs.add(new MMult(a, b, c, i, size));
    }

    Rootbeer rootbeer = new Rootbeer();
    rootbeer.run(jobs);
  }

  public void cpuMultMatrices(int[] a, int[] b, int[] c, int size){
    for(int x = 0; x < size; ++x){
      for(int y = 0; y < size; ++y){
        int sum = 0;
        for(int k = 0; k < size; ++k){
          sum += (a[x*size+y]*b[y*size+k]);
        }
        c[x*size+y] = sum;
      }
    }
  }

  public static void main(String[] args){
    MMultApp app = new MMultApp();
    int size = 4096;
    int[] a = new int[size*size];
    int[] b = new int[size*size];
    int[] c_gpu = new int[size*size];
    int[] c_cpu = new int[size*size];
    
    for(int x = 0; x < size; ++x){
      for(int y = 0; y < size; ++y){
        a[x*size+y] = x*size+y;
        b[x*size+y] = x*size+y;
      }
    }

    Stopwatch watch = new Stopwatch();
    watch.start();
    app.multMatrices(a, b, c_gpu, size);
    watch.stop();
    System.out.println("gpu time: "+watch.elapsedTimeMillis());

    watch = new Stopwatch();
    watch.start();
    app.cpuMultMatrices(a, b, c_cpu, size);
    watch.stop();
    System.out.println("cpu time: "+watch.elapsedTimeMillis());
  }
}
