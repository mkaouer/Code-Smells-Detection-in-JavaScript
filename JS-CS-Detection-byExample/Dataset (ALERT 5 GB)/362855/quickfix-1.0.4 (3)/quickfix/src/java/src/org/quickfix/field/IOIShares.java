package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class IOIShares extends StringField 
{ 
public static final char SMALL = 'S'; 
public static final char MEDIUM = 'M'; 
public static final char LARGE = 'L'; 

  public IOIShares() 
  { 
    super(27);
  } 
  public IOIShares(String data) 
  { 
    super(27, data);
  } 
} 
