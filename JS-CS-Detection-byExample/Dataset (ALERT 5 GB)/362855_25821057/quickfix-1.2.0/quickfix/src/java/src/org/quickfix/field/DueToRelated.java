package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class DueToRelated extends StringField 
{ 
public static final char HALT_WAS_DUE_TO_RELATED_SECURITY_BEING_HALTED = 'Y'; 
public static final char HALT_WAS_NOT_RELATED_TO_A_HALT_OF_THE_RELATED_SECURITY = 'N'; 

  public DueToRelated() 
  { 
    super(329);
  } 
  public DueToRelated(String data) 
  { 
    super(329, data);
  } 
} 
