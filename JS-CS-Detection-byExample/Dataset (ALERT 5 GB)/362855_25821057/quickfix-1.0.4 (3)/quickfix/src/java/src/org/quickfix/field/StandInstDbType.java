package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class StandInstDbType extends IntField 
{ 
public static final char OTHER = '0'; 
public static final char DTC_SID = '1'; 
public static final char THOMSON_ALERT = '2'; 
public static final char A_GLOBAL_CUSTODIAN = '3'; 

  public StandInstDbType() 
  { 
    super(169);
  } 
  public StandInstDbType(int data) 
  { 
    super(169, data);
  } 
} 
