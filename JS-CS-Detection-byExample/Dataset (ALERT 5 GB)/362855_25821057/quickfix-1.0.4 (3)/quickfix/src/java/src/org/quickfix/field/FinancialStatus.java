package org.quickfix.field; 
import org.quickfix.CharField; 
import java.util.Date; 

public class FinancialStatus extends CharField 
{ 

  public FinancialStatus() 
  { 
    super(291);
  } 
  public FinancialStatus(char data) 
  { 
    super(291, data);
  } 
} 