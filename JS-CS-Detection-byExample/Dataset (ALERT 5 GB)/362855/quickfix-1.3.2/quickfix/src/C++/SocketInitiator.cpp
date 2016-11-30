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
#include "stdafx.h"
#else
#include "config.h"
#endif

#include "SocketInitiator.h"
#include "Settings.h"

namespace FIX
{
SocketInitiator::SocketInitiator( Application& application,
                                  MessageStoreFactory& factory,
                                  const SessionSettings& settings )
throw( ConfigError& )
    : Initiator( application, factory, settings ),
    m_connector( 1 ), m_elapsedTimeouts( 0 ),
m_reconnectInterval( 30 ), m_stop( false ) {}

SocketInitiator::SocketInitiator( Application& application,
                                  MessageStoreFactory& factory,
                                  const SessionSettings& settings,
                                  LogFactory& logFactory )
throw( ConfigError& )
    : Initiator( application, factory, settings, logFactory ),
    m_connector( 1 ), m_elapsedTimeouts( 0 ),
m_reconnectInterval( 30 ), m_stop( false ) {}

SocketInitiator::SocketInitiator( Application& application,
                                  MessageStoreFactory& factory,
                                  const SessionSettings& settings,
                                  bool& threw, ConfigError& ex )
    : Initiator( application, factory, settings, threw, ex ),
    m_connector( 1 ), m_elapsedTimeouts( 0 ),
m_reconnectInterval( 30 ), m_stop( false ) {}

SocketInitiator::SocketInitiator( Application& application,
                                  MessageStoreFactory& factory,
                                  const SessionSettings& settings,
                                  LogFactory& logFactory,
                                  bool& threw, ConfigError& ex )
    : Initiator( application, factory, settings, logFactory, threw, ex ),
    m_connector( 1 ), m_elapsedTimeouts( 0 ),
m_reconnectInterval( 30 ), m_stop( false ) {}

SocketInitiator::~SocketInitiator() {}

bool SocketInitiator::onStart( const SessionSettings& s )
{
  try { m_reconnectInterval = s.get().getLong( RECONNECT_INTERVAL ); }
  catch ( std::exception& ) {}

  connect();
  while ( !m_stop )
    m_connector.block( *this );
  return true;
}

void SocketInitiator::onStop()
{
  m_stop = true;
}

bool SocketInitiator::doConnect( const SessionID& s, const Dictionary& d )
{
  try
  {
    std::string address;
    short port = 0;

    getHost( s, d, address, port );

    int result = m_connector.connect( address, port );
    if ( !result ) return false;

    m_connections[ result ] = new SocketConnection
                              ( *this, s, result, &m_connector.getMonitor() );
    return true;
  }
catch ( std::exception& ) { return false; }
}

void SocketInitiator::onConnect( SocketConnector&, int s )
{}

void SocketInitiator::onData( SocketConnector& connector, int s )
{
  SocketConnections::iterator i = m_connections.find( s );
  if ( i == m_connections.end() ) return ;
  SocketConnection* pSocketConnection = i->second;
  while ( pSocketConnection->read( connector ) ) {}}

void SocketInitiator::onDisconnect( SocketConnector&, int s )
{
  SocketConnections::iterator i = m_connections.find( s );
  if ( i == m_connections.end() ) return ;
  SocketConnection* pSocketConnection = i->second;
  setConnected( pSocketConnection->getSession() ->getSessionID(), false );

  Session* pSession = pSocketConnection->getSession();
  if ( pSession )
  {
    pSession->disconnect();
    setConnected( pSession->getSessionID(), false );
  }

  delete pSocketConnection;
  m_connections.erase( s );
}

void SocketInitiator::onError( SocketConnector& ) {}

void SocketInitiator::onTimeout( SocketConnector& )
{
  if ( ++m_elapsedTimeouts >= m_reconnectInterval )
  {
    connect();
    m_elapsedTimeouts = 0;
  }

  SocketConnections::iterator i;
  for ( i = m_connections.begin(); i != m_connections.end(); ++i )
    i->second->onTimeout();
}

void SocketInitiator::getHost( const SessionID& s, const Dictionary& d,
                               std::string& address, short& port )
{
  int num = 0;
  SessionToHostNum::iterator i = m_sessionToHostNum.find( s );
  if ( i != m_sessionToHostNum.end() ) num = i->second;

  try
  {
    std::stringstream hostStream;
    hostStream << SOCKET_CONNECT_HOST << num;
    address = d.getString( hostStream.str() );

    std::stringstream portStream;
    portStream << SOCKET_CONNECT_PORT << num;
    port = ( short ) d.getLong( portStream.str() );
  }
  catch ( ConfigError& )
  {
    num = 0;
    address = d.getString( SOCKET_CONNECT_HOST );
    port = ( short ) d.getLong( SOCKET_CONNECT_PORT );
  }
  m_sessionToHostNum[ s ] = ++num;
}

}
