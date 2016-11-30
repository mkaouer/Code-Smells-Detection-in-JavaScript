package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class OrdRejReason extends IntField 
{ 
public static final char BROKER_OPTION = '0'; 
public static final char UNKNOWN_SYMBOL = '1'; 
public static final char EXCHANGE_CLOSED = '2'; 
public static final char ORDER_EXCEEDS_LIMIT = '3'; 
public static final char TOO_LATE_TO_ENTER = '4'; 
public static final char UNKNOWN_ORDER = '5'; 
public static final char DUPLICATE_ORDER = '6'; 
public static final char DUPLICATE_VERBALYES = '7'; 
public static final char STALE_ORDER = '8'; 

  public OrdRejReason() 
  { 
    super(103);
  } 
  public OrdRejReason(int data) 
  { 
    super(103, data);
  } 
} 
