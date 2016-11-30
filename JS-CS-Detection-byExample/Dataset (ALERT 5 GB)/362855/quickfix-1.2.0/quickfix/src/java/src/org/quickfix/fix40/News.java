package org.quickfix.fix40; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class News extends Message 
{ 

  public News() 
  { 
    getHeader().setField(new MsgType("B")); 
  } 
  public News(    
    Text aText ) 
  {  
    getHeader().setField(new MsgType("B")); 
    set(aText);  
  } 

  public void set(OrigTime value) 
  { 
    setField(value); 
  } 
  public OrigTime get(OrigTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrigTime getOrigTime() throws FieldNotFound
  { 
    OrigTime value = new OrigTime();  
    getField(value);  
    return value;  
  } 

  public void set(Urgency value) 
  { 
    setField(value); 
  } 
  public Urgency get(Urgency value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Urgency getUrgency() throws FieldNotFound
  { 
    Urgency value = new Urgency();  
    getField(value);  
    return value;  
  } 

  public void set(RelatdSym value) 
  { 
    setField(value); 
  } 
  public RelatdSym get(RelatdSym value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RelatdSym getRelatdSym() throws FieldNotFound
  { 
    RelatdSym value = new RelatdSym();  
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

  public void set(RawDataLength value) 
  { 
    setField(value); 
  } 
  public RawDataLength get(RawDataLength value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RawDataLength getRawDataLength() throws FieldNotFound
  { 
    RawDataLength value = new RawDataLength();  
    getField(value);  
    return value;  
  } 

  public void set(RawData value) 
  { 
    setField(value); 
  } 
  public RawData get(RawData value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RawData getRawData() throws FieldNotFound
  { 
    RawData value = new RawData();  
    getField(value);  
    return value;  
  } 
} 
