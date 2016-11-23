package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class TradSesMode extends IntField 
{ 

  public TradSesMode() 
  { 
    super(339);
  } 
  public TradSesMode(int data) 
  { 
    super(339, data);
  } 
} 
