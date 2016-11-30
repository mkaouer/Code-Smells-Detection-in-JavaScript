package rootbeer.examples.gtc2013;

public class MatrixCpuThread implements Runnable {

  private int[] m_a;
  private int[] m_b;
  private int[] m_c;
  private int m_index;
  private int m_blockSize;
  private int m_gridSize;
  private int m_numCores;
  private Thread m_thread;  

  public MatrixCpuThread(int[] a, int[] b, int[] c, int index, int block_size, 
    int grid_size, int num_cores){

    m_a = a;
    m_b = b;
    m_c = c;
    m_index = index;
    m_blockSize = block_size;
    m_gridSize = grid_size;
    m_numCores = num_cores;
    m_thread = new Thread(this);
    m_thread.setDaemon(true);
    m_thread.start();
  }

  //see: http://blog.ryanrampersad.com/2010/01/matrix-multiplication-in-java/
  @Override
  public void run(){
    int num_each = m_blockSize / m_numCores;
    int start_row = m_index * num_each;
    int stop_row = (m_index + 1) * num_each;    
    if(m_index == m_numCores - 1){
      stop_row = m_blockSize;
    }

    int b_columns = m_blockSize * m_gridSize;
    int a_columns = m_blockSize;
    for(int i = start_row; i < stop_row; ++i){
      for(int j = 0; j < b_columns; ++j){
        int sum = 0;
        int dest_index = i*b_columns+j;
        for(int k = 0; k < a_columns; ++k){
          if(dest_index == 65){
            System.out.println("a["+i+"]["+k+"]="+m_a[i*a_columns+k]+" b["+k+"]["+j+"]="+m_b[k*b_columns+j]);
          }
          sum += m_a[i*a_columns+k] * m_b[k*b_columns+j];
        }
        m_c[i*b_columns+j] = sum;
      } 
    }
  }

  public void join(){
    try {
      m_thread.join();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
