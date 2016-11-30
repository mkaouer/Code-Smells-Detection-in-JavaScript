/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class StaticsTest3RunOnGpu extends StaticsTest3BaseClass implements Kernel {

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
  
  public StaticsTest3RunOnGpu(){
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
  }
  
  boolean compare(StaticsTest3RunOnGpu brhs) {
    if(m_Char2 != brhs.m_Char2){
      System.out.println("char");
      System.out.println("lhs: "+(int) m_Char2);
      System.out.println("rhs: "+(int) brhs.m_Char2);
      return false;
    }
    if(m_Short2 != brhs.m_Short2){
      System.out.println("short");
      System.out.println("lhs: "+(int) m_Short2);
      System.out.println("rhs: "+(int) brhs.m_Short2);
      return false;
    }
    if(m_Int2 != brhs.m_Int2){
      System.out.println("int");
      return false;
    }
    return true;
  }

}
