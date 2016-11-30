package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class QuoteCancel extends Message 
{ 

  public QuoteCancel() 
  { 
    getHeader().setField(new MsgType("Z")); 
  } 
  public QuoteCancel(    
    QuoteID aQuoteID,    
    QuoteCancelType aQuoteCancelType,    
    NoQuoteEntries aNoQuoteEntries ) 
  {  
    getHeader().setField(new MsgType("Z")); 
    set(aQuoteID); 
    set(aQuoteCancelType); 
    set(aNoQuoteEntries);  
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

  public void set(QuoteCancelType value) 
  { 
    setField(value); 
  } 
  public QuoteCancelType get(QuoteCancelType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteCancelType getQuoteCancelType() throws FieldNotFound
  { 
    QuoteCancelType value = new QuoteCancelType();  
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

  public void set(NoQuoteEntries value) 
  { 
    setField(value); 
  } 
  public NoQuoteEntries get(NoQuoteEntries value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoQuoteEntries getNoQuoteEntries() throws FieldNotFound
  { 
    NoQuoteEntries value = new NoQuoteEntries();  
    getField(value);  
    return value;  
  } 
} 
