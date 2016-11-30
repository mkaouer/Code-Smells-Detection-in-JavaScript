/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import org.trifort.rootbeer.runtime.FixedMemory;

public class AlignmentCheckedMemory extends FixedMemory {

  public AlignmentCheckedMemory(int size){
    super(size);
  }

  public void checkAlignment(int alignment){
    long ptr = getPointer();
    if(ptr % alignment != 0){
      throw new RuntimeException("alignment of: "+alignment+" not working.  ptr: "+ptr);
    }
  }

  @Override
  public int readInt(){
    checkAlignment(4);
    return super.readInt();
  }

  @Override
  public short readShort(){
    checkAlignment(2);
    return super.readShort();
  }

  @Override
  public float readFloat(){
    checkAlignment(4);
    return super.readFloat();
  }

  @Override
  public double readDouble(){
    checkAlignment(8);
    return super.readDouble();
  }

  @Override
  public long readLong(){
    checkAlignment(8);
    return super.readLong();
  }

  @Override
  public void writeInt(int value){
    checkAlignment(4);
    super.writeInt(value);
  }

  @Override
  public void writeShort(short value){
    checkAlignment(2);
    super.writeShort(value);
  }

  @Override
  public void writeFloat(float value){
    checkAlignment(4);
    super.writeFloat(value);
  }

  @Override
  public void writeDouble(double value){
    checkAlignment(8);
    super.writeDouble(value);
  }

  @Override
  public void writeLong(long value){
    checkAlignment(8);
    super.writeLong(value);
  }

}
