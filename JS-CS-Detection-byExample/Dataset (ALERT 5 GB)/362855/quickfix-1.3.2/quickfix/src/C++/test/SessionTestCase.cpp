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

#include "SessionTestCase.h"
#include "../Values.h"

namespace FIX
{
using namespace FIX42;

void fillHeader( Header& header, char* sender, char* target, int seq )
{
  header.setField( SenderCompID( sender ) );
  header.setField( TargetCompID( target ) );
  header.setField( SendingTime() );
  header.setField( MsgSeqNum( seq ) );
}

Logon createLogon( char* sender, char* target, int seq )
{
  Logon logon( EncryptMethod( 0 ), HeartBtInt( 30 ) );
  fillHeader( logon.getHeader(), sender, target, seq );
  return logon;
}

Logout createLogout( char* sender, char* target, int seq )
{
  Logout logout;
  fillHeader( logout.getHeader(), sender, target, seq );
  return logout;
}

Heartbeat createHeartbeat( char* sender, char* target, int seq )
{
  Heartbeat heartbeat;
  fillHeader( heartbeat.getHeader(), sender, target, seq );
  return heartbeat;
}

TestRequest createTestRequest( char* sender, char* target, int seq, char* id )
{
  TestRequest testRequest;
  testRequest.set( TestReqID( id ) );
  fillHeader( testRequest.getHeader(), sender, target, seq );
  return testRequest;
}

SequenceReset createSequenceReset( char* sender, char* target, int seq, int newSeq )
{
  SequenceReset sequenceReset;
  sequenceReset.set( NewSeqNo( newSeq ) );
  fillHeader( sequenceReset.getHeader(), sender, target, seq );
  return sequenceReset;
}

ResendRequest createResendRequest( char* sender, char* target, int seq, int begin, int end )
{
  ResendRequest resendRequest;
  resendRequest.set( BeginSeqNo( begin ) );
  resendRequest.set( EndSeqNo( end ) );
  fillHeader( resendRequest.getHeader(), sender, target, seq );
  return resendRequest;
}

Reject createReject( char* sender, char* target, int seq, int refSeq )
{
  Reject reject;
  reject.set( RefSeqNum( refSeq ) );
  fillHeader( reject.getHeader(), sender, target, seq );
  return reject;
}

TestRequest createNewOrderSingle( char* sender, char* target, int seq )
{
  NewOrderSingle newOrderSingle;
  fillHeader( newOrderSingle.getHeader(), sender, target, seq );
  return newOrderSingle;
}

bool SessionTestCase::Test::onSetup( Session*& pObject )
{
  pObject = createSession();
  pObject->setResponder( this );
  return true;
}

void SessionTestCase::nextLogon::onRun( Session& object )
{
  // send with an incorrect SenderCompID
  object.setResponder( this );
  object.next( createLogon( "BLAH", "TW", 1 ) );
  assert( !object.receivedLogon() );
  assert( m_toLogon == 0 );
  assert( m_disconnect == 1 );

  // send with an incorrect TargetCompID
  object.setResponder( this );
  object.next( createLogon( "ISLD", "BLAH", 1 ) );
  assert( !object.receivedLogon() );
  assert( m_toLogon == 0 );
  assert( m_disconnect == 2 );

  // send a correct logon
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( object.receivedLogon() );
  assert( m_toLogon == 1 );
  assert( m_disconnect == 2 );

  // check that we got a valid logon response
  SenderCompID senderCompID;
  TargetCompID targetCompID;
  HeartBtInt heartBtInt;
  EncryptMethod encryptMethod;
  try
  {
    m_logon.getHeader().getField( senderCompID );
    m_logon.getHeader().getField( targetCompID );
    m_logon.getField( heartBtInt );
    m_logon.getField( encryptMethod );
  }
  catch ( FieldNotFound& ) { assert( false ); }
  assert( senderCompID == "TW" );
  assert( targetCompID == "ISLD" );
  assert( heartBtInt == 30 );
  assert( encryptMethod == 0 );
}

void SessionTestCase::nextLogonNoEncryptMethod::onRun( Session& object )
{
  // send a correct logon
  object.setResponder( this );
  Logon logon;
  logon.setField( HeartBtInt( 30 ) );
  fillHeader( logon.getHeader(), "ISLD", "TW", 1 );
  object.next( logon );
  assert( object.receivedLogon() );
  assert( m_toLogon == 1 );
  assert( m_disconnect == 0 );

  // check that we got a valid logon response
  SenderCompID senderCompID;
  TargetCompID targetCompID;
  HeartBtInt heartBtInt;
  EncryptMethod encryptMethod;
  try
  {
    m_logon.getHeader().getField( senderCompID );
    m_logon.getHeader().getField( targetCompID );
    m_logon.getField( heartBtInt );
    m_logon.getField( encryptMethod );
  }
  catch ( FieldNotFound& ) { assert( false ); }
  assert( senderCompID == "TW" );
  assert( targetCompID == "ISLD" );
  assert( heartBtInt == 30 );
  assert( encryptMethod == 0 );
}

void SessionTestCase::notifyResendRequest::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 5 ) );
  assert( m_toResendRequest == 1 );
}

