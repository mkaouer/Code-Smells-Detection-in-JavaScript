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

#ifdef HAVE_MYSQL

#include "JVM.h"
#include "quickfix_MySQLStoreFactory.h"
#include "MySQLStore.h"
#include "SessionSettings.h"
#include "CallStack.h"
#include "Conversions.h"

JNIEXPORT void JNICALL Java_quickfix_MySQLStoreFactory_create__
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject object( obj );

  JVMObject jsettings( object.getObject( "settings", "Lquickfix/SessionSettings;" ) );
  FIX::SessionSettings* pSettings
  = ( FIX::SessionSettings* ) jsettings.getInt( "cppPointer" );

  FIX::MessageStoreFactory* pFactory = new FIX::MySQLStoreFactory( *pSettings );
  object.setInt( "cppPointer", ( int ) pFactory );

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_MySQLStoreFactory_destroy
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject jobject( obj );
  FIX::MySQLStoreFactory* pFactory
  = ( FIX::MySQLStoreFactory* ) jobject.getInt( "cppPointer" );
  delete pFactory;

  QF_STACK_CATCH
}

JNIEXPORT jobject JNICALL Java_quickfix_MySQLStoreFactory_create__Lquickfix_SessionID_2
( JNIEnv *pEnv, jobject obj, jobject sessionID )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject jobj( obj );
  JVMObject jsession( sessionID );

  FIX::MySQLStoreFactory* pFactory
  = ( FIX::MySQLStoreFactory* ) jobj.getInt( "cppPointer" );
  FIX::SessionID* pSessionID
  = ( FIX::SessionID* ) jsession.getInt( "cppPointer" );

  FIX::MessageStoreFactoryExceptionWrapper wrapper( pFactory );
  bool threw = false;
  FIX::ConfigError e;
  FIX::MessageStore* pStore = wrapper.create( *pSessionID, threw, e );
  if ( threw )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
    return 0;
  }

  FIX::MessageStoreExceptionWrapper* pWrapper =
    new FIX::MessageStoreExceptionWrapper( pStore );

  JVMClass type( "Lquickfix/FileStore;" );
  jmethodID method = pEnv->GetMethodID( type, "<init>", "(I)V" );
  jobject result = pEnv->NewObject( type, method, ( jint ) pWrapper );
  return result;

  QF_STACK_CATCH
}

#endif //HAVE_MYSQL
