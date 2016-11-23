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
using namespace System::Collections;

#include "quickfix_net.h"

#include "Message.h"
#include "SessionID.h"
#include "quickfix/include/MessageStore.h"
#include "vcclr.h"

namespace Fix
{
  public __gc __interface MessageStore
  {
    virtual bool set(Message* message) = 0;
    virtual bool get(int sequence, Message* message) = 0;
    virtual bool get(int begin, int end, ArrayList*) = 0;
    virtual int getNextSenderMsgSeqNum() = 0;
    virtual int getNextTargetMsgSeqNum() = 0;
    virtual void setNextSenderMsgSeqNum(int next) = 0;
    virtual void setNextTargetMsgSeqNum(int next) = 0;
    virtual void incrNextSenderMsgSeqNum() = 0;
    virtual void incrNextTargetMsgSeqNum() = 0;
    virtual DateTime getCreationTime() = 0;
    virtual void reset() = 0;
  };
}

class MessageStore : public FIX::MessageStore
{
public:
  MessageStore( Fix::MessageStore* store ) : m_store(store) {}

  bool set( const FIX::Message& message )
  { return m_store->set(new Fix::Message(message)); }
  bool get( int num, FIX::Message& message ) const 
  { return m_store->get(num, new Fix::Message(message)); }
  bool get( int begin, int end, std::vector<FIX::Message>& messages ) const
  {
    ArrayList* list = new ArrayList();
    bool result = m_store->get(begin, end, list);
    IEnumerator* e = list->GetEnumerator();
    while(e->MoveNext())
    {
      Fix::Message* message = dynamic_cast<Fix::Message*>(e->get_Current());
      messages.push_back(message->unmanaged());
    }
    return result;
  }
  int getNextSenderMsgSeqNum() const 
  { return m_store->getNextSenderMsgSeqNum(); }
  int getNextTargetMsgSeqNum() const
  { return m_store->getNextTargetMsgSeqNum(); }
  void setNextSenderMsgSeqNum(int num)
  { return m_store->setNextSenderMsgSeqNum(num); }
  void setNextTargetMsgSeqNum(int num)
  { return m_store->setNextTargetMsgSeqNum(num); }
  void incrNextSenderMsgSeqNum()
  { m_store->incrNextSenderMsgSeqNum(); }
  void incrNextTargetMsgSeqNum()
  { m_store->incrNextTargetMsgSeqNum(); }
  FIX::UtcTimeStamp getCreationTime() const 
  { DateTime d = m_store->getCreationTime();
    return FIX::UtcTimeStamp(d.get_Hour(), d.get_Minute(), d.get_Second(),
                             d.get_Day(), d.get_Month(), d.get_Year());
  }
  void reset() { m_store->reset(); }

  gcroot<Fix::MessageStore*> m_store;
};