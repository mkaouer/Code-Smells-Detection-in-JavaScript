package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class MassQuote extends Message 
{ 

  public MassQuote() 
  { 
    getHeader().setField(new MsgType("i")); 
  } 
  public MassQuote(    
    QuoteID aQuoteID,    
    NoQuoteSets aNoQuoteSets ) 
  {  
    getHeader().setField(new MsgType("i")); 
    set(aQuoteID); 
    set(aNoQuoteSets);  
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

  public void set(DefBidSize value) 
  { 
    setField(value); 
  } 
  public DefBidSize get(DefBidSize value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public DefBidSize getDefBidSize() throws FieldNotFound
  { 
    DefBidSize value = new DefBidSize();  
    getField(value);  
    return value;  
  } 

  public void set(DefOfferSize value) 
  { 
    setField(value); 
  } 
  public DefOfferSize get(DefOfferSize value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public DefOfferSize getDefOfferSize() throws FieldNotFound
  { 
    DefOfferSize value = new DefOfferSize();  
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
