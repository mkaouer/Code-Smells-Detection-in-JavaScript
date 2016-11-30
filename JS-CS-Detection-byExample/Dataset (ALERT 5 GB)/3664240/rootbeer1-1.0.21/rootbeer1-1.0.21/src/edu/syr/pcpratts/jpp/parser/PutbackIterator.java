/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The first item putback is the first to be returned from the putback list
 * @author pcpratts
 * @param <T>
 */
public class PutbackIterator<T> implements Iterator<T> {

  private Iterator<T> m_Iterator;
  private List<T> m_Putbacks;

  public PutbackIterator(Iterator<T> iter){
    m_Iterator = iter;
    m_Putbacks = new LinkedList<T>();
  }

  public boolean hasNext() {
    if(m_Putbacks.isEmpty() == false)
      return true;
    return m_Iterator.hasNext();
  }

  public T next() {
    if(m_Putbacks.isEmpty() == false){
      T ret = m_Putbacks.get(0);
      m_Putbacks.remove(0);
      return ret;
    } else {
      return m_Iterator.next();
    }
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void putback(T item){
    m_Putbacks.add(item);
  }
}
