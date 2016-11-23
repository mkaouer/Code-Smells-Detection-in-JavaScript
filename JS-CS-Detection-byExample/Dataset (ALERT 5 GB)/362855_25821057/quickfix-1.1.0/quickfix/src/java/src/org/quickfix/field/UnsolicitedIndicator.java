package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class UnsolicitedIndicator extends StringField 
{ 
public static final char MESSAGE_IS_BEING_SENT_UNSOLICITED = 'Y'; 
public static final char MESSAGE_IS_BEING_SENT_AS_A_RESULT_OF_A_PRIOR_REQUEST = 'N'; 

  public UnsolicitedIndicator() 
  { 
    super(325);
  } 
  public UnsolicitedIndicator(String data) 
  { 
    super(325, data);
  } 
} 