void SessionTestCase::incrMsgSeqNum::onRun( Session& object )
{
  assert( object.getExpectedSenderNum() == 1 );
  assert( object.getExpectedTargetNum() == 1 );

  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( object.getExpectedSenderNum() == 2 );
  assert( object.getExpectedTargetNum() == 2 );

  object.next( createHeartbeat( "ISLD", "TW", 2 ) );
  assert( object.getExpectedSenderNum() == 2 );
  assert( object.getExpectedTargetNum() == 3 );

  object.next( createHeartbeat( "ISLD", "TW", 3 ) );
  assert( object.getExpectedSenderNum() == 2 );
  assert( object.getExpectedTargetNum() == 4 );
}

void SessionTestCase::callDisconnect::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( m_disconnect == 0 );

  object.next( createHeartbeat( "ISLD", "TW", 2 ) );
  assert( m_disconnect == 0 );
  assert( m_fromHeartbeat == 1 );

  object.next( createHeartbeat( "ISLD", "TW", 3 ) );
  assert( m_disconnect == 0 );
  assert( m_fromHeartbeat == 2 );

  // message is dupicate
  Heartbeat heartbeat = createHeartbeat( "ISLD", "TW", 2 );
  heartbeat.getHeader().setField( PossDupFlag( true ) );

  UtcTimeStamp timeStamp;
  timeStamp.setSecond( 5 );
  OrigSendingTime origSendingTime( timeStamp );
  timeStamp.setSecond( 10 );
  SendingTime sendingTime( timeStamp );

  // message is a possible dup, remain connected
  heartbeat.getHeader().setField( sendingTime );
  heartbeat.getHeader().setField( origSendingTime );
  object.next( heartbeat );
  assert( m_disconnect == 0 );
  assert( m_fromHeartbeat == 2 );

  // message is not a possible dup, disconnect
  heartbeat.getHeader().setField( PossDupFlag( false ) );
  object.next( heartbeat );
  assert( m_disconnect == 1 );
  assert( m_fromHeartbeat == 2 );
}

