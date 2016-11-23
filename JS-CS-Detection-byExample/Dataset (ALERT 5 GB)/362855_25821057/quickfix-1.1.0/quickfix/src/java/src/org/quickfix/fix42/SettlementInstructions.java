package org.quickfix.fix42; 
import org.quickfix.Message; 
import org.quickfix.FieldNotFound; 
import org.quickfix.field.*; 

public class SettlementInstructions extends Message 
{ 

  public SettlementInstructions() 
  { 
    getHeader().setField(new MsgType("T")); 
  } 
  public SettlementInstructions(    
    SettlInstID aSettlInstID,    
    SettlInstTransType aSettlInstTransType,    
    SettlInstRefID aSettlInstRefID,    
    SettlInstMode aSettlInstMode,    
    SettlInstSource aSettlInstSource,    
    AllocAccount aAllocAccount,    
    TransactTime aTransactTime ) 
  {  
    getHeader().setField(new MsgType("T")); 
    set(aSettlInstID); 
    set(aSettlInstTransType); 
    set(aSettlInstRefID); 
    set(aSettlInstMode); 
    set(aSettlInstSource); 
    set(aAllocAccount); 
    set(aTransactTime);  
  } 

  public void set(SettlInstID value) 
  { 
    setField(value); 
  } 
  public SettlInstID get(SettlInstID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlInstID getSettlInstID() throws FieldNotFound
  { 
    SettlInstID value = new SettlInstID();  
    getField(value);  
    return value;  
  } 

  public void set(SettlInstTransType value) 
  { 
    setField(value); 
  } 
  public SettlInstTransType get(SettlInstTransType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlInstTransType getSettlInstTransType() throws FieldNotFound
  { 
    SettlInstTransType value = new SettlInstTransType();  
    getField(value);  
    return value;  
  } 

  public void set(SettlInstRefID value) 
  { 
    setField(value); 
  } 
  public SettlInstRefID get(SettlInstRefID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlInstRefID getSettlInstRefID() throws FieldNotFound
  { 
    SettlInstRefID value = new SettlInstRefID();  
    getField(value);  
    return value;  
  } 

  public void set(SettlInstMode value) 
  { 
    setField(value); 
  } 
  public SettlInstMode get(SettlInstMode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlInstMode getSettlInstMode() throws FieldNotFound
  { 
    SettlInstMode value = new SettlInstMode();  
    getField(value);  
    return value;  
  } 

  public void set(SettlInstSource value) 
  { 
    setField(value); 
  } 
  public SettlInstSource get(SettlInstSource value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlInstSource getSettlInstSource() throws FieldNotFound
  { 
    SettlInstSource value = new SettlInstSource();  
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

  public void set(SettlLocation value) 
  { 
    setField(value); 
  } 
  public SettlLocation get(SettlLocation value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlLocation getSettlLocation() throws FieldNotFound
  { 
    SettlLocation value = new SettlLocation();  
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

  public void set(StandInstDbType value) 
  { 
    setField(value); 
  } 
  public StandInstDbType get(StandInstDbType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public StandInstDbType getStandInstDbType() throws FieldNotFound
  { 
    StandInstDbType value = new StandInstDbType();  
    getField(value);  
    return value;  
  } 

  public void set(StandInstDbName value) 
  { 
    setField(value); 
  } 
  public StandInstDbName get(StandInstDbName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public StandInstDbName getStandInstDbName() throws FieldNotFound
  { 
    StandInstDbName value = new StandInstDbName();  
    getField(value);  
    return value;  
  } 

  public void set(StandInstDbID value) 
  { 
    setField(value); 
  } 
  public StandInstDbID get(StandInstDbID value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public StandInstDbID getStandInstDbID() throws FieldNotFound
  { 
    StandInstDbID value = new StandInstDbID();  
    getField(value);  
    return value;  
  } 

  public void set(SettlDeliveryType value) 
  { 
    setField(value); 
  } 
  public SettlDeliveryType get(SettlDeliveryType value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlDeliveryType getSettlDeliveryType() throws FieldNotFound
  { 
    SettlDeliveryType value = new SettlDeliveryType();  
    getField(value);  
    return value;  
  } 

  public void set(SettlDepositoryCode value) 
  { 
    setField(value); 
  } 
  public SettlDepositoryCode get(SettlDepositoryCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlDepositoryCode getSettlDepositoryCode() throws FieldNotFound
  { 
    SettlDepositoryCode value = new SettlDepositoryCode();  
    getField(value);  
    return value;  
  } 

  public void set(SettlBrkrCode value) 
  { 
    setField(value); 
  } 
  public SettlBrkrCode get(SettlBrkrCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlBrkrCode getSettlBrkrCode() throws FieldNotFound
  { 
    SettlBrkrCode value = new SettlBrkrCode();  
    getField(value);  
    return value;  
  } 

  public void set(SettlInstCode value) 
  { 
    setField(value); 
  } 
  public SettlInstCode get(SettlInstCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SettlInstCode getSettlInstCode() throws FieldNotFound
  { 
    SettlInstCode value = new SettlInstCode();  
    getField(value);  
    return value;  
  } 

  public void set(SecuritySettlAgentName value) 
  { 
    setField(value); 
  } 
  public SecuritySettlAgentName get(SecuritySettlAgentName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecuritySettlAgentName getSecuritySettlAgentName() throws FieldNotFound
  { 
    SecuritySettlAgentName value = new SecuritySettlAgentName();  
    getField(value);  
    return value;  
  } 

  public void set(SecuritySettlAgentCode value) 
  { 
    setField(value); 
  } 
  public SecuritySettlAgentCode get(SecuritySettlAgentCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecuritySettlAgentCode getSecuritySettlAgentCode() throws FieldNotFound
  { 
    SecuritySettlAgentCode value = new SecuritySettlAgentCode();  
    getField(value);  
    return value;  
  } 

  public void set(SecuritySettlAgentAcctNum value) 
  { 
    setField(value); 
  } 
  public SecuritySettlAgentAcctNum get(SecuritySettlAgentAcctNum value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecuritySettlAgentAcctNum getSecuritySettlAgentAcctNum() throws FieldNotFound
  { 
    SecuritySettlAgentAcctNum value = new SecuritySettlAgentAcctNum();  
    getField(value);  
    return value;  
  } 

  public void set(SecuritySettlAgentAcctName value) 
  { 
    setField(value); 
  } 
  public SecuritySettlAgentAcctName get(SecuritySettlAgentAcctName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecuritySettlAgentAcctName getSecuritySettlAgentAcctName() throws FieldNotFound
  { 
    SecuritySettlAgentAcctName value = new SecuritySettlAgentAcctName();  
    getField(value);  
    return value;  
  } 

  public void set(SecuritySettlAgentContactName value) 
  { 
    setField(value); 
  } 
  public SecuritySettlAgentContactName get(SecuritySettlAgentContactName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecuritySettlAgentContactName getSecuritySettlAgentContactName() throws FieldNotFound
  { 
    SecuritySettlAgentContactName value = new SecuritySettlAgentContactName();  
    getField(value);  
    return value;  
  } 

  public void set(SecuritySettlAgentContactPhone value) 
  { 
    setField(value); 
  } 
  public SecuritySettlAgentContactPhone get(SecuritySettlAgentContactPhone value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public SecuritySettlAgentContactPhone getSecuritySettlAgentContactPhone() throws FieldNotFound
  { 
    SecuritySettlAgentContactPhone value = new SecuritySettlAgentContactPhone();  
    getField(value);  
    return value;  
  } 

  public void set(CashSettlAgentName value) 
  { 
    setField(value); 
  } 
  public CashSettlAgentName get(CashSettlAgentName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashSettlAgentName getCashSettlAgentName() throws FieldNotFound
  { 
    CashSettlAgentName value = new CashSettlAgentName();  
    getField(value);  
    return value;  
  } 

  public void set(CashSettlAgentCode value) 
  { 
    setField(value); 
  } 
  public CashSettlAgentCode get(CashSettlAgentCode value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashSettlAgentCode getCashSettlAgentCode() throws FieldNotFound
  { 
    CashSettlAgentCode value = new CashSettlAgentCode();  
    getField(value);  
    return value;  
  } 

  public void set(CashSettlAgentAcctNum value) 
  { 
    setField(value); 
  } 
  public CashSettlAgentAcctNum get(CashSettlAgentAcctNum value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashSettlAgentAcctNum getCashSettlAgentAcctNum() throws FieldNotFound
  { 
    CashSettlAgentAcctNum value = new CashSettlAgentAcctNum();  
    getField(value);  
    return value;  
  } 

  public void set(CashSettlAgentAcctName value) 
  { 
    setField(value); 
  } 
  public CashSettlAgentAcctName get(CashSettlAgentAcctName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashSettlAgentAcctName getCashSettlAgentAcctName() throws FieldNotFound
  { 
    CashSettlAgentAcctName value = new CashSettlAgentAcctName();  
    getField(value);  
    return value;  
  } 

  public void set(CashSettlAgentContactName value) 
  { 
    setField(value); 
  } 
  public CashSettlAgentContactName get(CashSettlAgentContactName value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashSettlAgentContactName getCashSettlAgentContactName() throws FieldNotFound
  { 
    CashSettlAgentContactName value = new CashSettlAgentContactName();  
    getField(value);  
    return value;  
  } 

  public void set(CashSettlAgentContactPhone value) 
  { 
    setField(value); 
  } 
  public CashSettlAgentContactPhone get(CashSettlAgentContactPhone value) throws FieldNotFound
  { 
    getField(value); 
    return value; 
  } 
  public CashSettlAgentContactPhone getCashSettlAgentContactPhone() throws FieldNotFound
  { 
    CashSettlAgentContactPhone value = new CashSettlAgentContactPhone();  
    getField(value);  
    return value;  
  } 
} 
