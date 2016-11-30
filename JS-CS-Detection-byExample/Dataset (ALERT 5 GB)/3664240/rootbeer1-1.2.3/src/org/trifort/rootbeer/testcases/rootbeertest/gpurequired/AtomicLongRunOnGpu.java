/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.trifort.rootbeer.runtime.Kernel;

public class AtomicLongRunOnGpu implements Kernel {
  private AtomicLong m_along;
  private AtomicLong m_along2;
  private float m_random;
  private double m_random2;
  private Random random;
  private long m_num1;
  private long m_num2;
  
  public AtomicLongRunOnGpu(AtomicLong along, Random random){
    m_along = along;
    m_random = -100;
    m_random2 = -100;
    this.random = random;
  }

  @Override
  public void gpuMethod() {
    m_num1 = m_along.addAndGet(10);
    m_random = (float) random.nextDouble();
    m_random2 = Math.random();
    m_along2 = new AtomicLong(30);
    m_num1 = m_along2.addAndGet(5);
    m_num2 = m_along2.addAndGet(5);
    m_random2 = 10;
  }
  
  boolean compare(AtomicLongRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_num1 != grhs.m_num1){
      System.out.println("m_num1");
      System.out.println("lhs: "+m_num1);
      System.out.println("rhs: "+grhs.m_num1);
      return false;
    } 
    if(m_num2 != grhs.m_num2){
      System.out.println("m_num2");
      System.out.println("lhs: "+m_num2);
      System.out.println("rhs: "+grhs.m_num2);
      return false;
    }
    if(m_along2.get() != grhs.m_along2.get()){
      System.out.println("m_along2");
      System.out.println("lhs: "+m_along2.get());
      System.out.println("rhs: "+grhs.m_along2.get());
      return false;
    }
    if(m_along.get() != grhs.m_along.get()){
      System.out.println("m_along");
      System.out.println("lhs: "+m_along.get());
      System.out.println("rhs: "+grhs.m_along.get());
      return false;
    }
    if(grhs.m_random == -100){
      System.out.println("m_random");
      return false;
    }
    if(grhs.m_random2 == -100){
      System.out.println("m_random2");
      return false;
    }
    return true;
  }
}