void SessionTestCase::doesSessionExist::onRun( Session& object )
{
  Session * pSession1 = new Session
                        ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                                       SenderCompID( "TW" ), TargetCompID( "ISLD" ) ), DataDictionary(),
                          UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  Session* pSession2 = new Session
                       ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                                      SenderCompID( "WT" ), TargetCompID( "ISLD" ) ), DataDictionary(),
                         UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  Session* pSession3 = new Session
                       ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                                      SenderCompID( "TW" ), TargetCompID( "DLSI" ) ), DataDictionary(),
                         UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  Session* pSession4 = new Session
                       ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                                      SenderCompID( "OREN" ), TargetCompID( "NERO" ) ), DataDictionary(),
                         UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  Session* pSession5 = new Session
                       ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                                      SenderCompID( "OREN" ), TargetCompID( "NERO" ) ), DataDictionary(),
                         UtcTimeOnly(), UtcTimeOnly(), 0, 0 );

  pSession1->setResponder( this );
  pSession2->setResponder( this );
  pSession3->setResponder( this );
  pSession4->setResponder( this );
  pSession5->setResponder( this );

  assert( Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ), SenderCompID( "TW" ),
                       TargetCompID( "ISLD" ) ) ) );
  assert( Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ), SenderCompID( "WT" ),
                       TargetCompID( "ISLD" ) ) ) );
  assert( Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ), SenderCompID( "TW" ),
                       TargetCompID( "DLSI" ) ) ) );
  assert( Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ), SenderCompID( "OREN" ),
                       TargetCompID( "NERO" ) ) ) );

  assert( Session::numSessions() == 4 );

  delete pSession1;

  assert( !Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) );
  assert( Session::numSessions() == 3 );

  delete pSession2;
  delete pSession3;

  assert( Session::numSessions() == 1 );
  assert( Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ), SenderCompID( "OREN" ),
                       TargetCompID( "NERO" ) ) ) );

  delete pSession4;
  assert( Session::numSessions() == 0 );
  assert( !Session::doesSessionExist
          ( SessionID( BeginString( "FIX.4.2" ), SenderCompID( "OREN" ),
                       TargetCompID( "NERO" ) ) ) );

  delete pSession5;
}

bool SessionTestCase::lookupSession::onSetup( Session*& pObject )
{
  m_pSession1 = new Session
                ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                               SenderCompID( "TW" ), TargetCompID( "ISLD" ) ), DataDictionary(),
                  UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  m_pSession2 = new Session
                ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                               SenderCompID( "WT" ), TargetCompID( "ISLD" ) ), DataDictionary(),
                  UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  m_pSession3 = new Session
                ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                               SenderCompID( "TW" ), TargetCompID( "DLSI" ) ), DataDictionary(),
                  UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  m_pSession4 = new Session
                ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                               SenderCompID( "OREN" ), TargetCompID( "NERO" ) ), DataDictionary(),
                  UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  m_pSession5 = new Session
                ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                               SenderCompID( "OREN" ), TargetCompID( "NERO" ) ), DataDictionary(),
                  UtcTimeOnly(), UtcTimeOnly(), 0, 0 );

  m_pSession1->setResponder( this );
  m_pSession2->setResponder( this );
  m_pSession3->setResponder( this );
  m_pSession4->setResponder( this );
  m_pSession5->setResponder( this );
  return true;
}

void SessionTestCase::lookupSession::onRun( Session& object )
{
  assert( Session::lookupSession
          ( SessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) == m_pSession1 );
  assert( Session::lookupSession
          ( SessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "WT" ), TargetCompID( "ISLD" ) ) ) == m_pSession2 );
  assert( Session::lookupSession
          ( SessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "TW" ), TargetCompID( "DLSI" ) ) ) == m_pSession3 );
  assert( Session::lookupSession
          ( SessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "OREN" ), TargetCompID( "NERO" ) ) ) == m_pSession4 );
}

void SessionTestCase::lookupSession::onTeardown( Session* pObject )
{
  delete m_pSession1; delete m_pSession2; delete m_pSession3;
  delete m_pSession4; delete m_pSession5;
}

