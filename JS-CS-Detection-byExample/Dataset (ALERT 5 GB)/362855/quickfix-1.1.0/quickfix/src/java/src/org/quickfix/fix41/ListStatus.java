package org.quickfix.fix41; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class ListStatus extends Message 
{ 

  public ListStatus() 
  { 
    getHeader().setField(new MsgType("N")); 
  } 
  public ListStatus(    
    ListID aListID,    
    NoRpts aNoRpts,    
    RptSeq aRptSeq,    
    NoOrders aNoOrders ) 
  {  
    getHeader().setField(new MsgType("N")); 
    set(aListID); 
    set(aNoRpts); 
    set(aRptSeq); 
    set(aNoOrders);  
  } 

  public void set(ListID value) 
  { 
    setField(value); 
  } 
  public ListID get(ListID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListID getListID() throws FieldNotFound
  { 
    ListID value = new ListID();  
    getField(value);  
    return value;  
  } 

  public void set(WaveNo value) 
  { 
    setField(value); 
  } 
  public WaveNo get(WaveNo value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public WaveNo getWaveNo() throws FieldNotFound
  { 
    WaveNo value = new WaveNo();  
    getField(value);  
    return value;  
  } 

  public void set(NoRpts value) 
  { 
    setField(value); 
  } 
  public NoRpts get(NoRpts value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoRpts getNoRpts() throws FieldNotFound
  { 
    NoRpts value = new NoRpts();  
    getField(value);  
    return value;  
  } 

  public void set(RptSeq value) 
  { 
    setField(value); 
  } 
  public RptSeq get(RptSeq value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RptSeq getRptSeq() throws FieldNotFound
  { 
    RptSeq value = new RptSeq();  
    getField(value);  
    return value;  
  } 

  public void set(NoOrders value) 
  { 
    setField(value); 
  } 
  public NoOrders get(NoOrders value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoOrders getNoOrders() throws FieldNotFound
  { 
    NoOrders value = new NoOrders();  
    getField(value);  
    return value;  
  } 

  public void set(ClOrdID value) 
  { 
    setField(value); 
  } 
  public ClOrdID get(ClOrdID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClOrdID getClOrdID() throws FieldNotFound
  { 
    ClOrdID value = new ClOrdID();  
    getField(value);  
    return value;  
  } 
} 
