package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class BusinessMessageReject extends Message 
{ 

  public BusinessMessageReject() 
  { 
    getHeader().setField(new MsgType("j")); 
  } 
  public BusinessMessageReject(    
    RefMsgType aRefMsgType,    
    BusinessRejectReason aBusinessRejectReason ) 
  {  
    getHeader().setField(new MsgType("j")); 
    set(aRefMsgType); 
    set(aBusinessRejectReason);  
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

  public void set(BusinessRejectRefID value) 
  { 
    setField(value); 
  } 
  public BusinessRejectRefID get(BusinessRejectRefID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BusinessRejectRefID getBusinessRejectRefID() throws FieldNotFound
  { 
    BusinessRejectRefID value = new BusinessRejectRefID();  
    getField(value);  
    return value;  
  } 

  public void set(BusinessRejectReason value) 
  { 
    setField(value); 
  } 
  public BusinessRejectReason get(BusinessRejectReason value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BusinessRejectReason getBusinessRejectReason() throws FieldNotFound
  { 
    BusinessRejectReason value = new BusinessRejectReason();  
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
