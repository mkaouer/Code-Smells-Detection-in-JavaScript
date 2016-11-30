/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

import java.util.ArrayList;
import java.util.List;

public abstract class Memory_old {

  private Pointer m_StaticPointer;
  private Pointer m_InstancePointer;
  private Pointer m_CurrPointer;
  private List<List<Long>> m_IntegerList;

  public Memory_old(){
    m_StaticPointer = new Pointer();
    m_InstancePointer = new Pointer();
    m_CurrPointer = m_InstancePointer;
    m_IntegerList = new ArrayList<List<Long>>();
  }

  public void startIntegerList(){
    m_IntegerList.add(new ArrayList<Long>());
    pushAddress();
  }

  public void addIntegerToList(long value){
    List<Long> top = m_IntegerList.get(m_IntegerList.size()-1);
    top.add(value);
  }

  public void endIntegerList(){
    popAddress();
    List<Long> top = m_IntegerList.get(m_IntegerList.size()-1);
    for(Long curr : top){
      writeRef(curr);
    }
    m_IntegerList.remove(m_IntegerList.size()-1);
  }

  public void clearHeapEndPtr() {
    m_CurrPointer.m_EndPointer = 0;
    m_CurrPointer.m_Pointer = 0;
    m_CurrPointer.m_MallocPointer = 0;
  }

  public long getHeapEndPtr() {
    return m_CurrPointer.m_EndPointer;
  }

  public void align(){
    //align all new items on 8 bytes
    long mod = m_CurrPointer.m_Pointer % 8;
    if(mod != 0)
      m_CurrPointer.m_Pointer += (8 - mod);
    if(m_CurrPointer.m_Pointer > m_CurrPointer.m_EndPointer){
      m_CurrPointer.m_EndPointer = m_CurrPointer.m_Pointer;  
    }
  }
  
  public void finishReading(){
    long ptr = m_CurrPointer.m_Pointer / 8;
    ptr *= 8;
    if(ptr != m_CurrPointer.m_Pointer){
      ptr += 8;
    }
    setPointer(ptr);
  }

  public long getPointer(){
    return m_CurrPointer.m_Pointer;
  }

  public void setPointer(long ptr){
    m_CurrPointer.m_Pointer = ptr;
  }

  public void incPointer(long value){
    m_CurrPointer.m_Pointer += value;
  }

  public char readChar(){
    int value = readInt();
    char ret = (char) value;
    return ret;
  }

  public void writeChar(char value){
    writeInt(value);
  }
  
  
  public void useInstancePointer() {
    m_CurrPointer = m_InstancePointer;
  }

  public void useStaticPointer() {
    m_CurrPointer = m_StaticPointer;
  }

  public long mallocWithSize(int size){
    long mod = size % 16;
    if(mod != 0){
      size += (16 - mod);  
    }
    
    long ret = m_CurrPointer.m_EndPointer;
    m_CurrPointer.m_EndPointer += size;
    m_CurrPointer.m_Pointer = ret;
    
    return ret;
  }
  
  private class Pointer {
    public long m_Pointer;
    public long m_EndPointer;
    public long m_MallocPointer;
    
    public Pointer(){      
      m_Pointer = 0;
      m_EndPointer = 0;
      m_MallocPointer = 0;
    }
  }

  public abstract byte readByte();
  public abstract boolean readBoolean();
  public abstract short readShort();
  public abstract int readInt();
  public abstract float readFloat();
  public abstract double readDouble();
  public abstract long readLong();
  public abstract long readRef();
  public abstract void readArray(byte[] array);
  public abstract void readArray(boolean[] array);
  public abstract void readArray(short[] array);
  public abstract void readArray(int[] array);
  public abstract void readArray(float[] array);
  public abstract void readArray(double[] array);
  public abstract void readArray(long[] array);

  public abstract void writeByte(byte value);
  public abstract void writeBoolean(boolean value);
  public abstract void writeShort(short value);
  public abstract void writeInt(int value);
  public abstract void writeFloat(float value);
  public abstract void writeDouble(double value);
  public abstract void writeLong(long value);
  public abstract void writeRef(long value);
  public abstract void writeArray(byte[] array);
  public abstract void writeArray(boolean[] array);
  public abstract void writeArray(short[] array);
  public abstract void writeArray(int[] array);
  public abstract void writeArray(float[] array);
  public abstract void writeArray(double[] array);
  public abstract void writeArray(long[] array);

  public abstract void setAddress(long address);
  public abstract void incrementAddress(int offset);

  public abstract void pushAddress();
  public abstract void popAddress();

  public abstract List<byte[]> getBuffer();

  public abstract void finishCopy(long size);
  public abstract void finishRead();
  public abstract void readIntArray(int[] array, int size);
  
  public void checkAlignment(int alignment){
  }
}
