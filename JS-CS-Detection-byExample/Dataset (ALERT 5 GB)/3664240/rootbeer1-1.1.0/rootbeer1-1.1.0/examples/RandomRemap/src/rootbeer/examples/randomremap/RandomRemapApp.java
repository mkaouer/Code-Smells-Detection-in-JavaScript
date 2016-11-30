
package rootbeer.examples.randomremap;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;

public class RandomRemapApp {

  public void printRandomNumbers(){

    Random random = new Random();
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 50; ++i){
      jobs.add(new RandomRemap(random));
    }

    Rootbeer rootbeer = new Rootbeer();
    rootbeer.runAll(jobs);

    for(int i = 0; i < jobs.size(); ++i){
      RandomRemap curr = (RandomRemap) jobs.get(i);
      System.out.println(curr.getRandomNumber());
    }
  }
  
  public static void main(String[] args){
    RandomRemapApp app = new RandomRemapApp();
    app.printRandomNumbers();
  }
}
