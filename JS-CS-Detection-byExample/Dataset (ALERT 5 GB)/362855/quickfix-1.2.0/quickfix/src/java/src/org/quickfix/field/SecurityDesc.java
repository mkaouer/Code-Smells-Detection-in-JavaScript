package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class SecurityDesc extends StringField 
{ 

  public SecurityDesc() 
  { 
    super(107);
  } 
  public SecurityDesc(String data) 
  { 
    super(107, data);
  } 
} 
