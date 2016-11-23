/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class DotClassRunOnGpu implements Kernel {

  private String m_name;
  private String m_name2;
  private long m_ref;
  private long m_ref2;
  private DotClassChild m_child;
  
  public DotClassRunOnGpu(){
    m_child = new DotClassChild();
  }
 
  public void gpuMethod() {
    //m_name = DotClassRunOnGpu.class.getName();
    //m_name2 = int[][].class.getName();
    //m_child.exec();
  }
  
  public boolean compare(DotClassRunOnGpu rhs){ 
    /*
    if(m_name == null || rhs.m_name == null){
      System.out.println("m_name: "+m_name);
      System.out.println("rhs.m_name: "+rhs.m_name);
      return false;
    }
    
    if(m_name.equals(rhs.m_name) == false){
      System.out.println("m_name: "+m_name);
      System.out.println("rhs.m_name: "+rhs.m_name);
      return false;
    }
    
    if(m_name2 == null || rhs.m_name2 == null){
      System.out.println("m_name2: "+m_name2);
      System.out.println("rhs.m_name2: "+rhs.m_name2);
      return false;
    }
    
    if(m_name2.equals(rhs.m_name2) == false){
      System.out.println("m_name2: "+m_name2);
      System.out.println("rhs.m_name2: "+rhs.m_name2);
      return false;
    }
    
    if(m_child.getName() == null || rhs.m_child.getName() == null){
      System.out.println("child.name: "+m_child.getName());
      System.out.println("rhs.child.name: "+rhs.m_child.getName());
      return false;
    }
    
    if(m_child.getName().equals(rhs.m_child.getName()) == false){
      System.out.println("child.name: "+m_child.getName());
      System.out.println("rhs.child.name: "+rhs.m_child.getName());
      return false;
    }
    return true;
    */
    return false;
  }
}
