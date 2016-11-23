package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class CustomerOrFirm extends IntField 
{ 
public static final char CUSTOMER = '0'; 
public static final char FIRM = '1'; 

  public CustomerOrFirm() 
  { 
    super(204);
  } 
  public CustomerOrFirm(int data) 
  { 
    super(204, data);
  } 
} 
