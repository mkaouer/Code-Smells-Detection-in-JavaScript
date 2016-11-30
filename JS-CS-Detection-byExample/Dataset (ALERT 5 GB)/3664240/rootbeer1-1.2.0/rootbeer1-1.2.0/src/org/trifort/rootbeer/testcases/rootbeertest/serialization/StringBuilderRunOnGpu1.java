/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringBuilderRunOnGpu1 implements Kernel {
  
  private String m_str;

  public StringBuilderRunOnGpu1(){
    m_str = "str";
  }
  
  public void gpuMethod(){
    //StringBuilder.append(boolean)
    m_str = " " + true;
    
    //StringBuilder.append(char)
    m_str += " " + 'c';
    
    //StringBuilder.append(double)
    m_str += " " + 1.0;
    
    //StringBuilder.append(float)
    m_str += " " + 2.0f;
    
    //StringBuilder.append(int)
    m_str += " " + 3;
    
    //StringBuilder.append(long)
    m_str += " " + 4L;
    
    //StringBuilder.append(java.lang.String)
    m_str += " " + "str";
  }

  public boolean compare(StringBuilderRunOnGpu1 rhs) {
    if(rhs.m_str == null){
      System.out.println("rhs.m_str == null");
      return false;
    }
    if(m_str.equals(rhs.m_str) == false){
      System.out.println("m_str: ");
      System.out.println("  lhs: "+m_str);
      System.out.println("  rhs.m_str: "+rhs.m_str);
      return false;
    }
    return true;
  }
}
