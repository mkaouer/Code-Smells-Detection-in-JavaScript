package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class SettlLocation extends StringField 
{ 

  public SettlLocation() 
  { 
    super(166);
  } 
  public SettlLocation(String data) 
  { 
    super(166, data);
  } 
} 
