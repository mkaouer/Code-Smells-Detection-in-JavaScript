package org.quickfix.fix42; 
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
    ListStatusType aListStatusType,    
    NoRpts aNoRpts,    
    ListOrderStatus aListOrderStatus,    
    RptSeq aRptSeq,    
    TotNoOrders aTotNoOrders,    
    NoOrders aNoOrders ) 
  {  
    getHeader().setField(new MsgType("N")); 
    set(aListID); 
    set(aListStatusType); 
    set(aNoRpts); 
    set(aListOrderStatus); 
    set(aRptSeq); 
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

  public void set(ListStatusType value) 
  { 
    setField(value); 
  } 
  public ListStatusType get(ListStatusType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListStatusType getListStatusType() throws FieldNotFound
  { 
    ListStatusType value = new ListStatusType();  
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

  public void set(ListOrderStatus value) 
  { 
    setField(value); 
  } 
  public ListOrderStatus get(ListOrderStatus value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListOrderStatus getListOrderStatus() throws FieldNotFound
  { 
    ListOrderStatus value = new ListOrderStatus();  
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

  public void set(ListStatusText value) 
  { 
    setField(value); 
  } 
  public ListStatusText get(ListStatusText value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListStatusText getListStatusText() throws FieldNotFound
  { 
    ListStatusText value = new ListStatusText();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedListStatusTextLen value) 
  { 
    setField(value); 
  } 
  public EncodedListStatusTextLen get(EncodedListStatusTextLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedListStatusTextLen getEncodedListStatusTextLen() throws FieldNotFound
  { 
    EncodedListStatusTextLen value = new EncodedListStatusTextLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedListStatusText value) 
  { 
    setField(value); 
  } 
  public EncodedListStatusText get(EncodedListStatusText value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedListStatusText getEncodedListStatusText() throws FieldNotFound
  { 
    EncodedListStatusText value = new EncodedListStatusText();  
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
