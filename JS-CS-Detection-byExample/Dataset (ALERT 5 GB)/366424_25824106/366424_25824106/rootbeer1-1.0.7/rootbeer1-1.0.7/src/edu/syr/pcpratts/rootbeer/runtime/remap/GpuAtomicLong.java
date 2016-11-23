/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.remap;

import java.io.Serializable;

public class GpuAtomicLong extends Number implements Serializable {

  private volatile long m_Value;
  
  public GpuAtomicLong(long value){
    m_Value = value;
  }
  
  public GpuAtomicLong(){
    m_Value = 0;
  }
  
  public synchronized long get(){
    return m_Value;
  }
  
  public synchronized void set(long value){
    m_Value = value;
  }
  
  public synchronized void lazySet(long value){
    m_Value = value;
  }
  
  public synchronized long getAndSet(long value){
    long ret = m_Value;
    m_Value = value;
    return ret;
  }
  
  public synchronized boolean compareAndSet(long expect, long update){
    if(m_Value == expect){
      m_Value = update;
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized boolean weakCompareAndSet(long expect, long update){
    if(m_Value == expect){
      m_Value = update;
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized long getAndIncrement(){
    long ret = m_Value;
    m_Value++;
    return ret;
  }
  
  public synchronized long getAndDecrement(){
    long ret = m_Value;
    m_Value--;
    return ret;
  }
  
  public synchronized long getAndAdd(long value){
    long ret = m_Value;
    m_Value += value;
    return ret;
  }  
  
  public synchronized long incrementAndGet(){
    m_Value++;
    return m_Value;
  }
  
  public synchronized long decrementAndGet(){
    m_Value--;
    return m_Value;
  }
    
  public synchronized long addAndGet(long value){
    m_Value += value;
    return m_Value;
  }  
  
  @Override
  public String toString(){
    long value = get();
    return ""+value;
  }
  
  @Override
  public int intValue() {
    synchronized(this){
      return (int) m_Value;
    }
  }

  @Override
  public long longValue() {
    synchronized(this){
      return m_Value;
    }
  }

  @Override
  public float floatValue() {
    synchronized(this){
      return (float) m_Value;
    }
  }

  @Override
  public double doubleValue() {    
    synchronized(this){
      return (double) m_Value;
    }
  }  
}
