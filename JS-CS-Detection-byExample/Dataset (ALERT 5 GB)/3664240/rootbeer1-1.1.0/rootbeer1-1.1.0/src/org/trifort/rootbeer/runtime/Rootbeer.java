package org.trifort.rootbeer.runtime;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Rootbeer {

  private IRuntime m_openCLRuntime;
  private IRuntime m_cudaRuntime;
  private IRuntime m_nativeRuntime;
  
  private List<StatsRow> m_stats;
  private boolean m_ranGpu;
  private ThreadConfig m_threadConfig;
  
  private List<GpuDevice> m_cards;
  
  static {
    CUDALoader loader = new CUDALoader();
    loader.load();
  }
  
  public Rootbeer(){
    m_stats = new ArrayList<StatsRow>();
  }
  
  public List<GpuDevice> getDevices(){
    if(m_cards != null){
      return m_cards;
    }
    
    m_cards = new ArrayList<GpuDevice>();
    try {
      Class c = Class.forName("org.trifort.rootbeer.runtime.CUDARuntime");
      Constructor<IRuntime> ctor = c.getConstructor();
      m_cudaRuntime = ctor.newInstance();
      m_cards.addAll(m_cudaRuntime.getGpuDevices());
    } catch(Exception ex){
	  ex.printStackTrace();
      //ignore
    }
    
    //if(m_cards.isEmpty()){
    //  try {
    //    Class c = Class.forName("org.trifort.rootbeer.runtime.OpenCLRuntime");
    //    Constructor<IRuntime> ctor = c.getConstructor();
    //    m_openCLRuntime = ctor.newInstance();
    //    m_cards.addAll(m_openCLRuntime.getGpuDevices());
    //  } catch(Exception ex){
    //    //ignore
    //  }
    //}
    
    return m_cards;
  }
  
  public Context createDefaultContext(){
    List<GpuDevice> devices = getDevices();
    GpuDevice best = null;
    for(GpuDevice device : devices){
      if(best == null){
        best = device;
      } else {
        if(device.getMultiProcessorCount() > best.getMultiProcessorCount()){
          best = device;
        }
      }
    }
    if(best == null){
      return null;
    } else {
      return best.createContext();
    }
  }
  
  public ThreadConfig getThreadConfig(List<Kernel> kernels, GpuDevice device){
    BlockShaper block_shaper = new BlockShaper();
    block_shaper.run(kernels.size(), device.getMultiProcessorCount());
    
    return new ThreadConfig(block_shaper.getMaxThreadsPerBlock(), 
                            block_shaper.getMaxBlocksPerProc(),
                            kernels.size());
  }
  
  public void run(Kernel template, ThreadConfig thread_config){
    Context context = createDefaultContext();
    run(template, thread_config, context);
    context.close();
  }

  public void run(List<Kernel> work, ThreadConfig thread_config) {
    Context context = createDefaultContext();
    run(work, thread_config, context);
    context.close();
  }

  public void run(List<Kernel> work) {
    Context context = createDefaultContext();
    ThreadConfig thread_config = getThreadConfig(work, context.getDevice());
    context.run(work, thread_config);
    context.close();
  }
  
  public void run(Kernel template, ThreadConfig thread_config, Context context){
    context.run(template, thread_config);
  }

  public void run(List<Kernel> work, ThreadConfig thread_config, Context context) {
    context.run(work, thread_config);
  }

  public void run(List<Kernel> work, Context context) {
    ThreadConfig thread_config = getThreadConfig(work, context.getDevice());
    context.run(work, thread_config);
  } 
  
  public static void main(String[] args){
    Rootbeer rootbeer = new Rootbeer();
    List<GpuDevice> devices = rootbeer.getDevices();
    System.out.println("count: "+devices.size());
    for(GpuDevice device : devices){
      System.out.println("device: "+device.getDeviceName());
      System.out.println("  id: "+device.getDeviceId());
      System.out.println("  mem: "+device.getFreeGlobalMemoryBytes());
      System.out.println("  clock: "+device.getClockRateHz());
      System.out.println("  mp_count: "+device.getMultiProcessorCount());
    }
  }
}
