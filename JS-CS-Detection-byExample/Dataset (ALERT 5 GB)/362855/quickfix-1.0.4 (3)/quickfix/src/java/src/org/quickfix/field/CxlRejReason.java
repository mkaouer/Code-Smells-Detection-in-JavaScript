package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class CxlRejReason extends IntField 
{ 
public static final char TOO_LATE_TO_CANCEL = '0'; 
public static final char UNKNOWN_ORDER = '1'; 
public static final char BROKER_OPTION = '2'; 
public static final char ALREADY_PENDING = '3'; 

  public CxlRejReason() 
  { 
    super(102);
  } 
  public CxlRejReason(int data) 
  { 
    super(102, data);
  } 
} 
