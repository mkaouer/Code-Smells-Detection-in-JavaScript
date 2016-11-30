/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcreteIteratorEx<T> implements Iterator<T> {

  private Iterator<T> m_Iterator;
  private List<T> m_TokenList;
  private boolean m_NeedToCallHasNext;

  public ConcreteIteratorEx(Iterator<T> iter){
    m_Iterator = iter;
    m_TokenList = new ArrayList<T>();
  }

  public boolean hasNext() {
    m_NeedToCallHasNext = false;
    if(m_TokenList.isEmpty() == false)
      return true;
    return m_Iterator.hasNext();
  }

  public T next() {
    m_NeedToCallHasNext = true;
    if(m_TokenList.isEmpty() == false){
      T ret = m_TokenList.get(0);
      m_TokenList.remove(0);
      return ret;
    } else {
      return m_Iterator.next();
    }
  }
  
  public T peek(int tokens){ 
    while(m_TokenList.size() <= tokens){
      if(m_NeedToCallHasNext){
        if(m_Iterator.hasNext() == false){
          return null;
        }
        m_TokenList.add(m_Iterator.next());
        m_NeedToCallHasNext = true;
      } else {
        m_TokenList.add(m_Iterator.next());
        m_NeedToCallHasNext = true;
      }
    }
    return m_TokenList.get(tokens);
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void putback(T item){
    List<T> new_tokens = new ArrayList<T>();
    new_tokens.add(item);
    for(T tok : m_TokenList)
      new_tokens.add(tok);
    m_TokenList = new_tokens;
  }
}
