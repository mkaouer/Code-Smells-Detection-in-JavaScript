package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class UnderlyingCurrency extends DoubleField 
{ 

  public UnderlyingCurrency() 
  { 
    super(318);
  } 
  public UnderlyingCurrency(double data) 
  { 
    super(318, data);
  } 
} 
