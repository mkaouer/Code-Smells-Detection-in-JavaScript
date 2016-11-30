package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class OrderCancelReplaceRequest extends Message 
{ 

  public OrderCancelReplaceRequest() 
  { 
    getHeader().setField(new MsgType("G")); 
  } 
  public OrderCancelReplaceRequest(    
    OrigClOrdID aOrigClOrdID,    
    ClOrdID aClOrdID,    
    HandlInst aHandlInst,    
    Symbol aSymbol,    
    Side aSide,    
    TransactTime aTransactTime,    
    OrdType aOrdType ) 
  {  
    getHeader().setField(new MsgType("G")); 
    set(aOrigClOrdID); 
    set(aClOrdID); 
    set(aHandlInst); 
    set(aSymbol); 
    set(aSide); 
    set(aTransactTime); 
    set(aOrdType);  
  } 

  public void set(OrderID value) 
  { 
    setField(value); 
  } 
  public OrderID get(OrderID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrderID getOrderID() throws FieldNotFound
  { 
    OrderID value = new OrderID();  
    getField(value);  
    return value;  
  } 

  public void set(ClientID value) 
  { 
    setField(value); 
  } 
  public ClientID get(ClientID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClientID getClientID() throws FieldNotFound
  { 
    ClientID value = new ClientID();  
    getField(value);  
    return value;  
  } 

  public void set(ExecBroker value) 
  { 
    setField(value); 
  } 
  public ExecBroker get(ExecBroker value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExecBroker getExecBroker() throws FieldNotFound
  { 
    ExecBroker value = new ExecBroker();  
    getField(value);  
    return value;  
  } 

  public void set(OrigClOrdID value) 
  { 
    setField(value); 
  } 
  public OrigClOrdID get(OrigClOrdID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrigClOrdID getOrigClOrdID() throws FieldNotFound
  { 
    OrigClOrdID value = new OrigClOrdID();  
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

  public void set(ListID value) 
  { 
    setField(value); 
  } 
  public ListID get(ListID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ListID getListID() throws FieldNotFound
  { 
    ListID value = new ListID();  
    getField(value);  
    return value;  
  } 

  public void set(Account value) 
  { 
    setField(value); 
  } 
  public Account get(Account value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Account getAccount() throws FieldNotFound
  { 
    Account value = new Account();  
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

  public void set(HandlInst value) 
  { 
    setField(value); 
  } 
  public HandlInst get(HandlInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public HandlInst getHandlInst() throws FieldNotFound
  { 
    HandlInst value = new HandlInst();  
    getField(value);  
    return value;  
  } 

  public void set(ExecInst value) 
  { 
    setField(value); 
  } 
  public ExecInst get(ExecInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExecInst getExecInst() throws FieldNotFound
  { 
    ExecInst value = new ExecInst();  
    getField(value);  
    return value;  
  } 

  public void set(MinQty value) 
  { 
    setField(value); 
  } 
  public MinQty get(MinQty value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MinQty getMinQty() throws FieldNotFound
  { 
    MinQty value = new MinQty();  
    getField(value);  
    return value;  
  } 

  public void set(MaxFloor value) 
  { 
    setField(value); 
  } 
  public MaxFloor get(MaxFloor value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MaxFloor getMaxFloor() throws FieldNotFound
  { 
    MaxFloor value = new MaxFloor();  
    getField(value);  
    return value;  
  } 

  public void set(ExDestination value) 
  { 
    setField(value); 
  } 
  public ExDestination get(ExDestination value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExDestination getExDestination() throws FieldNotFound
  { 
    ExDestination value = new ExDestination();  
    getField(value);  
    return value;  
  } 

  public void set(NoTradingSessions value) 
  { 
    setField(value); 
  } 
  public NoTradingSessions get(NoTradingSessions value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoTradingSessions getNoTradingSessions() throws FieldNotFound
  { 
    NoTradingSessions value = new NoTradingSessions();  
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

  public void set(OrderQty value) 
  { 
    setField(value); 
  } 
  public OrderQty get(OrderQty value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrderQty getOrderQty() throws FieldNotFound
  { 
    OrderQty value = new OrderQty();  
    getField(value);  
    return value;  
  } 

  public void set(CashOrderQty value) 
  { 
    setField(value); 
  } 
  public CashOrderQty get(CashOrderQty value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashOrderQty getCashOrderQty() throws FieldNotFound
  { 
    CashOrderQty value = new CashOrderQty();  
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

  public void set(StopPx value) 
  { 
    setField(value); 
  } 
  public StopPx get(StopPx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public StopPx getStopPx() throws FieldNotFound
  { 
    StopPx value = new StopPx();  
    getField(value);  
    return value;  
  } 

  public void set(PegDifference value) 
  { 
    setField(value); 
  } 
  public PegDifference get(PegDifference value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public PegDifference getPegDifference() throws FieldNotFound
  { 
    PegDifference value = new PegDifference();  
    getField(value);  
    return value;  
  } 

  public void set(DiscretionInst value) 
  { 
    setField(value); 
  } 
  public DiscretionInst get(DiscretionInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public DiscretionInst getDiscretionInst() throws FieldNotFound
  { 
    DiscretionInst value = new DiscretionInst();  
    getField(value);  
    return value;  
  } 

  public void set(DiscretionOffset value) 
  { 
    setField(value); 
  } 
  public DiscretionOffset get(DiscretionOffset value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public DiscretionOffset getDiscretionOffset() throws FieldNotFound
  { 
    DiscretionOffset value = new DiscretionOffset();  
    getField(value);  
    return value;  
  } 

  public void set(ComplianceID value) 
  { 
    setField(value); 
  } 
  public ComplianceID get(ComplianceID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ComplianceID getComplianceID() throws FieldNotFound
  { 
    ComplianceID value = new ComplianceID();  
    getField(value);  
    return value;  
  } 

  public void set(SolicitedFlag value) 
  { 
    setField(value); 
  } 
  public SolicitedFlag get(SolicitedFlag value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SolicitedFlag getSolicitedFlag() throws FieldNotFound
  { 
    SolicitedFlag value = new SolicitedFlag();  
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

  public void set(TimeInForce value) 
  { 
    setField(value); 
  } 
  public TimeInForce get(TimeInForce value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public TimeInForce getTimeInForce() throws FieldNotFound
  { 
    TimeInForce value = new TimeInForce();  
    getField(value);  
    return value;  
  } 

  public void set(EffectiveTime value) 
  { 
    setField(value); 
  } 
  public EffectiveTime get(EffectiveTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public EffectiveTime getEffectiveTime() throws FieldNotFound
  { 
    EffectiveTime value = new EffectiveTime();  
    getField(value);  
    return value;  
  } 

  public void set(ExpireDate value) 
  { 
    setField(value); 
  } 
  public ExpireDate get(ExpireDate value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExpireDate getExpireDate() throws FieldNotFound
  { 
    ExpireDate value = new ExpireDate();  
    getField(value);  
    return value;  
  } 

  public void set(ExpireTime value) 
  { 
    setField(value); 
  } 
  public ExpireTime get(ExpireTime value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExpireTime getExpireTime() throws FieldNotFound
  { 
    ExpireTime value = new ExpireTime();  
    getField(value);  
    return value;  
  } 

  public void set(GTBookingInst value) 
  { 
    setField(value); 
  } 
  public GTBookingInst get(GTBookingInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public GTBookingInst getGTBookingInst() throws FieldNotFound
  { 
    GTBookingInst value = new GTBookingInst();  
    getField(value);  
    return value;  
  } 

  public void set(Commission value) 
  { 
    setField(value); 
  } 
  public Commission get(Commission value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Commission getCommission() throws FieldNotFound
  { 
    Commission value = new Commission();  
    getField(value);  
    return value;  
  } 

  public void set(CommType value) 
  { 
    setField(value); 
  } 
  public CommType get(CommType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CommType getCommType() throws FieldNotFound
  { 
    CommType value = new CommType();  
    getField(value);  
    return value;  
  } 

  public void set(Rule80A value) 
  { 
    setField(value); 
  } 
  public Rule80A get(Rule80A value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public Rule80A getRule80A() throws FieldNotFound
  { 
    Rule80A value = new Rule80A();  
    getField(value);  
    return value;  
  } 

  public void set(ForexReq value) 
  { 
    setField(value); 
  } 
  public ForexReq get(ForexReq value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ForexReq getForexReq() throws FieldNotFound
  { 
    ForexReq value = new ForexReq();  
    getField(value);  
    return value;  
  } 

  public void set(SettlCurrency value) 
  { 
    setField(value); 
  } 
  public SettlCurrency get(SettlCurrency value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlCurrency getSettlCurrency() throws FieldNotFound
  { 
    SettlCurrency value = new SettlCurrency();  
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

  public void set(CoveredOrUncovered value) 
  { 
    setField(value); 
  } 
  public CoveredOrUncovered get(CoveredOrUncovered value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CoveredOrUncovered getCoveredOrUncovered() throws FieldNotFound
  { 
    CoveredOrUncovered value = new CoveredOrUncovered();  
    getField(value);  
    return value;  
  } 

  public void set(CustomerOrFirm value) 
  { 
    setField(value); 
  } 
  public CustomerOrFirm get(CustomerOrFirm value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CustomerOrFirm getCustomerOrFirm() throws FieldNotFound
  { 
    CustomerOrFirm value = new CustomerOrFirm();  
    getField(value);  
    return value;  
  } 

  public void set(MaxShow value) 
  { 
    setField(value); 
  } 
  public MaxShow get(MaxShow value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MaxShow getMaxShow() throws FieldNotFound
  { 
    MaxShow value = new MaxShow();  
    getField(value);  
    return value;  
  } 

  public void set(LocateReqd value) 
  { 
    setField(value); 
  } 
  public LocateReqd get(LocateReqd value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LocateReqd getLocateReqd() throws FieldNotFound
  { 
    LocateReqd value = new LocateReqd();  
    getField(value);  
    return value;  
  } 

  public void set(ClearingFirm value) 
  { 
    setField(value); 
  } 
  public ClearingFirm get(ClearingFirm value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClearingFirm getClearingFirm() throws FieldNotFound
  { 
    ClearingFirm value = new ClearingFirm();  
    getField(value);  
    return value;  
  } 

  public void set(ClearingAccount value) 
  { 
    setField(value); 
  } 
  public ClearingAccount get(ClearingAccount value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ClearingAccount getClearingAccount() throws FieldNotFound
  { 
    ClearingAccount value = new ClearingAccount();  
    getField(value);  
    return value;  
  } 
} 
