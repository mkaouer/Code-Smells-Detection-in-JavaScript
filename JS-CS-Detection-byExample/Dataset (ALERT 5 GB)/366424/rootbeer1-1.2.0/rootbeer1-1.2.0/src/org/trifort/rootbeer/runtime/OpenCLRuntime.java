package org.trifort.rootbeer.runtime;

import java.io.File;
import java.util.List;

public class OpenCLRuntime implements IRuntime {

  private List<GpuDevice> m_cards;
  
  public OpenCLRuntime(){
    File native_runtime = new File("csrc/rootbeer_opencl_runtime_x64.so.1");
    System.load(native_runtime.getAbsolutePath());
    
    m_cards = loadGpuDevices();
  }

  @Override
  public List<GpuDevice> getGpuDevices() {
    return m_cards;
  }

  private native List<GpuDevice> loadGpuDevices();
}
