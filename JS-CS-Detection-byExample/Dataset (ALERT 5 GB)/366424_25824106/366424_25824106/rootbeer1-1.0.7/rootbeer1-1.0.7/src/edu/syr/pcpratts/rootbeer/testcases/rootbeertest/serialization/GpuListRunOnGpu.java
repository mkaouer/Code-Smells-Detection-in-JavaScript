/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class GpuListRunOnGpu implements Kernel {

  private GpuList<Item> m_List;
  
  public GpuListRunOnGpu(){
    m_List = new GpuList<Item>();
  }
  
  @Override
  public void gpuMethod() {
    m_List = new GpuList<Item>();
    for(int i = 0; i < 10; ++i){
      m_List.add(new Item());
    }
  }
  
  public GpuList<Item> getList(){
    return m_List;
  }
}
