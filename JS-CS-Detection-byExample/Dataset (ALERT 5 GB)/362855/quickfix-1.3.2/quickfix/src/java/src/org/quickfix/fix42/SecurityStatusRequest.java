package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.Group; 
import org.quickfix.field.*; 

public class SecurityStatusRequest extends Message 
{ 

  public SecurityStatusRequest() 
  { 
    getHeader().setField(new MsgType("e")); 
  } 
  public SecurityStatusRequest(    
    org.quickfix.field.SecurityStatusReqID aSecurityStatusReqID,    
    org.quickfix.field.Symbol aSymbol,    
    org.quickfix.field.SubscriptionRequestType aSubscriptionRequestType ) 
  {  
    getHeader().setField(new MsgType("e")); 
    set(aSecurityStatusReqID); 
    set(aSymbol); 
    set(aSubscriptionRequestType);  
  } 

  public void set(org.quickfix.field.SecurityStatusReqID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SecurityStatusReqID get(org.quickfix.field.SecurityStatusReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SecurityStatusReqID getSecurityStatusReqID() throws FieldNotFound
  { 
    org.quickfix.field.SecurityStatusReqID value = new org.quickfix.field.SecurityStatusReqID();  
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

  public void set(org.quickfix.field.SecurityType value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SecurityType get(org.quickfix.field.SecurityType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SecurityType getSecurityType() throws FieldNotFound
  { 
    org.quickfix.field.SecurityType value = new org.quickfix.field.SecurityType();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.MaturityMonthYear value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.MaturityMonthYear get(org.quickfix.field.MaturityMonthYear value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.MaturityMonthYear getMaturityMonthYear() throws FieldNotFound
  { 
    org.quickfix.field.MaturityMonthYear value = new org.quickfix.field.MaturityMonthYear();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.MaturityDay value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.MaturityDay get(org.quickfix.field.MaturityDay value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.MaturityDay getMaturityDay() throws FieldNotFound
  { 
    org.quickfix.field.MaturityDay value = new org.quickfix.field.MaturityDay();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.PutOrCall value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.PutOrCall get(org.quickfix.field.PutOrCall value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.PutOrCall getPutOrCall() throws FieldNotFound
  { 
    org.quickfix.field.PutOrCall value = new org.quickfix.field.PutOrCall();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.StrikePrice value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.StrikePrice get(org.quickfix.field.StrikePrice value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.StrikePrice getStrikePrice() throws FieldNotFound
  { 
    org.quickfix.field.StrikePrice value = new org.quickfix.field.StrikePrice();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.OptAttribute value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.OptAttribute get(org.quickfix.field.OptAttribute value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.OptAttribute getOptAttribute() throws FieldNotFound
  { 
    org.quickfix.field.OptAttribute value = new org.quickfix.field.OptAttribute();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.ContractMultiplier value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.ContractMultiplier get(org.quickfix.field.ContractMultiplier value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.ContractMultiplier getContractMultiplier() throws FieldNotFound
  { 
    org.quickfix.field.ContractMultiplier value = new org.quickfix.field.ContractMultiplier();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.CouponRate value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.CouponRate get(org.quickfix.field.CouponRate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.CouponRate getCouponRate() throws FieldNotFound
  { 
    org.quickfix.field.CouponRate value = new org.quickfix.field.CouponRate();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.SecurityExchange value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SecurityExchange get(org.quickfix.field.SecurityExchange value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SecurityExchange getSecurityExchange() throws FieldNotFound
  { 
    org.quickfix.field.SecurityExchange value = new org.quickfix.field.SecurityExchange();  
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

  public void set(org.quickfix.field.EncodedIssuerLen value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.EncodedIssuerLen get(org.quickfix.field.EncodedIssuerLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.EncodedIssuerLen getEncodedIssuerLen() throws FieldNotFound
  { 
    org.quickfix.field.EncodedIssuerLen value = new org.quickfix.field.EncodedIssuerLen();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.EncodedIssuer value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.EncodedIssuer get(org.quickfix.field.EncodedIssuer value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.EncodedIssuer getEncodedIssuer() throws FieldNotFound
  { 
    org.quickfix.field.EncodedIssuer value = new org.quickfix.field.EncodedIssuer();  
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

  public void set(org.quickfix.field.EncodedSecurityDescLen value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.EncodedSecurityDescLen get(org.quickfix.field.EncodedSecurityDescLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.EncodedSecurityDescLen getEncodedSecurityDescLen() throws FieldNotFound
  { 
    org.quickfix.field.EncodedSecurityDescLen value = new org.quickfix.field.EncodedSecurityDescLen();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.EncodedSecurityDesc value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.EncodedSecurityDesc get(org.quickfix.field.EncodedSecurityDesc value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.EncodedSecurityDesc getEncodedSecurityDesc() throws FieldNotFound
  { 
    org.quickfix.field.EncodedSecurityDesc value = new org.quickfix.field.EncodedSecurityDesc();  
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

  public void set(org.quickfix.field.SubscriptionRequestType value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.SubscriptionRequestType get(org.quickfix.field.SubscriptionRequestType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.SubscriptionRequestType getSubscriptionRequestType() throws FieldNotFound
  { 
    org.quickfix.field.SubscriptionRequestType value = new org.quickfix.field.SubscriptionRequestType();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.TradingSessionID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.TradingSessionID get(org.quickfix.field.TradingSessionID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.TradingSessionID getTradingSessionID() throws FieldNotFound
  { 
    org.quickfix.field.TradingSessionID value = new org.quickfix.field.TradingSessionID();  
    getField(value);  
    return value;  
  } 
} 
