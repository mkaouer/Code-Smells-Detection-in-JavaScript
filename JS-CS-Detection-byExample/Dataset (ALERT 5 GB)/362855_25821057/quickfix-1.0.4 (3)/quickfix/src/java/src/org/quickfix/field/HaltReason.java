package org.quickfix.field; 
import org.quickfix.CharField; 
import java.util.Date; 

public class HaltReason extends CharField 
{ 

  public HaltReason() 
  { 
    super(327);
  } 
  public HaltReason(char data) 
  { 
    super(327, data);
  } 
} 
