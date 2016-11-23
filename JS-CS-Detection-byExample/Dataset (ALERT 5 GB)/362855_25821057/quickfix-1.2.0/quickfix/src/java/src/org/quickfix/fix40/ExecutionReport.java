package org.quickfix.fix40; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class ExecutionReport extends Message 
{ 

  public ExecutionReport() 
  { 
    getHeader().setField(new MsgType("8")); 
  } 
  public ExecutionReport(    
    OrderID aOrderID,    
    ExecID aExecID,    
    ExecTransType aExecTransType,    
    OrdStatus aOrdStatus,    
    Symbol aSymbol,    
    Side aSide,    
    OrderQty aOrderQty,    
    LastShares aLastShares,    
    LastPx aLastPx,    
    CumQty aCumQty,    
    AvgPx aAvgPx ) 
  {  
    getHeader().setField(new MsgType("8")); 
    set(aOrderID); 
    set(aExecID); 
    set(aExecTransType); 
    set(aOrdStatus); 
    set(aSymbol); 
    set(aSide); 
    set(aOrderQty); 
    set(aLastShares); 
    set(aLastPx); 
    set(aCumQty); 
    set(aAvgPx);  
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

  public void set(ExecID value) 
  { 
    setField(value); 
  } 
  public ExecID get(ExecID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExecID getExecID() throws FieldNotFound
  { 
    ExecID value = new ExecID();  
    getField(value);  
    return value;  
  } 

  public void set(ExecTransType value) 
  { 
    setField(value); 
  } 
  public ExecTransType get(ExecTransType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExecTransType getExecTransType() throws FieldNotFound
  { 
    ExecTransType value = new ExecTransType();  
    getField(value);  
    return value;  
  } 

  public void set(ExecRefID value) 
  { 
    setField(value); 
  } 
  public ExecRefID get(ExecRefID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ExecRefID getExecRefID() throws FieldNotFound
  { 
    ExecRefID value = new ExecRefID();  
    getField(value);  
    return value;  
  } 

  public void set(OrdStatus value) 
  { 
    setField(value); 
  } 
  public OrdStatus get(OrdStatus value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrdStatus getOrdStatus() throws FieldNotFound
  { 
    OrdStatus value = new OrdStatus();  
    getField(value);  
    return value;  
  } 

  public void set(OrdRejReason value) 
  { 
    setField(value); 
  } 
  public OrdRejReason get(OrdRejReason value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public OrdRejReason getOrdRejReason() throws FieldNotFound
  { 
    OrdRejReason value = new OrdRejReason();  
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

  public void set(LastShares value) 
  { 
    setField(value); 
  } 
  public LastShares get(LastShares value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LastShares getLastShares() throws FieldNotFound
  { 
    LastShares value = new LastShares();  
    getField(value);  
    return value;  
  } 

  public void set(LastPx value) 
  { 
    setField(value); 
  } 
  public LastPx get(LastPx value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LastPx getLastPx() throws FieldNotFound
  { 
    LastPx value = new LastPx();  
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

  public void set(LastCapacity value) 
  { 
    setField(value); 
  } 
  public LastCapacity get(LastCapacity value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public LastCapacity getLastCapacity() throws FieldNotFound
  { 
    LastCapacity value = new LastCapacity();  
    getField(value);  
    return value;  
  } 

  public void set(CumQty value) 
  { 
    setField(value); 
  } 
  public CumQty get(CumQty value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CumQty getCumQty() throws FieldNotFound
  { 
    CumQty value = new CumQty();  
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

  public void set(ReportToExch value) 
  { 
    setField(value); 
  } 
  public ReportToExch get(ReportToExch value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ReportToExch getReportToExch() throws FieldNotFound
  { 
    ReportToExch value = new ReportToExch();  
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

  public void set(NoMiscFees value) 
  { 
    setField(value); 
  } 
  public NoMiscFees get(NoMiscFees value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoMiscFees getNoMiscFees() throws FieldNotFound
  { 
    NoMiscFees value = new NoMiscFees();  
    getField(value);  
    return value;  
  } 

  public void set(MiscFeeAmt value) 
  { 
    setField(value); 
  } 
  public MiscFeeAmt get(MiscFeeAmt value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MiscFeeAmt getMiscFeeAmt() throws FieldNotFound
  { 
    MiscFeeAmt value = new MiscFeeAmt();  
    getField(value);  
    return value;  
  } 

  public void set(MiscFeeType value) 
  { 
    setField(value); 
  } 
  public MiscFeeType get(MiscFeeType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MiscFeeType getMiscFeeType() throws FieldNotFound
  { 
    MiscFeeType value = new MiscFeeType();  
    getField(value);  
    return value;  
  } 

  public void set(SettlCurrAmt value) 
  { 
    setField(value); 
  } 
  public SettlCurrAmt get(SettlCurrAmt value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlCurrAmt getSettlCurrAmt() throws FieldNotFound
  { 
    SettlCurrAmt value = new SettlCurrAmt();  
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
} 
