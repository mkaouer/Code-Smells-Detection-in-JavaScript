/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class RegularExpressionRunOnGpu implements Kernel {

  private String m_inputString1;
  private String m_inputString2;
  private String m_exprString;
  private boolean m_matches1;
  private boolean m_matches2;
  
  public RegularExpressionRunOnGpu(){
    m_inputString1 = "build.xml";
    m_inputString2 = "pom.xml";
    m_exprString = "build";
  }
  
  public void gpuMethod() {
    //m_matches1 = m_inputString1.matches(m_exprString);
    //m_matches2 = m_inputString2.matches(m_exprString);
  }
  
  public boolean compare(RegularExpressionRunOnGpu rhs) {
    /*
    if(m_matches1 != rhs.m_matches1){
      System.out.println("matches1");
      System.out.println(" lhs: "+m_matches1);
      System.out.println(" rhs: "+rhs.m_matches1);
      return false;
    }
    if(m_matches2 != rhs.m_matches2){
      System.out.println("matches2");
      System.out.println(" lhs: "+m_matches2);
      System.out.println(" rhs: "+rhs.m_matches2);
      return false;
    }
    return true;
    */
    return false;
  }
}