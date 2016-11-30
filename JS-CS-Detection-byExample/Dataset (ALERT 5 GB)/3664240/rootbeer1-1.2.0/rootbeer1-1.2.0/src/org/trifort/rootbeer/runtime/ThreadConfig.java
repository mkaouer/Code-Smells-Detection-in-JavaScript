/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public class ThreadConfig {

  private int m_blockShapeX;
  private int m_gridShapeX;
  private int m_numThreads;
  
  public ThreadConfig(int block_shape_x, int grid_shape_x, int num_threads){
    m_blockShapeX = block_shape_x;
    m_gridShapeX = grid_shape_x;
    m_numThreads = num_threads;
  }
  
  public int getBlockShapeX(){
    return m_blockShapeX;
  }
  
  public int getGridShapeX(){
    return m_gridShapeX;
  }
  
  public int getNumThreads(){
    return m_numThreads;
  } 
}