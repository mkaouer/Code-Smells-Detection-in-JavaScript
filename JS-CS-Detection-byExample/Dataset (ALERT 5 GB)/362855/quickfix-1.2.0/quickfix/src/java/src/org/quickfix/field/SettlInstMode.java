package org.quickfix.field; 
import org.quickfix.CharField; 
import java.util.Date; 

public class SettlInstMode extends CharField 
{ 
public static final char DEFAULT = '0'; 
public static final char STANDING_INSTRUCTIONS_PROVIDED = '1'; 
public static final char SPECIFIC_ALLOCATION_ACCOUNT_OVERRIDING = '2'; 
public static final char SPECIFIC_ALLOCATION_ACCOUNT_STANDING = '3'; 

  public SettlInstMode() 
  { 
    super(160);
  } 
  public SettlInstMode(char data) 
  { 
    super(160, data);
  } 
} 
