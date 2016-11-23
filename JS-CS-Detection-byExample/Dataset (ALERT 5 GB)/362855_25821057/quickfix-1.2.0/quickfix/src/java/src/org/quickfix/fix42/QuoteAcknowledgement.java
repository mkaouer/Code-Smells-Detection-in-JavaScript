package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class QuoteAcknowledgement extends Message 
{ 

  public QuoteAcknowledgement() 
  { 
    getHeader().setField(new MsgType("b")); 
  } 
  public QuoteAcknowledgement(    
    QuoteAckStatus aQuoteAckStatus ) 
  {  
    getHeader().setField(new MsgType("b")); 
    set(aQuoteAckStatus);  
  } 

  public void set(QuoteReqID value) 
  { 
    setField(value); 
  } 
  public QuoteReqID get(QuoteReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteReqID getQuoteReqID() throws FieldNotFound
  { 
    QuoteReqID value = new QuoteReqID();  
    getField(value);  
    return value;  
  } 

  public void set(QuoteID value) 
  { 
    setField(value); 
  } 
  public QuoteID get(QuoteID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteID getQuoteID() throws FieldNotFound
  { 
    QuoteID value = new QuoteID();  
    getField(value);  
    return value;  
  } 

  public void set(QuoteAckStatus value) 
  { 
    setField(value); 
  } 
  public QuoteAckStatus get(QuoteAckStatus value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteAckStatus getQuoteAckStatus() throws FieldNotFound
  { 
    QuoteAckStatus value = new QuoteAckStatus();  
    getField(value);  
    return value;  
  } 

  public void set(QuoteRejectReason value) 
  { 
    setField(value); 
  } 
  public QuoteRejectReason get(QuoteRejectReason value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteRejectReason getQuoteRejectReason() throws FieldNotFound
  { 
    QuoteRejectReason value = new QuoteRejectReason();  
    getField(value);  
    return value;  
  } 

  public void set(QuoteResponseLevel value) 
  { 
    setField(value); 
  } 
  public QuoteResponseLevel get(QuoteResponseLevel value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteResponseLevel getQuoteResponseLevel() throws FieldNotFound
  { 
    QuoteResponseLevel value = new QuoteResponseLevel();  
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

  public void set(NoQuoteSets value) 
  { 
    setField(value); 
  } 
  public NoQuoteSets get(NoQuoteSets value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoQuoteSets getNoQuoteSets() throws FieldNotFound
  { 
    NoQuoteSets value = new NoQuoteSets();  
    getField(value);  
    return value;  
  } 
} 
