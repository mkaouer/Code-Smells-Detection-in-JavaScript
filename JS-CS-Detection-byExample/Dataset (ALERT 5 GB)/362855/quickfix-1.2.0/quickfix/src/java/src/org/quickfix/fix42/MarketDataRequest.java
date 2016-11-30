package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class MarketDataRequest extends Message 
{ 

  public MarketDataRequest() 
  { 
    getHeader().setField(new MsgType("V")); 
  } 
  public MarketDataRequest(    
    MDReqID aMDReqID,    
    SubscriptionRequestType aSubscriptionRequestType,    
    MarketDepth aMarketDepth,    
    NoMDEntryTypes aNoMDEntryTypes,    
    NoRelatedSym aNoRelatedSym ) 
  {  
    getHeader().setField(new MsgType("V")); 
    set(aMDReqID); 
    set(aSubscriptionRequestType); 
    set(aMarketDepth); 
    set(aNoMDEntryTypes); 
    set(aNoRelatedSym);  
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

  public void set(MarketDepth value) 
  { 
    setField(value); 
  } 
  public MarketDepth get(MarketDepth value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MarketDepth getMarketDepth() throws FieldNotFound
  { 
    MarketDepth value = new MarketDepth();  
    getField(value);  
    return value;  
  } 

  public void set(MDUpdateType value) 
  { 
    setField(value); 
  } 
  public MDUpdateType get(MDUpdateType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MDUpdateType getMDUpdateType() throws FieldNotFound
  { 
    MDUpdateType value = new MDUpdateType();  
    getField(value);  
    return value;  
  } 

  public void set(AggregatedBook value) 
  { 
    setField(value); 
  } 
  public AggregatedBook get(AggregatedBook value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AggregatedBook getAggregatedBook() throws FieldNotFound
  { 
    AggregatedBook value = new AggregatedBook();  
    getField(value);  
    return value;  
  } 

  public void set(NoMDEntryTypes value) 
  { 
    setField(value); 
  } 
  public NoMDEntryTypes get(NoMDEntryTypes value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoMDEntryTypes getNoMDEntryTypes() throws FieldNotFound
  { 
    NoMDEntryTypes value = new NoMDEntryTypes();  
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
