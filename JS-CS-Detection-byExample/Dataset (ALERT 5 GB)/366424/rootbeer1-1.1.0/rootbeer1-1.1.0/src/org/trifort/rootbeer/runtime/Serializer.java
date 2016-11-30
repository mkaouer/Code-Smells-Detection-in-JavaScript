/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class Serializer {

  public Memory mMem;
  public Memory mTextureMem;

  private static final Map<Object, Long> mWriteToGpuCache;
  private static final Map<Long, Object> mReverseWriteToGpuCache;
  private static final Map<Long, Object> mReadFromGpuCache;
  private static Map<Long, Integer> m_classRefToTypeNumber;
  
  static {
    mWriteToGpuCache = new IdentityHashMap<Object, Long>();
    mReverseWriteToGpuCache = new HashMap<Long, Object>();
    mReadFromGpuCache = new HashMap<Long, Object>();
    m_classRefToTypeNumber = new HashMap<Long, Integer>();
  }
  
  public Serializer(Memory mem, Memory texture_mem){
    mMem = mem;
    mTextureMem = texture_mem;
    mReadFromGpuCache.clear();
    mWriteToGpuCache.clear();
    mReverseWriteToGpuCache.clear();
    m_classRefToTypeNumber.clear();
  }
  
  public void writeStaticsToHeap(){
    doWriteStaticsToHeap();
  }

  public long writeToHeap(Object o){
    return writeToHeap(o, true);
  }
  
  private static class WriteCacheResult {
    public long m_Ref;
    public boolean m_NeedToWrite;
    public WriteCacheResult(long ref, boolean need_to_write){
      m_Ref = ref;
      m_NeedToWrite = need_to_write;
    }
  }
  
  public void addClassRef(long ref, int class_number){
    m_classRefToTypeNumber.put(ref, class_number);
  }
  
  public int[] getClassRefArray(){
    int max_type = 0;
    for(int num : m_classRefToTypeNumber.values()){
      if(num > max_type){
        max_type = num;
      }
    }
    int[] ret = new int[max_type+1];
    for(long value : m_classRefToTypeNumber.keySet()){
      int pos = m_classRefToTypeNumber.get(value);
      ret[pos] = (int) (value >> 4);
    }
    return ret;
  }
  
  private static synchronized WriteCacheResult checkWriteCache(Object o, int size, boolean read_only, Memory mem){
    //strings are cached in Java 1.6, we need to make strings individual units
    //for rootbeer so concurrent modifications change different objects
    if(o instanceof String){
      long ref = mem.mallocWithSize(size);
      return new WriteCacheResult(ref, true);
    } else {
      if(mWriteToGpuCache.containsKey(o)){
        long ref = mWriteToGpuCache.get(o);
        return new WriteCacheResult(ref, false);
      }
      long ref = mem.mallocWithSize(size);
      mWriteToGpuCache.put(o, ref);
      mReverseWriteToGpuCache.put(ref, o);
      return new WriteCacheResult(ref, true);
    }
  }
  
  public Object writeCacheFetch(long ref){
    synchronized(mWriteToGpuCache){
      if(mReverseWriteToGpuCache.containsKey(ref)){
        return mReverseWriteToGpuCache.get(ref);
      }
      return null;
    }
  }
  
  public long writeToHeap(Object o, boolean write_data){
    if(o == null)
      return -1;    
    int size = doGetSize(o);
    boolean read_only = false;
    WriteCacheResult result;
    result = checkWriteCache(o, size, read_only, mMem);
    
    if(result.m_NeedToWrite == false){
      return result.m_Ref;
    }
    //if(o == null){
    //  System.out.println("writeToHeap: null at addr: "+result.m_Ref);
    //} else {
    //  System.out.println("writeToHeap: "+o.toString()+" at addr: "+result.m_Ref);
    //}
    doWriteToHeap(o, write_data, result.m_Ref, read_only);
    //BufferPrinter printer = new BufferPrinter();
    //printer.print(mMem, result.m_Ref, 128);
    return result.m_Ref;
  }
  
  protected Object checkCache(long address, Object item){
    synchronized(mReadFromGpuCache){
      if(mReadFromGpuCache.containsKey(address)){
        return mReadFromGpuCache.get(address);
      } else {
        mReadFromGpuCache.put(address, item);
        return item;
      }
    }
  }

  public Object readFromHeap(Object o, boolean read_data, long address){
    synchronized(mReadFromGpuCache){
      if(mReadFromGpuCache.containsKey(address)){
        Object ret = mReadFromGpuCache.get(address);  
        return ret;
      }
    }
    long null_ptr_check = address >> 4;
    if(null_ptr_check == -1){
      return null;
    }
    //if(o == null){
    //  System.out.println("readFromHeap: null. addr: "+address);
    //} else {
    //  System.out.println("readFromHeap: "+o.toString()+". addr: "+address);
    //}
    //BufferPrinter printer = new BufferPrinter();
    //printer.print(mMem, address, 128);
    return doReadFromHeap(o, read_data, address);
  }

  public void readStaticsFromHeap(){
    doReadStaticsFromHeap();
  }
 
  public Object readField(Object base, String name){
    Class cls = base.getClass();
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        Object ret = f.get(base);      
        return ret;
      } catch(Exception ex){
        cls = cls.getSuperclass();
        //java.lang.Throwable.backtrace cannot be found this way, I don't know why.
        if(cls == null){
          return null;
        }
      }
    }
  }

  public Object readStaticField(Class cls, String name){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        Object ret = f.get(null);     
        return ret;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }

  public void writeField(Object base, String name, Object value){
    Class cls = base.getClass();
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.set(base, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticField(Class cls, String name, Object value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      } 
    }
  }
  
  public void writeStaticByteField(Class cls, String name, byte value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setByte(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticBooleanField(Class cls, String name, boolean value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setBoolean(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticCharField(Class cls, String name, char value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setChar(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticShortField(Class cls, String name, short value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setShort(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticIntField(Class cls, String name, int value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setInt(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticLongField(Class cls, String name, long value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setLong(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticFloatField(Class cls, String name, float value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setFloat(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticDoubleField(Class cls, String name, double value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.setDouble(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public abstract void doWriteToHeap(Object o, boolean write_data, long ref, boolean read_only);
  public abstract void doWriteStaticsToHeap();
  public abstract Object doReadFromHeap(Object o, boolean read_data, long ref);
  public abstract void doReadStaticsFromHeap();
  public abstract int doGetSize(Object o);
}
