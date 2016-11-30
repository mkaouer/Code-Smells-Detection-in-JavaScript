package org.trifort.rootbeer.runtime;

import java.util.List;

public interface Memory {
  
  public byte readByte();
  public boolean readBoolean();
  public short readShort();
  public char readChar();
  public int readInt();
  public float readFloat();
  public double readDouble();
  public long readLong();
  public long readRef();
  public void readArray(byte[] array);
  public void readArray(boolean[] array);
  public void readArray(short[] array);
  public void readArray(int[] array);
  public void readArray(float[] array);
  public void readArray(double[] array);
  public void readArray(long[] array);
  
  public void writeByte(byte value);
  public void writeBoolean(boolean value);
  public void writeChar(char value);
  public void writeShort(short value);
  public void writeInt(int value);
  public void writeFloat(float value);
  public void writeDouble(double value);
  public void writeLong(long value);
  public void writeRef(long value);
  public void writeArray(byte[] array);
  public void writeArray(boolean[] array);
  public void writeArray(short[] array);
  public void writeArray(int[] array);
  public void writeArray(float[] array);
  public void writeArray(double[] array);
  public void writeArray(long[] array);
  
  public void incrementAddress(int offset);
  public long mallocWithSize(int size);
  public void setPointer(long ptr);
  public void incPointer(long value);
  public void pushAddress();
  public void popAddress();
  public List<byte[]> getBuffer();
  public void finishCopy(long size);
  public void finishRead();
  public void readIntArray(int[] array, int size);
  public void useInstancePointer();
  public void useStaticPointer();
  public void align();
  public void clearHeapEndPtr();
  public long getHeapEndPtr();
  public long getPointer();
  public long getSize();
  public long getAddress();
  public void setAddress(long address);
  public void close();

  public void startIntegerList();
  public void addIntegerToList(long value);
  public void endIntegerList();
  public void finishReading();
}
