package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class PutOrCall extends IntField 
{ 
public static final char PUT = '0'; 
public static final char CALL = '1'; 

  public PutOrCall() 
  { 
    super(201);
  } 
  public PutOrCall(int data) 
  { 
    super(201, data);
  } 
} 
