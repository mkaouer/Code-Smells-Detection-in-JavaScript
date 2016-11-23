package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class BidForwardPoints extends DoubleField 
{ 

  public BidForwardPoints() 
  { 
    super(189);
  } 
  public BidForwardPoints(Double data) 
  { 
    super(189, data);
  } 
} 