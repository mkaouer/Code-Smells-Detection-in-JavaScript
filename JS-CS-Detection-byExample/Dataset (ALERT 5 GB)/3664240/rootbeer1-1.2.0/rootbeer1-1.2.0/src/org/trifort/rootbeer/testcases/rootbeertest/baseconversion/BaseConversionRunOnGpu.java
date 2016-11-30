/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.baseconversion;

import org.trifort.rootbeer.runtime.Kernel;

public class BaseConversionRunOnGpu implements Kernel {

  private int m_Index;
  private GpuList m_Ret;
  private int m_Count;
  
  public BaseConversionRunOnGpu(int index, int count) {
    m_Index = index;
    m_Count = count;
    m_Ret = new GpuList();
  }
  
  @Override
  public void gpuMethod() {  
    int count = m_Count;
    int start_index = m_Index;
    int n = 9600;
    int len = 3;
    IntList ret_list = new IntList();
    for(int i = 0; i < count; ++i){
      int index = start_index + i;
      while(index > 0){
        int mod = index % n;      
        ret_list.add(mod);
        index /= n;
      }
      if(index != 0){    
        ret_list.add(index);
      }
      while(ret_list.size() < len){   
        ret_list.add(0);
      }
    }
    m_Ret.add(ret_list);      
  }

  public GpuList getRet(){
    return m_Ret;
  }
}
