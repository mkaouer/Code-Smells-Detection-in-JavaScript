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
#include "CallStack.h"

#include "ThreadedSocketInitiator.h"
#include "Session.h"
#include "Settings.h"

namespace FIX
{
ThreadedSocketInitiator::ThreadedSocketInitiator(
  Application& application,
  MessageStoreFactory& factory,
  const SessionSettings& settings ) throw( ConfigError& )
: Initiator( application, factory, settings ),
  m_reconnectInterval( 30 ), m_stop( false )
{ socket_init(); }

ThreadedSocketInitiator::ThreadedSocketInitiator(
  Application& application,
  MessageStoreFactory& factory,
  const SessionSettings& settings,
  LogFactory& logFactory ) throw( ConfigError& )
: Initiator( application, factory, settings, logFactory ),
  m_reconnectInterval( 30 ), m_stop( false )
{ socket_init(); }

ThreadedSocketInitiator::~ThreadedSocketInitiator()
{ socket_term(); }

void ThreadedSocketInitiator::onConfigure( const SessionSettings& s )
throw ( ConfigError& )
{ QF_STACK_PUSH(ThreadedSocketInitiator::onConfigure)

  try { m_reconnectInterval = s.get().getLong( "ReconnectInterval" ); }
  catch ( std::exception& ) {}

  QF_STACK_POP
}

void ThreadedSocketInitiator::onInitialize( const SessionSettings& s ) 
throw ( RuntimeError& )
{ QF_STACK_PUSH(ThreadedSocketInitiator::onInitialize)
  QF_STACK_POP
}

void ThreadedSocketInitiator::onStart()
{ QF_STACK_PUSH(ThreadedSocketInitiator::onStart)

  connect();
  while ( !m_stop )
    process_sleep( 1 );

  QF_STACK_POP
}

bool ThreadedSocketInitiator::onPoll()
{ QF_STACK_PUSH(ThreadedSocketInitiator::onPoll)

  return false;

  QF_STACK_POP
}

void ThreadedSocketInitiator::onStop()
{ QF_STACK_PUSH(ThreadedSocketInitiator::onStop)

  m_stop = true;

  Locker l(m_mutex);

  SocketToThread::iterator i;
  for ( i = m_threads.begin(); i != m_threads.end(); ++i )
    socket_close( i->first );
  for ( i = m_threads.begin(); i != m_threads.end(); ++i )
    thread_join( i->second );

  QF_STACK_POP
}

bool ThreadedSocketInitiator::doConnect( const SessionID& s, const Dictionary& d )
{ QF_STACK_PUSH(ThreadedSocketInitiator::doConnect)

  try
  {
    std::string address;

    ThreadStruct* threadStruct = new ThreadStruct( this, s, d );

    if ( !thread_spawn( &socketThread, threadStruct ) )
      delete threadStruct;
    return true;
  }
  catch ( std::exception& ) { return false; }

  QF_STACK_POP
}

void ThreadedSocketInitiator::addThread( int s, int t )
{ QF_STACK_PUSH(ThreadedSocketInitiator::addThread)

  Locker l(m_mutex);
#ifdef _MSC_VER
  HANDLE handle;
  DuplicateHandle(
    GetCurrentProcess(),
    GetCurrentThread(),
    GetCurrentProcess(),
    &handle, 0, FALSE, DUPLICATE_SAME_ACCESS );
  t = ( int ) handle;
#endif
  m_threads[ s ] = t;

  QF_STACK_POP
}

void ThreadedSocketInitiator::removeThread( int s )
{ QF_STACK_PUSH(ThreadedSocketInitiator::removeThread)

  Locker l(m_mutex);
  SocketToThread::iterator i = m_threads.find( s );
  if ( i != m_threads.end() )
  {
#ifdef _MSC_VER
    CloseHandle( ( HANDLE ) i->second );
#endif
    thread_detach( i->second );
    m_threads.erase( i );
  }

  QF_STACK_POP
}

THREAD_PROC ThreadedSocketInitiator::socketThread( void* p )
{ QF_STACK_TRY
  QF_STACK_PUSH(ThreadedSocketInitiator::socketThread)

  ThreadStruct * threadStruct = reinterpret_cast < ThreadStruct* > ( p );

  ThreadedSocketInitiator* pInitiator = threadStruct->pInitiator;
  SessionID sessionID = threadStruct->sessionID;
  Dictionary dictionary = threadStruct->dictionary;
  delete threadStruct;
  Log* log = Session::lookupSession( sessionID )->getLog();

  while ( !pInitiator->m_stop )
  {
    std::string address;
    short port = 0;
    pInitiator->getHost( sessionID, dictionary, address, port );

    log->onEvent( "Connecting to " + address + " on port " + IntConvertor::convert((unsigned short)port) );
    int socket = socket_createConnector( address.c_str(), port );    
    if ( socket < 0 )
    {
      log->onEvent( "Connection failed" );
      process_sleep( pInitiator->m_reconnectInterval );
      continue;
    }
    log->onEvent( "Connection succeeded" );

    pInitiator->addThread( socket, thread_self() );

    ThreadedSocketConnection* pConnection =
      new ThreadedSocketConnection( sessionID, socket, pInitiator->getApplication() );

    while ( pConnection->read() && !pInitiator->m_stop ) {}
    if(!pInitiator->m_stop)
      pInitiator->removeThread( pConnection->getSocket() );
    delete pConnection;
    if(!pInitiator->m_stop)
      process_sleep( pInitiator->m_reconnectInterval );
  }
  return 0;

  QF_STACK_POP
  QF_STACK_CATCH
}

void ThreadedSocketInitiator::getHost( const SessionID& s, const Dictionary& d,
                                       std::string& address, short& port )
{ QF_STACK_PUSH(ThreadedSocketInitiator::getHost)

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

  QF_STACK_POP
}

ThreadedSocketInitiator::ThreadStruct::ThreadStruct
( ThreadedSocketInitiator* i, const SessionID& s, const Dictionary& d )
: pInitiator( i ), sessionID( s ), dictionary( d ) {}

}
