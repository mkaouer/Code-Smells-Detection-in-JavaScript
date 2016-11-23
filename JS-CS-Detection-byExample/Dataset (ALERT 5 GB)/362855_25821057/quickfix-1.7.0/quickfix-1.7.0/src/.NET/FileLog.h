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

#pragma once

using namespace System;

#include "quickfix_net.h"

#include "Log.h"
#include "LogFactory.h"
#include "SessionSettings.h"
#include "quickfix/FileLog.h"
#include "quickfix/CallStack.h"
#include "vcclr.h"

namespace QuickFix
{
public __gc class FileLog : public Log
{
public:
  FileLog( String* path, SessionID* sessionID )
  { QF_STACK_TRY

    char* upath = createUnmanagedString( path );
    m_pUnmanaged = new FIX::FileLog
                   ( upath, sessionID->unmanaged() );
    destroyUnmanagedString( upath );

    QF_STACK_CATCH
  }
  ~FileLog() { delete m_pUnmanaged; }

  void onIncoming( String* s )
  { QF_STACK_TRY

    char* us = createUnmanagedString( s );
    m_pUnmanaged->onIncoming( us ); 
    destroyUnmanagedString( us );

    QF_STACK_CATCH
  }

  void onOutgoing( String* s )
  { QF_STACK_TRY

    char* us = createUnmanagedString( s );
    m_pUnmanaged->onOutgoing( us ); 
    destroyUnmanagedString( us );

    QF_STACK_CATCH
  }

  void onEvent( String* s )
  { QF_STACK_TRY

    char* us = createUnmanagedString( s );
    m_pUnmanaged->onEvent( us ); 
    destroyUnmanagedString( us );

    QF_STACK_CATCH
  }

private:
  FIX::FileLog* m_pUnmanaged;
};

public __gc class FileLogFactory : public LogFactory
{
public:
  FileLogFactory( String* path ) : m_path( path ) {}

  Log* create( SessionID* sessionID )
  { QF_STACK_TRY

    if ( m_path ) return new FileLog( m_path, sessionID );

    FIX::SessionSettings& s = m_settings->unmanaged();
    FIX::Dictionary settings = s.get();
    try
    {
      m_path = settings.getString( FIX::FILE_LOG_PATH ).c_str();
      return new FileLog( m_path, sessionID );
    }
    catch ( FIX::ConfigError & e ) { throw new ConfigError( e.what() ); }

    QF_STACK_CATCH
  }

private:
  String* m_path;
  SessionSettings* m_settings;
};
}
