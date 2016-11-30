package org.quickfix.field; 
import org.quickfix.StringField; 
import java.util.Date; 

public class AggregatedBook extends StringField 
{ 
public static final char ONE_BOOK_ENTRY_PER_SIDE_PER_PRICE = 'Y'; 
public static final char MULTIPLE_ENTRIES_PER_SIDE_PER_PRICE_ALLOWED = 'N'; 

  public AggregatedBook() 
  { 
    super(266);
  } 
  public AggregatedBook(String data) 
  { 
    super(266, data);
  } 
} 
