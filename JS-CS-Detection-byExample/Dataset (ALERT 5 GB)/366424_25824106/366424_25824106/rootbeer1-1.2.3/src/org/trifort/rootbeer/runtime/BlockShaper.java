/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

import java.util.ArrayList;
import java.util.List;

public class BlockShaper {

  private int m_GridShape;
  private int m_BlockShape;
  private final int m_MaxThreadsPerBlock = 256;
  private final int m_MaxBlocksPerProc = 32;
  
  public int getMaxBlocksPerProc(){
    return m_MaxBlocksPerProc;
  }
  
  public int getMaxThreadsPerBlock(){
    return m_MaxThreadsPerBlock;  
  }
  
  public void run(int num_items, long num_processors) {
    int num_blocks = num_items;
    
    int max_blocks = getMaxThreads(num_processors);
    if(num_blocks == max_blocks){
      m_GridShape = (int) num_processors * m_MaxBlocksPerProc;
      m_BlockShape = m_MaxThreadsPerBlock;
    } else if(num_blocks < num_processors){
      m_GridShape = num_blocks;
      m_BlockShape = 1;
    } else {    
      int min_score = Integer.MAX_VALUE;
      for(int block_shape = 1; block_shape <= m_MaxThreadsPerBlock; ++block_shape){
        for(int grid_shape = 1; grid_shape <= num_processors * m_MaxBlocksPerProc; ++grid_shape){
          
          int score = m_MaxThreadsPerBlock - block_shape;
          if(grid_shape == num_processors){
            score -= 500;
          }
          if(grid_shape * block_shape < num_blocks + m_MaxThreadsPerBlock && grid_shape * block_shape >= num_blocks){
            score -= 1000;
            score -= m_MaxThreadsPerBlock - ((grid_shape * block_shape) - num_blocks);
          }
          if(score < min_score){
            min_score = score;
            m_BlockShape = block_shape;
            m_GridShape = grid_shape;
          }
        }
      }
    }
  }

  public int gridShape() {
    return m_GridShape;
  }

  public int blockShape() {
    return m_BlockShape;
  }
  
  public int getMaxThreads(long num_procs) {
    return (int) (num_procs * m_MaxThreadsPerBlock * m_MaxBlocksPerProc);
  }
  
  private void test(int num_items, long num_processors){
    run(num_items, num_processors);
    System.out.println("Items: "+num_items+" Procs: "+num_processors+" Grid: "+m_GridShape+" Block: "+m_BlockShape);
  }
  
  public static void main(String[] args){
    BlockShaper shaper = new BlockShaper();
    for(int procs = 2; procs <= 14; procs += 2){
      for(int items = 1; items <= procs * 32 * 4; ++items){
        shaper.test(items, procs);
      }
    }
  }

}
