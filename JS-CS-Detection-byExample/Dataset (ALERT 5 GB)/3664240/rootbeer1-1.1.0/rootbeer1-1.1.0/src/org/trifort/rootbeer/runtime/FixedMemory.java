package org.trifort.rootbeer.runtime;

import java.util.ArrayList;
import java.util.List;

public class FixedMemory implements Memory {

  protected long m_address;
  protected long m_size;
  protected long m_reserve;
  protected MemPointer m_staticPointer;
  protected MemPointer m_instancePointer;
  protected MemPointer m_currentPointer;  
  protected List<List<Long>> m_integerList;
    
  public FixedMemory(long size){
    m_reserve = 1024;
    size += m_reserve;
    m_address = malloc(size);    
    m_size = size;
    m_instancePointer = new MemPointer();
    m_staticPointer = new MemPointer();
    m_currentPointer = m_instancePointer;
    m_integerList = new ArrayList<List<Long>>();
  }
  
  protected long currPointer(){
    return m_currentPointer.m_pointer;
  }  
  
  @Override
  public byte readByte() {
    byte ret = doReadByte(currPointer(), m_address);
    incrementAddress(1);
    return ret;
  }

  @Override
  public boolean readBoolean() {
    boolean ret = doReadBoolean(currPointer(), m_address);
    incrementAddress(1);
    return ret;
  }

  @Override
  public short readShort() {
    short ret = doReadShort(currPointer(), m_address);
    incrementAddress(2);
    return ret;
  }

  @Override
  public int readInt() {
    int ret = doReadInt(currPointer(), m_address);
    incrementAddress(4);
    return ret;
  }

  @Override
  public float readFloat() {
    float ret = doReadFloat(currPointer(), m_address);
    incrementAddress(4);
    return ret;
  }

  @Override
  public double readDouble() {
    double ret = doReadDouble(currPointer(), m_address);
    incrementAddress(8);
    return ret;
  }

  @Override
  public long readLong() {
    long ret = doReadLong(currPointer(), m_address);
    incrementAddress(8);
    return ret;
  }
  
  @Override
  public long readRef() {
    long ret = readInt();
    ret = ret << 4;
    return ret;
  }


  @Override
  public void writeByte(byte value) {
    doWriteByte(currPointer(), value, m_address);
    incrementAddress(1);
  }

  @Override
  public void writeBoolean(boolean value) {
    doWriteBoolean(currPointer(), value, m_address);
    incrementAddress(1);
  }

  @Override
  public void writeShort(short value) {
    doWriteShort(currPointer(), value, m_address);
    incrementAddress(2);
  }

  @Override
  public void writeInt(int value) {
    doWriteInt(currPointer(), value, m_address);
    incrementAddress(4);
  }

  @Override
  public void writeRef(long value) {
    value = value >> 4;
    writeInt((int) value);
  }
  
  @Override
  public void writeFloat(float value) {
    doWriteFloat(currPointer(), value, m_address);
    incrementAddress(4);
  }

  @Override
  public void writeDouble(double value) {
    doWriteDouble(currPointer(), value, m_address);
    incrementAddress(8);
  }

  @Override
  public void writeLong(long value) {
    doWriteLong(currPointer(), value, m_address);
    incrementAddress(8);
  }
  
  @Override
  public void readArray(byte[] array){
    doReadByteArray(array, m_address+currPointer(), 0, array.length);  
  }
  
  @Override
  public void readArray(boolean[] array){
    doReadBooleanArray(array, m_address+currPointer(), 0, array.length);  
  }
    
  @Override
  public void readArray(short[] array){
    doReadShortArray(array, m_address+currPointer(), 0, array.length);  
  }
      
  @Override
  public void readArray(int[] array){
    doReadIntArray(array, m_address+currPointer(), 0, array.length);  
  }
    
  @Override
  public void readArray(float[] array){
    doReadFloatArray(array, m_address+currPointer(), 0, array.length);  
  }
  
