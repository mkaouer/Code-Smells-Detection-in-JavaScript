package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class IOINaturalFlag extends StringField 
{ 
public static final char NATURAL = 'Y'; 
public static final char NOT_NATURAL = 'N'; 

  public IOINaturalFlag() 
  { 
    super(130);
  } 
  public IOINaturalFlag(String data) 
  { 
    super(130, data);
  } 
} 
