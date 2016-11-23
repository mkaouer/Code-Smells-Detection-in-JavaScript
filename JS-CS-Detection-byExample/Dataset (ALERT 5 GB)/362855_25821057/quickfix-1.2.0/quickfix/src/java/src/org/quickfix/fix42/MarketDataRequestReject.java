package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class MarketDataRequestReject extends Message 
{ 

  public MarketDataRequestReject() 
  { 
    getHeader().setField(new MsgType("Y")); 
  } 
  public MarketDataRequestReject(    
    MDReqID aMDReqID ) 
  {  
    getHeader().setField(new MsgType("Y")); 
    set(aMDReqID);  
  } 

  public void set(MDReqID value) 
  { 
    setField(value); 
  } 
  public MDReqID get(MDReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MDReqID getMDReqID() throws FieldNotFound
  { 
    MDReqID value = new MDReqID();  
    getField(value);  
    return value;  
  } 

  public void set(MDReqRejReason value) 
  { 
    setField(value); 
  } 
  public MDReqRejReason get(MDReqRejReason value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MDReqRejReason getMDReqRejReason() throws FieldNotFound
  { 
    MDReqRejReason value = new MDReqRejReason();  
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
