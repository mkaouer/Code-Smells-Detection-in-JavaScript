/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp;

public class JppSettings {

  private static JppSettings m_Instance;

  public static JppSettings v(){
    if(m_Instance == null)
      m_Instance = new JppSettings();
    return m_Instance;
  }

  private boolean m_IsCpp;

  public boolean isCpp(){
    return m_IsCpp;
  }

  public void setCpp(boolean value){
    m_IsCpp = value;
  }
}
