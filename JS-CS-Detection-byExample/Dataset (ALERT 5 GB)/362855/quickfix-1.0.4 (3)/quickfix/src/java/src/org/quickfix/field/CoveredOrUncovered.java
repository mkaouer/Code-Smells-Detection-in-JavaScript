package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class CoveredOrUncovered extends IntField 
{ 
public static final char COVERED = '0'; 
public static final char UNCOVERED = '1'; 

  public CoveredOrUncovered() 
  { 
    super(203);
  } 
  public CoveredOrUncovered(int data) 
  { 
    super(203, data);
  } 
} 
