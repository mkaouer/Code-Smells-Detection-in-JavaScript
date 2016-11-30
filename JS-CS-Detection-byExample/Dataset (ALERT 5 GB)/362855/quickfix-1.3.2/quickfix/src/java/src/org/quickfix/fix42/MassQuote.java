package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.Group; 
import org.quickfix.field.*; 

public class MassQuote extends Message 
{ 

  public MassQuote() 
  { 
    getHeader().setField(new MsgType("i")); 
  } 
  public MassQuote(    
    org.quickfix.field.QuoteID aQuoteID,    
    org.quickfix.field.NoQuoteSets aNoQuoteSets ) 
  {  
    getHeader().setField(new MsgType("i")); 
    set(aQuoteID); 
    set(aNoQuoteSets);  
  } 

  public void set(org.quickfix.field.QuoteReqID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.QuoteReqID get(org.quickfix.field.QuoteReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.QuoteReqID getQuoteReqID() throws FieldNotFound
  { 
    org.quickfix.field.QuoteReqID value = new org.quickfix.field.QuoteReqID();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.QuoteID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.QuoteID get(org.quickfix.field.QuoteID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.QuoteID getQuoteID() throws FieldNotFound
  { 
    org.quickfix.field.QuoteID value = new org.quickfix.field.QuoteID();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.QuoteResponseLevel value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.QuoteResponseLevel get(org.quickfix.field.QuoteResponseLevel value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.QuoteResponseLevel getQuoteResponseLevel() throws FieldNotFound
  { 
    org.quickfix.field.QuoteResponseLevel value = new org.quickfix.field.QuoteResponseLevel();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.DefBidSize value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.DefBidSize get(org.quickfix.field.DefBidSize value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.DefBidSize getDefBidSize() throws FieldNotFound
  { 
    org.quickfix.field.DefBidSize value = new org.quickfix.field.DefBidSize();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.DefOfferSize value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.DefOfferSize get(org.quickfix.field.DefOfferSize value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.DefOfferSize getDefOfferSize() throws FieldNotFound
  { 
    org.quickfix.field.DefOfferSize value = new org.quickfix.field.DefOfferSize();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.NoQuoteSets value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.NoQuoteSets get(org.quickfix.field.NoQuoteSets value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.NoQuoteSets getNoQuoteSets() throws FieldNotFound
  { 
    org.quickfix.field.NoQuoteSets value = new org.quickfix.field.NoQuoteSets();  
    getField(value);  
    return value;  
  } 

public static class NoQuoteSets extends Group { 
  public NoQuoteSets() { 
    super(296,302, new int[]{302,311,312,309,305,310,313,314,315,316,317,436,435,308,306,362,363,307,364,365,367,304,295,0}); 
  } 
  public void set(org.quickfix.field.QuoteSetID value) 
  { setField(value); } 
  public org.quickfix.field.QuoteSetID get(org.quickfix.field.QuoteSetID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.QuoteSetID getQuoteSetID() throws FieldNotFound 
  { org.quickfix.field.QuoteSetID value = new org.quickfix.field.QuoteSetID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingSymbol value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingSymbol get(org.quickfix.field.UnderlyingSymbol value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingSymbol getUnderlyingSymbol() throws FieldNotFound 
  { org.quickfix.field.UnderlyingSymbol value = new org.quickfix.field.UnderlyingSymbol(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingSymbolSfx value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingSymbolSfx get(org.quickfix.field.UnderlyingSymbolSfx value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingSymbolSfx getUnderlyingSymbolSfx() throws FieldNotFound 
  { org.quickfix.field.UnderlyingSymbolSfx value = new org.quickfix.field.UnderlyingSymbolSfx(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingSecurityID value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingSecurityID get(org.quickfix.field.UnderlyingSecurityID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingSecurityID getUnderlyingSecurityID() throws FieldNotFound 
  { org.quickfix.field.UnderlyingSecurityID value = new org.quickfix.field.UnderlyingSecurityID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingIDSource value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingIDSource get(org.quickfix.field.UnderlyingIDSource value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingIDSource getUnderlyingIDSource() throws FieldNotFound 
  { org.quickfix.field.UnderlyingIDSource value = new org.quickfix.field.UnderlyingIDSource(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingSecurityType value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingSecurityType get(org.quickfix.field.UnderlyingSecurityType value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingSecurityType getUnderlyingSecurityType() throws FieldNotFound 
  { org.quickfix.field.UnderlyingSecurityType value = new org.quickfix.field.UnderlyingSecurityType(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingMaturityMonthYear value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingMaturityMonthYear get(org.quickfix.field.UnderlyingMaturityMonthYear value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingMaturityMonthYear getUnderlyingMaturityMonthYear() throws FieldNotFound 
  { org.quickfix.field.UnderlyingMaturityMonthYear value = new org.quickfix.field.UnderlyingMaturityMonthYear(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingMaturityDay value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingMaturityDay get(org.quickfix.field.UnderlyingMaturityDay value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingMaturityDay getUnderlyingMaturityDay() throws FieldNotFound 
  { org.quickfix.field.UnderlyingMaturityDay value = new org.quickfix.field.UnderlyingMaturityDay(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingPutOrCall value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingPutOrCall get(org.quickfix.field.UnderlyingPutOrCall value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingPutOrCall getUnderlyingPutOrCall() throws FieldNotFound 
  { org.quickfix.field.UnderlyingPutOrCall value = new org.quickfix.field.UnderlyingPutOrCall(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingStrikePrice value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingStrikePrice get(org.quickfix.field.UnderlyingStrikePrice value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingStrikePrice getUnderlyingStrikePrice() throws FieldNotFound 
  { org.quickfix.field.UnderlyingStrikePrice value = new org.quickfix.field.UnderlyingStrikePrice(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingOptAttribute value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingOptAttribute get(org.quickfix.field.UnderlyingOptAttribute value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingOptAttribute getUnderlyingOptAttribute() throws FieldNotFound 
  { org.quickfix.field.UnderlyingOptAttribute value = new org.quickfix.field.UnderlyingOptAttribute(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingContractMultiplier value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingContractMultiplier get(org.quickfix.field.UnderlyingContractMultiplier value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingContractMultiplier getUnderlyingContractMultiplier() throws FieldNotFound 
  { org.quickfix.field.UnderlyingContractMultiplier value = new org.quickfix.field.UnderlyingContractMultiplier(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingCouponRate value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingCouponRate get(org.quickfix.field.UnderlyingCouponRate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingCouponRate getUnderlyingCouponRate() throws FieldNotFound 
  { org.quickfix.field.UnderlyingCouponRate value = new org.quickfix.field.UnderlyingCouponRate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingSecurityExchange value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingSecurityExchange get(org.quickfix.field.UnderlyingSecurityExchange value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingSecurityExchange getUnderlyingSecurityExchange() throws FieldNotFound 
  { org.quickfix.field.UnderlyingSecurityExchange value = new org.quickfix.field.UnderlyingSecurityExchange(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingIssuer value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingIssuer get(org.quickfix.field.UnderlyingIssuer value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingIssuer getUnderlyingIssuer() throws FieldNotFound 
  { org.quickfix.field.UnderlyingIssuer value = new org.quickfix.field.UnderlyingIssuer(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedUnderlyingIssuerLen value) 
  { setField(value); } 
  public org.quickfix.field.EncodedUnderlyingIssuerLen get(org.quickfix.field.EncodedUnderlyingIssuerLen value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedUnderlyingIssuerLen getEncodedUnderlyingIssuerLen() throws FieldNotFound 
  { org.quickfix.field.EncodedUnderlyingIssuerLen value = new org.quickfix.field.EncodedUnderlyingIssuerLen(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedUnderlyingIssuer value) 
  { setField(value); } 
  public org.quickfix.field.EncodedUnderlyingIssuer get(org.quickfix.field.EncodedUnderlyingIssuer value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedUnderlyingIssuer getEncodedUnderlyingIssuer() throws FieldNotFound 
  { org.quickfix.field.EncodedUnderlyingIssuer value = new org.quickfix.field.EncodedUnderlyingIssuer(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.UnderlyingSecurityDesc value) 
  { setField(value); } 
  public org.quickfix.field.UnderlyingSecurityDesc get(org.quickfix.field.UnderlyingSecurityDesc value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.UnderlyingSecurityDesc getUnderlyingSecurityDesc() throws FieldNotFound 
  { org.quickfix.field.UnderlyingSecurityDesc value = new org.quickfix.field.UnderlyingSecurityDesc(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedUnderlyingSecurityDescLen value) 
  { setField(value); } 
  public org.quickfix.field.EncodedUnderlyingSecurityDescLen get(org.quickfix.field.EncodedUnderlyingSecurityDescLen value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedUnderlyingSecurityDescLen getEncodedUnderlyingSecurityDescLen() throws FieldNotFound 
  { org.quickfix.field.EncodedUnderlyingSecurityDescLen value = new org.quickfix.field.EncodedUnderlyingSecurityDescLen(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedUnderlyingSecurityDesc value) 
  { setField(value); } 
  public org.quickfix.field.EncodedUnderlyingSecurityDesc get(org.quickfix.field.EncodedUnderlyingSecurityDesc value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedUnderlyingSecurityDesc getEncodedUnderlyingSecurityDesc() throws FieldNotFound 
  { org.quickfix.field.EncodedUnderlyingSecurityDesc value = new org.quickfix.field.EncodedUnderlyingSecurityDesc(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.QuoteSetValidUntilTime value) 
  { setField(value); } 
  public org.quickfix.field.QuoteSetValidUntilTime get(org.quickfix.field.QuoteSetValidUntilTime value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.QuoteSetValidUntilTime getQuoteSetValidUntilTime() throws FieldNotFound 
  { org.quickfix.field.QuoteSetValidUntilTime value = new org.quickfix.field.QuoteSetValidUntilTime(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TotQuoteEntries value) 
  { setField(value); } 
  public org.quickfix.field.TotQuoteEntries get(org.quickfix.field.TotQuoteEntries value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TotQuoteEntries getTotQuoteEntries() throws FieldNotFound 
  { org.quickfix.field.TotQuoteEntries value = new org.quickfix.field.TotQuoteEntries(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.NoQuoteEntries value) 
  { setField(value); } 
  public org.quickfix.field.NoQuoteEntries get(org.quickfix.field.NoQuoteEntries value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.NoQuoteEntries getNoQuoteEntries() throws FieldNotFound 
  { org.quickfix.field.NoQuoteEntries value = new org.quickfix.field.NoQuoteEntries(); 
    getField(value); return value; } 


public static class NoQuoteEntries extends Group { 
  public NoQuoteEntries() { 
    super(295,299, new int[]{299,55,65,48,22,167,200,205,201,202,206,231,223,207,106,348,349,107,350,351,132,133,134,135,62,188,190,189,191,60,336,64,40,193,192,15,0}); 
  } 
  public void set(org.quickfix.field.QuoteEntryID value) 
  { setField(value); } 
  public org.quickfix.field.QuoteEntryID get(org.quickfix.field.QuoteEntryID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.QuoteEntryID getQuoteEntryID() throws FieldNotFound 
  { org.quickfix.field.QuoteEntryID value = new org.quickfix.field.QuoteEntryID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.Symbol value) 
  { setField(value); } 
  public org.quickfix.field.Symbol get(org.quickfix.field.Symbol value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.Symbol getSymbol() throws FieldNotFound 
  { org.quickfix.field.Symbol value = new org.quickfix.field.Symbol(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.SymbolSfx value) 
  { setField(value); } 
  public org.quickfix.field.SymbolSfx get(org.quickfix.field.SymbolSfx value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.SymbolSfx getSymbolSfx() throws FieldNotFound 
  { org.quickfix.field.SymbolSfx value = new org.quickfix.field.SymbolSfx(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.SecurityID value) 
  { setField(value); } 
  public org.quickfix.field.SecurityID get(org.quickfix.field.SecurityID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.SecurityID getSecurityID() throws FieldNotFound 
  { org.quickfix.field.SecurityID value = new org.quickfix.field.SecurityID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.IDSource value) 
  { setField(value); } 
  public org.quickfix.field.IDSource get(org.quickfix.field.IDSource value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.IDSource getIDSource() throws FieldNotFound 
  { org.quickfix.field.IDSource value = new org.quickfix.field.IDSource(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.SecurityType value) 
  { setField(value); } 
  public org.quickfix.field.SecurityType get(org.quickfix.field.SecurityType value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.SecurityType getSecurityType() throws FieldNotFound 
  { org.quickfix.field.SecurityType value = new org.quickfix.field.SecurityType(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MaturityMonthYear value) 
  { setField(value); } 
  public org.quickfix.field.MaturityMonthYear get(org.quickfix.field.MaturityMonthYear value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MaturityMonthYear getMaturityMonthYear() throws FieldNotFound 
  { org.quickfix.field.MaturityMonthYear value = new org.quickfix.field.MaturityMonthYear(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MaturityDay value) 
  { setField(value); } 
  public org.quickfix.field.MaturityDay get(org.quickfix.field.MaturityDay value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MaturityDay getMaturityDay() throws FieldNotFound 
  { org.quickfix.field.MaturityDay value = new org.quickfix.field.MaturityDay(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.PutOrCall value) 
  { setField(value); } 
  public org.quickfix.field.PutOrCall get(org.quickfix.field.PutOrCall value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.PutOrCall getPutOrCall() throws FieldNotFound 
  { org.quickfix.field.PutOrCall value = new org.quickfix.field.PutOrCall(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.StrikePrice value) 
  { setField(value); } 
  public org.quickfix.field.StrikePrice get(org.quickfix.field.StrikePrice value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.StrikePrice getStrikePrice() throws FieldNotFound 
  { org.quickfix.field.StrikePrice value = new org.quickfix.field.StrikePrice(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OptAttribute value) 
  { setField(value); } 
  public org.quickfix.field.OptAttribute get(org.quickfix.field.OptAttribute value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OptAttribute getOptAttribute() throws FieldNotFound 
  { org.quickfix.field.OptAttribute value = new org.quickfix.field.OptAttribute(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.ContractMultiplier value) 
  { setField(value); } 
  public org.quickfix.field.ContractMultiplier get(org.quickfix.field.ContractMultiplier value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.ContractMultiplier getContractMultiplier() throws FieldNotFound 
  { org.quickfix.field.ContractMultiplier value = new org.quickfix.field.ContractMultiplier(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.CouponRate value) 
  { setField(value); } 
  public org.quickfix.field.CouponRate get(org.quickfix.field.CouponRate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.CouponRate getCouponRate() throws FieldNotFound 
  { org.quickfix.field.CouponRate value = new org.quickfix.field.CouponRate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.SecurityExchange value) 
  { setField(value); } 
  public org.quickfix.field.SecurityExchange get(org.quickfix.field.SecurityExchange value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.SecurityExchange getSecurityExchange() throws FieldNotFound 
  { org.quickfix.field.SecurityExchange value = new org.quickfix.field.SecurityExchange(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.Issuer value) 
  { setField(value); } 
  public org.quickfix.field.Issuer get(org.quickfix.field.Issuer value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.Issuer getIssuer() throws FieldNotFound 
  { org.quickfix.field.Issuer value = new org.quickfix.field.Issuer(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedIssuerLen value) 
  { setField(value); } 
  public org.quickfix.field.EncodedIssuerLen get(org.quickfix.field.EncodedIssuerLen value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedIssuerLen getEncodedIssuerLen() throws FieldNotFound 
  { org.quickfix.field.EncodedIssuerLen value = new org.quickfix.field.EncodedIssuerLen(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedIssuer value) 
  { setField(value); } 
  public org.quickfix.field.EncodedIssuer get(org.quickfix.field.EncodedIssuer value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedIssuer getEncodedIssuer() throws FieldNotFound 
  { org.quickfix.field.EncodedIssuer value = new org.quickfix.field.EncodedIssuer(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.SecurityDesc value) 
  { setField(value); } 
  public org.quickfix.field.SecurityDesc get(org.quickfix.field.SecurityDesc value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.SecurityDesc getSecurityDesc() throws FieldNotFound 
  { org.quickfix.field.SecurityDesc value = new org.quickfix.field.SecurityDesc(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedSecurityDescLen value) 
  { setField(value); } 
  public org.quickfix.field.EncodedSecurityDescLen get(org.quickfix.field.EncodedSecurityDescLen value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedSecurityDescLen getEncodedSecurityDescLen() throws FieldNotFound 
  { org.quickfix.field.EncodedSecurityDescLen value = new org.quickfix.field.EncodedSecurityDescLen(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedSecurityDesc value) 
  { setField(value); } 
  public org.quickfix.field.EncodedSecurityDesc get(org.quickfix.field.EncodedSecurityDesc value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedSecurityDesc getEncodedSecurityDesc() throws FieldNotFound 
  { org.quickfix.field.EncodedSecurityDesc value = new org.quickfix.field.EncodedSecurityDesc(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.BidPx value) 
  { setField(value); } 
  public org.quickfix.field.BidPx get(org.quickfix.field.BidPx value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.BidPx getBidPx() throws FieldNotFound 
  { org.quickfix.field.BidPx value = new org.quickfix.field.BidPx(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OfferPx value) 
  { setField(value); } 
  public org.quickfix.field.OfferPx get(org.quickfix.field.OfferPx value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OfferPx getOfferPx() throws FieldNotFound 
  { org.quickfix.field.OfferPx value = new org.quickfix.field.OfferPx(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.BidSize value) 
  { setField(value); } 
  public org.quickfix.field.BidSize get(org.quickfix.field.BidSize value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.BidSize getBidSize() throws FieldNotFound 
  { org.quickfix.field.BidSize value = new org.quickfix.field.BidSize(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OfferSize value) 
  { setField(value); } 
  public org.quickfix.field.OfferSize get(org.quickfix.field.OfferSize value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OfferSize getOfferSize() throws FieldNotFound 
  { org.quickfix.field.OfferSize value = new org.quickfix.field.OfferSize(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.ValidUntilTime value) 
  { setField(value); } 
  public org.quickfix.field.ValidUntilTime get(org.quickfix.field.ValidUntilTime value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.ValidUntilTime getValidUntilTime() throws FieldNotFound 
  { org.quickfix.field.ValidUntilTime value = new org.quickfix.field.ValidUntilTime(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.BidSpotRate value) 
  { setField(value); } 
  public org.quickfix.field.BidSpotRate get(org.quickfix.field.BidSpotRate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.BidSpotRate getBidSpotRate() throws FieldNotFound 
  { org.quickfix.field.BidSpotRate value = new org.quickfix.field.BidSpotRate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OfferSpotRate value) 
  { setField(value); } 
  public org.quickfix.field.OfferSpotRate get(org.quickfix.field.OfferSpotRate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OfferSpotRate getOfferSpotRate() throws FieldNotFound 
  { org.quickfix.field.OfferSpotRate value = new org.quickfix.field.OfferSpotRate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.BidForwardPoints value) 
  { setField(value); } 
  public org.quickfix.field.BidForwardPoints get(org.quickfix.field.BidForwardPoints value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.BidForwardPoints getBidForwardPoints() throws FieldNotFound 
  { org.quickfix.field.BidForwardPoints value = new org.quickfix.field.BidForwardPoints(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OfferForwardPoints value) 
  { setField(value); } 
  public org.quickfix.field.OfferForwardPoints get(org.quickfix.field.OfferForwardPoints value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OfferForwardPoints getOfferForwardPoints() throws FieldNotFound 
  { org.quickfix.field.OfferForwardPoints value = new org.quickfix.field.OfferForwardPoints(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TransactTime value) 
  { setField(value); } 
  public org.quickfix.field.TransactTime get(org.quickfix.field.TransactTime value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TransactTime getTransactTime() throws FieldNotFound 
  { org.quickfix.field.TransactTime value = new org.quickfix.field.TransactTime(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TradingSessionID value) 
  { setField(value); } 
  public org.quickfix.field.TradingSessionID get(org.quickfix.field.TradingSessionID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TradingSessionID getTradingSessionID() throws FieldNotFound 
  { org.quickfix.field.TradingSessionID value = new org.quickfix.field.TradingSessionID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.FutSettDate value) 
  { setField(value); } 
  public org.quickfix.field.FutSettDate get(org.quickfix.field.FutSettDate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.FutSettDate getFutSettDate() throws FieldNotFound 
  { org.quickfix.field.FutSettDate value = new org.quickfix.field.FutSettDate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OrdType value) 
  { setField(value); } 
  public org.quickfix.field.OrdType get(org.quickfix.field.OrdType value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OrdType getOrdType() throws FieldNotFound 
  { org.quickfix.field.OrdType value = new org.quickfix.field.OrdType(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.FutSettDate2 value) 
  { setField(value); } 
  public org.quickfix.field.FutSettDate2 get(org.quickfix.field.FutSettDate2 value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.FutSettDate2 getFutSettDate2() throws FieldNotFound 
  { org.quickfix.field.FutSettDate2 value = new org.quickfix.field.FutSettDate2(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OrderQty2 value) 
  { setField(value); } 
  public org.quickfix.field.OrderQty2 get(org.quickfix.field.OrderQty2 value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OrderQty2 getOrderQty2() throws FieldNotFound 
  { org.quickfix.field.OrderQty2 value = new org.quickfix.field.OrderQty2(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.Currency value) 
  { setField(value); } 
  public org.quickfix.field.Currency get(org.quickfix.field.Currency value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.Currency getCurrency() throws FieldNotFound 
  { org.quickfix.field.Currency value = new org.quickfix.field.Currency(); 
    getField(value); return value; } 

} 
} 
} 
