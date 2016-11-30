package org.quickfix.fix41; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Heartbeat extends Message 
{ 

  public Heartbeat() 
  { 
    getHeader().setField(new MsgType("0")); 
  } 

  public void set(TestReqID value) 
  { 
    setField(value); 
  } 
  public TestReqID get(TestReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TestReqID getTestReqID() throws FieldNotFound
  { 
    TestReqID value = new TestReqID();  
    getField(value);  
    return value;  
  } 
} 
