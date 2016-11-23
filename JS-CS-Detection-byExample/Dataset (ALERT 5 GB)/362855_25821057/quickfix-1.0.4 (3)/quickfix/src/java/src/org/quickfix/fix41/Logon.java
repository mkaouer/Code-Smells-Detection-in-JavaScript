package org.quickfix.fix41; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Logon extends Message 
{ 

  public Logon() 
  { 
    getHeader().setField(new MsgType("A")); 
  } 
  public Logon(    
    EncryptMethod aEncryptMethod,    
    HeartBtInt aHeartBtInt ) 
  {  
    getHeader().setField(new MsgType("A")); 
    set(aEncryptMethod); 
    set(aHeartBtInt);  
  } 

  public void set(EncryptMethod value) 
  { 
    setField(value); 
  } 
  public EncryptMethod get(EncryptMethod value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncryptMethod getEncryptMethod() throws FieldNotFound
  { 
    EncryptMethod value = new EncryptMethod();  
    getField(value);  
    return value;  
  } 

  public void set(HeartBtInt value) 
  { 
    setField(value); 
  } 
  public HeartBtInt get(HeartBtInt value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public HeartBtInt getHeartBtInt() throws FieldNotFound
  { 
    HeartBtInt value = new HeartBtInt();  
    getField(value);  
    return value;  
  } 

  public void set(RawDataLength value) 
  { 
    setField(value); 
  } 
  public RawDataLength get(RawDataLength value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RawDataLength getRawDataLength() throws FieldNotFound
  { 
    RawDataLength value = new RawDataLength();  
    getField(value);  
    return value;  
  } 

  public void set(RawData value) 
  { 
    setField(value); 
  } 
  public RawData get(RawData value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RawData getRawData() throws FieldNotFound
  { 
    RawData value = new RawData();  
    getField(value);  
    return value;  
  } 

  public void set(ResetSeqNumFlag value) 
  { 
    setField(value); 
  } 
  public ResetSeqNumFlag get(ResetSeqNumFlag value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ResetSeqNumFlag getResetSeqNumFlag() throws FieldNotFound
  { 
    ResetSeqNumFlag value = new ResetSeqNumFlag();  
    getField(value);  
    return value;  
  } 
} 
