package org.trifort.rootbeer.runtime;

import java.io.Closeable;
import java.util.List;

public interface Context extends Closeable {

  public GpuDevice getDevice();
  public void setUsingHandles(boolean value);
  public void setMemorySize(long memorySize);
  public void setCacheConfig(CacheConfig config);
  public void setThreadConfig(ThreadConfig thread_config);
  public void setThreadConfig(int block_shape_x, int grid_shape_x, int num_threads);
  public void setKernel(Kernel kernelTemple);
  public void useCheckedMemory();
  public void buildState();
  public void run();
  public void run(List<Kernel> work);
  public GpuFuture runAsync();
  public GpuFuture runAsync(List<Kernel> work);
  public long getRequiredMemory();
  public void close();
  public StatsRow getStats();
  
}
