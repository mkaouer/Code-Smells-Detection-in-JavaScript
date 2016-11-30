package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class ReportToExch extends StringField 
{ 
public static final char YES = 'Y'; 
public static final char NO = 'N'; 

  public ReportToExch() 
  { 
    super(113);
  } 
  public ReportToExch(String data) 
  { 
    super(113, data);
  } 
} 
