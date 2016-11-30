/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.otherpackage;

import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.CompositeClass4;

class CompositeClass5 extends CompositeClass4 {
  
  private long m_ThisWillBreakStuff;
  private int m_Modified;
  
  public CompositeClass5(){
    super(5000);        
  }
  
  @Override
  public int go(){
    m_Modified = 40;
    m_ThisWillBreakStuff = 20;
    return super.go() + (int) m_ThisWillBreakStuff;
  }
  
  public int getModified(){
    return m_Modified;
  }
}
