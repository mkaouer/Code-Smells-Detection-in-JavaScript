package org.quickfix.fix42; 
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
    Symbol aSymbol ) 
  {  
    getHeader().setField(new MsgType("S")); 
    set(aQuoteID); 
    set(aSymbol);  
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

  public void set(QuoteResponseLevel value) 
  { 
    setField(value); 
  } 
  public QuoteResponseLevel get(QuoteResponseLevel value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public QuoteResponseLevel getQuoteResponseLevel() throws FieldNotFound
  { 
    QuoteResponseLevel value = new QuoteResponseLevel();  
    getField(value);  
    return value;  
  } 

  public void set(TradingSessionID value) 
  { 
    setField(value); 
  } 
  public TradingSessionID get(TradingSessionID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradingSessionID getTradingSessionID() throws FieldNotFound
  { 
    TradingSessionID value = new TradingSessionID();  
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

  public void set(SecurityType value) 
  { 
    setField(value); 
  } 
  public SecurityType get(SecurityType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecurityType getSecurityType() throws FieldNotFound
  { 
    SecurityType value = new SecurityType();  
    getField(value);  
    return value;  
  } 

  public void set(MaturityMonthYear value) 
  { 
    setField(value); 
  } 
  public MaturityMonthYear get(MaturityMonthYear value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MaturityMonthYear getMaturityMonthYear() throws FieldNotFound
  { 
    MaturityMonthYear value = new MaturityMonthYear();  
    getField(value);  
    return value;  
  } 

  public void set(MaturityDay value) 
  { 
    setField(value); 
  } 
  public MaturityDay get(MaturityDay value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MaturityDay getMaturityDay() throws FieldNotFound
  { 
    MaturityDay value = new MaturityDay();  
    getField(value);  
    return value;  
  } 

  public void set(PutOrCall value) 
  { 
    setField(value); 
  } 
  public PutOrCall get(PutOrCall value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public PutOrCall getPutOrCall() throws FieldNotFound
  { 
    PutOrCall value = new PutOrCall();  
    getField(value);  
    return value;  
  } 

  public void set(StrikePrice value) 
  { 
    setField(value); 
  } 
  public StrikePrice get(StrikePrice value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public StrikePrice getStrikePrice() throws FieldNotFound
  { 
    StrikePrice value = new StrikePrice();  
    getField(value);  
    return value;  
  } 

  public void set(OptAttribute value) 
  { 
    setField(value); 
  } 
  public OptAttribute get(OptAttribute value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OptAttribute getOptAttribute() throws FieldNotFound
  { 
    OptAttribute value = new OptAttribute();  
    getField(value);  
    return value;  
  } 

  public void set(ContractMultiplier value) 
  { 
    setField(value); 
  } 
  public ContractMultiplier get(ContractMultiplier value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ContractMultiplier getContractMultiplier() throws FieldNotFound
  { 
    ContractMultiplier value = new ContractMultiplier();  
    getField(value);  
    return value;  
  } 

  public void set(CouponRate value) 
  { 
    setField(value); 
  } 
  public CouponRate get(CouponRate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CouponRate getCouponRate() throws FieldNotFound
  { 
    CouponRate value = new CouponRate();  
    getField(value);  
    return value;  
  } 

  public void set(SecurityExchange value) 
  { 
    setField(value); 
  } 
  public SecurityExchange get(SecurityExchange value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecurityExchange getSecurityExchange() throws FieldNotFound
  { 
    SecurityExchange value = new SecurityExchange();  
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

  public void set(EncodedIssuerLen value) 
  { 
    setField(value); 
  } 
  public EncodedIssuerLen get(EncodedIssuerLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedIssuerLen getEncodedIssuerLen() throws FieldNotFound
  { 
    EncodedIssuerLen value = new EncodedIssuerLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedIssuer value) 
  { 
    setField(value); 
  } 
  public EncodedIssuer get(EncodedIssuer value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedIssuer getEncodedIssuer() throws FieldNotFound
  { 
    EncodedIssuer value = new EncodedIssuer();  
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

  public void set(EncodedSecurityDescLen value) 
  { 
    setField(value); 
  } 
  public EncodedSecurityDescLen get(EncodedSecurityDescLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedSecurityDescLen getEncodedSecurityDescLen() throws FieldNotFound
  { 
    EncodedSecurityDescLen value = new EncodedSecurityDescLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedSecurityDesc value) 
  { 
    setField(value); 
  } 
  public EncodedSecurityDesc get(EncodedSecurityDesc value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedSecurityDesc getEncodedSecurityDesc() throws FieldNotFound
  { 
    EncodedSecurityDesc value = new EncodedSecurityDesc();  
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

  public void set(BidSpotRate value) 
  { 
    setField(value); 
  } 
  public BidSpotRate get(BidSpotRate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BidSpotRate getBidSpotRate() throws FieldNotFound
  { 
    BidSpotRate value = new BidSpotRate();  
    getField(value);  
    return value;  
  } 

  public void set(OfferSpotRate value) 
  { 
    setField(value); 
  } 
  public OfferSpotRate get(OfferSpotRate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OfferSpotRate getOfferSpotRate() throws FieldNotFound
  { 
    OfferSpotRate value = new OfferSpotRate();  
    getField(value);  
    return value;  
  } 

  public void set(BidForwardPoints value) 
  { 
    setField(value); 
  } 
  public BidForwardPoints get(BidForwardPoints value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BidForwardPoints getBidForwardPoints() throws FieldNotFound
  { 
    BidForwardPoints value = new BidForwardPoints();  
    getField(value);  
    return value;  
  } 

  public void set(OfferForwardPoints value) 
  { 
    setField(value); 
  } 
  public OfferForwardPoints get(OfferForwardPoints value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OfferForwardPoints getOfferForwardPoints() throws FieldNotFound
  { 
    OfferForwardPoints value = new OfferForwardPoints();  
    getField(value);  
    return value;  
  } 

  public void set(TransactTime value) 
  { 
    setField(value); 
  } 
  public TransactTime get(TransactTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TransactTime getTransactTime() throws FieldNotFound
  { 
    TransactTime value = new TransactTime();  
    getField(value);  
    return value;  
  } 

  public void set(FutSettDate value) 
  { 
    setField(value); 
  } 
  public FutSettDate get(FutSettDate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public FutSettDate getFutSettDate() throws FieldNotFound
  { 
    FutSettDate value = new FutSettDate();  
    getField(value);  
    return value;  
  } 

  public void set(OrdType value) 
  { 
    setField(value); 
  } 
  public OrdType get(OrdType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrdType getOrdType() throws FieldNotFound
  { 
    OrdType value = new OrdType();  
    getField(value);  
    return value;  
  } 

  public void set(FutSettDate2 value) 
  { 
    setField(value); 
  } 
  public FutSettDate2 get(FutSettDate2 value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public FutSettDate2 getFutSettDate2() throws FieldNotFound
  { 
    FutSettDate2 value = new FutSettDate2();  
    getField(value);  
    return value;  
  } 

  public void set(OrderQty2 value) 
  { 
    setField(value); 
  } 
  public OrderQty2 get(OrderQty2 value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrderQty2 getOrderQty2() throws FieldNotFound
  { 
    OrderQty2 value = new OrderQty2();  
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
} 
