package org.quickfix.fix40; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Reject extends Message 
{ 

  public Reject() 
  { 
    getHeader().setField(new MsgType("3")); 
  } 
  public Reject(    
    RefSeqNum aRefSeqNum ) 
  {  
    getHeader().setField(new MsgType("3")); 
    set(aRefSeqNum);  
  } 

  public void set(RefSeqNum value) 
  { 
    setField(value); 
  } 
  public RefSeqNum get(RefSeqNum value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RefSeqNum getRefSeqNum() throws FieldNotFound
  { 
    RefSeqNum value = new RefSeqNum();  
    getField(value);  
    return value;  
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
