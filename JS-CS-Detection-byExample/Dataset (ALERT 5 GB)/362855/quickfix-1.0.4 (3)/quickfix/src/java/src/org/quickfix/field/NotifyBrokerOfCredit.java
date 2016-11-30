package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class NotifyBrokerOfCredit extends StringField 
{ 

  public NotifyBrokerOfCredit() 
  { 
    super(208);
  } 
  public NotifyBrokerOfCredit(String data) 
  { 
    super(208, data);
  } 
} 
