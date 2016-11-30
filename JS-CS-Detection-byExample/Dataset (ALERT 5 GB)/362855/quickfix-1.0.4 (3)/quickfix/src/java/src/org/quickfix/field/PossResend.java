package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class PossResend extends StringField 
{ 

  public PossResend() 
  { 
    super(97);
  } 
  public PossResend(String data) 
  { 
    super(97, data);
  } 
} 
