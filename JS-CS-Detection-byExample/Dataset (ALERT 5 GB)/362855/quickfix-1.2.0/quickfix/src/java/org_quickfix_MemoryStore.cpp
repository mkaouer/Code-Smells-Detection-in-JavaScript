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

#include "JVM.h"
#include "org_quickfix_MemoryStore.h"
#include "quickfix/include/MessageStore.h"
#include "Conversions.h"

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_create
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = new FIX::MemoryStore();
  jobject.setInt("cppPointer", (int)pStore);
}

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_destroy
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  delete pStore;
}

JNIEXPORT jboolean JNICALL Java_org_quickfix_MemoryStore_set0
  (JNIEnv *pEnv, jobject obj, jobject message)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  JVMObject jmessage(message);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  FIX::Message* pMessage = (FIX::Message*)jmessage.getInt("cppPointer");
  return pStore->set(*pMessage);
}

JNIEXPORT jboolean JNICALL Java_org_quickfix_MemoryStore_get0__ILMessage_2
  (JNIEnv *pEnv, jobject obj, jint seq, jobject message)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  JVMObject jmessage(message);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  FIX::Message* pMessage = (FIX::Message*)jmessage.getInt("cppPointer");
  return pStore->get(seq, *pMessage);
}

JNIEXPORT jboolean JNICALL Java_org_quickfix_MemoryStore_get0__IILjava_util_Collection_2
  (JNIEnv *pEnv, jobject obj, jint start, jint end, jobject array)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  JVMObject jarray(array);

  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  std::vector<FIX::Message> messages;
  bool result = pStore->get(start, end, messages);
  if(result == false) return false;

  jmethodID methodID = jarray.getClass().getMethodID("add", "(Ljava/lang/Object;)Z");
  std::vector<FIX::Message>::iterator i;
  for(i = messages.begin(); i != messages.end(); ++i)
    pEnv->CallVoidMethod(jarray, methodID, newMessage(*i));
  return true;
}

JNIEXPORT jint JNICALL Java_org_quickfix_MemoryStore_getNextSenderMsgSeqNum0
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  return pStore->getNextSenderMsgSeqNum();
}

JNIEXPORT jint JNICALL Java_org_quickfix_MemoryStore_getNextTargetMsgSeqNum0
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  return pStore->getNextTargetMsgSeqNum();
}

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_setNextSenderMsgSeqNum0
  (JNIEnv *pEnv, jobject obj, jint seq)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  pStore->setNextSenderMsgSeqNum(seq);
}

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_setNextTargetMsgSeqNum0
  (JNIEnv *pEnv, jobject obj, jint seq)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  pStore->setNextTargetMsgSeqNum(seq);
}

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_incrNextSenderMsgSeqNum0
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  pStore->incrNextSenderMsgSeqNum();
}

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_incrNextTargetMsgSeqNum0
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  pStore->incrNextTargetMsgSeqNum();
}

JNIEXPORT jobject JNICALL Java_org_quickfix_MemoryStore_getCreationTime0
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  return newDate(pStore->getCreationTime());
}

JNIEXPORT void JNICALL Java_org_quickfix_MemoryStore_reset0
  (JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  FIX::MessageStore* pStore = (FIX::MessageStore*)jobject.getInt("cppPointer");
  pStore->reset();
}