bool SessionTestCase::registerSession::onSetup( Session*& pObject )
{
  m_pSession = new Session
               ( *this, m_factory, SessionID( BeginString( "FIX.4.2" ),
                                              SenderCompID( "TW" ), TargetCompID( "ISLD" ) ), DataDictionary(),
                 UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  return true;
}

void SessionTestCase::registerSession::onRun( Session& object )
{
  assert( Session::registerSession( SessionID( BeginString( "FIX.4.1" ),
                                    SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) == 0 );
  assert( Session::registerSession( SessionID( BeginString( "FIX.4.2" ),
                                    SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) == m_pSession );
  assert( Session::isSessionRegistered( SessionID( BeginString( "FIX.4.2" ),
                                        SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) );
  assert( Session::registerSession( SessionID( BeginString( "FIX.4.2" ),
                                    SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) == 0 );
  Session::unregisterSession( SessionID( BeginString( "FIX.4.2" ),
                                         SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) );
  assert( !Session::isSessionRegistered( SessionID( BeginString( "FIX.4.2" ),
                                         SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) );
  assert( Session::registerSession( SessionID( BeginString( "FIX.4.2" ),
                                    SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) == m_pSession );
  delete m_pSession;
  assert( !Session::isSessionRegistered( SessionID( BeginString( "FIX.4.2" ),
                                         SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) );
  assert( Session::registerSession( SessionID( BeginString( "FIX.4.2" ),
                                    SenderCompID( "TW" ), TargetCompID( "ISLD" ) ) ) == 0 );
}

void SessionTestCase::nextTestRequest::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );

  object.next( createTestRequest( "ISLD", "TW", 2, "HELLO" ) );
  assert( m_fromTestRequest == 1 );
  assert( m_toHeartbeat == 1 );

  TestReqID testReqID;
  m_heartbeat.getField( testReqID );
  assert( testReqID == "HELLO" );
}

void SessionTestCase::outOfOrder::onRun( Session& object )
{
  assert( object.getExpectedSenderNum() == 1 );
  assert( object.getExpectedTargetNum() == 1 );

  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( object.getExpectedSenderNum() == 2 );
  assert( object.getExpectedTargetNum() == 2 );

  object.next( createHeartbeat( "ISLD", "TW", 3 ) );
  assert( object.getExpectedSenderNum() == 3 );
  assert( object.getExpectedTargetNum() == 2 );
  assert( m_fromHeartbeat == 0 );

  object.next( createHeartbeat( "ISLD", "TW", 2 ) );
  assert( object.getExpectedSenderNum() == 3 );
  assert( object.getExpectedTargetNum() == 4 );
  assert( m_fromHeartbeat == 2 );
}

void SessionTestCase::logout::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  object.next( createLogout( "ISLD", "TW", 2 ) );
  assert( !object.receivedLogon() );
  assert( m_disconnect == 1 );
  assert( m_toLogout == 1 );
  assert( m_fromLogout == 1 );
}

void SessionTestCase::badOrigSendingTime::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  UtcTimeStamp timeStamp;
  timeStamp.setSecond( 10 );
  SendingTime sendingTime( timeStamp );
  timeStamp.setSecond( 20 );
  OrigSendingTime origSendingTime( timeStamp );

  object.next( createNewOrderSingle( "ISLD", "TW", 2 ) );
  object.next( createNewOrderSingle( "ISLD", "TW", 3 ) );

  NewOrderSingle newOrderSingle = createNewOrderSingle( "ISLD", "TW", 2 );
  newOrderSingle.getHeader().setField( sendingTime );
  newOrderSingle.getHeader().setField( origSendingTime );
  newOrderSingle.getHeader().setField( PossDupFlag( true ) );
  object.next( newOrderSingle );
  assert( m_toReject == 1 );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 0 );

  object.next( createLogout( "ISLD", "TW", 4 ) );
  assert( m_disconnect == 1 );
  assert( m_toLogout == 1 );
}

void SessionTestCase::noOrigSendingTime::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  UtcTimeStamp timeStamp;
  timeStamp.setSecond( 10 );
  SendingTime sendingTime( timeStamp );

  object.next( createNewOrderSingle( "ISLD", "TW", 2 ) );
  object.next( createNewOrderSingle( "ISLD", "TW", 3 ) );

  NewOrderSingle newOrderSingle = createNewOrderSingle( "ISLD", "TW", 2 );
  newOrderSingle.getHeader().setField( sendingTime );
  newOrderSingle.getHeader().setField( PossDupFlag( true ) );
  object.next( newOrderSingle );
  assert( m_toReject == 1 );
  assert( m_toLogout == 0 );
  assert( m_disconnect == 0 );

  object.next( createLogout( "ISLD", "TW", 4 ) );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 1 );
}

void SessionTestCase::badCompID::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  object.next( createNewOrderSingle( "ISLD", "WT", 3 ) );
  assert( m_toReject == 1 );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 0 );

  object.next( createLogout( "ISLD", "TW", 4 ) );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 1 );
}

