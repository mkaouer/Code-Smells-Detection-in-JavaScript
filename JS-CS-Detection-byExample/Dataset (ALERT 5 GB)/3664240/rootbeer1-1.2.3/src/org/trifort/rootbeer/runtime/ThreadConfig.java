/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public class ThreadConfig {

  private int threadCountX;
  private int threadCountY;
  private int threadCountZ;
  private int blockCountX;
  private int blockCountY;
  private int numThreads;
  
  public ThreadConfig(int threadCountX, int threadCountY, int threadCountZ,
      int blockCountX, int blockCountY, int numThreads) {
  
    this.threadCountX = threadCountX;
    this.threadCountY = threadCountY;
    this.threadCountZ = threadCountZ;
    this.blockCountX = blockCountX;
    this.blockCountY = blockCountY;
    this.numThreads = numThreads;
  }

  public int getThreadCountX(){
    return threadCountX;
  }
  
  public int getThreadCountY(){
    return threadCountY;
  }
  
  public int getThreadCountZ(){
    return threadCountZ;
  }
  
  public int getBlockCountX(){
    return blockCountX;
  }
  
  public int getBlockCountY(){
    return blockCountY;
  }
  
  public int getNumThreads(){
    return numThreads;
  } 
}