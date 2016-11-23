package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class RoutingType extends IntField 
{ 

  public RoutingType() 
  { 
    super(216);
  } 
  public RoutingType(int data) 
  { 
    super(216, data);
  } 
} 
