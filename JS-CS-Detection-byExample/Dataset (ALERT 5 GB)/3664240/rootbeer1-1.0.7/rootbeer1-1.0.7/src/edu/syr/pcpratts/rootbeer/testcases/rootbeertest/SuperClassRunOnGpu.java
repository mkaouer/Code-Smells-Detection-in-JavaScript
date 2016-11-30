/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

import edu.syr.pcpratts.rootbeer.testcases.otherpackage.CompositeClass6;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class SuperClassRunOnGpu implements Kernel {

  private CompositeClass6 m_Class;
  private int ret;
  private String str;
  
  public SuperClassRunOnGpu(){
    m_Class = new CompositeClass6(); 
    str = "hello";
  }
  
  @Override
  public void gpuMethod() {
    m_Class = new CompositeClass6();
    ret = m_Class.go();
    //str += " world";
    //for(int i = 0; i < 5; ++i){
    //  str += "!";
    //}
  }
  
  public int getRet(){
    return ret;
  }

  boolean compare(SuperClassRunOnGpu brhs) {
    if(str.equals(brhs.str) == false){
      System.out.println("Failed at str");
      System.out.println("lhs: "+str);
      System.out.println("rhs: "+brhs.str);
      return false;
    }
    if(ret != brhs.ret){
      System.out.println("Failed at ret");
      System.out.println("lhs: "+ret);
      System.out.println("rhs: "+brhs.ret);
      return false;
    }
    /*
    if(m_Class.getModified() != brhs.m_Class.getModified()){
      System.out.println("Failed at m_Class");
      System.out.println("lhs: "+m_Class.getModified());
      System.out.println("rhs: "+brhs.m_Class.getModified());
      return false;
    }
    */
    return true;
  }
}
