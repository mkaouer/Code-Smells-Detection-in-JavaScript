package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class GapFillFlag extends StringField 
{ 
public static final char GAP_FILL_MESSAGE_MSGSEQNUM_FIELD_VALID = 'Y'; 
public static final char SEQUENCE_RESET_IGNORE_MSGSEQNUM = 'N'; 

  public GapFillFlag() 
  { 
    super(123);
  } 
  public GapFillFlag(String data) 
  { 
    super(123, data);
  } 
} 
