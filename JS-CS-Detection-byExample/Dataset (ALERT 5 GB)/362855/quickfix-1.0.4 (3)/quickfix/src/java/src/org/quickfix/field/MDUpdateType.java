package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class MDUpdateType extends IntField 
{ 
public static final char FULL_REFRESH = '0'; 
public static final char INCREMENTAL_REFRESH = '1'; 

  public MDUpdateType() 
  { 
    super(265);
  } 
  public MDUpdateType(int data) 
  { 
    super(265, data);
  } 
} 
