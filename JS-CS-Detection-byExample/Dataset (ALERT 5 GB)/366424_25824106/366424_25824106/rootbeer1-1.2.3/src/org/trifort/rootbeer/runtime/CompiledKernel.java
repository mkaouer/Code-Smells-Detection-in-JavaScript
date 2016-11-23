/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

public interface CompiledKernel {
  public String getCodeUnix();
  public String getCodeWindows();
  public int getNullPointerNumber();
  public int getOutOfMemoryNumber();
  public String getCubin32();
  public int getCubin32Size();
  public boolean getCubin32Error();
  public String getCubin64();
  public int getCubin64Size();
  public boolean getCubin64Error();
  public Serializer getSerializer(Memory memory, Memory texture_memory);
  public boolean isUsingGarbageCollector();
}
