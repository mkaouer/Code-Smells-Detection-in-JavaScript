package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class Shares extends DoubleField 
{ 

  public Shares() 
  { 
    super(53);
  } 
  public Shares(double data) 
  { 
    super(53, data);
  } 
} 
