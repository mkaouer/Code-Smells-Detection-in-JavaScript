package org.quickfix.fix40; 
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
    OrderQty aOrderQty,    
    OrdType aOrdType ) 
  {  
    getHeader().setField(new MsgType("G")); 
    set(aOrigClOrdID); 
    set(aClOrdID); 
    set(aHandlInst); 
    set(aSymbol); 
    set(aSide); 
    set(aOrderQty); 
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
} 
