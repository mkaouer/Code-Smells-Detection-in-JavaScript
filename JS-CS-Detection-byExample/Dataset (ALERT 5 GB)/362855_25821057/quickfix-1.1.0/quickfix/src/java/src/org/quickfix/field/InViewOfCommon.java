package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class InViewOfCommon extends StringField 
{ 
public static final char HALT_WAS_DUE_TO_COMMON_STOCK_BEING_HALTED = 'Y'; 
public static final char HALT_WAS_NOT_RELATED_TO_A_HALT_OF_THE_COMMON_STOCK = 'N'; 

  public InViewOfCommon() 
  { 
    super(328);
  } 
  public InViewOfCommon(String data) 
  { 
    super(328, data);
  } 
} 
