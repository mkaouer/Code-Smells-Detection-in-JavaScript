/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.lang.reflect.Field;

public class PrivateFields {

  public byte readByte(Object obj, String name){
    try {
      return getField(obj, name).getByte(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public short readShort(Object obj, String name){
    try {
      return getField(obj, name).getShort(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public char readChar(Object obj, String name){
    try {
      return getField(obj, name).getChar(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public boolean readBoolean(Object obj, String name){
    try {
      return getField(obj, name).getBoolean(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public int readInt(Object obj, String name){
    try {
      return getField(obj, name).getInt(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public long readLong(Object obj, String name){
    try {
      return getField(obj, name).getLong(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public float readFloat(Object obj, String name){
    try {
      return getField(obj, name).getFloat(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
    
  public double readDouble(Object obj, String name){
    try {
      return getField(obj, name).getDouble(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public void writeByte(Object obj, String name, byte value){
    try {
      getField(obj, name).setByte(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public void writeShort(Object obj, String name, short value){
    try {
      getField(obj, name).setShort(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public void writeChar(Object obj, String name, char value){
    try {
      getField(obj, name).setChar(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeBoolean(Object obj, String name, boolean value){
    try {
      getField(obj, name).setBoolean(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeInt(Object obj, String name, int value){
    try {
      getField(obj, name).setInt(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeLong(Object obj, String name, long value){
    try {
      getField(obj, name).setLong(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeFloat(Object obj, String name, float value){
    try {
      getField(obj, name).setFloat(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeDouble(Object obj, String name, double value){
    try {
      getField(obj, name).setDouble(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public Field getField(Object base, String name){
    Class cls = base.getClass();
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        return f;      
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    } 
  }
}
