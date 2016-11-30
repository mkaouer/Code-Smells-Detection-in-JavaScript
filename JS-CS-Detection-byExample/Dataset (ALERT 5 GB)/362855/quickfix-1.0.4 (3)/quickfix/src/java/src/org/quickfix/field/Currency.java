package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class Currency extends DoubleField 
{ 

  public Currency() 
  { 
    super(15);
  } 
  public Currency(double data) 
  { 
    super(15, data);
  } 
} 
