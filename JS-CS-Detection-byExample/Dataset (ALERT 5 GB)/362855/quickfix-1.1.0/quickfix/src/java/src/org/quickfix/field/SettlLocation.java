package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class SettlLocation extends StringField 
{ 
public static final String CEDEL = "CED"; 
public static final String DEPOSITORY_TRUST_COMPANY = "DTC"; 
public static final String EUROCLEAR = "EUR"; 
public static final String FEDERAL_BOOK_ENTRY = "FED"; 
public static final String PHYSICAL = "PNY"; 
public static final String PARTICIPANT_TRUST_COMPANY = "PTC"; 
public static final String LOCAL_MARKET_SETTLE_LOCATION = "ISO"; 

  public SettlLocation() 
  { 
    super(166);
  } 
  public SettlLocation(String data) 
  { 
    super(166, data);
  } 
} 
