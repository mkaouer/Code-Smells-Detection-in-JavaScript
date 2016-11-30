import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class Multiply implements Kernel {

  private int[] m_array;
  private int m_index;
  private int m_width;

  public Multiply(int[] array, int index, int width){
    m_array = array;
    m_index = index;
    m_width = width;
  }

  public void gpuMethod(){
    m_array[m_index] *= m_width;
  }
}
