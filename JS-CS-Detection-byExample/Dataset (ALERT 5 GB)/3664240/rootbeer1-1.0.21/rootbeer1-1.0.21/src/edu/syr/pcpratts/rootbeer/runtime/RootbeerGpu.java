/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

public class RootbeerGpu {

  private static boolean m_IsOnGpu;
  
  static {
    m_IsOnGpu = false;
  }
  
  public static boolean isOnGpu(){
    return m_IsOnGpu;
  }
  
  public static void setIsOnGpu(boolean value){
    m_IsOnGpu = value;
  }

  public static int getThreadId() {
    return 0;
  }

  public static long getRef(Object obj) {
    return 0;
  }
}