  @Override
  public void readArray(double[] array){
    doReadDoubleArray(array, m_address+currPointer(), 0, array.length);  
  }
    
  @Override
  public void readArray(long[] array){
    doReadLongArray(array, m_address+currPointer(), 0, array.length);  
  }
    
  @Override
  public void writeArray(byte[] array){
    doWriteByteArray(array, m_address+currPointer(), 0, array.length);
  }
    
  @Override
  public void writeArray(boolean[] array){
    doWriteBooleanArray(array, m_address+currPointer(), 0, array.length);
  }
    
  @Override
  public void writeArray(short[] array){
    doWriteShortArray(array, m_address+currPointer(), 0, array.length);
  }
    
  @Override
  public void writeArray(int[] array){
    doWriteIntArray(array, m_address+currPointer(), 0, array.length);
  }
    
  @Override
  public void writeArray(float[] array){
    doWriteFloatArray(array, m_address+currPointer(), 0, array.length);
  }
  
  @Override
  public void writeArray(double[] array){
    doWriteDoubleArray(array, m_address+currPointer(), 0, array.length);
  }
    
  @Override
  public void writeArray(long[] array){
    doWriteLongArray(array, m_address+currPointer(), 0, array.length);
  }
    
  @Override
  public char readChar(){
    int value = readInt();
    char ret = (char) value;
    return ret;
  }

  @Override
  public void writeChar(char value){
    writeInt(value);
  }
  
  public native void doReadByteArray(byte[] array, long addr, int start, int len);
  public native void doReadBooleanArray(boolean[] array, long addr, int start, int len);
  public native void doReadShortArray(short[] array, long addr, int start, int len);
  public native void doReadIntArray(int[] array, long addr, int start, int len);
  public native void doReadFloatArray(float[] array, long addr, int start, int len);
  public native void doReadDoubleArray(double[] array, long addr, int start, int len);
  public native void doReadLongArray(long[] array, long addr, int start, int len);
  
  public native void doWriteByteArray(byte[] array, long addr, int start, int len);
  public native void doWriteBooleanArray(boolean[] array, long addr, int start, int len);
  public native void doWriteShortArray(short[] array, long addr, int start, int len);
  public native void doWriteIntArray(int[] array, long addr, int start, int len);
  public native void doWriteFloatArray(float[] array, long addr, int start, int len);
  public native void doWriteDoubleArray(double[] array, long addr, int start, int len);
  public native void doWriteLongArray(long[] array, long addr, int start, int len);
  
  
  public native byte doReadByte(long ptr, long cpu_base);
  public native boolean doReadBoolean(long ptr, long cpu_base);
  public native short doReadShort(long ptr, long cpu_base);
  public native int doReadInt(long ptr, long cpu_base);
  public native float doReadFloat(long ptr, long cpu_base);
  public native double doReadDouble(long ptr, long cpu_base);
  public native long doReadLong(long ptr, long cpu_base);
  public native void doWriteByte(long ptr, byte value, long cpu_base);
  public native void doWriteBoolean(long ptr, boolean value, long cpu_base);
  public native void doWriteShort(long ptr, short value, long cpu_base);
  public native void doWriteInt(long ptr, int value, long cpu_base);
  public native void doWriteFloat(long ptr, float value, long cpu_base);
  public native void doWriteDouble(long ptr, double value, long cpu_base);
  public native void doWriteLong(long ptr, long value, long cpu_base);
  
  private native long malloc(long size);
  private native void free(long address);

  @Override
  public void clearHeapEndPtr() {
    m_currentPointer.clearHeapEndPtr();
  }

  @Override
  public long getHeapEndPtr() {
    return m_currentPointer.m_heapEnd;
  }
  
  @Override
  public long getSize() {
    return m_size;
  }
  
  @Override
  public long getAddress() {
    return m_address;
  }

