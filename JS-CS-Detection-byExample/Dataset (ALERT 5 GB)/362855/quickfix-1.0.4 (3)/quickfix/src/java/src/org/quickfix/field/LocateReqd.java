package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class LocateReqd extends StringField 
{ 
public static final char YES = 'Y'; 
public static final char NO = 'N'; 

  public LocateReqd() 
  { 
    super(114);
  } 
  public LocateReqd(String data) 
  { 
    super(114, data);
  } 
} 
