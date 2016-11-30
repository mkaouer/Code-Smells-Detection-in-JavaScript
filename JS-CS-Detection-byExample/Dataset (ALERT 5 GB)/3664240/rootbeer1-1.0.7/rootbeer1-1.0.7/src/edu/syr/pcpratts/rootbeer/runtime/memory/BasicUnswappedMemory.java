/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.memory;

public class BasicUnswappedMemory extends BasicMemory {

  public BasicUnswappedMemory(long i) {
    super(i);
    
    loffset0 = 7;
    loffset1 = 6;
    loffset2 = 5;
    loffset3 = 4;
    loffset4 = 3;
    loffset5 = 2;
    loffset6 = 1;
    loffset7 = 0;
    
    ioffset0 = 3;
    ioffset1 = 2;
    ioffset2 = 1;
    ioffset3 = 0;
    
    soffset0 = 1;
    soffset1 = 0;
  }

  @Override
  public void useInstancePointer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void useStaticPointer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
