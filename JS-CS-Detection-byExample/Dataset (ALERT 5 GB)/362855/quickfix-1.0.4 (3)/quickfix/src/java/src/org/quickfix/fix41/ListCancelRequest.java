package org.quickfix.fix41; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class ListCancelRequest extends Message 
{ 

  public ListCancelRequest() 
  { 
    getHeader().setField(new MsgType("K")); 
  } 
  public ListCancelRequest(    
    ListID aListID ) 
  {  
    getHeader().setField(new MsgType("K")); 
    set(aListID);  
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
