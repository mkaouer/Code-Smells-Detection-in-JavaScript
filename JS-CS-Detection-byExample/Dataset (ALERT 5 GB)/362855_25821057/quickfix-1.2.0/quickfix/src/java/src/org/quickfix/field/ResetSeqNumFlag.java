package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class ResetSeqNumFlag extends StringField 
{ 
public static final char YES_RESET_SEQUENCE_NUMBERS = 'Y'; 
public static final char NO = 'N'; 

  public ResetSeqNumFlag() 
  { 
    super(141);
  } 
  public ResetSeqNumFlag(String data) 
  { 
    super(141, data);
  } 
} 
