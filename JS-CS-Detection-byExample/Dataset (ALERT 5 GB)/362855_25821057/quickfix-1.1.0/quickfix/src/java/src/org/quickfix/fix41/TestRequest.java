package org.quickfix.fix41; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class TestRequest extends Message 
{ 

  public TestRequest() 
  { 
    getHeader().setField(new MsgType("1")); 
  } 
  public TestRequest(    
    TestReqID aTestReqID ) 
  {  
    getHeader().setField(new MsgType("1")); 
    set(aTestReqID);  
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
