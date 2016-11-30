package rootbeer.examples.gtc2013;

public class MatrixCpuThread implements Runnable {

  private float[] m_a;
  private float[] m_b;
  private float[] m_c;
  private int m_index;
  private int m_blockSize;
  private int m_gridSize;
  private int m_numCores;
  private Thread m_thread;  
  private boolean m_transpose;

  public MatrixCpuThread(float[] a, float[] b, float[] c, int index, int block_size, 
    int grid_size, int num_cores, boolean transpose){

    m_a = a;
    m_b = b;
    m_c = c;
    m_index = index;
    m_blockSize = block_size;
    m_gridSize = grid_size;
    m_numCores = num_cores;
    m_transpose = transpose;
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
        float sum = 0;
        int dest_index = i*b_columns+j;
        for(int k = 0; k < a_columns; ++k){
          int a_src = i*a_columns+k;
          int b_src;
          if(m_transpose){
            b_src = j*a_columns+k;
          } else {
            b_src = k*b_columns+j;
          }
          float a_value = m_a[a_src];
          float b_value = m_b[b_src];
          sum += a_value * b_value;
        }
        m_c[dest_index] = sum;
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
