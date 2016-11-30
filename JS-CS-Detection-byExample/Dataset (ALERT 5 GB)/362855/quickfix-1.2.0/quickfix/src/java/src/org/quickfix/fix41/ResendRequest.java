package org.quickfix.fix41; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class ResendRequest extends Message 
{ 

  public ResendRequest() 
  { 
    getHeader().setField(new MsgType("2")); 
  } 
  public ResendRequest(    
    BeginSeqNo aBeginSeqNo,    
    EndSeqNo aEndSeqNo ) 
  {  
    getHeader().setField(new MsgType("2")); 
    set(aBeginSeqNo); 
    set(aEndSeqNo);  
  } 

  public void set(BeginSeqNo value) 
  { 
    setField(value); 
  } 
  public BeginSeqNo get(BeginSeqNo value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BeginSeqNo getBeginSeqNo() throws FieldNotFound
  { 
    BeginSeqNo value = new BeginSeqNo();  
    getField(value);  
    return value;  
  } 

  public void set(EndSeqNo value) 
  { 
    setField(value); 
  } 
  public EndSeqNo get(EndSeqNo value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EndSeqNo getEndSeqNo() throws FieldNotFound
  { 
    EndSeqNo value = new EndSeqNo();  
    getField(value);  
    return value;  
  } 
} 
