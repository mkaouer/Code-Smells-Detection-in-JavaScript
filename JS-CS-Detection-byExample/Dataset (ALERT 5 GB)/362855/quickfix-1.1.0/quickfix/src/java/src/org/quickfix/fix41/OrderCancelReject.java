package org.quickfix.fix41; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class OrderCancelReject extends Message 
{ 

  public OrderCancelReject() 
  { 
    getHeader().setField(new MsgType("9")); 
  } 
  public OrderCancelReject(    
    OrderID aOrderID,    
    ClOrdID aClOrdID,    
    OrigClOrdID aOrigClOrdID,    
    OrdStatus aOrdStatus ) 
  {  
    getHeader().setField(new MsgType("9")); 
    set(aOrderID); 
    set(aClOrdID); 
    set(aOrigClOrdID); 
    set(aOrdStatus);  
  } 

  public void set(OrderID value) 
  { 
    setField(value); 
  } 
  public OrderID get(OrderID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrderID getOrderID() throws FieldNotFound
  { 
    OrderID value = new OrderID();  
    getField(value);  
    return value;  
  } 

  public void set(SecondaryOrderID value) 
  { 
    setField(value); 
  } 
  public SecondaryOrderID get(SecondaryOrderID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecondaryOrderID getSecondaryOrderID() throws FieldNotFound
  { 
    SecondaryOrderID value = new SecondaryOrderID();  
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

  public void set(OrigClOrdID value) 
  { 
    setField(value); 
  } 
  public OrigClOrdID get(OrigClOrdID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrigClOrdID getOrigClOrdID() throws FieldNotFound
  { 
    OrigClOrdID value = new OrigClOrdID();  
    getField(value);  
    return value;  
  } 

  public void set(OrdStatus value) 
  { 
    setField(value); 
  } 
  public OrdStatus get(OrdStatus value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrdStatus getOrdStatus() throws FieldNotFound
  { 
    OrdStatus value = new OrdStatus();  
    getField(value);  
    return value;  
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

  public void set(CxlRejReason value) 
  { 
    setField(value); 
  } 
  public CxlRejReason get(CxlRejReason value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CxlRejReason getCxlRejReason() throws FieldNotFound
  { 
    CxlRejReason value = new CxlRejReason();  
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
} 
