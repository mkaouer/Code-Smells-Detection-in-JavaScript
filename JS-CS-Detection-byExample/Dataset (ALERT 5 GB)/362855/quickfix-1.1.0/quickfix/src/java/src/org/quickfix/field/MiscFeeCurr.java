package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class MiscFeeCurr extends DoubleField 
{ 

  public MiscFeeCurr() 
  { 
    super(138);
  } 
  public MiscFeeCurr(double data) 
  { 
    super(138, data);
  } 
} 
