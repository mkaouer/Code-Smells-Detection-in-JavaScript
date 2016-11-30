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

#include "JavaApplication.h"
#include "Conversions.h"

JavaApplication::JavaApplication( JVMObject object, JVMObject factory )
    : m_object( object.newGlobalRef() ), m_factory( factory.newGlobalRef() ),
    doNotSendID( "Lorg/quickfix/DoNotSend;" ),
    rejectLogonID( "Lorg/quickfix/RejectLogon;" ),
    unsupportedMessageTypeID( "Lorg/quickfix/UnsupportedMessageType;" ),
    fieldNotFoundID( "Lorg/quickfix/FieldNotFound;" ),
    incorrectTagValueID( "Lorg/quickfix/IncorrectTagValue;" )
{
  onCreateId = object.getClass()
               .getMethodID( "onCreate", "(Lorg/quickfix/SessionID;)V" );

  onLogonId = object.getClass()
              .getMethodID( "onLogon", "(Lorg/quickfix/SessionID;)V" );

  onLogoutId = object.getClass()
               .getMethodID( "onLogout", "(Lorg/quickfix/SessionID;)V" );

  notifyToAdminId = object.getClass()
                    .getMethodID( "toAdmin", "(Lorg/quickfix/Message;Lorg/quickfix/SessionID;)V" );

  notifyToAppId = object.getClass()
                  .getMethodID( "toApp", "(Lorg/quickfix/Message;Lorg/quickfix/SessionID;)V" );

  notifyFromAdminId = object.getClass()
                      .getMethodID( "fromAdmin", "(Lorg/quickfix/Message;Lorg/quickfix/SessionID;)V" );

  notifyFromAppId = object.getClass()
                    .getMethodID( "fromApp", "(Lorg/quickfix/Message;Lorg/quickfix/SessionID;)V" );

  onRunId = object.getClass().getMethodID( "onRun", "()V" );
}

JavaApplication::~JavaApplication() { m_factory.deleteGlobalRef(); m_object.deleteGlobalRef(); }

void JavaApplication::onCreate( const FIX::SessionID& sessionID )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod( m_object, onCreateId,
                        newSessionID( sessionID ) );
};

void JavaApplication::onLogon( const FIX::SessionID& sessionID )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod( m_object, onLogonId,
                        newSessionID( sessionID ) );
};

void JavaApplication::onLogout( const FIX::SessionID& sessionID )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod( m_object, onLogoutId,
                        newSessionID( sessionID ) );
};

void JavaApplication::toAdmin( FIX::Message& msg, const FIX::SessionID& sessionID )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod
  ( m_object, notifyToAdminId,
    newMessage( msg, m_factory ), newSessionID( sessionID ) );
}

void JavaApplication::toApp( FIX::Message& msg, const FIX::SessionID& sessionID )
throw( FIX::DoNotSend& )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  setupExceptions();
  pEnv->CallVoidMethod
  ( m_object, notifyToAppId,
    newMessage( msg, m_factory ), newSessionID( sessionID ) );
  handleException( pEnv );
}

void JavaApplication::fromAdmin( const FIX::Message& msg, const FIX::SessionID& sessionID )
throw( FIX::FieldNotFound&, FIX::RejectLogon& )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  setupExceptions();
  pEnv->CallVoidMethod
  ( m_object, notifyFromAdminId,
    newMessage( msg, m_factory ), newSessionID( sessionID ) );
  handleException( pEnv );
}

void JavaApplication::fromApp( const FIX::Message& msg, const FIX::SessionID& sessionID )
throw( FIX::FieldNotFound&, FIX::UnsupportedMessageType&, FIX::IncorrectTagValue& )
{
  FIX::Locker locker( m_mutex );
  JNIEnv* pEnv = ENV::get();
  setupExceptions();
  pEnv->CallVoidMethod
  ( m_object, notifyFromAppId,
    newMessage( msg, m_factory ), newSessionID( sessionID ) );
  handleException( pEnv );
};

void JavaApplication::onRun()
{
  JNIEnv * pEnv = ENV::get();
  pEnv->CallVoidMethod( m_object, onRunId );
  JVM::get() ->DetachCurrentThread();
};

void JavaApplication::setupExceptions() const
{
  doNotSendID = JVMClass( "Lorg/quickfix/DoNotSend;" );
  rejectLogonID = JVMClass( "Lorg/quickfix/RejectLogon;" );
  unsupportedMessageTypeID = JVMClass( "Lorg/quickfix/UnsupportedMessageType;" );
  fieldNotFoundID = JVMClass( "Lorg/quickfix/FieldNotFound;" );
  incorrectTagValueID = JVMClass( "Lorg/quickfix/IncorrectTagValue;" );
}

void JavaApplication::handleException( JNIEnv* env ) const
{
  jthrowable exception = env->ExceptionOccurred();
  if ( exception )
  {
    if ( doNotSendID.IsInstanceOf( exception ) )
    {
      env->ExceptionClear();
      throw FIX::DoNotSend();
    }
    else if ( rejectLogonID.IsInstanceOf( exception ) )
    {
      env->ExceptionClear();
      throw FIX::RejectLogon();
    }
    else if ( unsupportedMessageTypeID.IsInstanceOf( exception ) )
    {
      env->ExceptionClear();
      throw FIX::UnsupportedMessageType();
    }
    else if ( fieldNotFoundID.IsInstanceOf( exception ) )
    {
      env->ExceptionClear();
      throw FIX::FieldNotFound( 0 );
    }
    else if ( incorrectTagValueID.IsInstanceOf( exception ) )
    {
      env->ExceptionClear();
      throw FIX::IncorrectTagValue( 0 );
    }
    else
    {
      env->ExceptionDescribe();
      env->ExceptionClear();
    }
  }
}
