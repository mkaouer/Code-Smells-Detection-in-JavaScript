package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class EncodedSubjectLen extends IntField 
{ 

  public EncodedSubjectLen() 
  { 
    super(356);
  } 
  public EncodedSubjectLen(int data) 
  { 
    super(356, data);
  } 
} 
