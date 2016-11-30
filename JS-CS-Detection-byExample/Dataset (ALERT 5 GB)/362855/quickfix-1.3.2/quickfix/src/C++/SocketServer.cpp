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

#include "SocketServer.h"
#include "Utility.h"
#ifndef _MSC_VER
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#endif
#include <exception>

namespace FIX
{
class ServerWrapper : public SocketMonitor::Strategy
{
public:
  ServerWrapper( int socket, SocketServer& server,
                 SocketServer::Strategy& strategy )
: m_socket( socket ), m_server( server ), m_strategy( strategy ) {}

private:
  void onEvent( SocketMonitor&, int socket )
  {
    if ( socket == m_socket )
    {
      m_strategy.onConnect( m_server, m_server.accept() );
    }
    else
      if ( socket_disconnected( socket ) )
      {
        m_server.getMonitor().drop( socket );
      }
      else
        m_strategy.onData( m_server, socket );
  }

  void onError( SocketMonitor&, int socket )
  {
    m_strategy.onDisconnect( m_server, socket );
    m_server.getMonitor().drop( socket );
  }
  void onError( SocketMonitor& )
  { m_strategy.onError( m_server ); }
  void onTimeout( SocketMonitor& )
  {
    m_strategy.onTimeout( m_server );
  };

  int m_socket;
  SocketServer& m_server;
  SocketServer::Strategy& m_strategy;
};

SocketServer::SocketServer( int port, int timeout )
    : m_port( port ), m_monitor( timeout )
{
  m_socket = socket( PF_INET, SOCK_STREAM, 0 );
  if ( m_socket < 0 ) throw std::exception();

  m_address.sin_family = PF_INET;
  m_address.sin_port = htons( port );
  m_address.sin_addr.s_addr = INADDR_ANY;
  m_socklen = sizeof( m_address );

  socket_setsockopt( m_socket );
  if ( !bind() ) throw std::exception();
  if ( !listen() ) throw std::exception();
  m_monitor.add( m_socket );
}

bool SocketServer::bind()
{
  int result = ::bind( m_socket,
                       reinterpret_cast < sockaddr* > ( &m_address ),
                       m_socklen );
  return result >= 0;
}

bool SocketServer::listen()
{
  int result = ::listen( m_socket,
                         SOMAXCONN );
  return result >= 0;
}

int SocketServer::accept()
{
  int result = ::accept( m_socket, 0, 0 );
  if ( result >= 0 ) m_monitor.add( result );
  return result;
}

void SocketServer::close()
{
  socket_close( m_socket );
  socket_invalidate( m_socket );
}

bool SocketServer::block( Strategy& strategy )
{
  if ( socket_isValid( m_socket ) )
  {
    ServerWrapper wrapper( m_socket, *this, strategy );
    m_monitor.block( wrapper );
    return true;
  }
  return false;
}
}
