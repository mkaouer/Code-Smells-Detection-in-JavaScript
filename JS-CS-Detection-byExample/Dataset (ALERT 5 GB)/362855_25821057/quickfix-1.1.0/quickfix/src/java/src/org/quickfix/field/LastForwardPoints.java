package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class LastForwardPoints extends DoubleField 
{ 

  public LastForwardPoints() 
  { 
    super(195);
  } 
  public LastForwardPoints(Double data) 
  { 
    super(195, data);
  } 
} 
