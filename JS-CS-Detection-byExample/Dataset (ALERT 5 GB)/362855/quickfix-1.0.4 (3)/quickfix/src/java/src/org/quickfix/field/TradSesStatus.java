package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class TradSesStatus extends IntField 
{ 

  public TradSesStatus() 
  { 
    super(340);
  } 
  public TradSesStatus(int data) 
  { 
    super(340, data);
  } 
} 
