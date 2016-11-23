package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class SequenceReset extends Message 
{ 

  public SequenceReset() 
  { 
    getHeader().setField(new MsgType("4")); 
  } 
  public SequenceReset(    
    NewSeqNo aNewSeqNo ) 
  {  
    getHeader().setField(new MsgType("4")); 
    set(aNewSeqNo);  
  } 

  public void set(GapFillFlag value) 
  { 
    setField(value); 
  } 
  public GapFillFlag get(GapFillFlag value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public GapFillFlag getGapFillFlag() throws FieldNotFound
  { 
    GapFillFlag value = new GapFillFlag();  
    getField(value);  
    return value;  
  } 

  public void set(NewSeqNo value) 
  { 
    setField(value); 
  } 
  public NewSeqNo get(NewSeqNo value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NewSeqNo getNewSeqNo() throws FieldNotFound
  { 
    NewSeqNo value = new NewSeqNo();  
    getField(value);  
    return value;  
  } 
} 
