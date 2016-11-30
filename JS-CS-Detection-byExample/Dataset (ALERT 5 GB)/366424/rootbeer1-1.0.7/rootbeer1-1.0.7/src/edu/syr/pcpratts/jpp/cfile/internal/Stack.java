/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile.internal;

import java.util.LinkedList;
import java.util.List;

public class Stack<T> {
  
  private List<T> m_Elements;
  
  public Stack(){
    m_Elements = new LinkedList<T>();
  }
  
  public T top(){
    return m_Elements.get(m_Elements.size()-1);
  }
  
  public T pop(){
    T ret = top();
    m_Elements.remove(m_Elements.size()-1);
    return ret;
  }
  
  public void push(T element){
    m_Elements.add(element);
  }
  
  public int size(){
    return m_Elements.size();
  }
  
  public boolean isEmpty(){
    return m_Elements.isEmpty();  
  }
  
  public List<T> getList(){
    return m_Elements;
  }
}
