package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Allocation extends Message 
{ 

  public Allocation() 
  { 
    getHeader().setField(new MsgType("J")); 
  } 
  public Allocation(    
    AllocID aAllocID,    
    AllocTransType aAllocTransType,    
    NoOrders aNoOrders,    
    Side aSide,    
    Symbol aSymbol,    
    Shares aShares,    
    AvgPx aAvgPx,    
    TradeDate aTradeDate,    
    NoAllocs aNoAllocs ) 
  {  
    getHeader().setField(new MsgType("J")); 
    set(aAllocID); 
    set(aAllocTransType); 
    set(aNoOrders); 
    set(aSide); 
    set(aSymbol); 
    set(aShares); 
    set(aAvgPx); 
    set(aTradeDate); 
    set(aNoAllocs);  
  } 

  public void set(AllocID value) 
  { 
    setField(value); 
  } 
  public AllocID get(AllocID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocID getAllocID() throws FieldNotFound
  { 
    AllocID value = new AllocID();  
    getField(value);  
    return value;  
  } 

  public void set(AllocTransType value) 
  { 
    setField(value); 
  } 
  public AllocTransType get(AllocTransType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocTransType getAllocTransType() throws FieldNotFound
  { 
    AllocTransType value = new AllocTransType();  
    getField(value);  
    return value;  
  } 

  public void set(RefAllocID value) 
  { 
    setField(value); 
  } 
  public RefAllocID get(RefAllocID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public RefAllocID getRefAllocID() throws FieldNotFound
  { 
    RefAllocID value = new RefAllocID();  
    getField(value);  
    return value;  
  } 

  public void set(AllocLinkID value) 
  { 
    setField(value); 
  } 
  public AllocLinkID get(AllocLinkID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocLinkID getAllocLinkID() throws FieldNotFound
  { 
    AllocLinkID value = new AllocLinkID();  
    getField(value);  
    return value;  
  } 

  public void set(AllocLinkType value) 
  { 
    setField(value); 
  } 
  public AllocLinkType get(AllocLinkType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocLinkType getAllocLinkType() throws FieldNotFound
  { 
    AllocLinkType value = new AllocLinkType();  
    getField(value);  
    return value;  
  } 

  public void set(NoOrders value) 
  { 
    setField(value); 
  } 
  public NoOrders get(NoOrders value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoOrders getNoOrders() throws FieldNotFound
  { 
    NoOrders value = new NoOrders();  
    getField(value);  
    return value;  
  } 

  public void set(NoExecs value) 
  { 
    setField(value); 
  } 
  public NoExecs get(NoExecs value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoExecs getNoExecs() throws FieldNotFound
  { 
    NoExecs value = new NoExecs();  
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

  public void set(Shares value) 
  { 
    setField(value); 
  } 
  public Shares get(Shares value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Shares getShares() throws FieldNotFound
  { 
    Shares value = new Shares();  
    getField(value);  
    return value;  
  } 

  public void set(LastMkt value) 
  { 
    setField(value); 
  } 
  public LastMkt get(LastMkt value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LastMkt getLastMkt() throws FieldNotFound
  { 
    LastMkt value = new LastMkt();  
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

  public void set(AvgPx value) 
  { 
    setField(value); 
  } 
  public AvgPx get(AvgPx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AvgPx getAvgPx() throws FieldNotFound
  { 
    AvgPx value = new AvgPx();  
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

  public void set(AvgPrxPrecision value) 
  { 
    setField(value); 
  } 
  public AvgPrxPrecision get(AvgPrxPrecision value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AvgPrxPrecision getAvgPrxPrecision() throws FieldNotFound
  { 
    AvgPrxPrecision value = new AvgPrxPrecision();  
    getField(value);  
    return value;  
  } 

  public void set(TradeDate value) 
  { 
    setField(value); 
  } 
  public TradeDate get(TradeDate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TradeDate getTradeDate() throws FieldNotFound
  { 
    TradeDate value = new TradeDate();  
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

  public void set(SettlmntTyp value) 
  { 
    setField(value); 
  } 
  public SettlmntTyp get(SettlmntTyp value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlmntTyp getSettlmntTyp() throws FieldNotFound
  { 
    SettlmntTyp value = new SettlmntTyp();  
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

  public void set(GrossTradeAmt value) 
  { 
    setField(value); 
  } 
  public GrossTradeAmt get(GrossTradeAmt value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public GrossTradeAmt getGrossTradeAmt() throws FieldNotFound
  { 
    GrossTradeAmt value = new GrossTradeAmt();  
    getField(value);  
    return value;  
  } 

  public void set(NetMoney value) 
  { 
    setField(value); 
  } 
  public NetMoney get(NetMoney value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NetMoney getNetMoney() throws FieldNotFound
  { 
    NetMoney value = new NetMoney();  
    getField(value);  
    return value;  
  } 

  public void set(OpenClose value) 
  { 
    setField(value); 
  } 
  public OpenClose get(OpenClose value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OpenClose getOpenClose() throws FieldNotFound
  { 
    OpenClose value = new OpenClose();  
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

  public void set(EncodedTextLen value) 
  { 
    setField(value); 
  } 
  public EncodedTextLen get(EncodedTextLen value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedTextLen getEncodedTextLen() throws FieldNotFound
  { 
    EncodedTextLen value = new EncodedTextLen();  
    getField(value);  
    return value;  
  } 

  public void set(EncodedText value) 
  { 
    setField(value); 
  } 
  public EncodedText get(EncodedText value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EncodedText getEncodedText() throws FieldNotFound
  { 
    EncodedText value = new EncodedText();  
    getField(value);  
    return value;  
  } 

  public void set(NumDaysInterest value) 
  { 
    setField(value); 
  } 
  public NumDaysInterest get(NumDaysInterest value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NumDaysInterest getNumDaysInterest() throws FieldNotFound
  { 
    NumDaysInterest value = new NumDaysInterest();  
    getField(value);  
    return value;  
  } 

  public void set(AccruedInterestRate value) 
  { 
    setField(value); 
  } 
  public AccruedInterestRate get(AccruedInterestRate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AccruedInterestRate getAccruedInterestRate() throws FieldNotFound
  { 
    AccruedInterestRate value = new AccruedInterestRate();  
    getField(value);  
    return value;  
  } 

  public void set(NoAllocs value) 
  { 
    setField(value); 
  } 
  public NoAllocs get(NoAllocs value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoAllocs getNoAllocs() throws FieldNotFound
  { 
    NoAllocs value = new NoAllocs();  
    getField(value);  
    return value;  
  } 
} 
