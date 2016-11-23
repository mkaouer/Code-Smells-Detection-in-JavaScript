
package rootbeer.examples.randomremap;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class RandomRemap implements Kernel {
  
  private Random m_random; 
  private int m_randomNumber;
  
  public RandomRemap(Random random){
    m_random = random;
  }
  
  public void gpuMethod(){
    m_randomNumber = m_random.nextInt();
  }

  public int getRandomNumber(){
    return m_randomNumber;
  }
}
