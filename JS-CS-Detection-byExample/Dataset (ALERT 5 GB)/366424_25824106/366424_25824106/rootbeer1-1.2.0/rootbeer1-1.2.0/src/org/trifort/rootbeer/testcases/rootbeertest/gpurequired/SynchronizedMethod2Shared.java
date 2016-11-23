/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.gpurequired;

public class SynchronizedMethod2Shared {

  public int m_Value;

  public synchronized void increment() {
    m_Value++;
  }
}
