/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public class StatsRow {

  private long serializationTime;
  private long driverMemcopyToDeviceTime;
  private long driverExecTime;
  private long driverMemcopyFromDeviceTime;
  private long totalDriverExecutionTime;
  private long deserializationTime;
  private long overallTime;
  
  public StatsRow() {
  }
  
  public void setDriverTimes(long memcopyToDevice, long execTime, long memcopyFromDevice){
    driverMemcopyToDeviceTime = memcopyToDevice;
    driverExecTime = execTime;
    driverMemcopyFromDeviceTime = memcopyFromDevice;
  }
  
  public long getDriverMemcopyToDeviceTime(){
    return driverMemcopyToDeviceTime;
  }
  
  public long getDriverMemcopyFromDeviceTime(){
    return driverMemcopyFromDeviceTime;
  }
  
  public long getDriverExecTime(){
    return driverExecTime;
  }
  
  /**
   * Time to serialize from Java to GPU
   * @return 
   */
  public long getSerializationTime(){
    return serializationTime;
  }
  
  public void setSerializationTime(long time){
    serializationTime = time;
  }
  
  /**
   * Time to execute on GPU
   * @return 
   */
  public long getTotalDriverExecutionTime(){
    return totalDriverExecutionTime;
  }
  
  public void setExecutionTime(long time){
    totalDriverExecutionTime = time;
  }
  
  /**
   * Time to deserialize from GPU to Java
   * @return 
   */
  public long getDeserializationTime(){
    return deserializationTime;
  }
  
  public void setDeserializationTime(long time){
    deserializationTime = time;
  }

  /**
   * Overall time ((de)serialization + exec + time in runtime)
   * @return 
   */
  public long getOverallTime(){
    return overallTime;
  }
  
  public void setOverallTime(long time){
    overallTime = time;
  }
}
