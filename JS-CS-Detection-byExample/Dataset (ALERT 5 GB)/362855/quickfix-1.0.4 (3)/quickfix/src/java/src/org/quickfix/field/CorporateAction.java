package org.quickfix.field; 
import org.quickfix.CharField; 
import java.util.Date; 

public class CorporateAction extends CharField 
{ 

  public CorporateAction() 
  { 
    super(292);
  } 
  public CorporateAction(char data) 
  { 
    super(292, data);
  } 
} 
