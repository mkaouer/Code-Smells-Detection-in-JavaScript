package org.quickfix.fix41; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class Advertisement extends Message 
{ 

  public Advertisement() 
  { 
    getHeader().setField(new MsgType("7")); 
  } 
  public Advertisement(    
    AdvId aAdvId,    
    AdvTransType aAdvTransType,    
    Symbol aSymbol,    
    AdvSide aAdvSide,    
    Shares aShares ) 
  {  
    getHeader().setField(new MsgType("7")); 
    set(aAdvId); 
    set(aAdvTransType); 
    set(aSymbol); 
    set(aAdvSide); 
    set(aShares);  
  } 

  public void set(AdvId value) 
  { 
    setField(value); 
  } 
  public AdvId get(AdvId value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AdvId getAdvId() throws FieldNotFound
  { 
    AdvId value = new AdvId();  
    getField(value);  
    return value;  
  } 

  public void set(AdvTransType value) 
  { 
    setField(value); 
  } 
  public AdvTransType get(AdvTransType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AdvTransType getAdvTransType() throws FieldNotFound
  { 
    AdvTransType value = new AdvTransType();  
    getField(value);  
    return value;  
  } 

  public void set(AdvRefID value) 
  { 
    setField(value); 
  } 
  public AdvRefID get(AdvRefID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AdvRefID getAdvRefID() throws FieldNotFound
  { 
    AdvRefID value = new AdvRefID();  
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

  public void set(AdvSide value) 
  { 
    setField(value); 
  } 
  public AdvSide get(AdvSide value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public AdvSide getAdvSide() throws FieldNotFound
  { 
    AdvSide value = new AdvSide();  
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

  public void set(URLLink value) 
  { 
    setField(value); 
  } 
  public URLLink get(URLLink value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public URLLink getURLLink() throws FieldNotFound
  { 
    URLLink value = new URLLink();  
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
} 
