package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class AllocStatus extends IntField 
{ 
public static final char ACCEPTED = '0'; 
public static final char REJECTED = '1'; 
public static final char PARTIAL_ACCEPT = '2'; 
public static final char RECEIVED = '3'; 

  public AllocStatus() 
  { 
    super(87);
  } 
  public AllocStatus(int data) 
  { 
    super(87, data);
  } 
} 
