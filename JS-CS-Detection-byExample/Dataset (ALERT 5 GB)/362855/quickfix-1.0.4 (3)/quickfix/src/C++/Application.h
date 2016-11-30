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

#ifndef FIX_APPLICATION_H
#define FIX_APPLICATION_H

#include "Message.h"
#include "SessionID.h"

namespace FIX
{
  /*! \addtogroup user
   *  @{
   */
  /**
   * This interface must be implemented to define what your %FIX application
   * does.
   *
   * The onRun function is called in a spawned thread which must
   * contain your event based loop.  If your application has no need for
   * monitoring events, it should just sleep periodically.  As soon as onRun
   * is complete, the application will be shut down.
   *
   * The other functions notify your application about events that happen on
   * active %FIX sessions.  The various MessageCracker classes can be used
   * to parse the generic message structure into specific %FIX messages.
   */
  class Application
  {
  public:
    virtual ~Application() {};
    /// Notification of a session begin created
    virtual void onCreate( const SessionID& ) = 0;
    /// Notification of a session successfully logging on
    virtual void onLogon( const SessionID& ) = 0;
    /// Notification of a session logging off or disconnecting
    virtual void onLogout( const SessionID& ) = 0;
    /// Notification of admin message being sent to target
    virtual void toAdmin( const Message&, const SessionID& ) = 0;
    /// Notification of app message being sent to target
    virtual void toApp( const Message&, const SessionID& )
      throw(DoNotSend&) = 0;
    /// Notification of admin message being received from target
    virtual void fromAdmin( const Message&, const SessionID& )
      throw(FieldNotFound&, RejectLogon&) = 0;
    /// Notification of app message being received from target
    virtual void fromApp( const Message&, const SessionID& )
      throw(FieldNotFound&, UnsupportedMessageType&, IncorrectTagValue&) = 0;
    /// Implmentation of application loop
    virtual void onRun() = 0;
  };

  class NullApplication : public Application
  {
    void onCreate( const SessionID& ) {}
    void onLogon( const SessionID& ) {}
    void onLogout( const SessionID& ) {}
    void toAdmin( const Message&, const SessionID& ) {}
    void toApp( const Message&, const SessionID& )
      throw(DoNotSend&) {}
    void fromAdmin( const Message&, const SessionID& )
      throw(FieldNotFound&, RejectLogon&) {}
    void fromApp( const Message&, const SessionID& )
      throw(FieldNotFound&, UnsupportedMessageType&, IncorrectTagValue&) {}
    void onRun() = 0;
  };
  /*! @} */
}

#endif //FIX_APPLICATION_H
