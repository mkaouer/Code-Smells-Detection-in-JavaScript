package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class SecurityTradingStatus extends IntField 
{ 

  public SecurityTradingStatus() 
  { 
    super(326);
  } 
  public SecurityTradingStatus(int data) 
  { 
    super(326, data);
  } 
} 
