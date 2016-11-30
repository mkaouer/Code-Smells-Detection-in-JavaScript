package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class ForexReq extends StringField 
{ 
public static final char YES = 'Y'; 
public static final char NO = 'N'; 

  public ForexReq() 
  { 
    super(121);
  } 
  public ForexReq(String data) 
  { 
    super(121, data);
  } 
} 
