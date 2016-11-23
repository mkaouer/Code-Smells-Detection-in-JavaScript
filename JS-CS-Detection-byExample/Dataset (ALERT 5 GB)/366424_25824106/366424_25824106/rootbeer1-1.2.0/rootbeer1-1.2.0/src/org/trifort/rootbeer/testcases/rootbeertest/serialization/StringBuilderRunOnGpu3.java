/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class StringBuilderRunOnGpu3 implements Kernel {
  
  private String m_str;

  public StringBuilderRunOnGpu3(){
    m_str = "str";
  }
  
  public void gpuMethod(){
    m_str = " " + returnObject0();
    m_str = " " + returnObject1();
    m_str = " " + returnObject2();
    m_str = " " + returnObject3();
    m_str = " " + returnObject4();
    m_str = " " + returnObject5();
    m_str = " " + returnObject6();
  }
  
  private Object returnObject0() {
    return null;
  }
  
  private Object returnObject1() {
    return new Integer(0);
  }
  
  private Object returnObject2() {
    return new Long(1); 
  }
  
  private Object returnObject3() {
    return new Float(0.125678f);
  }
  
  private Object returnObject4() {
    return new Double(0.125678);
  }
  
  private Object returnObject5() {
    return new String("str");
  }
  
  private Object returnObject6() {
    return new Boolean(true);
  }

  public boolean compare(StringBuilderRunOnGpu3 rhs) {
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
