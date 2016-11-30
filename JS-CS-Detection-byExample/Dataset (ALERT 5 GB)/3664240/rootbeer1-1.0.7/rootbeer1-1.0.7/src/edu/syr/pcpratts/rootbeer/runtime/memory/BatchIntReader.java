/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.memory;

import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BatchIntReader {
  
  static {
    System.load(new File("native/batchint.so.1").getAbsolutePath());
  }
  
  public native void read(byte[] buffer, int length, int[] ret_buffer);
  public native void malloc(int size);
}