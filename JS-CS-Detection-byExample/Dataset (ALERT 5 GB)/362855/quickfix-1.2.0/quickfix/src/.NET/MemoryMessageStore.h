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

#include "MessageStore.h"
#include "quickfix/include/MessageStore.h"
#include "vcclr.h"

namespace Fix
{
  public __gc class MemoryStore : public MessageStore
  {
  public:
    MemoryStore() { m_pUnmanaged = new FIX::MemoryStore; }
    ~MemoryStore() { m_pUnmanaged->~MemoryStore(); }

    bool set(Message* message)
    { return m_pUnmanaged->set(message->unmanaged()); }
    bool get(int sequence, Message* message)
    { return m_pUnmanaged->get(sequence, message->unmanaged()); }
    bool get(int begin, int end, ArrayList* list)
    {
      std::vector<FIX::Message> messages;
      bool result = m_pUnmanaged->get(begin, end, messages);
      std::vector<FIX::Message>::iterator i;
      for(i = messages.begin(); i != messages.end(); ++i)
        list->Add(new Message(*i));
      return result;
    }
    int getNextSenderMsgSeqNum()
    { return m_pUnmanaged->getNextSenderMsgSeqNum(); }
    int getNextTargetMsgSeqNum()
    { return m_pUnmanaged->getNextTargetMsgSeqNum(); }
    void setNextSenderMsgSeqNum(int next)
    { m_pUnmanaged->setNextSenderMsgSeqNum(next); }
    void setNextTargetMsgSeqNum(int next)
    { m_pUnmanaged->setNextTargetMsgSeqNum(next); }
    void incrNextSenderMsgSeqNum()
    { m_pUnmanaged->incrNextSenderMsgSeqNum(); }
    void incrNextTargetMsgSeqNum()
    { m_pUnmanaged->incrNextTargetMsgSeqNum(); }
    DateTime getCreationTime()
    { FIX::UtcTimeStamp d = m_pUnmanaged->getCreationTime();
      return DateTime(d.getYear(), d.getMonth(), d.getDate(),
                      d.getHour(), d.getMinute(), d.getSecond()); 
    }
    void reset() { m_pUnmanaged->reset(); }

  private:
    FIX::MemoryStore* m_pUnmanaged;
  };

  public __gc class MemoryStoreFactory : public MessageStoreFactory
  {
    MessageStore* create(SessionID*)
    { return new MemoryStore(); }
  };
}