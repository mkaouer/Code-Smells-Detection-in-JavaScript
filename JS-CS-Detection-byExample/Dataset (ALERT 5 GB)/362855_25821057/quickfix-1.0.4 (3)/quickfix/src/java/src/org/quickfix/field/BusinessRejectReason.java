package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class BusinessRejectReason extends IntField 
{ 
public static final char OTHER = '0'; 
public static final char UNKOWN_ID = '1'; 
public static final char UNKNOWN_SECURITY = '2'; 
public static final char UNSUPPORTED_MESSAGE_TYPE = '3'; 
public static final char APPLICATION_NOT_AVAILABLE = '4'; 
public static final char CONDITIONALLY_REQUIRED_FIELD_MISSING = '5'; 

  public BusinessRejectReason() 
  { 
    super(380);
  } 
  public BusinessRejectReason(int data) 
  { 
    super(380, data);
  } 
} 