void SessionTestCase::nextReject::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );
  object.next( createTestRequest( "ISLD", "TW", 2, "HELLO" ) );

  object.next( createReject( "ISLD", "TW", 3, 2 ) );
  assert( m_fromReject == 1 );
  assert( m_toReject == 0 );
  assert( m_toLogout == 0 );
  assert( m_disconnect == 0 );
  assert( object.getExpectedTargetNum() == 4 );

  object.next( createHeartbeat( "ISLD", "TW", 4 ) );
  assert( m_toResendRequest == 0 );
}

class MsgWithBadType : public FIX42::Message
{
public:
MsgWithBadType() : Message( MsgType( "*" ) ) {}}
;

bool SessionTestCase::badMsgType::onSetup( Session*& pObject )
{
  SessionID sessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "TW" ), TargetCompID( "ISLD" ) );

  DataDictionary dataDictionary( "spec/FIX42.xml" );
  pObject = new Session( *this, m_factory, sessionID, dataDictionary,
                         UtcTimeOnly(), UtcTimeOnly(), 0, 0 );
  return true;
}

void SessionTestCase::badMsgType::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  MsgWithBadType msgWithBadType;
  fillHeader( msgWithBadType.getHeader(), "ISLD", "TW", 2 );
  object.next( msgWithBadType );
  assert( m_toReject == 1 );
  assert( m_disconnect == 0 );
  assert( m_toLogout == 0 );

  object.next( createLogout( "ISLD", "TW", 3 ) );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 1 );
}

void SessionTestCase::nextSequenceReset::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );

  // NewSeqNo is greater
  object.next( createSequenceReset( "ISLD", "TW", 0, 3 ) );
  assert( m_fromSequenceReset == 1 );
  assert( object.getExpectedTargetNum() == 3 );
  assert( m_toReject == 0 );

  // NewSeqNo is equal
  object.next( createSequenceReset( "ISLD", "TW", 0, 3 ) );
  assert( m_fromSequenceReset == 2 );
  assert( object.getExpectedTargetNum() == 3 );
  assert( m_toReject == 0 );

  // NewSeqNo is less
  object.next( createSequenceReset( "ISLD", "TW", 0, 2 ) );
  assert( m_fromSequenceReset == 3 );
  assert( object.getExpectedTargetNum() == 3 );
  assert( m_toReject == 1 );
}

void SessionTestCase::nextGapFill::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  // NewSeqNo is equal
  SequenceReset sequenceReset = createSequenceReset( "ISLD", "TW", 2, 20 );
  sequenceReset.set( GapFillFlag( true ) );
  object.next( sequenceReset );
  assert( m_fromSequenceReset == 1 );
  assert( m_toResendRequest == 0 );
  assert( object.getExpectedTargetNum() == 20 );

  // NewSeqNo is greater
  SequenceReset sequenceReset2 = createSequenceReset( "ISLD", "TW", 21, 40 );
  sequenceReset2.set( GapFillFlag( true ) );
  object.next( sequenceReset2 );
  assert( m_fromSequenceReset == 1 );
  assert( m_toResendRequest == 1 );
  assert( object.getExpectedTargetNum() == 20 );

  // NewSeqNo is less, PossDupFlag = Y
  SequenceReset sequenceReset3 = createSequenceReset( "ISLD", "TW", 19, 20 );
  sequenceReset3.set( GapFillFlag( true ) );
  sequenceReset3.getHeader().setField( PossDupFlag( true ) );
  sequenceReset3.getHeader().setField( OrigSendingTime() );
  object.next( sequenceReset3 );
  assert( m_fromSequenceReset == 1 );
  assert( m_toResendRequest == 1 );
  assert( object.getExpectedTargetNum() == 20 );
  assert( m_disconnect == 0 );

  // NewSeqNo is less, PossDupFlag = N
  SequenceReset sequenceReset4 = createSequenceReset( "ISLD", "TW", 19, 20 );
  sequenceReset4.set( GapFillFlag( true ) );
  sequenceReset4.getHeader().setField( PossDupFlag( false ) );
  object.next( sequenceReset4 );
  assert( m_fromSequenceReset == 1 );
  assert( m_toResendRequest == 1 );
  assert( object.getExpectedTargetNum() == 20 );
  assert( m_disconnect == 1 );
}

