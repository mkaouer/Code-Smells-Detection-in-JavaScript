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

#include "SocketAcceptor.h"
#include "Settings.h"
#include "Utility.h"

namespace FIX
{
SocketAcceptor::SocketAcceptor( Application& application,
                                MessageStoreFactory& factory,
                                const SessionSettings& settings )
throw( ConfigError& )
    : Acceptor( application, factory, settings ),
m_port( 0 ), m_pServer( 0 ), m_stop( false ) {}

SocketAcceptor::SocketAcceptor( Application& application,
                                MessageStoreFactory& factory,
                                const SessionSettings& settings,
                                LogFactory& logFactory )
throw( ConfigError& )
    : Acceptor( application, factory, settings, logFactory ),
m_port( 0 ), m_pServer( 0 ), m_stop( false ) {}

SocketAcceptor::SocketAcceptor( Application& application,
                                MessageStoreFactory& factory,
                                const SessionSettings& settings,
                                bool& threw, ConfigError& ex )
    : Acceptor( application, factory, settings, threw, ex ),
m_port( 0 ), m_pServer( 0 ), m_stop( false ) {}

SocketAcceptor::SocketAcceptor( Application& application,
                                MessageStoreFactory& factory,
                                const SessionSettings& settings,
                                LogFactory& logFactory,
                                bool& threw, ConfigError& ex )
    : Acceptor( application, factory, settings, logFactory, threw, ex ),
m_port( 0 ), m_pServer( 0 ), m_stop( false ) {}

SocketAcceptor::~SocketAcceptor()
{
  SocketConnections::iterator iter;
  for ( iter = m_connections.begin(); iter != m_connections.end(); ++iter )
    delete iter->second;
}

bool SocketAcceptor::onStart( const SessionSettings& s )
{
  m_port = ( short ) s.get().getLong( SOCKET_ACCEPT_PORT );

  SocketServer server( m_port, 1 );
  m_pServer = &server;

  while ( !m_stop && server.block( *this ) ) {}
  return true;
}

void SocketAcceptor::onStop()
{
  m_stop = true;
  if ( m_pServer ) m_pServer->close();
}

void SocketAcceptor::onConnect( SocketServer& server, int s )
{
  if ( !socket_isValid( s ) ) return ;
  SocketConnections::iterator i = m_connections.find( s );
  if ( i != m_connections.end() ) return ;
  m_connections[ s ] = new SocketConnection( s, &server.getMonitor() );
}

void SocketAcceptor::onData( SocketServer& server, int s )
{
  SocketConnections::iterator i = m_connections.find( s );
  if ( i == m_connections.end() ) return ;
  SocketConnection* pSocketConnection = i->second;
  while ( pSocketConnection->read( *this, server ) ) {}}

void SocketAcceptor::onDisconnect( SocketServer&, int s )
{
  SocketConnections::iterator i = m_connections.find( s );
  if ( i == m_connections.end() ) return ;
  SocketConnection* pSocketConnection = i->second;

  Session* pSession = pSocketConnection->getSession();
  if ( pSession ) pSession->disconnect();

  delete pSocketConnection;
  m_connections.erase( s );
}

void SocketAcceptor::onError( SocketServer& ) {}

void SocketAcceptor::onTimeout( SocketServer& )
{
  SocketConnections::iterator i;
  for ( i = m_connections.begin(); i != m_connections.end(); ++i )
    i->second->onTimeout();
}
}
