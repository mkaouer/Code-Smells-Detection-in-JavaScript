package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class AdvTransType extends StringField 
{ 
public static final char NEW = 'N'; 
public static final char CANCEL = 'C'; 
public static final char REPLACE = 'R'; 

  public AdvTransType() 
  { 
    super(5);
  } 
  public AdvTransType(String data) 
  { 
    super(5, data);
  } 
} 
