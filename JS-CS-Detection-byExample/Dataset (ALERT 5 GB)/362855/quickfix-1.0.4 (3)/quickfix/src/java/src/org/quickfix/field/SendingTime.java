package org.quickfix.field; 
import org.quickfix.UtcTimeStampField; 
import java.util.Date; 

public class SendingTime extends UtcTimeStampField 
{ 

  public SendingTime() 
  { 
    super(52);
  } 
  public SendingTime(Date data) 
  { 
    super(52, data);
  } 
} 
