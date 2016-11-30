package org.quickfix.field; 
import org.quickfix.CharField; 
import java.util.Date; 

public class MDReqRejReason extends CharField 
{ 
public static final char UNKNOWN_SYMBOL = '0'; 
public static final char DUPLICATE_MDREQID = '1'; 
public static final char INSUFFICIENT_BANDWIDTH = '2'; 
public static final char INSUFFICIENT_PERMISSIONS = '3'; 
public static final char UNSUPPORTED_SUBSCRIPTIONREQUESTTYPE = '4'; 
public static final char UNSUPPORTED_MARKETDEPTH = '5'; 
public static final char UNSUPPORTED_MDUPDATETYPE = '6'; 
public static final char UNSUPPORTED_AGGREGATEDBOOK = '7'; 
public static final char UNSUPPORTED_MDENTRYTYPE = '8'; 

  public MDReqRejReason() 
  { 
    super(281);
  } 
  public MDReqRejReason(char data) 
  { 
    super(281, data);
  } 
} 
