package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class DueToRelated extends StringField 
{ 

  public DueToRelated() 
  { 
    super(329);
  } 
  public DueToRelated(String data) 
  { 
    super(329, data);
  } 
} 
