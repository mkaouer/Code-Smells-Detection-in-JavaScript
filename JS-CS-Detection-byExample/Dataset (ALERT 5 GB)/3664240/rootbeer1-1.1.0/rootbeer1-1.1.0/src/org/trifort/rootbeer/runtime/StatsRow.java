/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public class StatsRow {

  private long m_serializationTime;
  private long m_executionTime;
  private long m_deserializationTime;
  private long m_initTime;
  private long m_overallTime;
  
  private int m_gridShape;
  private int m_blockShape;
  
  public StatsRow(long serialization_time, long execution_time, 
                  long deserialization_time, long overall_time,
                  int grid_shape, int block_shape) {
    m_serializationTime = serialization_time;
    m_executionTime = execution_time;
    m_deserializationTime = deserialization_time;
    m_overallTime = overall_time;
    m_gridShape = grid_shape;
    m_blockShape = block_shape;
  }
  
  /**
   * Time to serialize from Java to GPU
   * @return 
   */
  public long getSerializationTime(){
    return m_serializationTime;
  }
  
  /**
   * Time to execute on GPU
   * @return 
   */
  public long getExecutionTime(){
    return m_executionTime;
  }
  
  /**
   * Time to deserialize from GPU to Java
   * @return 
   */
  public long getDeserializationTime(){
    return m_deserializationTime;
  }
  
  /**
   * Time to init CudaRuntime2.v(). This is the same for an entire program.
   * @return 
   */
  public long getInitTime(){
    return m_initTime;
  }

  /**
   * Overall time ((de)serialization + exec + time in runtime)
   * @return 
   */
  public long getOverallTime(){
    return m_overallTime;
  }
    
  /**
   * Number of blocks
   * @return 
   */
  public int getNumBlocks(){
    return m_gridShape;
  }
  
  /**
   * Number of threads per block
   * @return 
   */
  public int getNumThreads(){
    return m_blockShape;
  }
}
