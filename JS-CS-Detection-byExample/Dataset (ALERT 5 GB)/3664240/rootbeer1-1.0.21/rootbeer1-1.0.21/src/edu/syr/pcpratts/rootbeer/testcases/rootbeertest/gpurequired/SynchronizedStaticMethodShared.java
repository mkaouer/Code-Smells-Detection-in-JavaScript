/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

public class SynchronizedStaticMethodShared {

  public static int m_Value;

  public static synchronized void increment() {
    m_Value++;
  }
}
