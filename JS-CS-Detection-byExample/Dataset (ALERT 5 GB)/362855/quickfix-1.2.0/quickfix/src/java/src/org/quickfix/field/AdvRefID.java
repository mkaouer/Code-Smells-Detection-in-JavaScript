package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class AdvRefID extends StringField 
{ 

  public AdvRefID() 
  { 
    super(3);
  } 
  public AdvRefID(String data) 
  { 
    super(3, data);
  } 
} 
