package org.trifort.rootbeer.runtime;

import java.util.List;

public class CUDARuntime implements IRuntime {

  private List<GpuDevice> m_cards;
  
  public CUDARuntime(){    
    m_cards = loadGpuDevices();
  }

  @Override
  public List<GpuDevice> getGpuDevices() {
    return m_cards;
  }

  private native List<GpuDevice> loadGpuDevices();
}
