package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class OfferForwardPoints extends DoubleField 
{ 

  public OfferForwardPoints() 
  { 
    super(191);
  } 
  public OfferForwardPoints(Double data) 
  { 
    super(191, data);
  } 
} 
