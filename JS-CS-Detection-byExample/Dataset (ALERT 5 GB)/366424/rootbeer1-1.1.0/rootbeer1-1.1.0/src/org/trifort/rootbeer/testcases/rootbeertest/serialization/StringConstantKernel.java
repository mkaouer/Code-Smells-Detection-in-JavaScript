/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class StringConstantKernel implements Kernel {

  private String m_string;
  
  public void gpuMethod() {
    m_string = "hello world";
  }

  boolean compare(StringConstantKernel rhs) {
    if(m_string.equals(rhs.m_string) == false){
      System.out.println("m_string: "+m_string);
      System.out.println("rhs.m_string: "+rhs.m_string);
      return false;
    }
    return true;
  }

}
