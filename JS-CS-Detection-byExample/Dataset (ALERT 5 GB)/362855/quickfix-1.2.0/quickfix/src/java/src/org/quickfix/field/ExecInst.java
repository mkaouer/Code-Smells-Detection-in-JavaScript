package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class ExecInst extends StringField 
{ 
public static final char NOT_HELD = '1'; 
public static final char WORK = '2'; 
public static final char GO_ALONG = '3'; 
public static final char OVER_THE_DAY = '4'; 
public static final char HELD = '5'; 
public static final char PARTICIPATE_DONT_INITIATE = '6'; 
public static final char STRICT_SCALE = '7'; 
public static final char TRY_TO_SCALE = '8'; 
public static final char STAY_ON_BIDSIDE = '9'; 
public static final char STAY_ON_OFFERSIDE = '0'; 
public static final char NO_CROSS = 'A'; 
public static final char OK_TO_CROSS = 'B'; 
public static final char CALL_FIRST = 'C'; 
public static final char PERCENT_OF_VOLUME = 'D'; 
public static final char DO_NOT_INCREASE_DNI = 'E'; 
public static final char DO_NOT_REDUCE_DNR = 'F'; 
public static final char ALL_OR_NONE_AON = 'G'; 
public static final char INSTITUTIONS_ONLY = 'I'; 
public static final char LAST_PEG = 'L'; 
public static final char MIDPRICE_PEG = 'M'; 
public static final char NONNEGOTIABLE = 'N'; 
public static final char OPENING_PEG = 'O'; 
public static final char PRIMARY_PEG = 'R'; 
public static final char SUSPEND = 'S'; 
public static final char FIXED_PEG = 'T'; 
public static final char CUSTOMER_DISPLAY_INSTRUCTION = 'U'; 
public static final char NETTING = 'V'; 
public static final char PEG_TO_VWAP = 'W'; 

  public ExecInst() 
  { 
    super(18);
  } 
  public ExecInst(String data) 
  { 
    super(18, data);
  } 
} 
