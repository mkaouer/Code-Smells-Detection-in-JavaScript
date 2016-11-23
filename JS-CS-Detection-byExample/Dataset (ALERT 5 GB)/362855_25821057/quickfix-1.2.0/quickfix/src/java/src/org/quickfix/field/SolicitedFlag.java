package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class SolicitedFlag extends StringField 
{ 
public static final char WAS_SOLCITIED = 'Y'; 
public static final char WAS_NOT_SOLICITED = 'N'; 

  public SolicitedFlag() 
  { 
    super(377);
  } 
  public SolicitedFlag(String data) 
  { 
    super(377, data);
  } 
} 
