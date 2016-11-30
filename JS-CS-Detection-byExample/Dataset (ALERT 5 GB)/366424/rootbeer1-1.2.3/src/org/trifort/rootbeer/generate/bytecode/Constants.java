/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

public class Constants {

  //if SiceGcInfo is 16, the synch tests fail
  public final static int SizeGcInfo = 32;
  public final static int ArrayOffsetSize = 32;
  public final static int MallocAlignBytes = 16;
}
