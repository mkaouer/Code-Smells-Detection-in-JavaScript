package org.quickfix.fix40; 
import org.quickfix.FieldNotFound; 
import org.quickfix.Group; 
import org.quickfix.field.*; 

public class ListStatusRequest extends Message 
{ 

  public ListStatusRequest() 
  { 
    getHeader().setField(new MsgType("M")); 
  } 
  public ListStatusRequest(    
    org.quickfix.field.ListID aListID ) 
  {  
    getHeader().setField(new MsgType("M")); 
    set(aListID);  
  } 

  public void set(org.quickfix.field.ListID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.ListID get(org.quickfix.field.ListID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.ListID getListID() throws FieldNotFound
  { 
    org.quickfix.field.ListID value = new org.quickfix.field.ListID();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.WaveNo value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.WaveNo get(org.quickfix.field.WaveNo value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.WaveNo getWaveNo() throws FieldNotFound
  { 
    org.quickfix.field.WaveNo value = new org.quickfix.field.WaveNo();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Text value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Text get(org.quickfix.field.Text value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Text getText() throws FieldNotFound
  { 
    org.quickfix.field.Text value = new org.quickfix.field.Text();  
    getField(value);  
    return value;  
  } 
} 
