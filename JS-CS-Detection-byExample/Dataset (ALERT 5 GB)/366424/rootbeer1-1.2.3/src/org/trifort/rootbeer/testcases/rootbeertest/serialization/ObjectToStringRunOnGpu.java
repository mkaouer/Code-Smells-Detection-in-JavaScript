/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class ObjectToStringRunOnGpu implements Kernel {

  private String m_toString0;
  private String m_toString1;
  private String m_toString2;
  private String m_toString3;
  private String m_toString4;
  private String m_toString5;
  private String m_toString6;
  
  public ObjectToStringRunOnGpu(){
    m_toString0 = "";
    m_toString1 = "";
    m_toString2 = "";
    m_toString3 = "";
    m_toString4 = "";
    m_toString5 = "";
    m_toString6 = "";
  }
  
  public void gpuMethod() {
    m_toString0 = String.valueOf(returnObject0());
    m_toString1 = String.valueOf(returnObject1());
    m_toString2 = String.valueOf(returnObject2());
    m_toString3 = String.valueOf(returnObject3());
    m_toString4 = String.valueOf(returnObject4());
    m_toString5 = String.valueOf(returnObject5());
    m_toString6 = String.valueOf(returnObject6());
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

  public boolean compare(ObjectToStringRunOnGpu rhs) {
    if(rhs.m_toString0 == null){
      System.out.println("rhs.m_toString0 == null");
      return false;
    }
    if(rhs.m_toString0.equals(m_toString0) == false){
      System.out.println("m_toString0");
      System.out.println("  lhs: "+m_toString0);
      System.out.println("  rhs: "+rhs.m_toString0);
      return false;
    }
    if(rhs.m_toString1 == null){
      System.out.println("rhs.m_toString1 == null");
      return false;
    }
    if(rhs.m_toString1.equals(m_toString1) == false){
      System.out.println("m_toString1");
      System.out.println("  lhs: "+m_toString1);
      System.out.println("  rhs: "+rhs.m_toString1);
      return false;
    }
    if(rhs.m_toString2 == null){
      System.out.println("rhs.m_toString2 == null");
      return false;
    }
    if(rhs.m_toString2.equals(m_toString2) == false){
      System.out.println("m_toString2");
      System.out.println("  lhs: "+m_toString2);
      System.out.println("  rhs: "+rhs.m_toString2);
      return false;
    }
    if(rhs.m_toString3 == null){
      System.out.println("rhs.m_toString3 == null");
      return false;
    }
    if(rhs.m_toString3.equals(m_toString3) == false){
      System.out.println("m_toString3");
      System.out.println("  lhs: "+m_toString3);
      System.out.println("  rhs: "+rhs.m_toString3);
      return false;
    }
    if(rhs.m_toString4 == null){
      System.out.println("rhs.m_toString4 == null");
      return false;
    }
    if(rhs.m_toString4.equals(m_toString4) == false){
      System.out.println("m_toString4");
      System.out.println("  lhs: "+m_toString4);
      System.out.println("  rhs: "+rhs.m_toString4);
      return false;
    }
    if(rhs.m_toString5 == null){
      System.out.println("rhs.m_toString5 == null");
      return false;
    }
    if(rhs.m_toString5.equals(m_toString5) == false){
      System.out.println("m_toString5");
      System.out.println("  lhs: "+m_toString5);
      System.out.println("  rhs: "+rhs.m_toString5);
      return false;
    }
    if(rhs.m_toString6 == null){
      System.out.println("rhs.m_toString6 == null");
      return false;
    }
    if(rhs.m_toString6.equals(m_toString6) == false){
      System.out.println("m_toString6");
      System.out.println("  lhs: "+m_toString6);
      System.out.println("  rhs: "+rhs.m_toString6);
      return false;
    }
    return true;
  }
}
