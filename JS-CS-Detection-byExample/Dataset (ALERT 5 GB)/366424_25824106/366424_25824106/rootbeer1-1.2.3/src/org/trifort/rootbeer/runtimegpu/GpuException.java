/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtimegpu;

import org.trifort.rootbeer.runtime.Sentinal;

public class GpuException {

  public int m_arrayLength;
  public int m_arrayIndex;
  public int m_array;

  public static GpuException arrayOutOfBounds(int index, int array, int length){
    GpuException ret = new GpuException();
    ret.m_arrayLength = length;
    ret.m_arrayIndex = index;
    ret.m_array = array;
    return ret;
  }
}
