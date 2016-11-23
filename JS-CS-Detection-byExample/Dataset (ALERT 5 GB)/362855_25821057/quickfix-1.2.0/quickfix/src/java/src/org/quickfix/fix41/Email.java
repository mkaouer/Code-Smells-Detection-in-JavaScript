package org.quickfix.fix41; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Email extends Message 
{ 

  public Email() 
  { 
    getHeader().setField(new MsgType("C")); 
  } 
  public Email(    
    EmailThreadID aEmailThreadID,    
    EmailType aEmailType,    
    Subject aSubject,    
    LinesOfText aLinesOfText ) 
  {  
    getHeader().setField(new MsgType("C")); 
    set(aEmailThreadID); 
    set(aEmailType); 
    set(aSubject); 
    set(aLinesOfText);  
  } 

  public void set(EmailThreadID value) 
  { 
    setField(value); 
  } 
  public EmailThreadID get(EmailThreadID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EmailThreadID getEmailThreadID() throws FieldNotFound
  { 
    EmailThreadID value = new EmailThreadID();  
    getField(value);  
    return value;  
  } 

  public void set(EmailType value) 
  { 
    setField(value); 
  } 
  public EmailType get(EmailType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EmailType getEmailType() throws FieldNotFound
  { 
    EmailType value = new EmailType();  
    getField(value);  
    return value;  
  } 

  public void set(OrigTime value) 
  { 
    setField(value); 
  } 
  public OrigTime get(OrigTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrigTime getOrigTime() throws FieldNotFound
  { 
    OrigTime value = new OrigTime();  
    getField(value);  
    return value;  
  } 

  public void set(Subject value) 
  { 
    setField(value); 
  } 
  public Subject get(Subject value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Subject getSubject() throws FieldNotFound
  { 
    Subject value = new Subject();  
    getField(value);  
    return value;  
  } 

  public void set(NoRelatedSym value) 
  { 
    setField(value); 
  } 
  public NoRelatedSym get(NoRelatedSym value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoRelatedSym getNoRelatedSym() throws FieldNotFound
  { 
    NoRelatedSym value = new NoRelatedSym();  
    getField(value);  
    return value;  
  } 

  public void set(RelatdSym value) 
  { 
    setField(value); 
  } 
  public RelatdSym get(RelatdSym value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RelatdSym getRelatdSym() throws FieldNotFound
  { 
    RelatdSym value = new RelatdSym();  
    getField(value);  
    return value;  
  } 

  public void set(ClOrdID value) 
  { 
    setField(value); 
  } 
  public ClOrdID get(ClOrdID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClOrdID getClOrdID() throws FieldNotFound
  { 
    ClOrdID value = new ClOrdID();  
    getField(value);  
    return value;  
  } 

  public void set(LinesOfText value) 
  { 
    setField(value); 
  } 
  public LinesOfText get(LinesOfText value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LinesOfText getLinesOfText() throws FieldNotFound
  { 
    LinesOfText value = new LinesOfText();  
    getField(value);  
    return value;  
  } 

  public void set(Text value) 
  { 
    setField(value); 
  } 
  public Text get(Text value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Text getText() throws FieldNotFound
  { 
    Text value = new Text();  
    getField(value);  
    return value;  
  } 

  public void set(RawData value) 
  { 
    setField(value); 
  } 
  public RawData get(RawData value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RawData getRawData() throws FieldNotFound
  { 
    RawData value = new RawData();  
    getField(value);  
    return value;  
  } 
} 
