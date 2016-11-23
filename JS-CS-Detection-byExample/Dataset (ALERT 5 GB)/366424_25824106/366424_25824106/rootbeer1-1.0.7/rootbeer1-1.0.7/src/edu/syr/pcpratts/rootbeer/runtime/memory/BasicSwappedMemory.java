/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.memory;

public class BasicSwappedMemory extends BasicMemory {
  
  public BasicSwappedMemory(long size){
    super(size);
    
    loffset0 = 0;
    loffset1 = 1;
    loffset2 = 2;
    loffset3 = 3;
    loffset4 = 4;
    loffset5 = 5;
    loffset6 = 6;
    loffset7 = 7;
    
    ioffset0 = 0;
    ioffset1 = 1;
    ioffset2 = 2;
    ioffset3 = 3;
    
    soffset0 = 0;
    soffset1 = 1;
  }
}
