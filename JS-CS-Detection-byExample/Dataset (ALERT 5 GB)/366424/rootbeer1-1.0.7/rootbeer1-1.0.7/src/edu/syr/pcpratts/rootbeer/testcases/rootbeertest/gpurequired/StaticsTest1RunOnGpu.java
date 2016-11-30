/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class StaticsTest1RunOnGpu extends StaticsTest1BaseClass implements Kernel {

  private static char m_Char;
  private static short m_Short;
  private static int m_Int;
  private static long m_Long;
  private static float m_Float;
  private static double m_Double;
  private static boolean m_Bool;
  
  private char m_Char2;
  private short m_Short2;
  private int m_Int2;
  private long m_Long2;
  private float m_Float2;
  private double m_Double2;
  private boolean m_Bool2;
  
  static {
    m_Bool = false; 
  }
  
  public StaticsTest1RunOnGpu(){
    m_Char = 5;
    m_Short = 10;
    m_Int = 15;
    m_Long = 20;
    m_Float = 25.0f;
    m_Double = 30.0;
    m_Bool2 = false;
  }
  
  @Override
  public void gpuMethod() {
    m_Char2 = (char) (m_Char + 1);
    m_Short2 = (short) (m_Short + 1);
    m_Int2 = m_Int + 1;
    m_Long2 = m_Long + m_BaseInt;
    m_Float2 = m_Float + 1;
    m_Double2 = m_Double + 1;
    if(RootbeerGpu.isOnGpu()){
      m_Bool = true;
      m_Bool2 = true;
    }
  }
  
  boolean compare(StaticsTest1RunOnGpu brhs) {
    if(m_Char2 != brhs.m_Char2){
      System.out.println("char");
      System.out.println("lhs: "+(int) m_Char2);
      System.out.println("rhs: "+(int) brhs.m_Char2);
      return false;
    }
    if(m_Short2 != brhs.m_Short2){
      System.out.println("short");
      return false;
    }
    if(m_Int2 != brhs.m_Int2){
      System.out.println("int");
      return false;
    }
    if(m_Long2 != brhs.m_Long2){
      System.out.println("long");
      return false;
    }
    if(m_Float2 != brhs.m_Float2){
      System.out.println("float");
      return false;
    }
    if(m_Double2 != brhs.m_Double2){
      System.out.println("double");
      return false;
    }
    if(brhs.m_Bool2 != true){
      System.out.println("instance bool");
      return false;
    }
    if(m_Bool != true){
      System.out.println("static bool");
      return false;
    }
    return true;
  }

}
