/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

import java.lang.reflect.Field;

public class PrivateFields {

  public byte readByte(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getByte(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public short readShort(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getShort(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public char readChar(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getChar(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public boolean readBoolean(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getBoolean(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public int readInt(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getInt(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public long readLong(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getLong(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public float readFloat(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getFloat(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
    
  public double readDouble(Object obj, String name, String cls_name){
    try {
      return getField(obj, name, cls_name).getDouble(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public void writeByte(Object obj, String name, String cls_name, byte value){
    try {
      getField(obj, name, cls_name).setByte(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public void writeShort(Object obj, String name, String cls_name, short value){
    try {
      getField(obj, name, cls_name).setShort(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public void writeChar(Object obj, String name, String cls_name, char value){
    try {
      getField(obj, name, cls_name).setChar(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeBoolean(Object obj, String name, String cls_name, boolean value){
    try {
      getField(obj, name, cls_name).setBoolean(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeInt(Object obj, String name, String cls_name, int value){
    try {
      getField(obj, name, cls_name).setInt(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeLong(Object obj, String name, String cls_name, long value){
    try {
      getField(obj, name, cls_name).setLong(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeFloat(Object obj, String name, String cls_name, float value){
    try {
      getField(obj, name, cls_name).setFloat(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeDouble(Object obj, String name, String cls_name, double value){
    try {
      getField(obj, name, cls_name).setDouble(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public Field getField(Object base, String name, String cls_name){
    Class cls = null;
    try {
      cls = Class.forName(cls_name);
    } catch(Exception ex){
      ex.printStackTrace(System.out);
      return null;
    }
    
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        return f;
      } catch(Exception ex){
        try {
          cls = cls.getSuperclass();
        } catch(Exception ex2){
          ex2.printStackTrace(System.out);
          return null;
        }
      }
    }
  }
}
