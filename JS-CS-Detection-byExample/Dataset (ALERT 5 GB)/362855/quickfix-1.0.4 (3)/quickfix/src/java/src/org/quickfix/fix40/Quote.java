package org.quickfix.fix40; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Quote extends Message 
{ 

  public Quote() 
  { 
    getHeader().setField(new MsgType("S")); 
  } 
  public Quote(    
    QuoteID aQuoteID,    
    Symbol aSymbol,    
    BidPx aBidPx ) 
  {  
    getHeader().setField(new MsgType("S")); 
    set(aQuoteID); 
    set(aSymbol); 
    set(aBidPx);  
  } 

  public void set(QuoteReqID value) 
  { 
    setField(value); 
  } 
  public QuoteReqID get(QuoteReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteReqID getQuoteReqID() throws FieldNotFound
  { 
    QuoteReqID value = new QuoteReqID();  
    getField(value);  
    return value;  
  } 

  public void set(QuoteID value) 
  { 
    setField(value); 
  } 
  public QuoteID get(QuoteID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteID getQuoteID() throws FieldNotFound
  { 
    QuoteID value = new QuoteID();  
    getField(value);  
    return value;  
  } 

  public void set(Symbol value) 
  { 
    setField(value); 
  } 
  public Symbol get(Symbol value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Symbol getSymbol() throws FieldNotFound
  { 
    Symbol value = new Symbol();  
    getField(value);  
    return value;  
  } 

  public void set(SymbolSfx value) 
  { 
    setField(value); 
  } 
  public SymbolSfx get(SymbolSfx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SymbolSfx getSymbolSfx() throws FieldNotFound
  { 
    SymbolSfx value = new SymbolSfx();  
    getField(value);  
    return value;  
  } 

  public void set(SecurityID value) 
  { 
    setField(value); 
  } 
  public SecurityID get(SecurityID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecurityID getSecurityID() throws FieldNotFound
  { 
    SecurityID value = new SecurityID();  
    getField(value);  
    return value;  
  } 

  public void set(IDSource value) 
  { 
    setField(value); 
  } 
  public IDSource get(IDSource value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IDSource getIDSource() throws FieldNotFound
  { 
    IDSource value = new IDSource();  
    getField(value);  
    return value;  
  } 

  public void set(Issuer value) 
  { 
    setField(value); 
  } 
  public Issuer get(Issuer value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Issuer getIssuer() throws FieldNotFound
  { 
    Issuer value = new Issuer();  
    getField(value);  
    return value;  
  } 

  public void set(SecurityDesc value) 
  { 
    setField(value); 
  } 
  public SecurityDesc get(SecurityDesc value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecurityDesc getSecurityDesc() throws FieldNotFound
  { 
    SecurityDesc value = new SecurityDesc();  
    getField(value);  
    return value;  
  } 

  public void set(BidPx value) 
  { 
    setField(value); 
  } 
  public BidPx get(BidPx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BidPx getBidPx() throws FieldNotFound
  { 
    BidPx value = new BidPx();  
    getField(value);  
    return value;  
  } 

  public void set(OfferPx value) 
  { 
    setField(value); 
  } 
  public OfferPx get(OfferPx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OfferPx getOfferPx() throws FieldNotFound
  { 
    OfferPx value = new OfferPx();  
    getField(value);  
    return value;  
  } 

  public void set(BidSize value) 
  { 
    setField(value); 
  } 
  public BidSize get(BidSize value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BidSize getBidSize() throws FieldNotFound
  { 
    BidSize value = new BidSize();  
    getField(value);  
    return value;  
  } 

  public void set(OfferSize value) 
  { 
    setField(value); 
  } 
  public OfferSize get(OfferSize value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OfferSize getOfferSize() throws FieldNotFound
  { 
    OfferSize value = new OfferSize();  
    getField(value);  
    return value;  
  } 

  public void set(ValidUntilTime value) 
  { 
    setField(value); 
  } 
  public ValidUntilTime get(ValidUntilTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ValidUntilTime getValidUntilTime() throws FieldNotFound
  { 
    ValidUntilTime value = new ValidUntilTime();  
    getField(value);  
    return value;  
  } 
} 
