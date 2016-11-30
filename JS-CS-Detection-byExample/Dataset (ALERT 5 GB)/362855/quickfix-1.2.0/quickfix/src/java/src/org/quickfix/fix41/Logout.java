package org.quickfix.fix41; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Logout extends Message 
{ 

  public Logout() 
  { 
    getHeader().setField(new MsgType("5")); 
  } 

  public void set(Text value) 
  { 
    setField(value); 
  } 
  public Text get(Text value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Text getText() throws FieldNotFound
  { 
    Text value = new Text();  
    getField(value);  
    return value;  
  } 
} 
