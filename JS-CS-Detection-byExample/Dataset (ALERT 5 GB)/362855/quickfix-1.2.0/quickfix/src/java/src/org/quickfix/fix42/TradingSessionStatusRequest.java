package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class TradingSessionStatusRequest extends Message 
{ 

  public TradingSessionStatusRequest() 
  { 
    getHeader().setField(new MsgType("g")); 
  } 
  public TradingSessionStatusRequest(    
    TradSesReqID aTradSesReqID,    
    SubscriptionRequestType aSubscriptionRequestType ) 
  {  
    getHeader().setField(new MsgType("g")); 
    set(aTradSesReqID); 
    set(aSubscriptionRequestType);  
  } 

  public void set(TradSesReqID value) 
  { 
    setField(value); 
  } 
  public TradSesReqID get(TradSesReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesReqID getTradSesReqID() throws FieldNotFound
  { 
    TradSesReqID value = new TradSesReqID();  
    getField(value);  
    return value;  
  } 

  public void set(TradingSessionID value) 
  { 
    setField(value); 
  } 
  public TradingSessionID get(TradingSessionID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradingSessionID getTradingSessionID() throws FieldNotFound
  { 
    TradingSessionID value = new TradingSessionID();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesMethod value) 
  { 
    setField(value); 
  } 
  public TradSesMethod get(TradSesMethod value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesMethod getTradSesMethod() throws FieldNotFound
  { 
    TradSesMethod value = new TradSesMethod();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesMode value) 
  { 
    setField(value); 
  } 
  public TradSesMode get(TradSesMode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesMode getTradSesMode() throws FieldNotFound
  { 
    TradSesMode value = new TradSesMode();  
    getField(value);  
    return value;  
  } 

  public void set(SubscriptionRequestType value) 
  { 
    setField(value); 
  } 
  public SubscriptionRequestType get(SubscriptionRequestType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SubscriptionRequestType getSubscriptionRequestType() throws FieldNotFound
  { 
    SubscriptionRequestType value = new SubscriptionRequestType();  
    getField(value);  
    return value;  
  } 
} 
