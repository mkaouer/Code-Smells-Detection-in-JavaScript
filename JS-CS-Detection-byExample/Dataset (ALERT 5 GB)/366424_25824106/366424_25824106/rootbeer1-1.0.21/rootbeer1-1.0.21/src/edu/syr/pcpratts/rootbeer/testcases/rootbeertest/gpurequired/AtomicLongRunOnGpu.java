/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongRunOnGpu implements Kernel {
  private AtomicLong m_ALong;
  private float m_Random;
  private double m_Random2;
  private Random random;
  private long m_Num1;
  private long m_Num2;
  
  public AtomicLongRunOnGpu(AtomicLong along, Random random){
    m_ALong = along;
    m_Random = -100;
    m_Random2 = -100;
    this.random = random;
  }

  @Override
  public void gpuMethod() {
    m_Num1 = m_ALong.addAndGet(10);
    m_Random = (float) random.nextDouble();
    m_Random2 = Math.random();
    //AtomicLong newlong = new AtomicLong(30);
    //m_Num1 = newlong.get();
    //m_Num2 = newlong.get();
    m_Random2 = 10;
    m_Num1 = 10;
    m_Num2 = 20;
  }
  
  boolean compare(AtomicLongRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_ALong.get() != grhs.m_ALong.get()){
      System.out.println("value");
      System.out.println("lhs: "+m_ALong.get());
      System.out.println("rhs: "+grhs.m_ALong.get());
      return false;
    }
    if(grhs.m_Random == -100){
      System.out.println("random");
      return false;
    }
    if(grhs.m_Random2 == -100){
      System.out.println("random2");
      return false;
    }
    if(m_Num1 != grhs.m_Num1){
      System.out.println("num1");
      System.out.println("lhs: "+m_Num1);
      System.out.println("rhs: "+grhs.m_Num1);
      return false;
    } 
    /*
    if(m_Num2 != grhs.m_Num2){
      System.out.println("num2");
      System.out.println("lhs: "+m_Num2);
      System.out.println("rhs: "+grhs.m_Num2);
      return false;
    }
    */
    return true;
  }
}