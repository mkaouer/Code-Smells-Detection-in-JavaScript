package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class SecurityStatusReqID extends StringField 
{ 

  public SecurityStatusReqID() 
  { 
    super(324);
  } 
  public SecurityStatusReqID(String data) 
  { 
    super(324, data);
  } 
} 
