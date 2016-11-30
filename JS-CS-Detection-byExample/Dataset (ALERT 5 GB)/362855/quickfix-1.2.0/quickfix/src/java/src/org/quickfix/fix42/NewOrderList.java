package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class NewOrderList extends Message 
{ 

  public NewOrderList() 
  { 
    getHeader().setField(new MsgType("E")); 
  } 
  public NewOrderList(    
    ListID aListID,    
    BidType aBidType,    
    TotNoOrders aTotNoOrders,    
    NoOrders aNoOrders ) 
  {  
    getHeader().setField(new MsgType("E")); 
    set(aListID); 
    set(aBidType); 
    set(aTotNoOrders); 
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

  public void set(BidID value) 
  { 
    setField(value); 
  } 
  public BidID get(BidID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BidID getBidID() throws FieldNotFound
  { 
    BidID value = new BidID();  
    getField(value);  
    return value;  
  } 

  public void set(ClientBidID value) 
  { 
    setField(value); 
  } 
  public ClientBidID get(ClientBidID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClientBidID getClientBidID() throws FieldNotFound
  { 
    ClientBidID value = new ClientBidID();  
    getField(value);  
    return value;  
  } 

  public void set(ProgRptReqs value) 
  { 
    setField(value); 
  } 
  public ProgRptReqs get(ProgRptReqs value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ProgRptReqs getProgRptReqs() throws FieldNotFound
  { 
    ProgRptReqs value = new ProgRptReqs();  
    getField(value);  
    return value;  
  } 

  public void set(BidType value) 
  { 
    setField(value); 
  } 
  public BidType get(BidType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BidType getBidType() throws FieldNotFound
  { 
    BidType value = new BidType();  
    getField(value);  
    return value;  
  } 

  public void set(ProgPeriodInterval value) 
  { 
    setField(value); 
  } 
  public ProgPeriodInterval get(ProgPeriodInterval value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ProgPeriodInterval getProgPeriodInterval() throws FieldNotFound
  { 
    ProgPeriodInterval value = new ProgPeriodInterval();  
    getField(value);  
    return value;  
  } 

  public void set(ListExecInstType value) 
  { 
    setField(value); 
  } 
  public ListExecInstType get(ListExecInstType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListExecInstType getListExecInstType() throws FieldNotFound
  { 
    ListExecInstType value = new ListExecInstType();  
    getField(value);  
    return value;  
  } 

  public void set(ListExecInst value) 
  { 
    setField(value); 
  } 
  public ListExecInst get(ListExecInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListExecInst getListExecInst() throws FieldNotFound
  { 
    ListExecInst value = new ListExecInst();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedListExecInstLen value) 
  { 
    setField(value); 
  } 
  public EncodedListExecInstLen get(EncodedListExecInstLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedListExecInstLen getEncodedListExecInstLen() throws FieldNotFound
  { 
    EncodedListExecInstLen value = new EncodedListExecInstLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedListExecInst value) 
  { 
    setField(value); 
  } 
  public EncodedListExecInst get(EncodedListExecInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedListExecInst getEncodedListExecInst() throws FieldNotFound
  { 
    EncodedListExecInst value = new EncodedListExecInst();  
    getField(value);  
    return value;  
  } 

  public void set(TotNoOrders value) 
  { 
    setField(value); 
  } 
  public TotNoOrders get(TotNoOrders value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TotNoOrders getTotNoOrders() throws FieldNotFound
  { 
    TotNoOrders value = new TotNoOrders();  
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
} 
