package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class UnsolicitedIndicator extends StringField 
{ 

  public UnsolicitedIndicator() 
  { 
    super(325);
  } 
  public UnsolicitedIndicator(String data) 
  { 
    super(325, data);
  } 
} 
