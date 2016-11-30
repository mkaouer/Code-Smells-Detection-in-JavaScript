package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class TradingSessionID extends StringField 
{ 

  public TradingSessionID() 
  { 
    super(336);
  } 
  public TradingSessionID(String data) 
  { 
    super(336, data);
  } 
} 
