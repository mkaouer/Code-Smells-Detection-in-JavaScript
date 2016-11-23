package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class PossDupFlag extends StringField 
{ 
public static final char POSSIBLE_DUPLICATE = 'Y'; 
public static final char ORIGINAL_TRANSMISSION = 'N'; 

  public PossDupFlag() 
  { 
    super(43);
  } 
  public PossDupFlag(String data) 
  { 
    super(43, data);
  } 
} 
