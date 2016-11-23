package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class Adjustment extends IntField 
{ 

  public Adjustment() 
  { 
    super(334);
  } 
  public Adjustment(int data) 
  { 
    super(334, data);
  } 
} 
