package rootbeer.examples.gtc2013;

import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;
import java.util.ArrayList;

public class ThreadConfigApp {

  public void run(){
    List<Kernel> kernels = new ArrayList<Kernel>();
    int block_dim = 14;
    int thread_dim = 32;
    for(int i = 0; i < block_dim * thread_dim; ++i){
      kernels.add(new ThreadConfigKernel());
    }

    Rootbeer rootbeer = new Rootbeer();
    rootbeer.setThreadConfig(thread_dim, block_dim);
    rootbeer.runAll(kernels);

    for(Kernel kernel : kernels){
      ThreadConfigKernel typed = (ThreadConfigKernel) kernel;
      typed.print();
      System.out.println();
    }
  }

  public static void main(String[] args){
    ThreadConfigApp app = new ThreadConfigApp();
    app.run();
  }
}
