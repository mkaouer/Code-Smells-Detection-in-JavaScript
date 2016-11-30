package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class SecurityType extends StringField 
{ 
public static final char UNKNOWN = '?'; 

  public SecurityType() 
  { 
    super(167);
  } 
  public SecurityType(String data) 
  { 
    super(167, data);
  } 
} 
