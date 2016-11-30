/* ====================================================================
* The QuickFIX Software License, Version 1.0
*
* Copyright (c) 2002 ThoughtWorks, Inc.  All rights
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

#ifdef _MSC_VER
#pragma warning( disable : 4876 )
#endif

#include "JVM.h"
#include "Conversions.h"
#include "quickfix_ThreadedSocketInitiator.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "JavaLogFactory.h"
#include <quickfix/ThreadedSocketInitiator.h>
#include <quickfix/Settings.h>
#include <quickfix/Utility.h>
#include <quickfix/CallStack.h>
#include <sstream>

FIX::ThreadedSocketInitiator* getCPPThreadedSocketInitiator( jobject obj )
{
  JVMObject jobject( obj );
  return ( FIX::ThreadedSocketInitiator* ) jobject.getInt( "cppPointer" );
}

FIX::SessionSettings& getThreadedInitiatorSettings( JVMObject& obj )
{ QF_STACK_TRY

  JVMObject jsettings = obj.getObject( "settings", "Lquickfix/SessionSettings;" );
  FIX::SessionSettings* pSettings
  = ( FIX::SessionSettings* ) jsettings.getInt( "cppPointer" );
  return *pSettings;

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ThreadedSocketInitiator_create
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject jobject( obj );

  JavaLogFactory* pLogFactory = 0;
  try
  { pLogFactory = &createLogFactory( jobject ); }
  catch ( JVMException& ) {}

  FIX::Initiator* pInitiator = 0;

  try
  {
    if ( pLogFactory )
    {
      pInitiator = new FIX::ThreadedSocketInitiator(
                    createApplication( jobject ),
                    createFactory( jobject ),
                    getSettings( jobject ),
                    *pLogFactory );
    }
    else
    {
      pInitiator = new FIX::ThreadedSocketInitiator(
                    createApplication( jobject ),
                    createFactory( jobject ),
                    getSettings( jobject ));
    }
  }
  catch( FIX::ConfigError& e )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
    return;
  }

  jobject.setInt( "cppPointer", ( int ) pInitiator );

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ThreadedSocketInitiator_destroy
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  try
  {
    FIX::ThreadedSocketInitiator * p = getCPPThreadedSocketInitiator( obj );
    if ( p == 0 ) return ;

    delete &p ->getApplication();
    delete &p ->getMessageStoreFactory();
    delete p;
  }
  catch( ... ) {}

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ThreadedSocketInitiator_doStart
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  try
  {
    getCPPThreadedSocketInitiator( obj ) ->start();
  }
  catch( FIX::ConfigError &e )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
  }
  catch( FIX::RuntimeError &e )
  {
    throwNew( "Lquickfix/RuntimeError;", e.what() );
  }

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ThreadedSocketInitiator_doBlock
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  try
  {
    getCPPThreadedSocketInitiator( obj ) ->block();
  }
  catch( FIX::ConfigError &e )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
  }
  catch( FIX::RuntimeError &e )
  {
    throwNew( "Lquickfix/RuntimeError;", e.what() );
  }

  QF_STACK_CATCH
}

JNIEXPORT jboolean JNICALL Java_quickfix_ThreadedSocketInitiator_doPoll
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  try
  {
    return getCPPThreadedSocketInitiator( obj ) ->poll();
  }
  catch( FIX::ConfigError &e )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
  }
  catch( FIX::RuntimeError &e )
  {
    throwNew( "Lquickfix/RuntimeError;", e.what() );
  }
  return false;

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ThreadedSocketInitiator_doStop
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  getCPPThreadedSocketInitiator( obj ) ->stop();

  QF_STACK_CATCH
}
