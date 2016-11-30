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

#ifndef FIX_MESSAGECRACKER_H
#define FIX_MESSAGECRACKER_H

#include "fix40/MessageCracker.h"
#include "fix41/MessageCracker.h"
#include "fix42/MessageCracker.h"
#include "fix43/MessageCracker.h"
#include "fix44/MessageCracker.h"
#include "Values.h"

namespace FIX
{
class MessageCracker
      : public FIX40::MessageCracker,
      public FIX41::MessageCracker,
      public FIX42::MessageCracker,
      public FIX43::MessageCracker,
      public FIX44::MessageCracker
{
public:
  void crack( const Message& message,
              const SessionID& sessionID )
  {
    FIX::BeginString beginString;
    message.getHeader().getField( beginString );
    if ( beginString == BeginString_FIX40 )
    {
      ( ( FIX40::MessageCracker& ) ( *this ) )
      .crack( ( FIX40::Message& ) message, sessionID );
    }
    else if ( beginString == BeginString_FIX41 )
    {
      ( ( FIX41::MessageCracker& ) ( *this ) )
      .crack( ( FIX41::Message& ) message, sessionID );
    }
    else if ( beginString == BeginString_FIX42 )
    {
      ( ( FIX42::MessageCracker& ) ( *this ) )
      .crack( ( FIX42::Message& ) message, sessionID );
    }
    else if ( beginString == BeginString_FIX43 )
    {
      ( ( FIX43::MessageCracker& ) ( *this ) )
      .crack( ( FIX43::Message& ) message, sessionID );
    }
    else if ( beginString == BeginString_FIX44 )
    {
      ( ( FIX44::MessageCracker& ) ( *this ) )
      .crack( ( FIX44::Message& ) message, sessionID );
    }
  }
};
}

#endif //FIX_MESSAGECRACKER_H
