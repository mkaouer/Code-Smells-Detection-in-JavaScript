package rootbeer.examples.gtc2013;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import edu.syr.pcpratts.rootbeer.runtimegpu.GpuException;

import java.util.List;
import java.util.ArrayList;

public class MatrixKernel implements Kernel {

  private float[] m_a;
  private float[] m_b;
  private float[] m_c;
  private int m_blockSize;
  private int m_gridSize;
  private int m_blockIters;
  public CalcList m_calcList;

  public MatrixKernel(float[] a, float[] b, float[] c, int block_size, int grid_size,
    int block_iters){
    m_a = a;
    m_b = b;
    m_c = c;
    m_blockSize = block_size;
    m_gridSize = grid_size;
    m_blockIters = block_iters;
    m_calcList = new CalcList();
  }

  public void gpuMethod(){

    //save off fields into local variables. each read from a field hits global
    //ram while a local variable is most likely in a register.
    int block_size = m_blockSize;
    int grid_size = m_gridSize;
    int block_iters = m_blockIters;

    //getBlockIdxx is CUDA blockIdx.x
    //getThreadIdxx is CUDA threadIdx.x
    int block_idxx = RootbeerGpu.getBlockIdxx();
    int thread_idxx = RootbeerGpu.getThreadIdxx();

    //Rootbeer thread index is single dimensional right now. Convert this
    //to a two dimensional index.
    int thread_row = thread_idxx / 32;
    int thread_col = thread_idxx % 32;

    //save off the arrays into local variables. the array elements are still
    //in global ram right now, but at least the pointers are local.
    float[] a = m_a;
    float[] b = m_b;
    float[] c = m_c;

    int sub_matrix_size = block_size / 32;
    sub_matrix_size *= sub_matrix_size;

    int m_size = block_size / 32;

    for(int block_iter = 0; block_iter < block_iters; ++block_iter){ 
      for(int sub_matrix = 0; sub_matrix < sub_matrix_size; ++sub_matrix){
        float sum = 0;
        int sub_matrix_row = sub_matrix / m_size;
        int sub_matrix_col = sub_matrix % m_size;

        int dest_row = (32 * sub_matrix_row) + thread_row;
        int dest_col = (32 * sub_matrix_col) + thread_col;

        int dest_index = (block_iter * block_size * block_size * grid_size) + (block_idxx * block_size * block_size) + dest_row * block_size + dest_col;   
  
        for(int m = 0; m < m_size; ++m){
          int a_src_row = (sub_matrix_row * 32) + thread_row;
          int a_src_col = (m * 32) + thread_col;
          int a_src = (a_src_row * block_size) + a_src_col;

          int b_src_row = (m * 32) + thread_row;
          int b_src_col = (sub_matrix_col * 32) + thread_col;
          int b_src = (b_src_row * block_size) + b_src_col;

          float a_value = a[a_src];
          float b_value = b[b_src];

          //store the a_value into shared memory at location shared_a[threadIdx.x]
          //each thread is loading a single value of global ram into shared ram
          //and then later in the for loop, all threads read from all values
          //placed in shared ram. Fetches from global ram take 200-300 clock cyles
          //while fetches from shared ram take 2-3 clock cycles. If we can have
          //each thread fetch a single value from global memory and store all of
          //the values into shared memory, most of the reads take 2-3 clock cycles
          //rather than 200-300.
          RootbeerGpu.setSharedFloat(thread_idxx * 4, a_value);
          //store the b_value into shared memory at location shared_b[threadIdx.x]
          RootbeerGpu.setSharedFloat((1024 + thread_idxx) * 4, b_value);

          //sync the threads within a block
          RootbeerGpu.syncthreads();

          //loop over all of shared_a[] and shared_b[]
          for(int k = 0; k < 32; ++k){
            //read the a_value from shared_a[thread_row][k]
            a_value = RootbeerGpu.getSharedFloat((thread_row * 32 + k) * 4);
            //read the b_value from shared_b[k][thread_col]
            b_value = RootbeerGpu.getSharedFloat((1024 + k * 32 + thread_col) * 4);
            //multiply a_value and b_value and accumulate
            sum += a_value * b_value;
          }
          //sync threads within a block
          RootbeerGpu.syncthreads();
        }
        //increment c[dest_index] with the sum
        c[dest_index] += sum;
      }
    }
  }
}
