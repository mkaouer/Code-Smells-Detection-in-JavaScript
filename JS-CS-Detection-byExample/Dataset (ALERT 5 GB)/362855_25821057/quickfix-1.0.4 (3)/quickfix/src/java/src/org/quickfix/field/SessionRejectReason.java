package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class SessionRejectReason extends IntField 
{ 
public static final char INVALID_TAG_NUMBER = '0'; 
public static final char REQUIRED_TAG_MISSING = '1'; 
public static final char TAG_NOT_DEFINED_FOR_THIS_MESSAGE_TYPE = '2'; 
public static final char UNDEFINED_TAG = '3'; 
public static final char TAG_SPECIFIED_WITHOUT_A_VALUE = '4'; 
public static final char VALUE_IS_INCORRECT = '5'; 
public static final char INCORRECT_DATA_FORMAT_FOR_VALUE = '6'; 
public static final char DECRYPTION_PROBLEM = '7'; 
public static final char SIGNATURE_PROBLEM = '8'; 
public static final char COMPID_PROBLEM = '9'; 

  public SessionRejectReason() 
  { 
    super(373);
  } 
  public SessionRejectReason(int data) 
  { 
    super(373, data);
  } 
} 
