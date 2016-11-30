package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class ExchangeForPhysical extends StringField 
{ 

  public ExchangeForPhysical() 
  { 
    super(411);
  } 
  public ExchangeForPhysical(String data) 
  { 
    super(411, data);
  } 
} 
