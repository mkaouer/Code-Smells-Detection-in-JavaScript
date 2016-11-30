package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */


import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class MatrixKernel implements Kernel {

  private int[] m_a;
  private int[] m_b;
  private int[] m_c;
  private int m_blockSize;
  private int m_gridSize;

  public MatrixKernel(int[] a, int[] b, int[] c, int block_size, int grid_size){
    m_a = a;
    m_b = b;
    m_c = c;
    m_blockSize = block_size;
    m_gridSize = grid_size;
  }

  public void gpuMethod(){

    int block_idxx = RootbeerGpu.getBlockIdxx();
    int thread_idxx = RootbeerGpu.getThreadIdxx();
    int b_columns = m_blockSize * m_gridSize;
    int a_columns = m_blockSize;
    int i = thread_idxx;
    int j = block_idxx;

    int sum = 0;
    for(int k = 0; k < a_columns; ++k){
      sum += m_a[i*a_columns+k] + m_b[k*b_columns+j];
    }
    m_c[i*b_columns+j] = sum;
    //RootbeerGpu.setSharedFloat((row*block_size) + col, m_a[row*block_size]); 
  }

  public boolean compare(MatrixKernel rhs) {
    int[] lhs_c = m_c;
    int[] rhs_c = rhs.m_c;
    
    if(lhs_c.length != rhs_c.length){
      System.out.println("length");
      return false;
    }
        
    for(int i = 0; i < lhs_c.length; ++i){
      int lhs_value = lhs_c[i];
      int rhs_value = rhs_c[i];
      if(lhs_value != rhs_value){
        System.out.println("m_c");
        System.out.println("lhs_value: "+lhs_value);
        System.out.println("rhs_value: "+rhs_value);
        System.out.println("index: "+i);
        return false;
      }
    }
    return true;
  }
}
