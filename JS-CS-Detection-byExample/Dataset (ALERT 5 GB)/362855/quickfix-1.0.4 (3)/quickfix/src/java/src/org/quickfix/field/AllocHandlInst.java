package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class AllocHandlInst extends IntField 
{ 

  public AllocHandlInst() 
  { 
    super(209);
  } 
  public AllocHandlInst(int data) 
  { 
    super(209, data);
  } 
} 
