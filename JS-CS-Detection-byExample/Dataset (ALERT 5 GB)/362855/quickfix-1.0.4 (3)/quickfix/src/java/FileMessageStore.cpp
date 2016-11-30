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
#include "FileMessageStore.h"
#include "quickfix/include/MessageStore.h"

JNIEXPORT void JNICALL Java_FileMessageStore_create
  (JNIEnv *e, jobject obj)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = new FIX::MemoryStore();
  jobject.setInt("cppPointer", (int)pStore);
}

JNIEXPORT void JNICALL Java_FileMessageStore_destroy
  (JNIEnv *e, jobject obj)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  delete pStore;
}

JNIEXPORT jboolean JNICALL Java_FileMessageStore_set0
  (JNIEnv *e, jobject obj, jobject jmessage)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  return false;
}

JNIEXPORT jboolean JNICALL Java_FileMessageStore_get0__ILMessage_2
  (JNIEnv *e, jobject obj, jint, jobject)
{
  return false;
}

JNIEXPORT jboolean JNICALL Java_FileMessageStore_get0__IILjava_util_Collection_2
  (JNIEnv *e, jobject obj, jint, jint, jobject)
{
  return false;
}

JNIEXPORT jint JNICALL Java_FileMessageStore_getNextSenderMsgSeqNum0
  (JNIEnv *e, jobject obj)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  return pStore->getNextSenderMsgSeqNum();
}

JNIEXPORT jint JNICALL Java_FileMessageStore_getNextTargetMsgSeqNum0
  (JNIEnv *e, jobject obj)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  return pStore->getNextTargetMsgSeqNum();
}

JNIEXPORT void JNICALL Java_FileMessageStore_setNextSenderMsgSeqNum0
  (JNIEnv *e, jobject obj, jint seq)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  pStore->setNextSenderMsgSeqNum(seq);
}

JNIEXPORT void JNICALL Java_FileMessageStore_setNextTargetMsgSeqNum0
  (JNIEnv *e, jobject obj, jint seq)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  pStore->setNextTargetMsgSeqNum(seq);
}

JNIEXPORT void JNICALL Java_FileMessageStore_incrNextSenderMsgSeqNum0
  (JNIEnv *e, jobject obj)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  pStore->incrNextSenderMsgSeqNum();
}

JNIEXPORT void JNICALL Java_FileMessageStore_incrNextTargetMsgSeqNum0
  (JNIEnv *e, jobject obj)
{
  ENV env(e);
  JVMObject jobject(obj, env);
  FIX::MemoryStore* pStore = (FIX::MemoryStore*)jobject.getInt("cppPointer");
  pStore->incrNextTargetMsgSeqNum();
}

JNIEXPORT void JNICALL Java_FileMessageStore_reset0
  (JNIEnv *, jobject obj)
{
}
