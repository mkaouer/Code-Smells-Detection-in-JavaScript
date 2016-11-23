package rootbeer.examples.gtc2013;

import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.StatsRow;
import java.util.List;
import java.util.ArrayList;

//see: http://www.shodor.org/media/content//petascale/materials/UPModules/matrixMultiplication/moduleDocument.pdf
public class MatrixApp {

  private int m_a[];
  private int m_b[];
  private int m_cgpu[];
  private int m_ccpu[];
  private int m_blockSize;
  private int m_gridSize;
  private int m_blockIters;
  private Stopwatch m_cpuWatch;
  private Stopwatch m_gpuWatch;

  public MatrixApp(){
    m_cpuWatch = new Stopwatch();
    m_gpuWatch = new Stopwatch();
  }

  public void init(){
    m_blockIters = 1;
    m_blockSize = 64;
    m_gridSize = 64;
    m_a = new int[m_blockSize*m_blockSize];
    m_b = new int[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_ccpu = new int[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_cgpu = new int[m_blockSize*m_blockSize*m_gridSize*m_blockIters];

    for(int i = 0; i < m_a.length; ++i){
      m_a[i] = i % 3;
    }

    for(int i = 0; i < m_b.length; ++i){
      m_b[i] = i % 3;
    }

    //printMatrix(m_a, m_blockSize);
    //printRow(m_a, m_blockSize, 0);
    //printCol(m_a, m_blockSize, 32);
  }

  private void printMatrix(int[] matrix, int block_size){
    int row_count = 0;
    for(int i = 0; i < matrix.length; ++i){
      System.out.print(matrix[i]+" ");
      row_count++;
      if(row_count == block_size){
        row_count = 0;
        System.out.println();
      }
    } 
  }

  private void printRow(int[] matrix, int block_size, int row){
    System.out.println("row: "+row);
    int start = row * block_size;
    for(int i = 0; i < block_size; ++i){
      System.out.print(matrix[start+i]);
    }
    System.out.println();
  }

  private void printCol(int[] matrix, int block_size, int col){
    System.out.println("col: "+col);
    for(int i = 0; i < block_size; ++i){
      System.out.print(matrix[(i * block_size) + col]);
    }
    System.out.println();
  }

  private void cpuRun(){
    int num_cores = Runtime.getRuntime().availableProcessors();
    m_cpuWatch.start();
    List<MatrixCpuThread> threads = new ArrayList<MatrixCpuThread>();
    for(int i = 0; i < num_cores; ++i){
      MatrixCpuThread thread = new MatrixCpuThread(m_a, m_b, m_ccpu, i,
        m_blockSize, m_gridSize*m_blockIters, num_cores);
      threads.add(thread);
    }
    for(int i = 0; i < num_cores; ++i){
      MatrixCpuThread thread = threads.get(i);
      thread.join();
    }
    m_cpuWatch.stop();
    System.out.println("avg cpu time: "+m_cpuWatch.getAverageTime()+" ms");
  }

  private void gpuRun(){
    m_gpuWatch.start();
    MatrixKernel matrix_kernel = new MatrixKernel(m_a, m_b, m_cgpu, m_blockSize, 
      m_gridSize, m_blockIters);
    Rootbeer rootbeer = new Rootbeer();
    rootbeer.setThreadConfig(1024, m_gridSize);
    rootbeer.runAll(matrix_kernel);
    m_gpuWatch.stop();
    System.out.println("avg gpu time: "+m_gpuWatch.getAverageTime()+" ms");

/*
    int sum = 0;
    for(Calculation calc : matrix_kernel.m_calcs){
      if(calc == null){
        continue;
      }
      System.out.println("  calc:");
      System.out.println("    k: "+calc.m_invalidIndexK);
      System.out.println("    row: "+calc.m_invalidIndexRow);
      System.out.println("    col: "+calc.m_invalidIndexCol);
      System.out.println("    a_value: "+calc.m_invalidAValue);
      System.out.println("    b_value: "+calc.m_invalidBValue);
      System.out.println("    prev_a: "+calc.m_invalidPrevA);
      System.out.println("    prev_b: "+calc.m_invalidPrevB);
      System.out.println("    m: "+calc.m_invalidIndexM);
      System.out.println("    sub_matrix_row: "+calc.m_invalidSubMatrixRow);
      System.out.println("    sub_matrix_col: "+calc.m_invalidSubMatrixCol);
      sum += (calc.m_invalidAValue * calc.m_invalidBValue);
    }
    System.out.println("SUM: "+sum);
*/

    List<StatsRow> stats = rootbeer.getStats();
    for(StatsRow row : stats){
      System.out.println("  StatsRow:");
      System.out.println("    init time: "+row.getInitTime());
      System.out.println("    serial time: "+row.getSerializationTime());
      System.out.println("    exec time: "+row.getExecutionTime());
      System.out.println("    deserial time: "+row.getDeserializationTime());
      System.out.println("    num blocks: "+row.getNumBlocks());
      System.out.println("    num threads: "+row.getNumThreads());
    }
  }

  private void verify(){
    for(int i = 0; i < m_ccpu.length; ++i){
      int cpu_value = m_ccpu[i];
      int gpu_value = m_cgpu[i];
      if(cpu_value != gpu_value){
        System.out.println("Verify Failed.");
        System.out.println("  cpu_value: "+cpu_value);
        System.out.println("  gpu_value: "+gpu_value);
        System.out.println("  index: "+i);
        System.exit(1);
        return;
      }
    }
    System.out.println("Verify PASSED!");
  }

  public void run(){
    for(int i = 0; i < 50; ++i){
      init();
      cpuRun();
      gpuRun();
      verify();
    }
  }

  public static void main(String[] args){
    Rootbeer.init();

    MatrixApp app = new MatrixApp();
    app.run();
  }
}
