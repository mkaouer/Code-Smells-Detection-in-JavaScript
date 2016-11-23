package rootbeer.examples.gtc2013;

import org.trifort.rootbeer.runtime.util.Stopwatch;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.StatsRow;
import org.trifort.rootbeer.runtime.ThreadConfig;
import java.util.List;
import java.util.ArrayList;

//see: http://www.shodor.org/media/content//petascale/materials/UPModules/matrixMultiplication/moduleDocument.pdf
public class MatrixApp {

  private float m_a[];
  private float m_bgpu[];
  private float m_bcpu[];
  private float m_bcpu2[];
  private float m_cgpu[];
  private float m_ccpu[];
  private float m_ccpu2[];
  private int m_blockSize;
  private int m_gridSize;
  private int m_blockIters;
  private Stopwatch m_cpuWatch;
  private Stopwatch m_gpuWatch;
  private Stopwatch m_transposeWatch;

  public MatrixApp(){
    m_cpuWatch = new Stopwatch();
    m_gpuWatch = new Stopwatch();
    m_transposeWatch = new Stopwatch();
  }

  public void init(){
    m_blockIters = 256;
    m_blockSize = 256;
    m_gridSize = 14;
    m_a = new float[m_blockSize*m_blockSize];
    m_bcpu = new float[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_bcpu2 = new float[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_bgpu = new float[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_ccpu = new float[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_ccpu2 = new float[m_blockSize*m_blockSize*m_gridSize*m_blockIters];
    m_cgpu = new float[m_blockSize*m_blockSize*m_gridSize*m_blockIters];

    for(int i = 0; i < m_a.length; ++i){
      m_a[i] = i % 3;
    }

    for(int i = 0; i < m_bgpu.length; ++i){
      m_bgpu[i] = i % 3;
      m_bcpu2[i] = i % 3;
    }

    m_transposeWatch.start();
    for(int i = 0; i < m_bgpu.length; ++i){
      int row = i / (m_blockSize*m_gridSize*m_blockIters);
      int col = i % (m_blockSize*m_gridSize*m_blockIters);
      int dest = col * m_blockSize + row;
      m_bcpu[dest] = m_bgpu[i];
    }

    m_transposeWatch.stop();
    System.out.println("transpose time: "+m_transposeWatch.getAverageTime()+" ms");
  }

  private void printMatrix(int[] matrix, int block_size, String heading){
    System.out.println(heading);
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
      MatrixCpuThread thread = new MatrixCpuThread(m_a, m_bcpu, m_ccpu, i,
        m_blockSize, m_gridSize*m_blockIters, num_cores, true);
      threads.add(thread);
    }
    for(int i = 0; i < num_cores; ++i){
      MatrixCpuThread thread = threads.get(i);
      thread.join();
    }
    m_cpuWatch.stop();
    System.out.println("avg cpu time: "+m_cpuWatch.getAverageTime()+" ms");
    
    //runs on cpu without transpose
    //threads = new ArrayList<MatrixCpuThread>();
    //for(int i = 0; i < num_cores; ++i){
    //  MatrixCpuThread thread = new MatrixCpuThread(m_a, m_bcpu2, m_ccpu2, i,
    //    m_blockSize, m_gridSize*m_blockIters, num_cores, false);
    //  threads.add(thread);
    //}
    //for(int i = 0; i < num_cores; ++i){
    //  MatrixCpuThread thread = threads.get(i);
    //  thread.join();
    //}
  }

  private void gpuRun(){
    m_gpuWatch.start();
    MatrixKernel matrix_kernel = new MatrixKernel(m_a, m_bgpu, m_cgpu, m_blockSize, 
      m_gridSize, m_blockIters);
    Rootbeer rootbeer = new Rootbeer();
    ThreadConfig thread_config = new ThreadConfig(1024, m_gridSize, 1024 * m_gridSize);
    rootbeer.run(matrix_kernel, thread_config);
    m_gpuWatch.stop();
    System.out.println("avg gpu time: "+m_gpuWatch.getAverageTime()+" ms");

    List<Calculation> calc_list = matrix_kernel.m_calcList.getList();
    for(Calculation calc : calc_list){
      if(calc == null){
        continue;
      }
      System.out.println(calc.toString());
    }

    //List<StatsRow> stats = rootbeer.getStats();
    //for(StatsRow row : stats){
    //  System.out.println("  StatsRow:");
    //  System.out.println("    init time: "+row.getInitTime());
    //  System.out.println("    serial time: "+row.getSerializationTime());
    //  System.out.println("    exec time: "+row.getExecutionTime());
    //  System.out.println("    deserial time: "+row.getDeserializationTime());
    //  System.out.println("    num blocks: "+row.getNumBlocks());
    //  System.out.println("    num threads: "+row.getNumThreads());
    //}
  }

  private void verifyCpuTranspose(){
    for(int i = 0; i < m_ccpu.length; ++i){
      float cpu_value = m_ccpu[i];
      float cpu_value2 = m_ccpu2[i];
      if(cpu_value != cpu_value2){
        System.out.println("Verify Failed.");
        System.out.println("  cpu_value: "+cpu_value);
        System.out.println("  cpu_value2: "+cpu_value2);
        System.out.println("  index: "+i);
        System.exit(1);
        return;
      }
    }
    System.out.println("Verify PASSED!");
  }

  private void verify(){
    for(int i = 0; i < m_ccpu.length; ++i){
      float cpu_value = m_ccpu[i];
      float gpu_value = m_cgpu[i];
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
      //verifyCpuTranspose();
      gpuRun();
      verify();
    }
  }

  public static void main(String[] args){
    MatrixApp app = new MatrixApp();
    app.run();
  }
}
