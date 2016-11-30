package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class News extends Message 
{ 

  public News() 
  { 
    getHeader().setField(new MsgType("B")); 
  } 
  public News(    
    Headline aHeadline,    
    LinesOfText aLinesOfText ) 
  {  
    getHeader().setField(new MsgType("B")); 
    set(aHeadline); 
    set(aLinesOfText);  
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

  public void set(Headline value) 
  { 
    setField(value); 
  } 
  public Headline get(Headline value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Headline getHeadline() throws FieldNotFound
  { 
    Headline value = new Headline();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedHeadlineLen value) 
  { 
    setField(value); 
  } 
  public EncodedHeadlineLen get(EncodedHeadlineLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedHeadlineLen getEncodedHeadlineLen() throws FieldNotFound
  { 
    EncodedHeadlineLen value = new EncodedHeadlineLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedHeadline value) 
  { 
    setField(value); 
  } 
  public EncodedHeadline get(EncodedHeadline value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedHeadline getEncodedHeadline() throws FieldNotFound
  { 
    EncodedHeadline value = new EncodedHeadline();  
    getField(value);  
    return value;  
  } 

  public void set(NoRoutingIDs value) 
  { 
    setField(value); 
  } 
  public NoRoutingIDs get(NoRoutingIDs value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoRoutingIDs getNoRoutingIDs() throws FieldNotFound
  { 
    NoRoutingIDs value = new NoRoutingIDs();  
    getField(value);  
    return value;  
  } 

  public void set(NoRelatedSym value) 
  { 
    setField(value); 
  } 
  public NoRelatedSym get(NoRelatedSym value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoRelatedSym getNoRelatedSym() throws FieldNotFound
  { 
    NoRelatedSym value = new NoRelatedSym();  
    getField(value);  
    return value;  
  } 

  public void set(LinesOfText value) 
  { 
    setField(value); 
  } 
  public LinesOfText get(LinesOfText value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LinesOfText getLinesOfText() throws FieldNotFound
  { 
    LinesOfText value = new LinesOfText();  
    getField(value);  
    return value;  
  } 

  public void set(URLLink value) 
  { 
    setField(value); 
  } 
  public URLLink get(URLLink value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public URLLink getURLLink() throws FieldNotFound
  { 
    URLLink value = new URLLink();  
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
