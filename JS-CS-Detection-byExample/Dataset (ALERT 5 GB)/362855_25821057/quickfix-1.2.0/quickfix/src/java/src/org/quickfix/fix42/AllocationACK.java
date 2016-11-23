package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class AllocationACK extends Message 
{ 

  public AllocationACK() 
  { 
    getHeader().setField(new MsgType("P")); 
  } 
  public AllocationACK(    
    AllocID aAllocID,    
    TradeDate aTradeDate,    
    AllocStatus aAllocStatus ) 
  {  
    getHeader().setField(new MsgType("P")); 
    set(aAllocID); 
    set(aTradeDate); 
    set(aAllocStatus);  
  } 

  public void set(ClientID value) 
  { 
    setField(value); 
  } 
  public ClientID get(ClientID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClientID getClientID() throws FieldNotFound
  { 
    ClientID value = new ClientID();  
    getField(value);  
    return value;  
  } 

  public void set(ExecBroker value) 
  { 
    setField(value); 
  } 
  public ExecBroker get(ExecBroker value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExecBroker getExecBroker() throws FieldNotFound
  { 
    ExecBroker value = new ExecBroker();  
    getField(value);  
    return value;  
  } 

  public void set(AllocID value) 
  { 
    setField(value); 
  } 
  public AllocID get(AllocID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocID getAllocID() throws FieldNotFound
  { 
    AllocID value = new AllocID();  
    getField(value);  
    return value;  
  } 

  public void set(TradeDate value) 
  { 
    setField(value); 
  } 
  public TradeDate get(TradeDate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradeDate getTradeDate() throws FieldNotFound
  { 
    TradeDate value = new TradeDate();  
    getField(value);  
    return value;  
  } 

  public void set(TransactTime value) 
  { 
    setField(value); 
  } 
  public TransactTime get(TransactTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TransactTime getTransactTime() throws FieldNotFound
  { 
    TransactTime value = new TransactTime();  
    getField(value);  
    return value;  
  } 

  public void set(AllocStatus value) 
  { 
    setField(value); 
  } 
  public AllocStatus get(AllocStatus value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocStatus getAllocStatus() throws FieldNotFound
  { 
    AllocStatus value = new AllocStatus();  
    getField(value);  
    return value;  
  } 

  public void set(AllocRejCode value) 
  { 
    setField(value); 
  } 
  public AllocRejCode get(AllocRejCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocRejCode getAllocRejCode() throws FieldNotFound
  { 
    AllocRejCode value = new AllocRejCode();  
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
