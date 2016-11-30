/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueue<T> {

  private LinkedBlockingQueue<T> m_Queue;

  public BlockingQueue(){
    m_Queue = new LinkedBlockingQueue<T>();
  }

  public void put(T item){
    while(true){
      try {
        m_Queue.put(item);
        return;
      } catch (Exception ex){
        //continue;
      }
    }
  }

  public int size(){
    return m_Queue.size();
  }

  public T take(){
    while(true){
      try {
        return m_Queue.take();
      } catch (Exception ex){
        //continue;
      }
    }
  }
}