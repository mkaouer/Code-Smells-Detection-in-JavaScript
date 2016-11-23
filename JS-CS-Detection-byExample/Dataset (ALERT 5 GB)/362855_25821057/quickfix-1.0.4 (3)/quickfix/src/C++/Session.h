/* -*- C++ -*- */
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

#ifndef FIX_SESSION_H
#define FIX_SESSION_H

#include "SessionID.h"
#include "Fields.h"
#include "Messages.h"
#include "MessageStore.h"
#include "MessageValidator.h"
#include "Application.h"
#include "Mutex.h"
#include <utility>
#include <map>
#include <queue>

namespace FIX
{
  /// Maintains the state and implements the logic of a %FIX %session.
  class Session
  {
  public:
    class Responder;

    Session( Application&, MessageStoreFactory&,
             const SessionID&, const std::string& url,
             const UtcTimeOnly& startTime, const UtcTimeOnly &endTime,
             int heartBtInt = 0 );
    ~Session();

    bool sentLogon()     { return m_sentLogon;                    }
    bool sentLogout()    { return m_sentLogout;                   }
    bool receivedLogon() { return m_receivedLogon;                }
    bool isLoggedOn()    { return receivedLogon() && sentLogon(); }
    void reset()         { disconnect(); m_pStore->reset();       }

    long getExpectedSenderNum()
    { return m_pStore->getNextSenderMsgSeqNum(); }
    long getExpectedTargetNum()
    { return m_pStore->getNextTargetMsgSeqNum(); }

    const SessionID& getSessionID() const { return m_sessionID; }
    void setResponder(Responder* pR) { m_pResponder = pR; }

    static bool sendToTarget( Message& )
      throw(SessionNotFound&);
    static bool sendToTarget( Message&, const SessionID& )
      throw(SessionNotFound&);
    static bool sendToTarget( Message&, const SenderCompID&, const TargetCompID& )
      throw(SessionNotFound&);
    static bool doesSessionExist( const SessionID& );
    static Session* lookupSession( const SessionID& );
    static int numSessions();
    static bool isSessionTime(const UtcTimeOnly& start, const UtcTimeOnly& end,
                              const UtcTimeStamp& time );
    static bool isSameSession(const UtcTimeOnly& start, const UtcTimeOnly& end,
                              const UtcTimeStamp& time1, const UtcTimeStamp& time2 );
    bool isSessionTime() { return checkSessionTime(UtcTimeStamp()); }

    bool send( Message& );
    void next();
    void next( const Message& );
    void disconnect();

  private:
    typedef std::map< SessionID, Session* > Sessions;
    static bool addSession( Session& );
    static void removeSession( Session& );

    typedef std::map<int, Message> Messages;
    void queue( int, const Message& );
    bool retreive( int, Message& );

    bool send(const std::string message)
    { if(m_pResponder) return m_pResponder->send(message); return false; }
    bool sendRaw( Message&, int msgSeqNum = 0 );
    void resend( Message& message );
    void fill( Header& );

    bool isGoodTime(const SendingTime& sendingTime)
    {
      UtcTimeStamp now;
      return abs(now - sendingTime) <= 120;
    }
    bool checkSessionTime(const UtcTimeStamp& time)
    {
      UtcTimeStamp creationTime = m_pStore->getCreationTime();

      if(!isSessionTime(m_startTime, m_endTime, time))
        return false;
      if((time - creationTime) > UTC_DAY)
        return false;
      return true;
    }
    bool isTargetTooHigh(const MsgSeqNum& msgSeqNum)
    { return msgSeqNum > (m_pStore->getNextTargetMsgSeqNum()); }
    bool isTargetTooLow(const MsgSeqNum& msgSeqNum)
    { return msgSeqNum < (m_pStore->getNextTargetMsgSeqNum()); }
    bool isCorrectCompID(const SenderCompID& senderCompID,
                         const TargetCompID& targetCompID)
    {
      return m_sessionID.getSenderCompID().getValue() == targetCompID.getValue()
        && m_sessionID.getTargetCompID().getValue() == senderCompID.getValue();
    }
    bool validLogonState(const MsgType& msgType)
    {
      if( msgType == "A" && !receivedLogon()
          || msgType != "A" && receivedLogon() )
        return true;
      if( msgType != "5" && sentLogout() )
        return true;
      if( msgType == "4" ) return true;
      return false;
    }

    void doBadTime(const Message& msg);
    void doBadCompID(const Message& msg);
    bool doPossDup(const Message& msg);
    bool doTargetTooLow(const Message& msg);
    void doTargetTooHigh(const Message& msg);
    void nextQueued();

    void nextLogon( const Message& );
    void nextHeartbeat( const Message& );
    void nextTestRequest( const Message& );
    void nextLogout( const Message& );
    void nextReject( const Message& );
    void nextSequenceReset( const Message& );
    void nextResendRequest( const Message& );

    void generateLogon();
    void generateLogon( const Message& );
    void generateResendRequest( const MsgSeqNum& );
    void generateSequenceReset( int, int );
    void generateHeartbeat();
    void generateHeartbeat( const Message& );
    void generateTestRequest();
    void generateReject( const Message&, int err, int field = 0 );
    void generateReject( const Message&, const std::string& );
    void generateBusinessReject( const Message&, int err );
    void generateLogout( const std::string& text = "" );

    bool verify( const Message& msg,
                 bool checkTooHigh = true, bool checkTooLow = true );

    Application& m_application;
    Responder* m_pResponder;
    SessionID m_sessionID;
    HeartBtInt m_heartBtInt;
    const std::string& m_url;
    UtcTimeOnly m_startTime;
    UtcTimeOnly m_endTime;

    bool m_receivedLogon;
    bool m_sentLogout;
    bool m_sentLogon;
    bool m_initiate;
    UtcTimeStamp m_lastSentTime;
    UtcTimeStamp m_lastReceivedTime;
    MessageValidator m_validator;
    MessageStoreFactory& m_factory;
    MessageStore* m_pStore;
    Messages m_queue;
    Mutex m_mutex;

    static Sessions s_sessions;
    static Mutex s_mutex;

  public:
    /// Interface implements sending on and disconnecting a transport.
    class Responder
    {
      friend class Session;
    public:
      virtual ~Responder() {}
      virtual bool send( const std::string& ) = 0;
      virtual void disconnect() = 0;
    };
  };
}

#endif //FIX_SESSION_H
