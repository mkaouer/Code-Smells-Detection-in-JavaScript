package org.quickfix.fix40; 
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
    ClOrdID aClOrdID,    
    Side aSide,    
    Symbol aSymbol,    
    Shares aShares,    
    AvgPx aAvgPx,    
    TradeDate aTradeDate,    
    NoAllocs aNoAllocs,    
    AllocAccount aAllocAccount,    
    AllocShares aAllocShares ) 
  {  
    getHeader().setField(new MsgType("J")); 
    set(aAllocID); 
    set(aAllocTransType); 
    set(aNoOrders); 
    set(aClOrdID); 
    set(aSide); 
    set(aSymbol); 
    set(aShares); 
    set(aAvgPx); 
    set(aTradeDate); 
    set(aNoAllocs); 
    set(aAllocAccount); 
    set(aAllocShares);  
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

  public void set(WaveNo value) 
  { 
    setField(value); 
  } 
  public WaveNo get(WaveNo value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public WaveNo getWaveNo() throws FieldNotFound
  { 
    WaveNo value = new WaveNo();  
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

  public void set(MiscFeeCurr value) 
  { 
    setField(value); 
  } 
  public MiscFeeCurr get(MiscFeeCurr value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public MiscFeeCurr getMiscFeeCurr() throws FieldNotFound
  { 
    MiscFeeCurr value = new MiscFeeCurr();  
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

  public void set(AllocAccount value) 
  { 
    setField(value); 
  } 
  public AllocAccount get(AllocAccount value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocAccount getAllocAccount() throws FieldNotFound
  { 
    AllocAccount value = new AllocAccount();  
    getField(value);  
    return value;  
  } 

  public void set(AllocShares value) 
  { 
    setField(value); 
  } 
  public AllocShares get(AllocShares value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AllocShares getAllocShares() throws FieldNotFound
  { 
    AllocShares value = new AllocShares();  
    getField(value);  
    return value;  
  } 

  public void set(ProcessCode value) 
  { 
    setField(value); 
  } 
  public ProcessCode get(ProcessCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public ProcessCode getProcessCode() throws FieldNotFound
  { 
    ProcessCode value = new ProcessCode();  
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

  public void set(NoDlvyInst value) 
  { 
    setField(value); 
  } 
  public NoDlvyInst get(NoDlvyInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public NoDlvyInst getNoDlvyInst() throws FieldNotFound
  { 
    NoDlvyInst value = new NoDlvyInst();  
    getField(value);  
    return value;  
  } 

  public void set(BrokerOfCredit value) 
  { 
    setField(value); 
  } 
  public BrokerOfCredit get(BrokerOfCredit value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public BrokerOfCredit getBrokerOfCredit() throws FieldNotFound
  { 
    BrokerOfCredit value = new BrokerOfCredit();  
    getField(value);  
    return value;  
  } 

  public void set(DlvyInst value) 
  { 
    setField(value); 
  } 
  public DlvyInst get(DlvyInst value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public DlvyInst getDlvyInst() throws FieldNotFound
  { 
    DlvyInst value = new DlvyInst();  
    getField(value);  
    return value;  
  } 
} 