  @Override
  public long getPointer(){
    return m_currentPointer.m_pointer;
  }
  
  @Override
  public void setAddress(long address) {
    m_currentPointer.setAddress(address);
  }

  @Override
  public void incrementAddress(int offset) {
    m_currentPointer.incrementAddress(offset);
  }
    
  @Override
  public long mallocWithSize(int size){
    return m_currentPointer.mallocWithSize(size);
  }
  
  @Override
  public void setPointer(long ptr){
    setAddress(ptr);
  }

  @Override
  public void incPointer(long value){
    incrementAddress((int) value);
  }

  @Override
  public void pushAddress() {
    m_currentPointer.pushAddress();
  }

  @Override
  public void popAddress() {
    m_currentPointer.popAddress();
  }

  @Override
  public List<byte[]> getBuffer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void finishCopy(long size) {
    
  }

  @Override
  public void finishRead() {
    
  }

  @Override
  public void readIntArray(int[] array, int size) {
    for(int i = 0; i < size; ++i){
      array[i] = readInt();
    }
  }

  @Override
  public void useInstancePointer() {
    m_currentPointer = m_instancePointer;
  }

  @Override
  public void useStaticPointer() {
    m_currentPointer = m_staticPointer;
  }
  
  @Override
  public void align(){
    m_currentPointer.align();
  }

  @Override
  public void close() {
    free(m_address);
  }
  
  public void startIntegerList(){
    m_integerList.add(new ArrayList<Long>());
    pushAddress();
  }

  public void addIntegerToList(long value){
    List<Long> top = m_integerList.get(m_integerList.size()-1);
    top.add(value);
  }

  public void endIntegerList(){
    popAddress();
    List<Long> top = m_integerList.get(m_integerList.size()-1);
    for(Long curr : top){
      writeRef(curr);
    }
    m_integerList.remove(m_integerList.size()-1);
  }
  
  public void finishReading(){
    long ptr = m_currentPointer.m_pointer / 8;
    ptr *= 8;
    if(ptr != m_currentPointer.m_pointer){
      ptr += 8;
    }
    setPointer(ptr);
  }
  
  private class MemPointer {
   
    private PointerStack m_stack; 
    private long m_endPointer;
    private long m_pointer;
    private long m_heapEnd;
    
    public MemPointer(){
      m_stack = new PointerStack();
      m_endPointer = 0;
    }
    
    public void popAddress() {
      m_pointer = m_stack.pop();
    }
    
    public void pushAddress() {
      m_stack.push(m_pointer);
    }

    public long mallocWithSize(int size){
      malloc();
      int mod = size % 16;
      if(mod != 0)
        size += (16 - mod);

      long ret = m_endPointer;
      m_endPointer += size;              
      if(ret + size > m_size){
        throw new OutOfMemoryError();
      }        
      m_pointer = ret;
      if(ret > m_heapEnd)
        m_heapEnd = ret;
      
      return ret;
    }
  
    public long malloc(){
      //align all new items on 8 bytes
      long mod = m_heapEnd % 8;
      if(mod != 0)
        m_heapEnd += (8 - mod);
      m_pointer = m_heapEnd;
      return m_heapEnd;
    }

    private void clearHeapEndPtr() {      
      m_heapEnd = 0;
      m_pointer = 0;
      m_endPointer = 0;
    }

    private void setAddress(long address) {
      m_pointer = address;
      if(address > m_heapEnd)
        m_heapEnd = address;
    }

    private void incrementAddress(int offset) { 
      m_pointer += offset;
      if(m_pointer > m_heapEnd){
        m_heapEnd = m_pointer;
      } 
    }

    private void align() {
      long mod = m_pointer % 8;
      if(mod != 0){
        m_pointer += (8 - mod);
      }
      if(m_pointer > m_heapEnd){
        m_heapEnd = m_pointer;
      }
      if(m_pointer > m_endPointer){
        m_endPointer = m_pointer;
      }
    }
  }
}
