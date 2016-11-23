/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.List;

public class Method {
  
  private String m_name;
  private List<String> m_invoked;
  
  public Method(String name){
    m_name = name;
  }
  
  public String getName(){
    return m_name;
  }

  public void setInvoked(List<String> invoked) {
    m_invoked = invoked;
  }
  
  public List<String> getInvoked(){
    return m_invoked;
  }
}
