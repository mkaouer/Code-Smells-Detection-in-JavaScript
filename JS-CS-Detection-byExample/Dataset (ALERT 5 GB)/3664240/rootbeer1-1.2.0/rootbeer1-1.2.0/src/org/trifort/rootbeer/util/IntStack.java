/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

public class IntStack {

  long[] mData;
  int mAlloc;
  int mTopPtr;

  public IntStack(){
    mTopPtr = -1;
    mAlloc = 20;
    mData = new long[20];
  }

  public long pop(){
    long ret = mData[mTopPtr];
    --mTopPtr;
    return ret;
  }

  public void push(long value){
    ++mTopPtr;
    if(mTopPtr >= mAlloc){
      mAlloc *= 2;
      long[] temp = new long[mAlloc];
      for(int i = 0; i < mTopPtr-1; ++i)
        temp[i] = mData[i];
      mData = temp;
    }
    mData[mTopPtr] = value;
  }
}
