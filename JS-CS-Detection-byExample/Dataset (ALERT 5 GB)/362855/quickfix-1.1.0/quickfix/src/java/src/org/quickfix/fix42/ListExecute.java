package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class ListExecute extends Message 
{ 

  public ListExecute() 
  { 
    getHeader().setField(new MsgType("L")); 
  } 
  public ListExecute(    
    ListID aListID,    
    TransactTime aTransactTime ) 
  {  
    getHeader().setField(new MsgType("L")); 
    set(aListID); 
    set(aTransactTime);  
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