void SessionTestCase::nextResendRequest::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );
  object.next( createTestRequest( "ISLD", "TW", 2, "HELLO" ) );
  object.next( createTestRequest( "ISLD", "TW", 3, "HELLO" ) );
  object.next( createTestRequest( "ISLD", "TW", 4, "HELLO" ) );

  object.next( createResendRequest( "ISLD", "TW", 5, 1, 4 ) );
  assert( m_toSequenceReset == 1 );

  FIX::Message message = createNewOrderSingle( "ISLD", "TW", 6 );
  assert( object.send( message ) );
  message = createNewOrderSingle( "ISLD", "TW", 7 );
  assert( object.send( message ) );
  message = createNewOrderSingle( "ISLD", "TW", 8 );
  assert( object.send( message ) );

  object.next( createResendRequest( "ISLD", "TW", 6, 5, 7 ) );
  assert( m_toSequenceReset == 1 );
}

void SessionTestCase::badBeginString::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  TestRequest testRequest = createTestRequest( "ISLD", "TW", 2, "HELLO" );
  testRequest.getHeader().setField( BeginString( BeginString_FIX41 ) );
  object.next( testRequest );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 0 );

  Logout logout = createLogout( "ISLD", "TW", 3 );
  logout.getHeader().setField( BeginString( BeginString_FIX41 ) );
  object.next( logout );
  assert( m_toLogout == 1 );
  assert( m_disconnect == 1 );
}

void SessionTestCase::unsupportedMsgType::onRun( Session& object )
{
  object.setResponder( this );
  object.next( createLogon( "ISLD", "TW", 1 ) );

  ExecutionReport message;
  fillHeader( message.getHeader(), "ISLD", "TW", 2 );
  object.next( message );
  assert( m_toBusinessMessageReject == 1 );
}

void SessionTestCase::isSessionTime::onRun( Session& object )
{
  UtcTimeOnly start( 3, 0, 0 );
  UtcTimeOnly end( 18, 0, 0 );

  UtcTimeStamp now( 10, 0, 0, 10, 10, 2000 );
  assert( Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 18, 0, 0, 10, 10, 2000 );
  assert( Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 2, 0, 0, 10, 10, 2000 );
  assert( !Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 19, 0, 0, 10, 10, 2000 );
  assert( !Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 18, 0, 1, 10, 10, 2000 );
  assert( !Session::isSessionTime( start, end, now ) );

  start = UtcTimeOnly( 18, 0, 0 );
  end = UtcTimeOnly( 3, 0, 0 );
  now = UtcTimeStamp( 18, 0, 0, 10, 10, 2000 );
  assert( Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 3, 0, 0, 10, 10, 2000 );
  assert( Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 4, 0, 0, 10, 10, 2000 );
  assert( !Session::isSessionTime( start, end, now ) );

  now = UtcTimeStamp( 17, 0, 0, 10, 10, 2000 );
  assert( !Session::isSessionTime( start, end, now ) );
}

