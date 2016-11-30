package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class HighPx extends DoubleField 
{ 

  public HighPx() 
  { 
    super(332);
  } 
  public HighPx(double data) 
  { 
    super(332, data);
  } 
} 
