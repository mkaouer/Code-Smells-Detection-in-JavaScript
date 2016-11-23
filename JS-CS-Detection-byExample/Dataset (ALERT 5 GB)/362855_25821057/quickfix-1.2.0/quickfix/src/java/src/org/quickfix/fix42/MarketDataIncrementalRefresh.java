package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class MarketDataIncrementalRefresh extends Message 
{ 

  public MarketDataIncrementalRefresh() 
  { 
    getHeader().setField(new MsgType("X")); 
  } 
  public MarketDataIncrementalRefresh(    
    NoMDEntries aNoMDEntries ) 
  {  
    getHeader().setField(new MsgType("X")); 
    set(aNoMDEntries);  
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

  public void set(NoMDEntries value) 
  { 
    setField(value); 
  } 
  public NoMDEntries get(NoMDEntries value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoMDEntries getNoMDEntries() throws FieldNotFound
  { 
    NoMDEntries value = new NoMDEntries();  
    getField(value);  
    return value;  
  } 
} 
