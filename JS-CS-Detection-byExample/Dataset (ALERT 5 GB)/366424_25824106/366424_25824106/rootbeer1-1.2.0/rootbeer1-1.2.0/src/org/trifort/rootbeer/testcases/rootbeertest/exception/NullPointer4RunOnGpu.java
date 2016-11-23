/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.exception;

import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;

public class NullPointer4RunOnGpu implements Kernel {

  private NullPointer4Object m_Obj;
  private int m_Result;
  private int m_Result2;
  
  public NullPointer4RunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    try {
      m_Result = m_Obj.m_Value;
    } catch(NullPointerException ex){
      m_Result = 10;
    }
    try {
      m_Obj.increment();
    } catch(NullPointerException ex){
      m_Result2 = 20;
    }
  }

  boolean compare(NullPointer4RunOnGpu rhs) {
    if(m_Result != rhs.m_Result){
      System.out.println("result");
      System.out.println("lhs: "+m_Result);
      System.out.println("rhs: "+rhs.m_Result);
      return false;
    }
    if(m_Result2 != rhs.m_Result2){
      System.out.println("result2");
      System.out.println("lhs: "+m_Result2);
      System.out.println("rhs: "+rhs.m_Result2);
      return false;
    }
    return true;
  }
}
