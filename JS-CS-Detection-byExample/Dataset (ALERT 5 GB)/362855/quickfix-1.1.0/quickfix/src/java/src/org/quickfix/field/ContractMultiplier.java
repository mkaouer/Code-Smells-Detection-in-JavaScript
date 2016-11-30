package org.quickfix.field; 
import org.quickfix.DoubleField; 
import java.util.Date; 

public class ContractMultiplier extends DoubleField 
{ 

  public ContractMultiplier() 
  { 
    super(231);
  } 
  public ContractMultiplier(double data) 
  { 
    super(231, data);
  } 
} 
