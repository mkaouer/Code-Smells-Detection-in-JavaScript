package org.quickfix.fix40; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class IndicationofInterest extends Message 
{ 

  public IndicationofInterest() 
  { 
    getHeader().setField(new MsgType("6")); 
  } 
  public IndicationofInterest(    
    IOIid aIOIid,    
    IOITransType aIOITransType,    
    Symbol aSymbol,    
    Side aSide,    
    IOIShares aIOIShares ) 
  {  
    getHeader().setField(new MsgType("6")); 
    set(aIOIid); 
    set(aIOITransType); 
    set(aSymbol); 
    set(aSide); 
    set(aIOIShares);  
  } 

  public void set(IOIid value) 
  { 
    setField(value); 
  } 
  public IOIid get(IOIid value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOIid getIOIid() throws FieldNotFound
  { 
    IOIid value = new IOIid();  
    getField(value);  
    return value;  
  } 

  public void set(IOITransType value) 
  { 
    setField(value); 
  } 
  public IOITransType get(IOITransType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOITransType getIOITransType() throws FieldNotFound
  { 
    IOITransType value = new IOITransType();  
    getField(value);  
    return value;  
  } 

  public void set(IOIRefID value) 
  { 
    setField(value); 
  } 
  public IOIRefID get(IOIRefID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOIRefID getIOIRefID() throws FieldNotFound
  { 
    IOIRefID value = new IOIRefID();  
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

  public void set(Side value) 
  { 
    setField(value); 
  } 
  public Side get(Side value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Side getSide() throws FieldNotFound
  { 
    Side value = new Side();  
    getField(value);  
    return value;  
  } 

  public void set(IOIShares value) 
  { 
    setField(value); 
  } 
  public IOIShares get(IOIShares value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOIShares getIOIShares() throws FieldNotFound
  { 
    IOIShares value = new IOIShares();  
    getField(value);  
    return value;  
  } 

  public void set(Price value) 
  { 
    setField(value); 
  } 
  public Price get(Price value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Price getPrice() throws FieldNotFound
  { 
    Price value = new Price();  
    getField(value);  
    return value;  
  } 

  public void set(Currency value) 
  { 
    setField(value); 
  } 
  public Currency get(Currency value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Currency getCurrency() throws FieldNotFound
  { 
    Currency value = new Currency();  
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

  public void set(IOIQltyInd value) 
  { 
    setField(value); 
  } 
  public IOIQltyInd get(IOIQltyInd value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOIQltyInd getIOIQltyInd() throws FieldNotFound
  { 
    IOIQltyInd value = new IOIQltyInd();  
    getField(value);  
    return value;  
  } 

  public void set(IOIOthSvc value) 
  { 
    setField(value); 
  } 
  public IOIOthSvc get(IOIOthSvc value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOIOthSvc getIOIOthSvc() throws FieldNotFound
  { 
    IOIOthSvc value = new IOIOthSvc();  
    getField(value);  
    return value;  
  } 

  public void set(IOINaturalFlag value) 
  { 
    setField(value); 
  } 
  public IOINaturalFlag get(IOINaturalFlag value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOINaturalFlag getIOINaturalFlag() throws FieldNotFound
  { 
    IOINaturalFlag value = new IOINaturalFlag();  
    getField(value);  
    return value;  
  } 

  public void set(IOIQualifier value) 
  { 
    setField(value); 
  } 
  public IOIQualifier get(IOIQualifier value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public IOIQualifier getIOIQualifier() throws FieldNotFound
  { 
    IOIQualifier value = new IOIQualifier();  
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
} 
