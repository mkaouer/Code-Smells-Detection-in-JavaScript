/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.remap;

import java.io.Serializable;

public class GpuAtomicLong extends Number implements Serializable {

  private static final long serialVersionUID = 1L;
  private volatile long value;
  
  public GpuAtomicLong(long start_value){
    value = start_value;
  }
  
  public GpuAtomicLong(){
    value = 0;
  }
  
  public synchronized long get(){
    return value;
  }
  
  public synchronized void set(long set_value){
    value = set_value;
  }
  
  public synchronized void lazySet(long set_value){
    value = set_value;
  }
  
  public synchronized long getAndSet(long set_value){
    long ret = value;
    value = set_value;
    return ret;
  }
  
  public synchronized boolean compareAndSet(long expect, long update){
    if(value == expect){
      value = update;
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized boolean weakCompareAndSet(long expect, long update){
    if(value == expect){
      value = update;
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized long getAndIncrement(){
    long ret = value;
    value++;
    return ret;
  }
  
  public synchronized long getAndDecrement(){
    long ret = value;
    value--;
    return ret;
  }
  
  public synchronized long getAndAdd(long add_value){
    long ret = value;
    value += add_value;
    return ret;
  }  
  
  public synchronized long incrementAndGet(){
    value++;
    return value;
  }
  
  public synchronized long decrementAndGet(){
    value--;
    return value;
  }
    
  public synchronized long addAndGet(long add_value){
    value += add_value;
    return value;
  }  
  
  @Override
  public String toString(){
    long value = get();
    return ""+value;
  }
  
  @Override
  public int intValue() {
    synchronized(this){
      return (int) value;
    }
  }

  @Override
  public long longValue() {
    synchronized(this){
      return value;
    }
  }

  @Override
  public float floatValue() {
    synchronized(this){
      return (float) value;
    }
  }

  @Override
  public double doubleValue() {    
    synchronized(this){
      return (double) value;
    }
  }  
}
