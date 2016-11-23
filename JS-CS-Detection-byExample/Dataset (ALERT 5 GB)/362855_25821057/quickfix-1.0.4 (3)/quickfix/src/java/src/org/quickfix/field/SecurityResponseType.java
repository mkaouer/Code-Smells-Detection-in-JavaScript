package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class SecurityResponseType extends IntField 
{ 

  public SecurityResponseType() 
  { 
    super(323);
  } 
  public SecurityResponseType(int data) 
  { 
    super(323, data);
  } 
} 
