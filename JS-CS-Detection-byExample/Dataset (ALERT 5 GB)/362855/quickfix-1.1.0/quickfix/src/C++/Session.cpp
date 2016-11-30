/* ====================================================================
 * The QuickFIX Software License, Version 1.0
 *
 * Copyright (c) 2001 ThoughtWorks, Inc.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        ThoughtWorks, Inc. (http://www.thoughtworks.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "QuickFIX" and "ThoughtWorks, Inc." must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact quickfix-users@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "QuickFIX",
 *    nor may "QuickFIX" appear in their name, without prior written
 *    permission of ThoughtWorks, Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THOUGHTWORKS INC OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

#ifdef _MSC_VER
#pragma warning( disable : 4503 4355 4786 )
#include "stdafx.h"
#else
#include "config.h"
#endif

#include "Session.h"
#include "Values.h"
#include <algorithm>
#include <iostream>

namespace FIX
{
  Session::Sessions Session::s_sessions;
  Session::Sessions Session::s_registered;
  Mutex Session::s_mutex;

  Session::Session( Application& application,
                    MessageStoreFactory& factory,
                    const SessionID& sessionID,
                    const DataDictionary& dataDictionary,
                    const UtcTimeOnly& startTime,
                    const UtcTimeOnly& endTime,
                    int heartBtInt )
  : m_application(application), m_pResponder(0),
    m_sessionID(sessionID), m_heartBtInt(heartBtInt),
    m_startTime(startTime), m_endTime(endTime),
    m_receivedLogon(false), m_sentLogout(false), m_sentLogon(false),
    m_dataDictionary(dataDictionary), m_factory(factory)
  {
    m_initiate = (heartBtInt != 0);
    m_pStore = m_factory.create(sessionID);

    UtcTimeStamp creationTime = m_pStore->getCreationTime();
    UtcTimeStamp now;

    if(creationTime > now ||
      !isSessionTime(startTime, endTime, now) ||
      !isSameSession(startTime, endTime, now, creationTime))
    {
      reset();
    }

    addSession(*this);
  }

  Session::~Session()
  {
    removeSession(*this);
    m_factory.destroy(m_pStore);
  }

  void Session::fill( Header& header )
  {
    m_lastSentTime.setCurrent();
    header.setField( m_sessionID.getBeginString() );
    header.setField( m_sessionID.getSenderCompID() );
    header.setField( m_sessionID.getTargetCompID() );
    header.setField( MsgSeqNum(getExpectedSenderNum()) );
    header.setField( SendingTime() );
  }

  void Session::next()
  {
    Locker locker(m_mutex);

    UtcTimeStamp now;
    if(!checkSessionTime(now))
    { reset(); return; }
    
    if( !receivedLogon() )
      {
        if(m_initiate && !m_sentLogon)
            generateLogon();
        else if(m_initiate && m_sentLogon)
          {
            UtcTimeStamp now;
            if(now - m_lastReceivedTime >= 10)
              disconnect();
          }
        return;
      }

    if( m_heartBtInt == 0 ) return;
    
    if(m_sentLogout && (now - m_lastReceivedTime >= 2))
      disconnect();

    if((now - m_lastSentTime) < m_heartBtInt) return;

    if((now - m_lastReceivedTime) >= (5 * m_heartBtInt))
      disconnect();
    else
      if((now - m_lastReceivedTime) >= (1.2 * m_heartBtInt))
        generateTestRequest();
      else
        generateHeartbeat();
  }

  void Session::nextLogon( const Message& logon )
  {
    if( m_initiate && !m_sentLogon )
      { disconnect(); return; }

    SenderCompID senderCompID;
    TargetCompID targetCompID;
    logon.getHeader().getField(senderCompID);
    logon.getHeader().getField(targetCompID);

    bool verified = verify(logon, false, true);
    if(isCorrectCompID(senderCompID, targetCompID))
      m_receivedLogon = true;
    if(!verified) return;

    if( !m_initiate )
      {
        logon.getField(m_heartBtInt);
        generateLogon(logon);
      }

    MsgSeqNum msgSeqNum;
    logon.getHeader().getField(msgSeqNum);
    if( isTargetTooHigh(msgSeqNum) )
      {
        doTargetTooHigh(logon);
      }
    else
      {
        m_pStore->incrNextTargetMsgSeqNum();
        nextQueued();
      }
 
    if(isLoggedOn()) 
      m_application.onLogon(m_sessionID);
  }

  void Session::nextHeartbeat( const Message& heartbeat )
  {
    if( !verify(heartbeat) ) return;
    m_pStore->incrNextTargetMsgSeqNum();
    nextQueued();
  }

  void Session::nextTestRequest( const Message& testRequest )
  {
    if( !verify(testRequest) ) return;
    generateHeartbeat( testRequest );
    m_pStore->incrNextTargetMsgSeqNum();
    nextQueued();
  }

  void Session::nextLogout( const Message& logout )
  {
    if( !verify(logout, false, false) ) return;
    if( !sentLogout() ) generateLogout();
    m_pStore->incrNextTargetMsgSeqNum();
    disconnect();
  }

  void Session::nextReject( const Message& reject )
  {
    if( !verify(reject) ) return;
    m_pStore->incrNextTargetMsgSeqNum();
    nextQueued();
  }

  void Session::nextSequenceReset( const Message& sequenceReset )
  {
    bool isGapFill = false;
    GapFillFlag gapFillFlag;
    if( sequenceReset.isSetField(gapFillFlag) )
      {
        sequenceReset.getField(gapFillFlag);
        isGapFill = gapFillFlag;
      }

    if( !verify(sequenceReset, isGapFill, isGapFill) ) return;

    NewSeqNo newSeqNo;
    if(sequenceReset.isSetField(newSeqNo))
      {
        sequenceReset.getField(newSeqNo);
        if( newSeqNo > getExpectedTargetNum() )
          m_pStore->setNextTargetMsgSeqNum(MsgSeqNum(newSeqNo));
        else if( newSeqNo < getExpectedTargetNum() )
          generateReject(sequenceReset, 5);
      }
  }

  void Session::nextResendRequest( const Message& resendRequest )
  {
    if( !verify(resendRequest, false, false) ) return;
    BeginSeqNo beginSeqNo;
    EndSeqNo endSeqNo;
    resendRequest.getField(beginSeqNo);
    resendRequest.getField(endSeqNo);
    std::vector<Message> messages;

    if(m_pStore->get(beginSeqNo, endSeqNo, messages))
      {
        std::vector<Message>::iterator i;
        MsgSeqNum msgSeqNum;
        MsgType msgType;
        int begin = 0;

        for( i = messages.begin(); i != messages.end(); ++i )
          {
            i->getHeader().getField(msgSeqNum);
            i->getHeader().getField(msgType);
            if( Message::isAdminMsgType(msgType) )
              {
                if(!begin) begin = msgSeqNum;
              }
            else
              {
                if(begin) generateSequenceReset(begin, msgSeqNum);
                resend(*i);
                begin = 0;
              }
          }
        if(begin) generateSequenceReset(begin, msgSeqNum+1);
      }
    else
      generateSequenceReset(beginSeqNo, getExpectedSenderNum());

    m_pStore->incrNextTargetMsgSeqNum();
  }

  bool Session::send( Message& message )
  {
    Locker locker(m_mutex);

    try
      {
        Header& header = message.getHeader();
        SenderSubID senderSubID;
        TargetSubID targetSubID;
        BeginString beginString;
        MsgType msgType;

        bool hasSenderSubID = header.isSetField(senderSubID);
        bool hasTargetSubID = header.isSetField(targetSubID);
        if(hasSenderSubID) header.getField(senderSubID);
        if(hasTargetSubID) header.getField(targetSubID);

        header.getField(beginString);
        header.getField(msgType);

        header.clear();

        header.setField(beginString);
        header.setField(msgType);
        if(hasSenderSubID) header.setField(senderSubID);
        if(hasTargetSubID) header.setField(targetSubID);
        return sendRaw(message);
      } catch(FieldNotFound&) { return false; }
  }

  bool Session::sendRaw( Message& message, int msgSeqNum )
  {
    bool result = false;
    Header& header = message.getHeader();
    MsgType msgType;
    header.getField(msgType);

    fill(header);

    if(msgSeqNum)
      header.setField(MsgSeqNum(msgSeqNum));

    if(Message::isAdminMsgType(msgType))
    {
      m_application.toAdmin(message, m_sessionID);
      if(
          msgType == "A" || msgType == "5" 
          || msgType == "2" || msgType == "4"
          || isLoggedOn()
        )
        result = send(message.getString());
    }
    else
    {
      try
      {
        m_application.toApp(message, m_sessionID);
        if(isLoggedOn()) result = send(message.getString());
      }
      catch(DoNotSend&) {}
    }

    if(!msgSeqNum)
    {
      m_pStore->set(message);
      m_pStore->incrNextSenderMsgSeqNum();
    }
    return result;
  }

  void Session::disconnect()
  {
    if(m_pResponder)
      {
        m_pResponder->disconnect();
        m_pResponder = 0;
      }

    if(receivedLogon() || sentLogon())
    {
      m_receivedLogon = false;
      m_sentLogon = false;
      m_application.onLogout(m_sessionID);
    }

    m_sentLogout = false;
    m_queue.clear();
  }

  void Session::resend( Message& message )
  {
    SendingTime sendingTime;
    Header& header = message.getHeader();
    header.getField(sendingTime);

    header.setField(OrigSendingTime(sendingTime));
    header.setField(SendingTime());
    header.setField(PossDupFlag(true));

    try
    {
      m_application.toApp(message, m_sessionID);
      send(message.getString());
    }
    catch(DoNotSend&)
    {
      MsgSeqNum msgSeqNum;
      header.getField(msgSeqNum);

      Message sequenceReset;
      fill( sequenceReset.getHeader() );
      sequenceReset.getHeader().setField(MsgType("4"));
      sequenceReset.getHeader().setField(PossDupFlag(true));
      sequenceReset.getHeader().setField(OrigSendingTime(sendingTime));
      sequenceReset.getHeader().setField(msgSeqNum);
      sequenceReset.setField(NewSeqNo(msgSeqNum+1));
      sequenceReset.setField(GapFillFlag(true));
      send(sequenceReset.getString());
    }
  }

  void Session::generateLogon()
  {
    Message logon;
    logon.getHeader().setField(MsgType("A"));
    logon.setField(EncryptMethod(0));
    logon.setField(m_heartBtInt);
    fill( logon.getHeader() );
    m_lastReceivedTime.setCurrent();
    m_sentLogon = true;
    sendRaw(logon);
  }

  void Session::generateLogon( const Message& aLogon )
  {
    EncryptMethod encryptMethod;
    HeartBtInt heartBtInt;
    aLogon.getField(encryptMethod);
    aLogon.getField(heartBtInt);

    Message logon;
    logon.getHeader().setField(MsgType("A"));
    logon.setField(encryptMethod);
    logon.setField(heartBtInt);
    fill( logon.getHeader() );
    sendRaw(logon);
    m_sentLogon = true;
  }

  void Session::generateResendRequest( const MsgSeqNum& msgSeqNum )
  {
    Message resendRequest;
    resendRequest.getHeader().setField(MsgType("2"));
    resendRequest.setField(BeginSeqNo((int)getExpectedTargetNum()));
    resendRequest.setField(EndSeqNo(msgSeqNum - 1));
    fill( resendRequest.getHeader() );
    sendRaw(resendRequest);
  }

  void Session::generateSequenceReset
  ( int beginSeqNo, int endSeqNo )
  {
    Message sequenceReset;
    sequenceReset.getHeader().setField(MsgType("4"));
    sequenceReset.getHeader().setField(PossDupFlag(true));
    sequenceReset.setField(NewSeqNo(endSeqNo));
    fill( sequenceReset.getHeader() );
    sequenceReset.getHeader().setField(MsgSeqNum(beginSeqNo));
    sequenceReset.setField(GapFillFlag(true));
    sendRaw(sequenceReset, beginSeqNo);
  }

  void Session::generateHeartbeat()
  {
    Message heartbeat;
    heartbeat.getHeader().setField(MsgType("0"));
    fill( heartbeat.getHeader() );
    sendRaw(heartbeat);
  }

  void Session::generateHeartbeat( const Message& testRequest )
  {
    Message heartbeat;
    heartbeat.getHeader().setField(MsgType("0"));
    fill( heartbeat.getHeader() );
    try
      {
        TestReqID testReqID;
        testRequest.getField(testReqID);
        heartbeat.setField(testReqID);
      }
    catch( FieldNotFound& ) {}

    sendRaw(heartbeat);
  }

  void Session::generateTestRequest()
  {
    Message testRequest;
    testRequest.getHeader().setField(MsgType("1"));
    fill( testRequest.getHeader() );
    TestReqID testReqID("TEST");
    testRequest.setField(testReqID);

    sendRaw(testRequest);
  }

  void Session::generateReject( const Message& message, int err, int field )
  {
    std::string beginString = m_sessionID.getBeginString();

    Message reject;
    reject.getHeader().setField(MsgType("3"));
    fill( reject.getHeader() );
    MsgSeqNum msgSeqNum;
    MsgType msgType;
    message.getHeader().getField(msgType);
    message.getHeader().getField(msgSeqNum);
    reject.setField(RefSeqNum(msgSeqNum));
    if(beginString >= FIX::BeginString_FIX42)
      reject.setField(RefMsgType(msgType));
    reject.setField(SessionRejectReason(err));
    if( msgType != "A" && msgType != "4")
      m_pStore->incrNextTargetMsgSeqNum();

    switch( err )
      {
      case 0:
        if(beginString >= FIX::BeginString_FIX42)
          reject.setField(RefTagID(field));
        reject.setField(Text("Invalid tag number")); break;
      case 1:
        if(beginString >= FIX::BeginString_FIX42)
          reject.setField(RefTagID(field));
        reject.setField(Text("Required tag missing")); break;
      case 2:
        if(beginString >= FIX::BeginString_FIX42)
          reject.setField(RefTagID(field));
        reject.setField(Text("Tag not defined for this message type"));
        break;
      case 4:
        if(beginString >= FIX::BeginString_FIX42)
          reject.setField(RefTagID(field));
        reject.setField(Text("Tag specified without a value")); break;
      case 5:
        if(beginString >= FIX::BeginString_FIX42 && field)
          reject.setField(RefTagID(field));
        reject.setField
          (Text("Value is incorrect (out of range) for this tag"));
        break;
      case 6:
        if(beginString >= FIX::BeginString_FIX42)
          reject.setField(RefTagID(field));
        reject.setField(Text("Incorrect data format for value")); break;
      case 9:
        reject.setField(Text("CompID problem")); break;
      case 10:
        reject.setField(Text("SendingTime accuracy problem")); break;
      case 11:
        reject.setField(Text("Invalid MsgType")); break;
      };

    sendRaw(reject);
  }

  void Session::generateReject( const Message& message, const std::string& str )
  {
    std::string beginString = m_sessionID.getBeginString();

    Message reject;
    reject.getHeader().setField(MsgType("3"));
    fill( reject.getHeader() );
    MsgType msgType;
    MsgSeqNum msgSeqNum;
    message.getHeader().getField(msgType);
    message.getHeader().getField(msgSeqNum);
    if(beginString >= FIX::BeginString_FIX42)
      reject.setField(RefMsgType(msgType));
    reject.setField(RefSeqNum(msgSeqNum));

    if( msgType != "A" && msgType != "4")
      m_pStore->incrNextTargetMsgSeqNum();
    reject.setField(Text(str));
    sendRaw(reject);
  }

  void Session::generateBusinessReject( const Message& message, int err )
  {
    Message reject;
    reject.getHeader().setField(MsgType("j"));
    fill( reject.getHeader() );
    MsgType msgType;
    MsgSeqNum msgSeqNum;
    message.getHeader().getField(msgType);
    message.getHeader().getField(msgSeqNum);
    reject.setField(RefMsgType(msgType));
    reject.setField(RefSeqNum(msgSeqNum));
    reject.setField(BusinessRejectReason(err));
    m_pStore->incrNextTargetMsgSeqNum();

    if( err == 3 )
      reject.setField(Text("Unsupported Message Type"));
    sendRaw(reject);
  }

  void Session::generateLogout( const std::string& text )
  {
    Message logout;
    logout.getHeader().setField(MsgType("5"));
    fill( logout.getHeader() );
    if(text.length())
      logout.setField(Text(text));
    sendRaw(logout);
    m_sentLogout = true;
  }

  bool Session::verify( const Message& msg, bool checkTooHigh, 
			bool checkTooLow )
  {
    SenderCompID senderCompID;
    TargetCompID targetCompID;
    SendingTime sendingTime;
    MsgType msgType;
    MsgSeqNum msgSeqNum;

    try
      {
        const Header& header = msg.getHeader();
        header.getField(senderCompID);
        header.getField(targetCompID);
        header.getField(sendingTime);
        header.getField(msgType);
        header.getField(msgSeqNum);

        if( !validLogonState(msgType) )
          throw std::exception();
        if( !isGoodTime(sendingTime) )
          doBadTime(msg);
        if( !isCorrectCompID(senderCompID, targetCompID) )
          doBadCompID(msg);

        if( checkTooHigh && isTargetTooHigh(msgSeqNum) )
          {
            doTargetTooHigh(msg);
            return false;
          }
        else if( checkTooLow && isTargetTooLow(msgSeqNum) )
          {
            if(doTargetTooLow(msg))
              fromCallback(msgType, msg, m_sessionID);
            return false;
          }

        m_lastReceivedTime.setCurrent();
      }
    catch( std::exception& )
      { disconnect(); return false; }

    fromCallback(msgType, msg, m_sessionID);
    return true;
  }

  bool Session::validLogonState(const MsgType& msgType)
  {
    if( msgType == "A" && !receivedLogon()
        || msgType != "A" && receivedLogon() )
      return true;
    if( msgType != "5" && sentLogout() )
      return true;
    if( msgType == "4" ) return true;
    return false;
  }

  void Session::fromCallback(const MsgType& msgType, const Message& msg, 
                             const SessionID& sessionID)
  {
    if(Message::isAdminMsgType(msgType))
      m_application.fromAdmin(msg, m_sessionID);
    else
      m_application.fromApp(msg, m_sessionID);
  }

  void Session::doBadTime(const Message& msg)
  {
    generateReject(msg, 10);
    generateLogout();
  }

  void Session::doBadCompID(const Message& msg)
  {
    if( !receivedLogon() )
      throw std::exception();
    generateReject(msg, 9);
    generateLogout();
  }

  bool Session::doPossDup(const Message& msg)
  {
    const Header& header = msg.getHeader();
    OrigSendingTime origSendingTime;
    SendingTime sendingTime;

    if(!header.isSetField(origSendingTime))
      {
        generateReject(msg, 1, origSendingTime.getField());
        return false;
      }
    header.getField(origSendingTime);
    header.getField(sendingTime);

    if(origSendingTime > sendingTime)
      {
        generateReject(msg, 10);
        generateLogout();
        return false;
      }
    return true;
  }

  bool Session::doTargetTooLow(const Message& msg)
  {
    const Header& header = msg.getHeader();
    MsgType msgType;
    PossDupFlag possDupFlag;
    header.getField(msgType);
    header.getField(possDupFlag);

    if(!possDupFlag) throw std::exception();
    return doPossDup(msg);
  }

  void Session::doTargetTooHigh(const Message& msg)
  {
    const Header& header = msg.getHeader();
    MsgSeqNum msgSeqNum;
    header.getField(msgSeqNum);

    queue(msgSeqNum, msg);
    generateResendRequest(msgSeqNum);
  }

  void Session::nextQueued()
  {
    Message msg;
    MsgType msgType;

    while(retreive(MsgSeqNum(getExpectedTargetNum()), msg))
      {
        msg.getHeader().getField(msgType);
        if(msgType != "A")
          next(msg.getString());
      }
  }

  void Session::next( const std::string& msg )
  {
    next(Message(msg, m_dataDictionary));
  }

  void Session::next( const Message& message )
  {
    Locker locker(m_mutex);

    if(!checkSessionTime(UtcTimeStamp()))
    { reset(); return; }

    MsgType msgType;
    BeginString beginString;
    SenderCompID senderCompID;
    TargetCompID targetCompID;

    try
      {
        message.getHeader().getField(msgType);
        message.getHeader().getField(beginString);
        message.getHeader().getField(senderCompID);
        message.getHeader().getField(targetCompID);

        if(beginString != m_sessionID.getBeginString())
          throw UnsupportedVersion();

        m_dataDictionary.validate(message);

        if( msgType == "A" )
          nextLogon(message);
        else if( msgType == "0" )
          nextHeartbeat(message);
        else if( msgType == "1" )
          nextTestRequest(message);
        else if( msgType == "4" )
          nextSequenceReset(message);
        else if( msgType == "5" )
          nextLogout(message);
        else if( msgType == "2" )
          nextResendRequest(message);
        else if( msgType == "3" )
          nextReject(message);
        else
          {
            if(!verify(message)) return;
            m_pStore->incrNextTargetMsgSeqNum();
          }
        nextQueued();
      }
    catch( MessageParseError& ) {}
    catch( RequiredTagMissing& e )
      { generateReject(message, 1, e.field); }
    catch( FieldNotFound& e )
      { generateReject(message, 1, e.field); 
        if(msgType == "A") disconnect();
      }
    catch( InvalidTagNumber& e )
      { generateReject(message, 0, e.field); }
    catch( NoTagValue& e )
      { generateReject(message, 4, e.field); }
    catch( TagNotDefinedForMessage& e)
      { generateReject(message, 2, e.field); }
    catch( InvalidMessageType& )
      { generateReject(message, 11); }
    catch( UnsupportedMessageType& )
      { if(beginString >= FIX::BeginString_FIX42)
          generateBusinessReject(message, 3); 
        else
          generateReject(message, "Unsupported message type");
      }
    catch( FieldsOutOfOrder& e )
      { generateReject(message, e.what()); }
    catch( IncorrectDataFormat& e )
      { generateReject(message, 6, e.field); }
    catch( IncorrectTagValue& e )
      { generateReject(message, 5, e.field); }
    catch( InvalidMessage& ) {}
    catch( RejectLogon& ) { disconnect(); }
    catch( UnsupportedVersion& )
      {
        if( msgType == "5" )
          nextLogout(message);
        else
          {
            generateLogout("Incorrect BeginString");
            m_pStore->incrNextTargetMsgSeqNum();
          }
      }
  }

  bool Session::sendToTarget( Message& message )
    throw(SessionNotFound&)
  {
    try
      {
        SessionID sessionID = message.getSessionID();
        Session* pSession = lookupSession(sessionID);
        if( !pSession ) throw SessionNotFound();
        bool result = pSession->send(message);
        return result;
      }
    catch( FieldNotFound& ) { throw SessionNotFound(); }
  }

  bool Session::sendToTarget( Message& message, const SessionID& sessionID )
    throw(SessionNotFound&)
  {
    message.setSessionID(sessionID);
    return sendToTarget(message);
  }

  bool Session::sendToTarget
  ( Message& message, const SenderCompID& senderCompID, 
    const TargetCompID& targetCompID )
    throw(SessionNotFound&)
  {
    message.getHeader().setField(senderCompID);
    message.getHeader().setField(targetCompID);
    return sendToTarget(message);
  }

  bool Session::doesSessionExist( const SessionID& sessionID )
  {
    Locker locker(s_mutex);
    return s_sessions.end() != s_sessions.find( sessionID );
  }

  Session* Session::lookupSession( const SessionID& sessionID )
  {
    Locker locker(s_mutex);
    Sessions::iterator find = s_sessions.find( sessionID );
    if( find != s_sessions.end() )
      return find->second;
    else
      return 0;
  }

  Session* Session::lookupSession( const std::string& string, bool reverse )
  {
    Message message;
    if( !message.setStringHeader(string) )
      return 0;

    BeginString beginString;
    SenderCompID senderCompID;
    TargetCompID targetCompID;
    try
    {
      message.getHeader().getField(beginString);
      message.getHeader().getField(senderCompID);
      message.getHeader().getField(targetCompID);
    }
    catch(FieldNotFound&) { return 0; }

    if(reverse)
    {
      return lookupSession(SessionID(beginString, SenderCompID(targetCompID), 
                          TargetCompID(senderCompID)));
    }
    else
    {
      return lookupSession(SessionID(beginString, senderCompID, targetCompID));
    }
  }

  bool Session::isSessionRegistered( const SessionID& sessionID )
  {
    Locker locker(s_mutex);
    return s_registered.end() != s_registered.find( sessionID );
  }

  Session* Session::registerSession( const SessionID& sessionID )
  {
    Locker locker(s_mutex);
    Session* pSession = lookupSession(sessionID);
    if(pSession == 0) return 0;
    if(isSessionRegistered(sessionID)) return 0;
    s_registered[sessionID] = pSession;
    return pSession;
  }

  void Session::unregisterSession( const SessionID& sessionID )
  {
    s_registered.erase(sessionID);
  }

  int Session::numSessions()
  {
    Locker locker(s_mutex);
    return s_sessions.size();
  }

  bool Session::addSession( Session& s )
  {
    Locker locker(s_mutex);
    Sessions::iterator it = s_sessions.find( s.m_sessionID );
    if (it == s_sessions.end())
      {
        s_sessions[s.m_sessionID] = &s;
        return true;
      }
    else
      return false;
  }

  void Session::removeSession( Session& s )
  {
    Locker locker(s_mutex);
    s_sessions.erase( s.m_sessionID );
    s_registered.erase( s.m_sessionID );
  }

  bool Session::isSessionTime(const UtcTimeOnly& start, const UtcTimeOnly& end,
                              const UtcTimeStamp& time )
  {
    UtcTimeOnly timeOnly(time);

    if(start < end)
      return( timeOnly >= start && timeOnly <= end );
    else
      return( timeOnly >= start || timeOnly <= end );
  }

  bool Session::isSameSession(const UtcTimeOnly& start, 
			      const UtcTimeOnly& end,
                              const UtcTimeStamp& time1, 
			      const UtcTimeStamp& time2 )
  {
    if(!isSessionTime(start, end, time1)) return false;
    if(!isSessionTime(start, end, time2)) return false;

    if(time1 == time2) return true;

    UtcDate time1Date(time1);
    UtcDate time2Date(time2);

    if(start < end)
      return time1Date == time2Date;
    else
      return abs(time1Date - time2Date) <= 1;
      
    return false;
  }

  void Session::queue( int msgSeqNum, const Message& message )
  {
    m_queue[msgSeqNum] = message;
  }

  bool Session::retreive( int msgSeqNum, Message& message )
  {
    Messages::iterator i = m_queue.find(msgSeqNum);
    if( i != m_queue.end() )
      {
        message = i->second;
        m_queue.erase(i);
        return true;
      }
    return false;
  }
}
