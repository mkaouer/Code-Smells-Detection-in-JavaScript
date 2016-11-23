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

#include "JavaMessageStore.h"
#include "Conversions.h"
#include <iostream>

JavaMessageStore::JavaMessageStore(JVMObject object) 
: messageStore(object.newGlobalRef())
{
  setId = object.getClass()
    .getMethodID("set", "(Lorg/quickfix/Message;)Z");

  getId = object.getClass()
    .getMethodID("get", "(ILorg/quickfix/Message;)Z");

  getRangeId = object.getClass()
    .getMethodID("get", "(IILjava/util/Collection;)Z");

  getNextSenderMsgSeqNumId = object.getClass()
    .getMethodID("getNextSenderMsgSeqNum", "()I");

  getNextTargetMsgSeqNumId = object.getClass()
    .getMethodID("getNextTargetMsgSeqNum", "()I");

  setNextSenderMsgSeqNumId = object.getClass()
    .getMethodID("setNextSenderMsgSeqNum", "(I)V");

  setNextTargetMsgSeqNumId = object.getClass()
    .getMethodID("setNextTargetMsgSeqNum", "(I)V");

  incrNextSenderMsgSeqNumId = object.getClass()
    .getMethodID("incrNextSenderMsgSeqNum", "()V");

  incrNextTargetMsgSeqNumId = object.getClass()
    .getMethodID("incrNextTargetMsgSeqNum", "()V");

  getCreationTimeId = object.getClass()
    .getMethodID("getCreationTime", "()Ljava/util/Date;");

  resetId = object.getClass()
    .getMethodID("reset", "()V");
}

JavaMessageStore::~JavaMessageStore() { messageStore.deleteGlobalRef(); }

bool JavaMessageStore::set( const FIX::Message& message )
{
  JNIEnv* pEnv = ENV::get();
  return pEnv->CallBooleanMethod(messageStore, setId,
    newMessage(message)) != 0;
}

bool JavaMessageStore::get( int seq, FIX::Message& message ) const
{
  JNIEnv* pEnv = ENV::get();
  return pEnv->CallBooleanMethod(messageStore, getId, seq,
    createJavaMessage()) != 0;
}
  
bool JavaMessageStore::get( int start, int end, 
                            std::vector<FIX::Message>& messages ) const
{
  JNIEnv* pEnv = ENV::get();
  JVMObject collection(createCollection());

  jboolean result = pEnv->CallBooleanMethod(messageStore, getRangeId,
    start, end, collection);
  if(result == false) return false;

  jint size = collection.callIntMethod("size");
  jmethodID methodID = collection.getClass()
    .getMethodID("get", "(I)Ljava/lang/Object;");

  for(jint i = 0; i < size; ++i)
  {
    JVMObject jmessage(pEnv->CallObjectMethod(collection, 
              methodID, i));
    FIX::Message* pMessage = (FIX::Message*)jmessage.getInt("cppPointer");
    messages.push_back(*pMessage);
  }

  return true;
}

int JavaMessageStore::getNextSenderMsgSeqNum() const
{
  JNIEnv* pEnv = ENV::get();
  return pEnv->CallIntMethod(messageStore, getNextSenderMsgSeqNumId);
}

int JavaMessageStore::getNextTargetMsgSeqNum() const
{
  JNIEnv* pEnv = ENV::get();
  return pEnv->CallIntMethod(messageStore, getNextTargetMsgSeqNumId);
}

void JavaMessageStore::setNextSenderMsgSeqNum(int seq)
{
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod(messageStore, setNextSenderMsgSeqNumId, seq);
}

void JavaMessageStore::setNextTargetMsgSeqNum(int seq)
{
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod(messageStore, setNextTargetMsgSeqNumId, seq);
}

void JavaMessageStore::incrNextSenderMsgSeqNum()
{
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod(messageStore, incrNextSenderMsgSeqNumId);
}

void JavaMessageStore::incrNextTargetMsgSeqNum()
{
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod(messageStore, incrNextTargetMsgSeqNumId);
}

FIX::UtcTimeStamp JavaMessageStore::getCreationTime() const
{
  JNIEnv* pEnv = ENV::get();
  JVMObject date(pEnv->CallObjectMethod(messageStore, getCreationTimeId));
  return FIX::UtcTimeStamp(date.callLongMethod("getTime")/1000);
}

void JavaMessageStore::reset()
{
  JNIEnv* pEnv = ENV::get();
  pEnv->CallVoidMethod(messageStore, resetId);
}

jobject JavaMessageStore::messageToJavaMessage( const FIX::Message& message )
{
  JNIEnv* pEnv = ENV::get();
  JVMClass type("Lorg/quickfix/Message;");
  jmethodID method = pEnv->GetMethodID(type, "<init>", "()V");
  jobject result =  pEnv->NewObject(type, method);
  return result;
}

jobject JavaMessageStore::createJavaMessage() const
{
  JNIEnv* pEnv = ENV::get();
  JVMClass type("Lorg/quickfix/Message;");
  jmethodID method = pEnv->GetMethodID(type, "<init>", "()V");
  jobject result =  pEnv->NewObject(type, method);
  return result;
}

jobject JavaMessageStore::createCollection() const
{
  JNIEnv* pEnv = ENV::get();
  JVMClass type("Ljava/util/ArrayList;");
  jmethodID method = pEnv->GetMethodID(type, "<init>", "()V");
  jobject result =  pEnv->NewObject(type, method);
  return result;
}
