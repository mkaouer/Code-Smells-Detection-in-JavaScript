package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class QuoteRequest extends Message 
{ 

  public QuoteRequest() 
  { 
    getHeader().setField(new MsgType("R")); 
  } 
  public QuoteRequest(    
    QuoteReqID aQuoteReqID,    
    NoRelatedSym aNoRelatedSym ) 
  {  
    getHeader().setField(new MsgType("R")); 
    set(aQuoteReqID); 
    set(aNoRelatedSym);  
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

  public void set(NoRelatedSym value) 
  { 
    setField(value); 
  } 
  public NoRelatedSym get(NoRelatedSym value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoRelatedSym getNoRelatedSym() throws FieldNotFound
  { 
    NoRelatedSym value = new NoRelatedSym();  
    getField(value);  
    return value;  
  } 
} 
