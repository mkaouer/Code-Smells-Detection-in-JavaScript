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

#include "Parser.h"
#include "Utility.h"
#include "FieldConvertors.h"
#include <algorithm>

namespace FIX
{
void Parser::allocate( int length )
{ QF_STACK_PUSH(Parser::allocate)

  char* newBuffer = new char[length+1];
  if( m_readBuffer && m_bufferSize )
  {
    strcpy(newBuffer, m_readBuffer);
    delete [] m_readBuffer;
  }
  m_readBuffer = newBuffer;
  m_bufferSize = length;
  m_buffer.reserve( length + 1 );

  QF_STACK_POP
}

bool Parser::extractLength( int& length, std::string::size_type& pos,
                            const std::string& buffer )
throw( MessageParseError& )
{ QF_STACK_PUSH(Parser::extractLength)

  if( !buffer.size() ) return false;
  
  std::string::size_type startPos = buffer.find( "\0019=", 0 );
  if( startPos == std::string::npos ) return false;
  startPos += 3;
  std::string::size_type endPos = buffer.find( "\001", startPos );
  if( endPos == std::string::npos ) return false;

  std::string strLength( buffer, startPos, endPos - startPos );

  try
  {
    length = IntConvertor::convert( strLength );
    if( length < 0 ) throw MessageParseError();
  }
  catch( FieldConvertError& )
  { throw MessageParseError(); }

  pos = endPos + 1;
  return true;

  QF_STACK_POP
}

bool Parser::readFixMessage( std::string& str )
throw( MessageParseError&, RecvFailed& )
{ QF_STACK_PUSH(Parser::readFixMessage)

  readFromStream();

  std::string::size_type pos = 0;

  if( m_buffer.length() < 2 ) return false;
  pos = m_buffer.find( "8=" );
  if( pos == std::string::npos ) return false;
  m_buffer.erase( 0, pos );

  int length = 0;

  try
  {
    if( extractLength(length, pos, m_buffer) )
    {
      pos += length;
      if( m_buffer.size() < pos )
        return false;

      pos = m_buffer.find( "\00110=", pos-1 );
      if( pos == std::string::npos ) return false;
      pos += 4;
      pos = m_buffer.find( "\001", pos );
      if( pos == std::string::npos ) return false;
      pos += 1;

      str = m_buffer.substr( 0, pos );
      m_buffer.erase( 0, pos );
      return true;
    }
  }
  catch( MessageParseError& e )
  {
    m_buffer.erase( 0, pos + length );
    throw e;
  }

  readFromStream();
  return false;

  QF_STACK_POP
}

bool Parser::readFromStream() throw( RecvFailed& )
{ QF_STACK_PUSH(Parser::readFromStream)

  int size = 0;
  if ( m_pStream )
  {
    m_pStream->read( m_readBuffer, m_bufferSize );
    size = m_pStream->gcount();
    if ( size == 0 ) return false;
  }
  else if ( m_socket )
  {
    int bytes = 0;
    if ( !socket_fionread( m_socket, bytes ) )
      return false;
    if ( bytes == 0 )
      return false;

    size = recv( m_socket, m_readBuffer, m_bufferSize, 0 );
    if ( size <= 0 ) throw RecvFailed();
    if( size == m_bufferSize ) allocate( m_bufferSize * 2 );
  }
  else return true;

  m_readBuffer[ size ] = '\0';
  m_buffer += m_readBuffer;

  return true;

  QF_STACK_POP
}
}
