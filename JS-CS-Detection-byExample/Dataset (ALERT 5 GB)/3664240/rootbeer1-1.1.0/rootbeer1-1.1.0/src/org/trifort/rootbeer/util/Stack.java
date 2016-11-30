/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
  
  private List<T> m_Data;
  
  public Stack(){
    m_Data = new ArrayList<T>();
  }
  
  public T top()          
  {
    return m_Data.get(m_Data.size()-1);
  }
  
  public void pop(){
    m_Data.remove(m_Data.size()-1);
  }
  
  public void push(T value){
    m_Data.add(value);
  }

  public int size() {
    return m_Data.size();
  }
}
