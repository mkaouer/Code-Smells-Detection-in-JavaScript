package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class NotifyBrokerOfCredit extends StringField 
{ 
public static final char DETAILS_SHOULD_BE_COMMUNICATED = 'Y'; 
public static final char DETAILS_SHOULD_NOT_BE_COMMUNICATED = 'N'; 

  public NotifyBrokerOfCredit() 
  { 
    super(208);
  } 
  public NotifyBrokerOfCredit(String data) 
  { 
    super(208, data);
  } 
} 
