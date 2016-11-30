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

#ifndef FIX_SOCKETSTREAM_H
#define FIX_SOCKETSTREAM_H

#ifdef _MSC_VER
#pragma warning( disable : 4355 )
#endif

#include <iostream>
#include <string>
#ifdef _MSC_VER
#include <Winsock2.h>
#else
#include <sys/socket.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#endif

#ifdef _MSC_VER
using std::istream;
#endif

namespace FIX
{
  /// Wraps a socket into a stream interface.
  class SocketStream : public std::streambuf, public std::istream
  {
  public:
    SocketStream( int socket ) :
#ifdef _MSC_VER
      istream(this),
#else
      std::istream(this),
#endif
           m_socket(socket)
    {
      setg( m_buffer, m_buffer + 4096, m_buffer + 4096 );
    }

    int getSocket() const { return m_socket; }

  protected:
    virtual int underflow()
    {
      // current pointer is null
      if( !gptr() ) return EOF;
      // grab data from the buffer and return
      if( gptr() < egptr() ) return *gptr();

      // nothing left in buffer, read from socket
      int length = m_buffer + 4096 - eback();
      length = recv(m_socket, eback(), length, 0);

      // no data read, socket closed?
      if( length == 0 ) return EOF;
      // read failed
      else if( length < 0 )
        {
          clear(std::ios::failbit | rdstate());
          return EOF;
        }

      // reset buffer pointers
      setg( eback(), eback(), eback() + length);
      return *gptr();
    }

    char m_buffer[4096];
    int m_socket;
  };
}

#endif //FIX_SOCKETSTREAM_H
