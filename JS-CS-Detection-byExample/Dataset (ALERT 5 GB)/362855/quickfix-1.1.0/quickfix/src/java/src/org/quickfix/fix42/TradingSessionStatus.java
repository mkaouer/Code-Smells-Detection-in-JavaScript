package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class TradingSessionStatus extends Message 
{ 

  public TradingSessionStatus() 
  { 
    getHeader().setField(new MsgType("h")); 
  } 
  public TradingSessionStatus(    
    TradingSessionID aTradingSessionID,    
    TradSesStatus aTradSesStatus ) 
  {  
    getHeader().setField(new MsgType("h")); 
    set(aTradingSessionID); 
    set(aTradSesStatus);  
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

  public void set(UnsolicitedIndicator value) 
  { 
    setField(value); 
  } 
  public UnsolicitedIndicator get(UnsolicitedIndicator value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public UnsolicitedIndicator getUnsolicitedIndicator() throws FieldNotFound
  { 
    UnsolicitedIndicator value = new UnsolicitedIndicator();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesStatus value) 
  { 
    setField(value); 
  } 
  public TradSesStatus get(TradSesStatus value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesStatus getTradSesStatus() throws FieldNotFound
  { 
    TradSesStatus value = new TradSesStatus();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesStartTime value) 
  { 
    setField(value); 
  } 
  public TradSesStartTime get(TradSesStartTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesStartTime getTradSesStartTime() throws FieldNotFound
  { 
    TradSesStartTime value = new TradSesStartTime();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesOpenTime value) 
  { 
    setField(value); 
  } 
  public TradSesOpenTime get(TradSesOpenTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesOpenTime getTradSesOpenTime() throws FieldNotFound
  { 
    TradSesOpenTime value = new TradSesOpenTime();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesPreCloseTime value) 
  { 
    setField(value); 
  } 
  public TradSesPreCloseTime get(TradSesPreCloseTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesPreCloseTime getTradSesPreCloseTime() throws FieldNotFound
  { 
    TradSesPreCloseTime value = new TradSesPreCloseTime();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesCloseTime value) 
  { 
    setField(value); 
  } 
  public TradSesCloseTime get(TradSesCloseTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesCloseTime getTradSesCloseTime() throws FieldNotFound
  { 
    TradSesCloseTime value = new TradSesCloseTime();  
    getField(value);  
    return value;  
  } 

  public void set(TradSesEndTime value) 
  { 
    setField(value); 
  } 
  public TradSesEndTime get(TradSesEndTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradSesEndTime getTradSesEndTime() throws FieldNotFound
  { 
    TradSesEndTime value = new TradSesEndTime();  
    getField(value);  
    return value;  
  } 

  public void set(TotalVolumeTraded value) 
  { 
    setField(value); 
  } 
  public TotalVolumeTraded get(TotalVolumeTraded value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TotalVolumeTraded getTotalVolumeTraded() throws FieldNotFound
  { 
    TotalVolumeTraded value = new TotalVolumeTraded();  
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
