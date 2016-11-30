package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class OrderStatusRequest extends Message 
{ 

  public OrderStatusRequest() 
  { 
    getHeader().setField(new MsgType("H")); 
  } 
  public OrderStatusRequest(    
    ClOrdID aClOrdID,    
    Symbol aSymbol,    
    Side aSide ) 
  {  
    getHeader().setField(new MsgType("H")); 
    set(aClOrdID); 
    set(aSymbol); 
    set(aSide);  
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
} 
