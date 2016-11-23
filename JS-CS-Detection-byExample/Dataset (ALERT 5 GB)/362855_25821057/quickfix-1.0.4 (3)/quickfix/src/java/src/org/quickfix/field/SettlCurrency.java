package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class SettlCurrency extends DoubleField 
{ 

  public SettlCurrency() 
  { 
    super(120);
  } 
  public SettlCurrency(double data) 
  { 
    super(120, data);
  } 
} 
