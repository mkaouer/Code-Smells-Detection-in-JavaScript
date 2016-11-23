package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class SecurityRequestType extends IntField 
{ 

  public SecurityRequestType() 
  { 
    super(321);
  } 
  public SecurityRequestType(int data) 
  { 
    super(321, data);
  } 
} 
