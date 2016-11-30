package org.trifort.rootbeer.examples.multigpu;

import java.util.List;
import java.util.ArrayList;
import org.trifort.rootbeer.runtime.Kernel;

public class ArrayMult implements Kernel {
  
  private int[] m_source;
  private int m_index;
  
  public ArrayMult(int[] source, int index){
    m_source = source;
    m_index = index;
  }
  
  public void gpuMethod(){
    m_source[m_index] *= 11;
  }
}
