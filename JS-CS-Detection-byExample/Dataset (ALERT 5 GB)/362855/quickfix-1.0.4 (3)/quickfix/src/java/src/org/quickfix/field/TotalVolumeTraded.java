package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class TotalVolumeTraded extends DoubleField 
{ 

  public TotalVolumeTraded() 
  { 
    super(387);
  } 
  public TotalVolumeTraded(double data) 
  { 
    super(387, data);
  } 
} 
