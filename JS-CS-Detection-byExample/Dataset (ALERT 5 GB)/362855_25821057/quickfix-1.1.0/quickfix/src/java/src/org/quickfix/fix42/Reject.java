package org.quickfix.fix42; 
import org.quickfix.Message; 
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

  public void set(RefTagID value) 
  { 
    setField(value); 
  } 
  public RefTagID get(RefTagID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RefTagID getRefTagID() throws FieldNotFound
  { 
    RefTagID value = new RefTagID();  
    getField(value);  
    return value;  
  } 

  public void set(RefMsgType value) 
  { 
    setField(value); 
  } 
  public RefMsgType get(RefMsgType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RefMsgType getRefMsgType() throws FieldNotFound
  { 
    RefMsgType value = new RefMsgType();  
    getField(value);  
    return value;  
  } 

  public void set(SessionRejectReason value) 
  { 
    setField(value); 
  } 
  public SessionRejectReason get(SessionRejectReason value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SessionRejectReason getSessionRejectReason() throws FieldNotFound
  { 
    SessionRejectReason value = new SessionRejectReason();  
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

  public void set(EncodedTextLen value) 
  { 
    setField(value); 
  } 
  public EncodedTextLen get(EncodedTextLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedTextLen getEncodedTextLen() throws FieldNotFound
  { 
    EncodedTextLen value = new EncodedTextLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedText value) 
  { 
    setField(value); 
  } 
  public EncodedText get(EncodedText value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedText getEncodedText() throws FieldNotFound
  { 
    EncodedText value = new EncodedText();  
    getField(value);  
    return value;  
  } 
} 
