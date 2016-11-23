package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class IDSource extends StringField 
{ 
public static final char CUSIP = '1'; 
public static final char SEDOL = '2'; 
public static final char QUIK = '3'; 
public static final char ISIN_NUMBER = '4'; 
public static final char RIC_CODE = '5'; 
public static final char ISO_CURRENCY_CODE = '6'; 
public static final char ISO_COUNTRY_CODE = '7'; 
public static final char EXCHANGE_SYMBOL = '8'; 
public static final char CONSOLIDATED_TAPE_ASSOCIATION = '9'; 

  public IDSource() 
  { 
    super(22);
  } 
  public IDSource(String data) 
  { 
    super(22, data);
  } 
} 
