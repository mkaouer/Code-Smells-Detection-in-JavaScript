package org.trifort.rootbeer.runtime;

import java.util.List;

public interface Context {

  public void init();
  public void init(int object_mem_size);
  public void close();
  public GpuDevice getDevice();
  public List<StatsRow> getStats();
  
  public void run(Kernel template, ThreadConfig thread_config);
  public void run(List<Kernel> work, ThreadConfig thread_config);
}
