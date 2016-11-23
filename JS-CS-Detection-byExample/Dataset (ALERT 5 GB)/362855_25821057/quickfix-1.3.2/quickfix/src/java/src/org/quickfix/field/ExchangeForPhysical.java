package org.quickfix.field; 
import org.quickfix.BooleanField; 
import java.util.Date; 

public class ExchangeForPhysical extends BooleanField 
{ 
public static final boolean TRUE = true; 
public static final boolean FALSE = false; 

  public ExchangeForPhysical() 
  { 
    super(411);
  } 
  public ExchangeForPhysical(boolean data) 
  { 
    super(411, data);
  } 
} 
