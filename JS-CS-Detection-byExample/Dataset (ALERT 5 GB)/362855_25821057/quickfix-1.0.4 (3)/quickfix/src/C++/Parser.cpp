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

#include "Parser.h"
#include <algorithm>

namespace FIX
{
  void throw_isdigit(const char c)
  {
    if( !isdigit(c) ) throw MessageParseError();
  }

  bool Parser::readFixMessage( std::string& str, int size )
    throw( MessageParseError& )
  {
    if( size > 4096 ) size = 4096;
    m_pos = 0;
    m_pStream->read(m_readBuffer, size);
    m_readBuffer[m_pStream->gcount()] = '\0';
    m_buffer += m_readBuffer;

    ignoreGarbage();
    if( !positionBeforeChksum() ) return false;
    if( !positionAfterChksum() ) return false;

    str = advanceBuffer();
    return true;
  }

  void Parser::ignoreGarbage()
  {
    if( m_buffer[0] != '8' && m_buffer[1] != '=' )
      {
        std::string::size_type ver = m_buffer.find("8=");
        if( ver == std::string::npos )
          m_buffer = "";
        else if( ver != 0 )
          m_buffer = m_buffer.substr(ver);
      }
  }

  bool Parser::positionBeforeChksum()
  {
    std::string::size_type begin = m_buffer.find("\0019=");
    if( begin == std::string::npos ) return false;
    begin += 3;

    std::string::size_type end = m_buffer.find("\001",begin);
    if( end == std::string::npos ) return false;
    long offset = end - begin;

    std::string strResult = m_buffer.substr(begin, offset);
    std::for_each(strResult.begin(), strResult.end(), throw_isdigit);
    long length = atol(strResult.c_str());

    return setPosition( end + 1 + length );
  }

  bool Parser::positionAfterChksum()
    throw( MessageParseError& )
  {
    std::string::size_type pos = m_buffer.find("10=", m_pos);
    if( pos != m_pos )
      {
        m_buffer = m_buffer.substr(m_pos);
        throw MessageParseError();
      }

    pos = m_buffer.find("\001", m_pos);
    if( pos == std::string::npos ) return false;
    return setPosition( pos );
  }

  bool Parser::setPosition( std::string::size_type pos )
  {
    if( pos > m_buffer.length() ) return false;
    m_pos = pos;
    return true;
  }

  std::string Parser::advanceBuffer()
  {
    std::string result = m_buffer.substr(0, m_pos + 1);
    m_buffer = m_buffer.substr(m_pos + 1);
    return result;
  }
}
