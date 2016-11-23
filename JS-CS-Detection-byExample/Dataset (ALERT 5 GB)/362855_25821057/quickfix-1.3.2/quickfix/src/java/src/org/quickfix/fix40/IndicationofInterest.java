package org.quickfix.fix40; 
import org.quickfix.FieldNotFound; 
import org.quickfix.Group; 
import org.quickfix.field.*; 

public class IndicationofInterest extends Message 
{ 

  public IndicationofInterest() 
  { 
    getHeader().setField(new MsgType("6")); 
  } 
  public IndicationofInterest(    
    org.quickfix.field.IOIid aIOIid,    
    org.quickfix.field.IOITransType aIOITransType,    
    org.quickfix.field.Symbol aSymbol,    
    org.quickfix.field.Side aSide,    
    org.quickfix.field.IOIShares aIOIShares ) 
  {  
    getHeader().setField(new MsgType("6")); 
    set(aIOIid); 
    set(aIOITransType); 
    set(aSymbol); 
    set(aSide); 
    set(aIOIShares);  
  } 

  public void set(org.quickfix.field.IOIid value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOIid get(org.quickfix.field.IOIid value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOIid getIOIid() throws FieldNotFound
  { 
    org.quickfix.field.IOIid value = new org.quickfix.field.IOIid();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOITransType value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOITransType get(org.quickfix.field.IOITransType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOITransType getIOITransType() throws FieldNotFound
  { 
    org.quickfix.field.IOITransType value = new org.quickfix.field.IOITransType();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOIRefID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOIRefID get(org.quickfix.field.IOIRefID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOIRefID getIOIRefID() throws FieldNotFound
  { 
    org.quickfix.field.IOIRefID value = new org.quickfix.field.IOIRefID();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Symbol value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Symbol get(org.quickfix.field.Symbol value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Symbol getSymbol() throws FieldNotFound
  { 
    org.quickfix.field.Symbol value = new org.quickfix.field.Symbol();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.SymbolSfx value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SymbolSfx get(org.quickfix.field.SymbolSfx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SymbolSfx getSymbolSfx() throws FieldNotFound
  { 
    org.quickfix.field.SymbolSfx value = new org.quickfix.field.SymbolSfx();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.SecurityID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SecurityID get(org.quickfix.field.SecurityID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SecurityID getSecurityID() throws FieldNotFound
  { 
    org.quickfix.field.SecurityID value = new org.quickfix.field.SecurityID();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IDSource value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IDSource get(org.quickfix.field.IDSource value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IDSource getIDSource() throws FieldNotFound
  { 
    org.quickfix.field.IDSource value = new org.quickfix.field.IDSource();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Issuer value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Issuer get(org.quickfix.field.Issuer value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Issuer getIssuer() throws FieldNotFound
  { 
    org.quickfix.field.Issuer value = new org.quickfix.field.Issuer();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.SecurityDesc value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SecurityDesc get(org.quickfix.field.SecurityDesc value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SecurityDesc getSecurityDesc() throws FieldNotFound
  { 
    org.quickfix.field.SecurityDesc value = new org.quickfix.field.SecurityDesc();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Side value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Side get(org.quickfix.field.Side value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Side getSide() throws FieldNotFound
  { 
    org.quickfix.field.Side value = new org.quickfix.field.Side();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOIShares value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOIShares get(org.quickfix.field.IOIShares value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOIShares getIOIShares() throws FieldNotFound
  { 
    org.quickfix.field.IOIShares value = new org.quickfix.field.IOIShares();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Price value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Price get(org.quickfix.field.Price value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Price getPrice() throws FieldNotFound
  { 
    org.quickfix.field.Price value = new org.quickfix.field.Price();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Currency value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Currency get(org.quickfix.field.Currency value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Currency getCurrency() throws FieldNotFound
  { 
    org.quickfix.field.Currency value = new org.quickfix.field.Currency();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.ValidUntilTime value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.ValidUntilTime get(org.quickfix.field.ValidUntilTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.ValidUntilTime getValidUntilTime() throws FieldNotFound
  { 
    org.quickfix.field.ValidUntilTime value = new org.quickfix.field.ValidUntilTime();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOIQltyInd value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOIQltyInd get(org.quickfix.field.IOIQltyInd value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOIQltyInd getIOIQltyInd() throws FieldNotFound
  { 
    org.quickfix.field.IOIQltyInd value = new org.quickfix.field.IOIQltyInd();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOIOthSvc value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOIOthSvc get(org.quickfix.field.IOIOthSvc value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOIOthSvc getIOIOthSvc() throws FieldNotFound
  { 
    org.quickfix.field.IOIOthSvc value = new org.quickfix.field.IOIOthSvc();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOINaturalFlag value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOINaturalFlag get(org.quickfix.field.IOINaturalFlag value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOINaturalFlag getIOINaturalFlag() throws FieldNotFound
  { 
    org.quickfix.field.IOINaturalFlag value = new org.quickfix.field.IOINaturalFlag();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.IOIQualifier value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.IOIQualifier get(org.quickfix.field.IOIQualifier value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.IOIQualifier getIOIQualifier() throws FieldNotFound
  { 
    org.quickfix.field.IOIQualifier value = new org.quickfix.field.IOIQualifier();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.Text value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.Text get(org.quickfix.field.Text value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.Text getText() throws FieldNotFound
  { 
    org.quickfix.field.Text value = new org.quickfix.field.Text();  
    getField(value);  
    return value;  
  } 
} 
