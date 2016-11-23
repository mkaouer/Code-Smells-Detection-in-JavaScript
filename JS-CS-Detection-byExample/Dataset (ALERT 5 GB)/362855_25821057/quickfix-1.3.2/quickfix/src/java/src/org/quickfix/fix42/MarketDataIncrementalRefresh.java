package org.quickfix.fix42; 
import org.quickfix.FieldNotFound; 
import org.quickfix.Group; 
import org.quickfix.field.*; 

public class MarketDataIncrementalRefresh extends Message 
{ 

  public MarketDataIncrementalRefresh() 
  { 
    getHeader().setField(new MsgType("X")); 
  } 
  public MarketDataIncrementalRefresh(    
    org.quickfix.field.NoMDEntries aNoMDEntries ) 
  {  
    getHeader().setField(new MsgType("X")); 
    set(aNoMDEntries);  
  } 

  public void set(org.quickfix.field.MDReqID value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.MDReqID get(org.quickfix.field.MDReqID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.MDReqID getMDReqID() throws FieldNotFound
  { 
    org.quickfix.field.MDReqID value = new org.quickfix.field.MDReqID();  
    getField(value);  
    return value;  
  } 

  public void set(org.quickfix.field.NoMDEntries value) 
  { 
    setField(value); 
  } 
  public org.quickfix.field.NoMDEntries get(org.quickfix.field.NoMDEntries value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public org.quickfix.field.NoMDEntries getNoMDEntries() throws FieldNotFound
  { 
    org.quickfix.field.NoMDEntries value = new org.quickfix.field.NoMDEntries();  
    getField(value);  
    return value;  
  } 

public static class NoMDEntries extends Group { 
  public NoMDEntries() { 
    super(268,279, new int[]{279,285,269,278,280,55,65,48,22,167,200,205,201,202,206,231,223,207,106,348,349,107,350,351,291,292,270,15,271,272,273,274,275,336,276,277,282,283,284,286,59,432,126,110,18,287,37,299,288,289,346,290,387,58,354,355,0}); 
  } 
  public void set(org.quickfix.field.MDUpdateAction value) 
  { setField(value); } 
  public org.quickfix.field.MDUpdateAction get(org.quickfix.field.MDUpdateAction value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDUpdateAction getMDUpdateAction() throws FieldNotFound 
  { org.quickfix.field.MDUpdateAction value = new org.quickfix.field.MDUpdateAction(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.DeleteReason value) 
  { setField(value); } 
  public org.quickfix.field.DeleteReason get(org.quickfix.field.DeleteReason value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.DeleteReason getDeleteReason() throws FieldNotFound 
  { org.quickfix.field.DeleteReason value = new org.quickfix.field.DeleteReason(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryType value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryType get(org.quickfix.field.MDEntryType value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryType getMDEntryType() throws FieldNotFound 
  { org.quickfix.field.MDEntryType value = new org.quickfix.field.MDEntryType(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryID value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryID get(org.quickfix.field.MDEntryID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryID getMDEntryID() throws FieldNotFound 
  { org.quickfix.field.MDEntryID value = new org.quickfix.field.MDEntryID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryRefID value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryRefID get(org.quickfix.field.MDEntryRefID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryRefID getMDEntryRefID() throws FieldNotFound 
  { org.quickfix.field.MDEntryRefID value = new org.quickfix.field.MDEntryRefID(); 
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

  public void set(org.quickfix.field.FinancialStatus value) 
  { setField(value); } 
  public org.quickfix.field.FinancialStatus get(org.quickfix.field.FinancialStatus value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.FinancialStatus getFinancialStatus() throws FieldNotFound 
  { org.quickfix.field.FinancialStatus value = new org.quickfix.field.FinancialStatus(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.CorporateAction value) 
  { setField(value); } 
  public org.quickfix.field.CorporateAction get(org.quickfix.field.CorporateAction value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.CorporateAction getCorporateAction() throws FieldNotFound 
  { org.quickfix.field.CorporateAction value = new org.quickfix.field.CorporateAction(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryPx value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryPx get(org.quickfix.field.MDEntryPx value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryPx getMDEntryPx() throws FieldNotFound 
  { org.quickfix.field.MDEntryPx value = new org.quickfix.field.MDEntryPx(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.Currency value) 
  { setField(value); } 
  public org.quickfix.field.Currency get(org.quickfix.field.Currency value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.Currency getCurrency() throws FieldNotFound 
  { org.quickfix.field.Currency value = new org.quickfix.field.Currency(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntrySize value) 
  { setField(value); } 
  public org.quickfix.field.MDEntrySize get(org.quickfix.field.MDEntrySize value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntrySize getMDEntrySize() throws FieldNotFound 
  { org.quickfix.field.MDEntrySize value = new org.quickfix.field.MDEntrySize(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryDate value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryDate get(org.quickfix.field.MDEntryDate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryDate getMDEntryDate() throws FieldNotFound 
  { org.quickfix.field.MDEntryDate value = new org.quickfix.field.MDEntryDate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryTime value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryTime get(org.quickfix.field.MDEntryTime value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryTime getMDEntryTime() throws FieldNotFound 
  { org.quickfix.field.MDEntryTime value = new org.quickfix.field.MDEntryTime(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TickDirection value) 
  { setField(value); } 
  public org.quickfix.field.TickDirection get(org.quickfix.field.TickDirection value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TickDirection getTickDirection() throws FieldNotFound 
  { org.quickfix.field.TickDirection value = new org.quickfix.field.TickDirection(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDMkt value) 
  { setField(value); } 
  public org.quickfix.field.MDMkt get(org.quickfix.field.MDMkt value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDMkt getMDMkt() throws FieldNotFound 
  { org.quickfix.field.MDMkt value = new org.quickfix.field.MDMkt(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TradingSessionID value) 
  { setField(value); } 
  public org.quickfix.field.TradingSessionID get(org.quickfix.field.TradingSessionID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TradingSessionID getTradingSessionID() throws FieldNotFound 
  { org.quickfix.field.TradingSessionID value = new org.quickfix.field.TradingSessionID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.QuoteCondition value) 
  { setField(value); } 
  public org.quickfix.field.QuoteCondition get(org.quickfix.field.QuoteCondition value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.QuoteCondition getQuoteCondition() throws FieldNotFound 
  { org.quickfix.field.QuoteCondition value = new org.quickfix.field.QuoteCondition(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TradeCondition value) 
  { setField(value); } 
  public org.quickfix.field.TradeCondition get(org.quickfix.field.TradeCondition value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TradeCondition getTradeCondition() throws FieldNotFound 
  { org.quickfix.field.TradeCondition value = new org.quickfix.field.TradeCondition(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryOriginator value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryOriginator get(org.quickfix.field.MDEntryOriginator value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryOriginator getMDEntryOriginator() throws FieldNotFound 
  { org.quickfix.field.MDEntryOriginator value = new org.quickfix.field.MDEntryOriginator(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.LocationID value) 
  { setField(value); } 
  public org.quickfix.field.LocationID get(org.quickfix.field.LocationID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.LocationID getLocationID() throws FieldNotFound 
  { org.quickfix.field.LocationID value = new org.quickfix.field.LocationID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.DeskID value) 
  { setField(value); } 
  public org.quickfix.field.DeskID get(org.quickfix.field.DeskID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.DeskID getDeskID() throws FieldNotFound 
  { org.quickfix.field.DeskID value = new org.quickfix.field.DeskID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OpenCloseSettleFlag value) 
  { setField(value); } 
  public org.quickfix.field.OpenCloseSettleFlag get(org.quickfix.field.OpenCloseSettleFlag value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OpenCloseSettleFlag getOpenCloseSettleFlag() throws FieldNotFound 
  { org.quickfix.field.OpenCloseSettleFlag value = new org.quickfix.field.OpenCloseSettleFlag(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TimeInForce value) 
  { setField(value); } 
  public org.quickfix.field.TimeInForce get(org.quickfix.field.TimeInForce value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TimeInForce getTimeInForce() throws FieldNotFound 
  { org.quickfix.field.TimeInForce value = new org.quickfix.field.TimeInForce(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.ExpireDate value) 
  { setField(value); } 
  public org.quickfix.field.ExpireDate get(org.quickfix.field.ExpireDate value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.ExpireDate getExpireDate() throws FieldNotFound 
  { org.quickfix.field.ExpireDate value = new org.quickfix.field.ExpireDate(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.ExpireTime value) 
  { setField(value); } 
  public org.quickfix.field.ExpireTime get(org.quickfix.field.ExpireTime value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.ExpireTime getExpireTime() throws FieldNotFound 
  { org.quickfix.field.ExpireTime value = new org.quickfix.field.ExpireTime(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MinQty value) 
  { setField(value); } 
  public org.quickfix.field.MinQty get(org.quickfix.field.MinQty value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MinQty getMinQty() throws FieldNotFound 
  { org.quickfix.field.MinQty value = new org.quickfix.field.MinQty(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.ExecInst value) 
  { setField(value); } 
  public org.quickfix.field.ExecInst get(org.quickfix.field.ExecInst value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.ExecInst getExecInst() throws FieldNotFound 
  { org.quickfix.field.ExecInst value = new org.quickfix.field.ExecInst(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.SellerDays value) 
  { setField(value); } 
  public org.quickfix.field.SellerDays get(org.quickfix.field.SellerDays value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.SellerDays getSellerDays() throws FieldNotFound 
  { org.quickfix.field.SellerDays value = new org.quickfix.field.SellerDays(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.OrderID value) 
  { setField(value); } 
  public org.quickfix.field.OrderID get(org.quickfix.field.OrderID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.OrderID getOrderID() throws FieldNotFound 
  { org.quickfix.field.OrderID value = new org.quickfix.field.OrderID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.QuoteEntryID value) 
  { setField(value); } 
  public org.quickfix.field.QuoteEntryID get(org.quickfix.field.QuoteEntryID value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.QuoteEntryID getQuoteEntryID() throws FieldNotFound 
  { org.quickfix.field.QuoteEntryID value = new org.quickfix.field.QuoteEntryID(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryBuyer value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryBuyer get(org.quickfix.field.MDEntryBuyer value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryBuyer getMDEntryBuyer() throws FieldNotFound 
  { org.quickfix.field.MDEntryBuyer value = new org.quickfix.field.MDEntryBuyer(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntrySeller value) 
  { setField(value); } 
  public org.quickfix.field.MDEntrySeller get(org.quickfix.field.MDEntrySeller value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntrySeller getMDEntrySeller() throws FieldNotFound 
  { org.quickfix.field.MDEntrySeller value = new org.quickfix.field.MDEntrySeller(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.NumberOfOrders value) 
  { setField(value); } 
  public org.quickfix.field.NumberOfOrders get(org.quickfix.field.NumberOfOrders value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.NumberOfOrders getNumberOfOrders() throws FieldNotFound 
  { org.quickfix.field.NumberOfOrders value = new org.quickfix.field.NumberOfOrders(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.MDEntryPositionNo value) 
  { setField(value); } 
  public org.quickfix.field.MDEntryPositionNo get(org.quickfix.field.MDEntryPositionNo value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.MDEntryPositionNo getMDEntryPositionNo() throws FieldNotFound 
  { org.quickfix.field.MDEntryPositionNo value = new org.quickfix.field.MDEntryPositionNo(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.TotalVolumeTraded value) 
  { setField(value); } 
  public org.quickfix.field.TotalVolumeTraded get(org.quickfix.field.TotalVolumeTraded value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.TotalVolumeTraded getTotalVolumeTraded() throws FieldNotFound 
  { org.quickfix.field.TotalVolumeTraded value = new org.quickfix.field.TotalVolumeTraded(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.Text value) 
  { setField(value); } 
  public org.quickfix.field.Text get(org.quickfix.field.Text value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.Text getText() throws FieldNotFound 
  { org.quickfix.field.Text value = new org.quickfix.field.Text(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedTextLen value) 
  { setField(value); } 
  public org.quickfix.field.EncodedTextLen get(org.quickfix.field.EncodedTextLen value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedTextLen getEncodedTextLen() throws FieldNotFound 
  { org.quickfix.field.EncodedTextLen value = new org.quickfix.field.EncodedTextLen(); 
    getField(value); return value; } 

  public void set(org.quickfix.field.EncodedText value) 
  { setField(value); } 
  public org.quickfix.field.EncodedText get(org.quickfix.field.EncodedText value) throws FieldNotFound 
  { getField(value); return value; } 
  public org.quickfix.field.EncodedText getEncodedText() throws FieldNotFound 
  { org.quickfix.field.EncodedText value = new org.quickfix.field.EncodedText(); 
    getField(value); return value; } 

} 
} 
