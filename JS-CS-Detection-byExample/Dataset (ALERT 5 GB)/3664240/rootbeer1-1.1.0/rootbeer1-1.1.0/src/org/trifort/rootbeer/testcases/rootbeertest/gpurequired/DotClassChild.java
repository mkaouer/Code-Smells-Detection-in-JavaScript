/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

public class DotClassChild {

  private String m_name;
  
  public void exec(){
    m_name = DotClassChild.class.getName();
  }
  
  public String getName(){
    return m_name;
  }
}