void SessionTestCase::isSameSession::onRun( Session& object )
{
  UtcTimeOnly start( 3, 0, 0 );
  UtcTimeOnly end( 18, 0, 0 );

  // same time
  UtcTimeStamp time1( 10, 0, 0, 10, 10, 2000 );
  UtcTimeStamp time2( 10, 0, 0, 10, 10, 2000 );
  assert( Session::isSameSession( start, end, time1, time2 ) );

  // time 2 in same session but greater
  time1 = UtcTimeStamp( 10, 0, 0, 10, 10, 2000 );
  time2 = UtcTimeStamp( 11, 0, 0, 10, 10, 2000 );
  assert( Session::isSameSession( start, end, time1, time2 ) );

  // time 2 in same session but less
  time1 = UtcTimeStamp( 11, 0, 0, 10, 10, 2000 );
  time2 = UtcTimeStamp( 10, 0, 0, 10, 10, 2000 );
  assert( Session::isSameSession( start, end, time1, time2 ) );

  // time 1 not in session
  time1 = UtcTimeStamp( 19, 0, 0, 10, 10, 2000 );
  time2 = UtcTimeStamp( 10, 0, 0, 10, 10, 2000 );
  assert( !Session::isSameSession( start, end, time1, time2 ) );

  // time 2 not in session
  time1 = UtcTimeStamp( 10, 0, 0, 10, 10, 2000 );
  time2 = UtcTimeStamp( 2, 0, 0, 10, 10, 2000 );
  assert( !Session::isSameSession( start, end, time1, time2 ) );

  start = UtcTimeOnly( 18, 0, 0 );
  end = UtcTimeOnly( 3, 0, 0 );

  // same session same day
  time1 = UtcTimeStamp( 19, 0, 0, 10, 10, 2000 );
  time2 = UtcTimeStamp( 20, 0, 0, 10, 10, 2000 );
  assert( Session::isSameSession( start, end, time1, time2 ) );

  // same session time 2 is in next day
  time1 = UtcTimeStamp( 19, 0, 0, 10, 10, 2000 );
  time2 = UtcTimeStamp( 2, 0, 0, 11, 10, 2000 );
  assert( Session::isSameSession( start, end, time1, time2 ) );

  // same session time 1 is in next day
  time1 = UtcTimeStamp( 2, 0, 0, 11, 10, 2000 );
  time2 = UtcTimeStamp( 19, 0, 0, 10, 10, 2000 );
  assert( Session::isSameSession( start, end, time1, time2 ) );
}

bool SessionTestCase::resetOnEndTime::onSetup( Session*& pObject )
{
  m_startTime.setCurrent();
  m_endTime.setCurrent();
  m_endTime += 2;
  return Test::onSetup( pObject );
}

void SessionTestCase::resetOnEndTime::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );
  object.next( createHeartbeat( "ISLD", "TW", 2 ) );

  assert( m_disconnect == 0 );
  process_sleep( 1 );
  object.next();
  assert( m_disconnect == 0 );
  process_sleep( 2 );
  object.next();
  assert( m_disconnect == 1 );
}

bool SessionTestCase::disconnectBeforeStartTime::onSetup( Session*& pObject )
{
  m_startTime.setCurrent();
  m_startTime += 1;
  m_endTime.setCurrent();
  m_endTime += 4;
  return Test::onSetup( pObject );
}

void SessionTestCase::disconnectBeforeStartTime::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( m_disconnect == 1 );

  process_sleep( 2 );
  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( m_disconnect == 1 );
}

bool SessionTestCase::resetOnNewSession::onSetup( Session*& pObject )
{
  m_startTime.setCurrent();  
  m_endTime = m_startTime;
  m_endTime += 2;
  m_startTime += -2;  
  return Test::onSetup( pObject );
}

void SessionTestCase::resetOnNewSession::onRun( Session& object )
{
  object.next( createLogon( "ISLD", "TW", 1 ) );
  assert( m_disconnect == 0 );

  process_sleep( 3 );
  object.next();
  assert( m_disconnect == 1 );
}

Session* SessionTestCase::resetOnNewSession::createSession()
{
  SessionID sessionID( BeginString( "FIX.4.2" ),
                       SenderCompID( "TW" ), TargetCompID( "ISLD" ) );

  return new Session( *this, m_previousDayFactory, sessionID,
                      DataDictionary(), m_startTime, m_endTime , 0, 0 );
}
}
