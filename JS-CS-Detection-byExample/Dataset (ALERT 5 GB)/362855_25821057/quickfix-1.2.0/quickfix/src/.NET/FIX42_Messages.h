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

#include "Message.h"
#include "Fields.h"

namespace Fix42
{
  public __gc class Header : public Fix::Message::Header
  {
  public:
    Header(Fix::Message* message) : Fix::Message::Header(message) {}
    
    void set(Fix::BeginString* value)
    { setField(value); }
    Fix::BeginString* get(Fix::BeginString* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BeginString* getBeginString() throw(Fix::FieldNotFound*)
    { Fix::BeginString* value = new Fix::BeginString();
      getField(value); return value; }
 
    void set(Fix::BodyLength* value)
    { setField(value); }
    Fix::BodyLength* get(Fix::BodyLength* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BodyLength* getBodyLength() throw(Fix::FieldNotFound*)
    { Fix::BodyLength* value = new Fix::BodyLength();
      getField(value); return value; }
 
    void set(Fix::MsgType* value)
    { setField(value); }
    Fix::MsgType* get(Fix::MsgType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MsgType* getMsgType() throw(Fix::FieldNotFound*)
    { Fix::MsgType* value = new Fix::MsgType();
      getField(value); return value; }
 
    void set(Fix::SenderCompID* value)
    { setField(value); }
    Fix::SenderCompID* get(Fix::SenderCompID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SenderCompID* getSenderCompID() throw(Fix::FieldNotFound*)
    { Fix::SenderCompID* value = new Fix::SenderCompID();
      getField(value); return value; }
 
    void set(Fix::TargetCompID* value)
    { setField(value); }
    Fix::TargetCompID* get(Fix::TargetCompID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TargetCompID* getTargetCompID() throw(Fix::FieldNotFound*)
    { Fix::TargetCompID* value = new Fix::TargetCompID();
      getField(value); return value; }
 
    void set(Fix::OnBehalfOfCompID* value)
    { setField(value); }
    Fix::OnBehalfOfCompID* get(Fix::OnBehalfOfCompID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OnBehalfOfCompID* getOnBehalfOfCompID() throw(Fix::FieldNotFound*)
    { Fix::OnBehalfOfCompID* value = new Fix::OnBehalfOfCompID();
      getField(value); return value; }
 
    void set(Fix::DeliverToCompID* value)
    { setField(value); }
    Fix::DeliverToCompID* get(Fix::DeliverToCompID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DeliverToCompID* getDeliverToCompID() throw(Fix::FieldNotFound*)
    { Fix::DeliverToCompID* value = new Fix::DeliverToCompID();
      getField(value); return value; }
 
    void set(Fix::SecureDataLen* value)
    { setField(value); }
    Fix::SecureDataLen* get(Fix::SecureDataLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecureDataLen* getSecureDataLen() throw(Fix::FieldNotFound*)
    { Fix::SecureDataLen* value = new Fix::SecureDataLen();
      getField(value); return value; }
 
    void set(Fix::SecureData* value)
    { setField(value); }
    Fix::SecureData* get(Fix::SecureData* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecureData* getSecureData() throw(Fix::FieldNotFound*)
    { Fix::SecureData* value = new Fix::SecureData();
      getField(value); return value; }
 
    void set(Fix::MsgSeqNum* value)
    { setField(value); }
    Fix::MsgSeqNum* get(Fix::MsgSeqNum* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MsgSeqNum* getMsgSeqNum() throw(Fix::FieldNotFound*)
    { Fix::MsgSeqNum* value = new Fix::MsgSeqNum();
      getField(value); return value; }
 
    void set(Fix::SenderSubID* value)
    { setField(value); }
    Fix::SenderSubID* get(Fix::SenderSubID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SenderSubID* getSenderSubID() throw(Fix::FieldNotFound*)
    { Fix::SenderSubID* value = new Fix::SenderSubID();
      getField(value); return value; }
 
    void set(Fix::SenderLocationID* value)
    { setField(value); }
    Fix::SenderLocationID* get(Fix::SenderLocationID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SenderLocationID* getSenderLocationID() throw(Fix::FieldNotFound*)
    { Fix::SenderLocationID* value = new Fix::SenderLocationID();
      getField(value); return value; }
 
    void set(Fix::TargetSubID* value)
    { setField(value); }
    Fix::TargetSubID* get(Fix::TargetSubID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TargetSubID* getTargetSubID() throw(Fix::FieldNotFound*)
    { Fix::TargetSubID* value = new Fix::TargetSubID();
      getField(value); return value; }
 
    void set(Fix::TargetLocationID* value)
    { setField(value); }
    Fix::TargetLocationID* get(Fix::TargetLocationID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TargetLocationID* getTargetLocationID() throw(Fix::FieldNotFound*)
    { Fix::TargetLocationID* value = new Fix::TargetLocationID();
      getField(value); return value; }
 
    void set(Fix::OnBehalfOfSubID* value)
    { setField(value); }
    Fix::OnBehalfOfSubID* get(Fix::OnBehalfOfSubID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OnBehalfOfSubID* getOnBehalfOfSubID() throw(Fix::FieldNotFound*)
    { Fix::OnBehalfOfSubID* value = new Fix::OnBehalfOfSubID();
      getField(value); return value; }
 
    void set(Fix::OnBehalfOfLocationID* value)
    { setField(value); }
    Fix::OnBehalfOfLocationID* get(Fix::OnBehalfOfLocationID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OnBehalfOfLocationID* getOnBehalfOfLocationID() throw(Fix::FieldNotFound*)
    { Fix::OnBehalfOfLocationID* value = new Fix::OnBehalfOfLocationID();
      getField(value); return value; }
 
    void set(Fix::DeliverToSubID* value)
    { setField(value); }
    Fix::DeliverToSubID* get(Fix::DeliverToSubID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DeliverToSubID* getDeliverToSubID() throw(Fix::FieldNotFound*)
    { Fix::DeliverToSubID* value = new Fix::DeliverToSubID();
      getField(value); return value; }
 
    void set(Fix::DeliverToLocationID* value)
    { setField(value); }
    Fix::DeliverToLocationID* get(Fix::DeliverToLocationID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DeliverToLocationID* getDeliverToLocationID() throw(Fix::FieldNotFound*)
    { Fix::DeliverToLocationID* value = new Fix::DeliverToLocationID();
      getField(value); return value; }
 
    void set(Fix::PossDupFlag* value)
    { setField(value); }
    Fix::PossDupFlag* get(Fix::PossDupFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PossDupFlag* getPossDupFlag() throw(Fix::FieldNotFound*)
    { Fix::PossDupFlag* value = new Fix::PossDupFlag();
      getField(value); return value; }
 
    void set(Fix::PossResend* value)
    { setField(value); }
    Fix::PossResend* get(Fix::PossResend* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PossResend* getPossResend() throw(Fix::FieldNotFound*)
    { Fix::PossResend* value = new Fix::PossResend();
      getField(value); return value; }
 
    void set(Fix::SendingTime* value)
    { setField(value); }
    Fix::SendingTime* get(Fix::SendingTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SendingTime* getSendingTime() throw(Fix::FieldNotFound*)
    { Fix::SendingTime* value = new Fix::SendingTime();
      getField(value); return value; }
 
    void set(Fix::OrigSendingTime* value)
    { setField(value); }
    Fix::OrigSendingTime* get(Fix::OrigSendingTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigSendingTime* getOrigSendingTime() throw(Fix::FieldNotFound*)
    { Fix::OrigSendingTime* value = new Fix::OrigSendingTime();
      getField(value); return value; }
 
    void set(Fix::XmlDataLen* value)
    { setField(value); }
    Fix::XmlDataLen* get(Fix::XmlDataLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::XmlDataLen* getXmlDataLen() throw(Fix::FieldNotFound*)
    { Fix::XmlDataLen* value = new Fix::XmlDataLen();
      getField(value); return value; }
 
    void set(Fix::XmlData* value)
    { setField(value); }
    Fix::XmlData* get(Fix::XmlData* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::XmlData* getXmlData() throw(Fix::FieldNotFound*)
    { Fix::XmlData* value = new Fix::XmlData();
      getField(value); return value; }
 
    void set(Fix::MessageEncoding* value)
    { setField(value); }
    Fix::MessageEncoding* get(Fix::MessageEncoding* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MessageEncoding* getMessageEncoding() throw(Fix::FieldNotFound*)
    { Fix::MessageEncoding* value = new Fix::MessageEncoding();
      getField(value); return value; }
 
    void set(Fix::LastMsgSeqNumProcessed* value)
    { setField(value); }
    Fix::LastMsgSeqNumProcessed* get(Fix::LastMsgSeqNumProcessed* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastMsgSeqNumProcessed* getLastMsgSeqNumProcessed() throw(Fix::FieldNotFound*)
    { Fix::LastMsgSeqNumProcessed* value = new Fix::LastMsgSeqNumProcessed();
      getField(value); return value; }
 
    void set(Fix::OnBehalfOfSendingTime* value)
    { setField(value); }
    Fix::OnBehalfOfSendingTime* get(Fix::OnBehalfOfSendingTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OnBehalfOfSendingTime* getOnBehalfOfSendingTime() throw(Fix::FieldNotFound*)
    { Fix::OnBehalfOfSendingTime* value = new Fix::OnBehalfOfSendingTime();
      getField(value); return value; }
 
  };
  
  public __gc class Trailer : public Fix::Message::Trailer
  {
  public:
    Trailer(Fix::Message* message) : Fix::Message::Trailer(message) {}
    
    void set(Fix::SignatureLength* value)
    { setField(value); }
    Fix::SignatureLength* get(Fix::SignatureLength* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SignatureLength* getSignatureLength() throw(Fix::FieldNotFound*)
    { Fix::SignatureLength* value = new Fix::SignatureLength();
      getField(value); return value; }
 
    void set(Fix::Signature* value)
    { setField(value); }
    Fix::Signature* get(Fix::Signature* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Signature* getSignature() throw(Fix::FieldNotFound*)
    { Fix::Signature* value = new Fix::Signature();
      getField(value); return value; }
 
    void set(Fix::CheckSum* value)
    { setField(value); }
    Fix::CheckSum* get(Fix::CheckSum* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CheckSum* getCheckSum() throw(Fix::FieldNotFound*)
    { Fix::CheckSum* value = new Fix::CheckSum();
      getField(value); return value; }
 
  };
  
  public __gc class Message : public Fix::Message
  {
  public: 
    Message() : Fix::Message(new Fix::BeginString("FIX.4.2"))
    {
      m_header = new Header(this);
      m_trailer = new Trailer(this);
    }

    Message( Fix::MsgType* msgType ) 
    : Fix::Message(
      new Fix::BeginString("FIX.4.2"),msgType ) 
      { 
        m_header = new Header(this); 
        m_trailer = new Trailer(this);
      }

    Header* getHeader() { return dynamic_cast<Header*>(Fix::Message::getHeader()); }
  };

  public __gc class Heartbeat : public Message
  {
  public: 
    Heartbeat() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("0"); }
  
    void set(Fix::TestReqID* value)
    { setField(value); }
    Fix::TestReqID* get(Fix::TestReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TestReqID* getTestReqID() throw(Fix::FieldNotFound*)
    { Fix::TestReqID* value = new Fix::TestReqID();
      getField(value); return value; }
 
  };

  public __gc class Logon : public Message
  {
  public: 
    Logon() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("A"); }
    //
    Logon(
      Fix::EncryptMethod* aEncryptMethod,
      Fix::HeartBtInt* aHeartBtInt )
    : Message(MsgType())
    
    {
      set(aEncryptMethod);
      set(aHeartBtInt);
    }
  
    void set(Fix::EncryptMethod* value)
    { setField(value); }
    Fix::EncryptMethod* get(Fix::EncryptMethod* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncryptMethod* getEncryptMethod() throw(Fix::FieldNotFound*)
    { Fix::EncryptMethod* value = new Fix::EncryptMethod();
      getField(value); return value; }
 
    void set(Fix::HeartBtInt* value)
    { setField(value); }
    Fix::HeartBtInt* get(Fix::HeartBtInt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::HeartBtInt* getHeartBtInt() throw(Fix::FieldNotFound*)
    { Fix::HeartBtInt* value = new Fix::HeartBtInt();
      getField(value); return value; }
 
    void set(Fix::RawDataLength* value)
    { setField(value); }
    Fix::RawDataLength* get(Fix::RawDataLength* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RawDataLength* getRawDataLength() throw(Fix::FieldNotFound*)
    { Fix::RawDataLength* value = new Fix::RawDataLength();
      getField(value); return value; }
 
    void set(Fix::RawData* value)
    { setField(value); }
    Fix::RawData* get(Fix::RawData* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RawData* getRawData() throw(Fix::FieldNotFound*)
    { Fix::RawData* value = new Fix::RawData();
      getField(value); return value; }
 
    void set(Fix::ResetSeqNumFlag* value)
    { setField(value); }
    Fix::ResetSeqNumFlag* get(Fix::ResetSeqNumFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ResetSeqNumFlag* getResetSeqNumFlag() throw(Fix::FieldNotFound*)
    { Fix::ResetSeqNumFlag* value = new Fix::ResetSeqNumFlag();
      getField(value); return value; }
 
    void set(Fix::MaxMessageSize* value)
    { setField(value); }
    Fix::MaxMessageSize* get(Fix::MaxMessageSize* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxMessageSize* getMaxMessageSize() throw(Fix::FieldNotFound*)
    { Fix::MaxMessageSize* value = new Fix::MaxMessageSize();
      getField(value); return value; }
 
    void set(Fix::NoMsgTypes* value)
    { setField(value); }
    Fix::NoMsgTypes* get(Fix::NoMsgTypes* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoMsgTypes* getNoMsgTypes() throw(Fix::FieldNotFound*)
    { Fix::NoMsgTypes* value = new Fix::NoMsgTypes();
      getField(value); return value; }
 
  };

  public __gc class TestRequest : public Message
  {
  public: 
    TestRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("1"); }
    //
    TestRequest(
      Fix::TestReqID* aTestReqID )
    : Message(MsgType())
    
    {
      set(aTestReqID);
    }
  
    void set(Fix::TestReqID* value)
    { setField(value); }
    Fix::TestReqID* get(Fix::TestReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TestReqID* getTestReqID() throw(Fix::FieldNotFound*)
    { Fix::TestReqID* value = new Fix::TestReqID();
      getField(value); return value; }
 
  };

  public __gc class ResendRequest : public Message
  {
  public: 
    ResendRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("2"); }
    //
    ResendRequest(
      Fix::BeginSeqNo* aBeginSeqNo,
      Fix::EndSeqNo* aEndSeqNo )
    : Message(MsgType())
    
    {
      set(aBeginSeqNo);
      set(aEndSeqNo);
    }
  
    void set(Fix::BeginSeqNo* value)
    { setField(value); }
    Fix::BeginSeqNo* get(Fix::BeginSeqNo* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BeginSeqNo* getBeginSeqNo() throw(Fix::FieldNotFound*)
    { Fix::BeginSeqNo* value = new Fix::BeginSeqNo();
      getField(value); return value; }
 
    void set(Fix::EndSeqNo* value)
    { setField(value); }
    Fix::EndSeqNo* get(Fix::EndSeqNo* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EndSeqNo* getEndSeqNo() throw(Fix::FieldNotFound*)
    { Fix::EndSeqNo* value = new Fix::EndSeqNo();
      getField(value); return value; }
 
  };

  public __gc class Reject : public Message
  {
  public: 
    Reject() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("3"); }
    //
    Reject(
      Fix::RefSeqNum* aRefSeqNum )
    : Message(MsgType())
    
    {
      set(aRefSeqNum);
    }
  
    void set(Fix::RefSeqNum* value)
    { setField(value); }
    Fix::RefSeqNum* get(Fix::RefSeqNum* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RefSeqNum* getRefSeqNum() throw(Fix::FieldNotFound*)
    { Fix::RefSeqNum* value = new Fix::RefSeqNum();
      getField(value); return value; }
 
    void set(Fix::RefTagID* value)
    { setField(value); }
    Fix::RefTagID* get(Fix::RefTagID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RefTagID* getRefTagID() throw(Fix::FieldNotFound*)
    { Fix::RefTagID* value = new Fix::RefTagID();
      getField(value); return value; }
 
    void set(Fix::RefMsgType* value)
    { setField(value); }
    Fix::RefMsgType* get(Fix::RefMsgType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RefMsgType* getRefMsgType() throw(Fix::FieldNotFound*)
    { Fix::RefMsgType* value = new Fix::RefMsgType();
      getField(value); return value; }
 
    void set(Fix::SessionRejectReason* value)
    { setField(value); }
    Fix::SessionRejectReason* get(Fix::SessionRejectReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SessionRejectReason* getSessionRejectReason() throw(Fix::FieldNotFound*)
    { Fix::SessionRejectReason* value = new Fix::SessionRejectReason();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class SequenceReset : public Message
  {
  public: 
    SequenceReset() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("4"); }
    //
    SequenceReset(
      Fix::NewSeqNo* aNewSeqNo )
    : Message(MsgType())
    
    {
      set(aNewSeqNo);
    }
  
    void set(Fix::GapFillFlag* value)
    { setField(value); }
    Fix::GapFillFlag* get(Fix::GapFillFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::GapFillFlag* getGapFillFlag() throw(Fix::FieldNotFound*)
    { Fix::GapFillFlag* value = new Fix::GapFillFlag();
      getField(value); return value; }
 
    void set(Fix::NewSeqNo* value)
    { setField(value); }
    Fix::NewSeqNo* get(Fix::NewSeqNo* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NewSeqNo* getNewSeqNo() throw(Fix::FieldNotFound*)
    { Fix::NewSeqNo* value = new Fix::NewSeqNo();
      getField(value); return value; }
 
  };

  public __gc class Logout : public Message
  {
  public: 
    Logout() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("5"); }
  
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class Advertisement : public Message
  {
  public: 
    Advertisement() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("7"); }
    //
    Advertisement(
      Fix::AdvId* aAdvId,
      Fix::AdvTransType* aAdvTransType,
      Fix::Symbol* aSymbol,
      Fix::AdvSide* aAdvSide,
      Fix::Shares* aShares )
    : Message(MsgType())
    
    {
      set(aAdvId);
      set(aAdvTransType);
      set(aSymbol);
      set(aAdvSide);
      set(aShares);
    }
  
    void set(Fix::AdvId* value)
    { setField(value); }
    Fix::AdvId* get(Fix::AdvId* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AdvId* getAdvId() throw(Fix::FieldNotFound*)
    { Fix::AdvId* value = new Fix::AdvId();
      getField(value); return value; }
 
    void set(Fix::AdvTransType* value)
    { setField(value); }
    Fix::AdvTransType* get(Fix::AdvTransType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AdvTransType* getAdvTransType() throw(Fix::FieldNotFound*)
    { Fix::AdvTransType* value = new Fix::AdvTransType();
      getField(value); return value; }
 
    void set(Fix::AdvRefID* value)
    { setField(value); }
    Fix::AdvRefID* get(Fix::AdvRefID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AdvRefID* getAdvRefID() throw(Fix::FieldNotFound*)
    { Fix::AdvRefID* value = new Fix::AdvRefID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::AdvSide* value)
    { setField(value); }
    Fix::AdvSide* get(Fix::AdvSide* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AdvSide* getAdvSide() throw(Fix::FieldNotFound*)
    { Fix::AdvSide* value = new Fix::AdvSide();
      getField(value); return value; }
 
    void set(Fix::Shares* value)
    { setField(value); }
    Fix::Shares* get(Fix::Shares* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Shares* getShares() throw(Fix::FieldNotFound*)
    { Fix::Shares* value = new Fix::Shares();
      getField(value); return value; }
 
    void set(Fix::Price* value)
    { setField(value); }
    Fix::Price* get(Fix::Price* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Price* getPrice() throw(Fix::FieldNotFound*)
    { Fix::Price* value = new Fix::Price();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::TradeDate* value)
    { setField(value); }
    Fix::TradeDate* get(Fix::TradeDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradeDate* getTradeDate() throw(Fix::FieldNotFound*)
    { Fix::TradeDate* value = new Fix::TradeDate();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::URLLink* value)
    { setField(value); }
    Fix::URLLink* get(Fix::URLLink* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::URLLink* getURLLink() throw(Fix::FieldNotFound*)
    { Fix::URLLink* value = new Fix::URLLink();
      getField(value); return value; }
 
    void set(Fix::LastMkt* value)
    { setField(value); }
    Fix::LastMkt* get(Fix::LastMkt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastMkt* getLastMkt() throw(Fix::FieldNotFound*)
    { Fix::LastMkt* value = new Fix::LastMkt();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
  };

  public __gc class IndicationofInterest : public Message
  {
  public: 
    IndicationofInterest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("6"); }
    //
    IndicationofInterest(
      Fix::IOIid* aIOIid,
      Fix::IOITransType* aIOITransType,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide,
      Fix::IOIShares* aIOIShares )
    : Message(MsgType())
    
    {
      set(aIOIid);
      set(aIOITransType);
      set(aSymbol);
      set(aSide);
      set(aIOIShares);
    }
  
    void set(Fix::IOIid* value)
    { setField(value); }
    Fix::IOIid* get(Fix::IOIid* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOIid* getIOIid() throw(Fix::FieldNotFound*)
    { Fix::IOIid* value = new Fix::IOIid();
      getField(value); return value; }
 
    void set(Fix::IOITransType* value)
    { setField(value); }
    Fix::IOITransType* get(Fix::IOITransType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOITransType* getIOITransType() throw(Fix::FieldNotFound*)
    { Fix::IOITransType* value = new Fix::IOITransType();
      getField(value); return value; }
 
    void set(Fix::IOIRefID* value)
    { setField(value); }
    Fix::IOIRefID* get(Fix::IOIRefID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOIRefID* getIOIRefID() throw(Fix::FieldNotFound*)
    { Fix::IOIRefID* value = new Fix::IOIRefID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::IOIShares* value)
    { setField(value); }
    Fix::IOIShares* get(Fix::IOIShares* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOIShares* getIOIShares() throw(Fix::FieldNotFound*)
    { Fix::IOIShares* value = new Fix::IOIShares();
      getField(value); return value; }
 
    void set(Fix::Price* value)
    { setField(value); }
    Fix::Price* get(Fix::Price* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Price* getPrice() throw(Fix::FieldNotFound*)
    { Fix::Price* value = new Fix::Price();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::ValidUntilTime* value)
    { setField(value); }
    Fix::ValidUntilTime* get(Fix::ValidUntilTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ValidUntilTime* getValidUntilTime() throw(Fix::FieldNotFound*)
    { Fix::ValidUntilTime* value = new Fix::ValidUntilTime();
      getField(value); return value; }
 
    void set(Fix::IOIQltyInd* value)
    { setField(value); }
    Fix::IOIQltyInd* get(Fix::IOIQltyInd* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOIQltyInd* getIOIQltyInd() throw(Fix::FieldNotFound*)
    { Fix::IOIQltyInd* value = new Fix::IOIQltyInd();
      getField(value); return value; }
 
    void set(Fix::IOINaturalFlag* value)
    { setField(value); }
    Fix::IOINaturalFlag* get(Fix::IOINaturalFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOINaturalFlag* getIOINaturalFlag() throw(Fix::FieldNotFound*)
    { Fix::IOINaturalFlag* value = new Fix::IOINaturalFlag();
      getField(value); return value; }
 
    void set(Fix::NoIOIQualifiers* value)
    { setField(value); }
    Fix::NoIOIQualifiers* get(Fix::NoIOIQualifiers* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoIOIQualifiers* getNoIOIQualifiers() throw(Fix::FieldNotFound*)
    { Fix::NoIOIQualifiers* value = new Fix::NoIOIQualifiers();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::URLLink* value)
    { setField(value); }
    Fix::URLLink* get(Fix::URLLink* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::URLLink* getURLLink() throw(Fix::FieldNotFound*)
    { Fix::URLLink* value = new Fix::URLLink();
      getField(value); return value; }
 
    void set(Fix::NoRoutingIDs* value)
    { setField(value); }
    Fix::NoRoutingIDs* get(Fix::NoRoutingIDs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRoutingIDs* getNoRoutingIDs() throw(Fix::FieldNotFound*)
    { Fix::NoRoutingIDs* value = new Fix::NoRoutingIDs();
      getField(value); return value; }
 
    void set(Fix::SpreadToBenchmark* value)
    { setField(value); }
    Fix::SpreadToBenchmark* get(Fix::SpreadToBenchmark* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SpreadToBenchmark* getSpreadToBenchmark() throw(Fix::FieldNotFound*)
    { Fix::SpreadToBenchmark* value = new Fix::SpreadToBenchmark();
      getField(value); return value; }
 
    void set(Fix::Benchmark* value)
    { setField(value); }
    Fix::Benchmark* get(Fix::Benchmark* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Benchmark* getBenchmark() throw(Fix::FieldNotFound*)
    { Fix::Benchmark* value = new Fix::Benchmark();
      getField(value); return value; }
 
  };

  public __gc class News : public Message
  {
  public: 
    News() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("B"); }
    //
    News(
      Fix::Headline* aHeadline,
      Fix::LinesOfText* aLinesOfText )
    : Message(MsgType())
    
    {
      set(aHeadline);
      set(aLinesOfText);
    }
  
    void set(Fix::OrigTime* value)
    { setField(value); }
    Fix::OrigTime* get(Fix::OrigTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigTime* getOrigTime() throw(Fix::FieldNotFound*)
    { Fix::OrigTime* value = new Fix::OrigTime();
      getField(value); return value; }
 
    void set(Fix::Urgency* value)
    { setField(value); }
    Fix::Urgency* get(Fix::Urgency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Urgency* getUrgency() throw(Fix::FieldNotFound*)
    { Fix::Urgency* value = new Fix::Urgency();
      getField(value); return value; }
 
    void set(Fix::Headline* value)
    { setField(value); }
    Fix::Headline* get(Fix::Headline* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Headline* getHeadline() throw(Fix::FieldNotFound*)
    { Fix::Headline* value = new Fix::Headline();
      getField(value); return value; }
 
    void set(Fix::EncodedHeadlineLen* value)
    { setField(value); }
    Fix::EncodedHeadlineLen* get(Fix::EncodedHeadlineLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedHeadlineLen* getEncodedHeadlineLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedHeadlineLen* value = new Fix::EncodedHeadlineLen();
      getField(value); return value; }
 
    void set(Fix::EncodedHeadline* value)
    { setField(value); }
    Fix::EncodedHeadline* get(Fix::EncodedHeadline* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedHeadline* getEncodedHeadline() throw(Fix::FieldNotFound*)
    { Fix::EncodedHeadline* value = new Fix::EncodedHeadline();
      getField(value); return value; }
 
    void set(Fix::NoRoutingIDs* value)
    { setField(value); }
    Fix::NoRoutingIDs* get(Fix::NoRoutingIDs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRoutingIDs* getNoRoutingIDs() throw(Fix::FieldNotFound*)
    { Fix::NoRoutingIDs* value = new Fix::NoRoutingIDs();
      getField(value); return value; }
 
    void set(Fix::NoRelatedSym* value)
    { setField(value); }
    Fix::NoRelatedSym* get(Fix::NoRelatedSym* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRelatedSym* getNoRelatedSym() throw(Fix::FieldNotFound*)
    { Fix::NoRelatedSym* value = new Fix::NoRelatedSym();
      getField(value); return value; }
 
    void set(Fix::LinesOfText* value)
    { setField(value); }
    Fix::LinesOfText* get(Fix::LinesOfText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LinesOfText* getLinesOfText() throw(Fix::FieldNotFound*)
    { Fix::LinesOfText* value = new Fix::LinesOfText();
      getField(value); return value; }
 
    void set(Fix::URLLink* value)
    { setField(value); }
    Fix::URLLink* get(Fix::URLLink* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::URLLink* getURLLink() throw(Fix::FieldNotFound*)
    { Fix::URLLink* value = new Fix::URLLink();
      getField(value); return value; }
 
    void set(Fix::RawDataLength* value)
    { setField(value); }
    Fix::RawDataLength* get(Fix::RawDataLength* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RawDataLength* getRawDataLength() throw(Fix::FieldNotFound*)
    { Fix::RawDataLength* value = new Fix::RawDataLength();
      getField(value); return value; }
 
    void set(Fix::RawData* value)
    { setField(value); }
    Fix::RawData* get(Fix::RawData* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RawData* getRawData() throw(Fix::FieldNotFound*)
    { Fix::RawData* value = new Fix::RawData();
      getField(value); return value; }
 
  };

  public __gc class Email : public Message
  {
  public: 
    Email() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("C"); }
    //
    Email(
      Fix::EmailThreadID* aEmailThreadID,
      Fix::EmailType* aEmailType,
      Fix::Subject* aSubject,
      Fix::LinesOfText* aLinesOfText )
    : Message(MsgType())
    
    {
      set(aEmailThreadID);
      set(aEmailType);
      set(aSubject);
      set(aLinesOfText);
    }
  
    void set(Fix::EmailThreadID* value)
    { setField(value); }
    Fix::EmailThreadID* get(Fix::EmailThreadID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EmailThreadID* getEmailThreadID() throw(Fix::FieldNotFound*)
    { Fix::EmailThreadID* value = new Fix::EmailThreadID();
      getField(value); return value; }
 
    void set(Fix::EmailType* value)
    { setField(value); }
    Fix::EmailType* get(Fix::EmailType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EmailType* getEmailType() throw(Fix::FieldNotFound*)
    { Fix::EmailType* value = new Fix::EmailType();
      getField(value); return value; }
 
    void set(Fix::OrigTime* value)
    { setField(value); }
    Fix::OrigTime* get(Fix::OrigTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigTime* getOrigTime() throw(Fix::FieldNotFound*)
    { Fix::OrigTime* value = new Fix::OrigTime();
      getField(value); return value; }
 
    void set(Fix::Subject* value)
    { setField(value); }
    Fix::Subject* get(Fix::Subject* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Subject* getSubject() throw(Fix::FieldNotFound*)
    { Fix::Subject* value = new Fix::Subject();
      getField(value); return value; }
 
    void set(Fix::EncodedSubjectLen* value)
    { setField(value); }
    Fix::EncodedSubjectLen* get(Fix::EncodedSubjectLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSubjectLen* getEncodedSubjectLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSubjectLen* value = new Fix::EncodedSubjectLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSubject* value)
    { setField(value); }
    Fix::EncodedSubject* get(Fix::EncodedSubject* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSubject* getEncodedSubject() throw(Fix::FieldNotFound*)
    { Fix::EncodedSubject* value = new Fix::EncodedSubject();
      getField(value); return value; }
 
    void set(Fix::NoRoutingIDs* value)
    { setField(value); }
    Fix::NoRoutingIDs* get(Fix::NoRoutingIDs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRoutingIDs* getNoRoutingIDs() throw(Fix::FieldNotFound*)
    { Fix::NoRoutingIDs* value = new Fix::NoRoutingIDs();
      getField(value); return value; }
 
    void set(Fix::NoRelatedSym* value)
    { setField(value); }
    Fix::NoRelatedSym* get(Fix::NoRelatedSym* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRelatedSym* getNoRelatedSym() throw(Fix::FieldNotFound*)
    { Fix::NoRelatedSym* value = new Fix::NoRelatedSym();
      getField(value); return value; }
 
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::LinesOfText* value)
    { setField(value); }
    Fix::LinesOfText* get(Fix::LinesOfText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LinesOfText* getLinesOfText() throw(Fix::FieldNotFound*)
    { Fix::LinesOfText* value = new Fix::LinesOfText();
      getField(value); return value; }
 
    void set(Fix::RawDataLength* value)
    { setField(value); }
    Fix::RawDataLength* get(Fix::RawDataLength* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RawDataLength* getRawDataLength() throw(Fix::FieldNotFound*)
    { Fix::RawDataLength* value = new Fix::RawDataLength();
      getField(value); return value; }
 
    void set(Fix::RawData* value)
    { setField(value); }
    Fix::RawData* get(Fix::RawData* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RawData* getRawData() throw(Fix::FieldNotFound*)
    { Fix::RawData* value = new Fix::RawData();
      getField(value); return value; }
 
  };

  public __gc class QuoteRequest : public Message
  {
  public: 
    QuoteRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("R"); }
    //
    QuoteRequest(
      Fix::QuoteReqID* aQuoteReqID,
      Fix::NoRelatedSym* aNoRelatedSym )
    : Message(MsgType())
    
    {
      set(aQuoteReqID);
      set(aNoRelatedSym);
    }
  
    void set(Fix::QuoteReqID* value)
    { setField(value); }
    Fix::QuoteReqID* get(Fix::QuoteReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteReqID* getQuoteReqID() throw(Fix::FieldNotFound*)
    { Fix::QuoteReqID* value = new Fix::QuoteReqID();
      getField(value); return value; }
 
    void set(Fix::NoRelatedSym* value)
    { setField(value); }
    Fix::NoRelatedSym* get(Fix::NoRelatedSym* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRelatedSym* getNoRelatedSym() throw(Fix::FieldNotFound*)
    { Fix::NoRelatedSym* value = new Fix::NoRelatedSym();
      getField(value); return value; }
 
  };

  public __gc class Quote : public Message
  {
  public: 
    Quote() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("S"); }
    //
    Quote(
      Fix::QuoteID* aQuoteID,
      Fix::Symbol* aSymbol )
    : Message(MsgType())
    
    {
      set(aQuoteID);
      set(aSymbol);
    }
  
    void set(Fix::QuoteReqID* value)
    { setField(value); }
    Fix::QuoteReqID* get(Fix::QuoteReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteReqID* getQuoteReqID() throw(Fix::FieldNotFound*)
    { Fix::QuoteReqID* value = new Fix::QuoteReqID();
      getField(value); return value; }
 
    void set(Fix::QuoteID* value)
    { setField(value); }
    Fix::QuoteID* get(Fix::QuoteID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteID* getQuoteID() throw(Fix::FieldNotFound*)
    { Fix::QuoteID* value = new Fix::QuoteID();
      getField(value); return value; }
 
    void set(Fix::QuoteResponseLevel* value)
    { setField(value); }
    Fix::QuoteResponseLevel* get(Fix::QuoteResponseLevel* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteResponseLevel* getQuoteResponseLevel() throw(Fix::FieldNotFound*)
    { Fix::QuoteResponseLevel* value = new Fix::QuoteResponseLevel();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::BidPx* value)
    { setField(value); }
    Fix::BidPx* get(Fix::BidPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidPx* getBidPx() throw(Fix::FieldNotFound*)
    { Fix::BidPx* value = new Fix::BidPx();
      getField(value); return value; }
 
    void set(Fix::OfferPx* value)
    { setField(value); }
    Fix::OfferPx* get(Fix::OfferPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OfferPx* getOfferPx() throw(Fix::FieldNotFound*)
    { Fix::OfferPx* value = new Fix::OfferPx();
      getField(value); return value; }
 
    void set(Fix::BidSize* value)
    { setField(value); }
    Fix::BidSize* get(Fix::BidSize* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidSize* getBidSize() throw(Fix::FieldNotFound*)
    { Fix::BidSize* value = new Fix::BidSize();
      getField(value); return value; }
 
    void set(Fix::OfferSize* value)
    { setField(value); }
    Fix::OfferSize* get(Fix::OfferSize* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OfferSize* getOfferSize() throw(Fix::FieldNotFound*)
    { Fix::OfferSize* value = new Fix::OfferSize();
      getField(value); return value; }
 
    void set(Fix::ValidUntilTime* value)
    { setField(value); }
    Fix::ValidUntilTime* get(Fix::ValidUntilTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ValidUntilTime* getValidUntilTime() throw(Fix::FieldNotFound*)
    { Fix::ValidUntilTime* value = new Fix::ValidUntilTime();
      getField(value); return value; }
 
    void set(Fix::BidSpotRate* value)
    { setField(value); }
    Fix::BidSpotRate* get(Fix::BidSpotRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidSpotRate* getBidSpotRate() throw(Fix::FieldNotFound*)
    { Fix::BidSpotRate* value = new Fix::BidSpotRate();
      getField(value); return value; }
 
    void set(Fix::OfferSpotRate* value)
    { setField(value); }
    Fix::OfferSpotRate* get(Fix::OfferSpotRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OfferSpotRate* getOfferSpotRate() throw(Fix::FieldNotFound*)
    { Fix::OfferSpotRate* value = new Fix::OfferSpotRate();
      getField(value); return value; }
 
    void set(Fix::BidForwardPoints* value)
    { setField(value); }
    Fix::BidForwardPoints* get(Fix::BidForwardPoints* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidForwardPoints* getBidForwardPoints() throw(Fix::FieldNotFound*)
    { Fix::BidForwardPoints* value = new Fix::BidForwardPoints();
      getField(value); return value; }
 
    void set(Fix::OfferForwardPoints* value)
    { setField(value); }
    Fix::OfferForwardPoints* get(Fix::OfferForwardPoints* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OfferForwardPoints* getOfferForwardPoints() throw(Fix::FieldNotFound*)
    { Fix::OfferForwardPoints* value = new Fix::OfferForwardPoints();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::FutSettDate* value)
    { setField(value); }
    Fix::FutSettDate* get(Fix::FutSettDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate* getFutSettDate() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate* value = new Fix::FutSettDate();
      getField(value); return value; }
 
    void set(Fix::OrdType* value)
    { setField(value); }
    Fix::OrdType* get(Fix::OrdType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdType* getOrdType() throw(Fix::FieldNotFound*)
    { Fix::OrdType* value = new Fix::OrdType();
      getField(value); return value; }
 
    void set(Fix::FutSettDate2* value)
    { setField(value); }
    Fix::FutSettDate2* get(Fix::FutSettDate2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate2* getFutSettDate2() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate2* value = new Fix::FutSettDate2();
      getField(value); return value; }
 
    void set(Fix::OrderQty2* value)
    { setField(value); }
    Fix::OrderQty2* get(Fix::OrderQty2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty2* getOrderQty2() throw(Fix::FieldNotFound*)
    { Fix::OrderQty2* value = new Fix::OrderQty2();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
  };

  public __gc class MassQuote : public Message
  {
  public: 
    MassQuote() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("i"); }
    //
    MassQuote(
      Fix::QuoteID* aQuoteID,
      Fix::NoQuoteSets* aNoQuoteSets )
    : Message(MsgType())
    
    {
      set(aQuoteID);
      set(aNoQuoteSets);
    }
  
    void set(Fix::QuoteReqID* value)
    { setField(value); }
    Fix::QuoteReqID* get(Fix::QuoteReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteReqID* getQuoteReqID() throw(Fix::FieldNotFound*)
    { Fix::QuoteReqID* value = new Fix::QuoteReqID();
      getField(value); return value; }
 
    void set(Fix::QuoteID* value)
    { setField(value); }
    Fix::QuoteID* get(Fix::QuoteID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteID* getQuoteID() throw(Fix::FieldNotFound*)
    { Fix::QuoteID* value = new Fix::QuoteID();
      getField(value); return value; }
 
    void set(Fix::QuoteResponseLevel* value)
    { setField(value); }
    Fix::QuoteResponseLevel* get(Fix::QuoteResponseLevel* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteResponseLevel* getQuoteResponseLevel() throw(Fix::FieldNotFound*)
    { Fix::QuoteResponseLevel* value = new Fix::QuoteResponseLevel();
      getField(value); return value; }
 
    void set(Fix::DefBidSize* value)
    { setField(value); }
    Fix::DefBidSize* get(Fix::DefBidSize* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DefBidSize* getDefBidSize() throw(Fix::FieldNotFound*)
    { Fix::DefBidSize* value = new Fix::DefBidSize();
      getField(value); return value; }
 
    void set(Fix::DefOfferSize* value)
    { setField(value); }
    Fix::DefOfferSize* get(Fix::DefOfferSize* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DefOfferSize* getDefOfferSize() throw(Fix::FieldNotFound*)
    { Fix::DefOfferSize* value = new Fix::DefOfferSize();
      getField(value); return value; }
 
    void set(Fix::NoQuoteSets* value)
    { setField(value); }
    Fix::NoQuoteSets* get(Fix::NoQuoteSets* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoQuoteSets* getNoQuoteSets() throw(Fix::FieldNotFound*)
    { Fix::NoQuoteSets* value = new Fix::NoQuoteSets();
      getField(value); return value; }
 
  };

  public __gc class QuoteCancel : public Message
  {
  public: 
    QuoteCancel() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("Z"); }
    //
    QuoteCancel(
      Fix::QuoteID* aQuoteID,
      Fix::QuoteCancelType* aQuoteCancelType,
      Fix::NoQuoteEntries* aNoQuoteEntries )
    : Message(MsgType())
    
    {
      set(aQuoteID);
      set(aQuoteCancelType);
      set(aNoQuoteEntries);
    }
  
    void set(Fix::QuoteReqID* value)
    { setField(value); }
    Fix::QuoteReqID* get(Fix::QuoteReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteReqID* getQuoteReqID() throw(Fix::FieldNotFound*)
    { Fix::QuoteReqID* value = new Fix::QuoteReqID();
      getField(value); return value; }
 
    void set(Fix::QuoteID* value)
    { setField(value); }
    Fix::QuoteID* get(Fix::QuoteID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteID* getQuoteID() throw(Fix::FieldNotFound*)
    { Fix::QuoteID* value = new Fix::QuoteID();
      getField(value); return value; }
 
    void set(Fix::QuoteCancelType* value)
    { setField(value); }
    Fix::QuoteCancelType* get(Fix::QuoteCancelType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteCancelType* getQuoteCancelType() throw(Fix::FieldNotFound*)
    { Fix::QuoteCancelType* value = new Fix::QuoteCancelType();
      getField(value); return value; }
 
    void set(Fix::QuoteResponseLevel* value)
    { setField(value); }
    Fix::QuoteResponseLevel* get(Fix::QuoteResponseLevel* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteResponseLevel* getQuoteResponseLevel() throw(Fix::FieldNotFound*)
    { Fix::QuoteResponseLevel* value = new Fix::QuoteResponseLevel();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::NoQuoteEntries* value)
    { setField(value); }
    Fix::NoQuoteEntries* get(Fix::NoQuoteEntries* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoQuoteEntries* getNoQuoteEntries() throw(Fix::FieldNotFound*)
    { Fix::NoQuoteEntries* value = new Fix::NoQuoteEntries();
      getField(value); return value; }
 
  };

  public __gc class QuoteStatusRequest : public Message
  {
  public: 
    QuoteStatusRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("a"); }
    //
    QuoteStatusRequest(
      Fix::Symbol* aSymbol )
    : Message(MsgType())
    
    {
      set(aSymbol);
    }
  
    void set(Fix::QuoteID* value)
    { setField(value); }
    Fix::QuoteID* get(Fix::QuoteID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteID* getQuoteID() throw(Fix::FieldNotFound*)
    { Fix::QuoteID* value = new Fix::QuoteID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
  };

  public __gc class QuoteAcknowledgement : public Message
  {
  public: 
    QuoteAcknowledgement() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("b"); }
    //
    QuoteAcknowledgement(
      Fix::QuoteAckStatus* aQuoteAckStatus )
    : Message(MsgType())
    
    {
      set(aQuoteAckStatus);
    }
  
    void set(Fix::QuoteReqID* value)
    { setField(value); }
    Fix::QuoteReqID* get(Fix::QuoteReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteReqID* getQuoteReqID() throw(Fix::FieldNotFound*)
    { Fix::QuoteReqID* value = new Fix::QuoteReqID();
      getField(value); return value; }
 
    void set(Fix::QuoteID* value)
    { setField(value); }
    Fix::QuoteID* get(Fix::QuoteID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteID* getQuoteID() throw(Fix::FieldNotFound*)
    { Fix::QuoteID* value = new Fix::QuoteID();
      getField(value); return value; }
 
    void set(Fix::QuoteAckStatus* value)
    { setField(value); }
    Fix::QuoteAckStatus* get(Fix::QuoteAckStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteAckStatus* getQuoteAckStatus() throw(Fix::FieldNotFound*)
    { Fix::QuoteAckStatus* value = new Fix::QuoteAckStatus();
      getField(value); return value; }
 
    void set(Fix::QuoteRejectReason* value)
    { setField(value); }
    Fix::QuoteRejectReason* get(Fix::QuoteRejectReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteRejectReason* getQuoteRejectReason() throw(Fix::FieldNotFound*)
    { Fix::QuoteRejectReason* value = new Fix::QuoteRejectReason();
      getField(value); return value; }
 
    void set(Fix::QuoteResponseLevel* value)
    { setField(value); }
    Fix::QuoteResponseLevel* get(Fix::QuoteResponseLevel* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteResponseLevel* getQuoteResponseLevel() throw(Fix::FieldNotFound*)
    { Fix::QuoteResponseLevel* value = new Fix::QuoteResponseLevel();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::NoQuoteSets* value)
    { setField(value); }
    Fix::NoQuoteSets* get(Fix::NoQuoteSets* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoQuoteSets* getNoQuoteSets() throw(Fix::FieldNotFound*)
    { Fix::NoQuoteSets* value = new Fix::NoQuoteSets();
      getField(value); return value; }
 
  };

  public __gc class MarketDataRequest : public Message
  {
  public: 
    MarketDataRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("V"); }
    //
    MarketDataRequest(
      Fix::MDReqID* aMDReqID,
      Fix::SubscriptionRequestType* aSubscriptionRequestType,
      Fix::MarketDepth* aMarketDepth,
      Fix::NoMDEntryTypes* aNoMDEntryTypes,
      Fix::NoRelatedSym* aNoRelatedSym )
    : Message(MsgType())
    
    {
      set(aMDReqID);
      set(aSubscriptionRequestType);
      set(aMarketDepth);
      set(aNoMDEntryTypes);
      set(aNoRelatedSym);
    }
  
    void set(Fix::MDReqID* value)
    { setField(value); }
    Fix::MDReqID* get(Fix::MDReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MDReqID* getMDReqID() throw(Fix::FieldNotFound*)
    { Fix::MDReqID* value = new Fix::MDReqID();
      getField(value); return value; }
 
    void set(Fix::SubscriptionRequestType* value)
    { setField(value); }
    Fix::SubscriptionRequestType* get(Fix::SubscriptionRequestType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SubscriptionRequestType* getSubscriptionRequestType() throw(Fix::FieldNotFound*)
    { Fix::SubscriptionRequestType* value = new Fix::SubscriptionRequestType();
      getField(value); return value; }
 
    void set(Fix::MarketDepth* value)
    { setField(value); }
    Fix::MarketDepth* get(Fix::MarketDepth* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MarketDepth* getMarketDepth() throw(Fix::FieldNotFound*)
    { Fix::MarketDepth* value = new Fix::MarketDepth();
      getField(value); return value; }
 
    void set(Fix::MDUpdateType* value)
    { setField(value); }
    Fix::MDUpdateType* get(Fix::MDUpdateType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MDUpdateType* getMDUpdateType() throw(Fix::FieldNotFound*)
    { Fix::MDUpdateType* value = new Fix::MDUpdateType();
      getField(value); return value; }
 
    void set(Fix::AggregatedBook* value)
    { setField(value); }
    Fix::AggregatedBook* get(Fix::AggregatedBook* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AggregatedBook* getAggregatedBook() throw(Fix::FieldNotFound*)
    { Fix::AggregatedBook* value = new Fix::AggregatedBook();
      getField(value); return value; }
 
    void set(Fix::NoMDEntryTypes* value)
    { setField(value); }
    Fix::NoMDEntryTypes* get(Fix::NoMDEntryTypes* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoMDEntryTypes* getNoMDEntryTypes() throw(Fix::FieldNotFound*)
    { Fix::NoMDEntryTypes* value = new Fix::NoMDEntryTypes();
      getField(value); return value; }
 
    void set(Fix::NoRelatedSym* value)
    { setField(value); }
    Fix::NoRelatedSym* get(Fix::NoRelatedSym* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRelatedSym* getNoRelatedSym() throw(Fix::FieldNotFound*)
    { Fix::NoRelatedSym* value = new Fix::NoRelatedSym();
      getField(value); return value; }
 
  };

  public __gc class MarketDataSnapshotFullRefresh : public Message
  {
  public: 
    MarketDataSnapshotFullRefresh() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("W"); }
    //
    MarketDataSnapshotFullRefresh(
      Fix::Symbol* aSymbol,
      Fix::NoMDEntries* aNoMDEntries )
    : Message(MsgType())
    
    {
      set(aSymbol);
      set(aNoMDEntries);
    }
  
    void set(Fix::MDReqID* value)
    { setField(value); }
    Fix::MDReqID* get(Fix::MDReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MDReqID* getMDReqID() throw(Fix::FieldNotFound*)
    { Fix::MDReqID* value = new Fix::MDReqID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::FinancialStatus* value)
    { setField(value); }
    Fix::FinancialStatus* get(Fix::FinancialStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FinancialStatus* getFinancialStatus() throw(Fix::FieldNotFound*)
    { Fix::FinancialStatus* value = new Fix::FinancialStatus();
      getField(value); return value; }
 
    void set(Fix::CorporateAction* value)
    { setField(value); }
    Fix::CorporateAction* get(Fix::CorporateAction* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CorporateAction* getCorporateAction() throw(Fix::FieldNotFound*)
    { Fix::CorporateAction* value = new Fix::CorporateAction();
      getField(value); return value; }
 
    void set(Fix::TotalVolumeTraded* value)
    { setField(value); }
    Fix::TotalVolumeTraded* get(Fix::TotalVolumeTraded* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TotalVolumeTraded* getTotalVolumeTraded() throw(Fix::FieldNotFound*)
    { Fix::TotalVolumeTraded* value = new Fix::TotalVolumeTraded();
      getField(value); return value; }
 
    void set(Fix::NoMDEntries* value)
    { setField(value); }
    Fix::NoMDEntries* get(Fix::NoMDEntries* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoMDEntries* getNoMDEntries() throw(Fix::FieldNotFound*)
    { Fix::NoMDEntries* value = new Fix::NoMDEntries();
      getField(value); return value; }
 
  };

  public __gc class MarketDataIncrementalRefresh : public Message
  {
  public: 
    MarketDataIncrementalRefresh() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("X"); }
    //
    MarketDataIncrementalRefresh(
      Fix::NoMDEntries* aNoMDEntries )
    : Message(MsgType())
    
    {
      set(aNoMDEntries);
    }
  
    void set(Fix::MDReqID* value)
    { setField(value); }
    Fix::MDReqID* get(Fix::MDReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MDReqID* getMDReqID() throw(Fix::FieldNotFound*)
    { Fix::MDReqID* value = new Fix::MDReqID();
      getField(value); return value; }
 
    void set(Fix::NoMDEntries* value)
    { setField(value); }
    Fix::NoMDEntries* get(Fix::NoMDEntries* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoMDEntries* getNoMDEntries() throw(Fix::FieldNotFound*)
    { Fix::NoMDEntries* value = new Fix::NoMDEntries();
      getField(value); return value; }
 
  };

  public __gc class MarketDataRequestReject : public Message
  {
  public: 
    MarketDataRequestReject() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("Y"); }
    //
    MarketDataRequestReject(
      Fix::MDReqID* aMDReqID )
    : Message(MsgType())
    
    {
      set(aMDReqID);
    }
  
    void set(Fix::MDReqID* value)
    { setField(value); }
    Fix::MDReqID* get(Fix::MDReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MDReqID* getMDReqID() throw(Fix::FieldNotFound*)
    { Fix::MDReqID* value = new Fix::MDReqID();
      getField(value); return value; }
 
    void set(Fix::MDReqRejReason* value)
    { setField(value); }
    Fix::MDReqRejReason* get(Fix::MDReqRejReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MDReqRejReason* getMDReqRejReason() throw(Fix::FieldNotFound*)
    { Fix::MDReqRejReason* value = new Fix::MDReqRejReason();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class SecurityDefinitionRequest : public Message
  {
  public: 
    SecurityDefinitionRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("c"); }
    //
    SecurityDefinitionRequest(
      Fix::SecurityReqID* aSecurityReqID,
      Fix::SecurityRequestType* aSecurityRequestType )
    : Message(MsgType())
    
    {
      set(aSecurityReqID);
      set(aSecurityRequestType);
    }
  
    void set(Fix::SecurityReqID* value)
    { setField(value); }
    Fix::SecurityReqID* get(Fix::SecurityReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityReqID* getSecurityReqID() throw(Fix::FieldNotFound*)
    { Fix::SecurityReqID* value = new Fix::SecurityReqID();
      getField(value); return value; }
 
    void set(Fix::SecurityRequestType* value)
    { setField(value); }
    Fix::SecurityRequestType* get(Fix::SecurityRequestType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityRequestType* getSecurityRequestType() throw(Fix::FieldNotFound*)
    { Fix::SecurityRequestType* value = new Fix::SecurityRequestType();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::NoRelatedSym* value)
    { setField(value); }
    Fix::NoRelatedSym* get(Fix::NoRelatedSym* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRelatedSym* getNoRelatedSym() throw(Fix::FieldNotFound*)
    { Fix::NoRelatedSym* value = new Fix::NoRelatedSym();
      getField(value); return value; }
 
  };

  public __gc class SecurityDefinition : public Message
  {
  public: 
    SecurityDefinition() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("d"); }
    //
    SecurityDefinition(
      Fix::SecurityReqID* aSecurityReqID,
      Fix::SecurityResponseID* aSecurityResponseID,
      Fix::TotalNumSecurities* aTotalNumSecurities )
    : Message(MsgType())
    
    {
      set(aSecurityReqID);
      set(aSecurityResponseID);
      set(aTotalNumSecurities);
    }
  
    void set(Fix::SecurityReqID* value)
    { setField(value); }
    Fix::SecurityReqID* get(Fix::SecurityReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityReqID* getSecurityReqID() throw(Fix::FieldNotFound*)
    { Fix::SecurityReqID* value = new Fix::SecurityReqID();
      getField(value); return value; }
 
    void set(Fix::SecurityResponseID* value)
    { setField(value); }
    Fix::SecurityResponseID* get(Fix::SecurityResponseID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityResponseID* getSecurityResponseID() throw(Fix::FieldNotFound*)
    { Fix::SecurityResponseID* value = new Fix::SecurityResponseID();
      getField(value); return value; }
 
    void set(Fix::SecurityResponseType* value)
    { setField(value); }
    Fix::SecurityResponseType* get(Fix::SecurityResponseType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityResponseType* getSecurityResponseType() throw(Fix::FieldNotFound*)
    { Fix::SecurityResponseType* value = new Fix::SecurityResponseType();
      getField(value); return value; }
 
    void set(Fix::TotalNumSecurities* value)
    { setField(value); }
    Fix::TotalNumSecurities* get(Fix::TotalNumSecurities* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TotalNumSecurities* getTotalNumSecurities() throw(Fix::FieldNotFound*)
    { Fix::TotalNumSecurities* value = new Fix::TotalNumSecurities();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::NoRelatedSym* value)
    { setField(value); }
    Fix::NoRelatedSym* get(Fix::NoRelatedSym* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRelatedSym* getNoRelatedSym() throw(Fix::FieldNotFound*)
    { Fix::NoRelatedSym* value = new Fix::NoRelatedSym();
      getField(value); return value; }
 
  };

  public __gc class SecurityStatusRequest : public Message
  {
  public: 
    SecurityStatusRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("e"); }
    //
    SecurityStatusRequest(
      Fix::SecurityStatusReqID* aSecurityStatusReqID,
      Fix::Symbol* aSymbol,
      Fix::SubscriptionRequestType* aSubscriptionRequestType )
    : Message(MsgType())
    
    {
      set(aSecurityStatusReqID);
      set(aSymbol);
      set(aSubscriptionRequestType);
    }
  
    void set(Fix::SecurityStatusReqID* value)
    { setField(value); }
    Fix::SecurityStatusReqID* get(Fix::SecurityStatusReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityStatusReqID* getSecurityStatusReqID() throw(Fix::FieldNotFound*)
    { Fix::SecurityStatusReqID* value = new Fix::SecurityStatusReqID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::SubscriptionRequestType* value)
    { setField(value); }
    Fix::SubscriptionRequestType* get(Fix::SubscriptionRequestType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SubscriptionRequestType* getSubscriptionRequestType() throw(Fix::FieldNotFound*)
    { Fix::SubscriptionRequestType* value = new Fix::SubscriptionRequestType();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
  };

  public __gc class SecurityStatus : public Message
  {
  public: 
    SecurityStatus() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("f"); }
    //
    SecurityStatus(
      Fix::Symbol* aSymbol )
    : Message(MsgType())
    
    {
      set(aSymbol);
    }
  
    void set(Fix::SecurityStatusReqID* value)
    { setField(value); }
    Fix::SecurityStatusReqID* get(Fix::SecurityStatusReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityStatusReqID* getSecurityStatusReqID() throw(Fix::FieldNotFound*)
    { Fix::SecurityStatusReqID* value = new Fix::SecurityStatusReqID();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::UnsolicitedIndicator* value)
    { setField(value); }
    Fix::UnsolicitedIndicator* get(Fix::UnsolicitedIndicator* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::UnsolicitedIndicator* getUnsolicitedIndicator() throw(Fix::FieldNotFound*)
    { Fix::UnsolicitedIndicator* value = new Fix::UnsolicitedIndicator();
      getField(value); return value; }
 
    void set(Fix::SecurityTradingStatus* value)
    { setField(value); }
    Fix::SecurityTradingStatus* get(Fix::SecurityTradingStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityTradingStatus* getSecurityTradingStatus() throw(Fix::FieldNotFound*)
    { Fix::SecurityTradingStatus* value = new Fix::SecurityTradingStatus();
      getField(value); return value; }
 
    void set(Fix::FinancialStatus* value)
    { setField(value); }
    Fix::FinancialStatus* get(Fix::FinancialStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FinancialStatus* getFinancialStatus() throw(Fix::FieldNotFound*)
    { Fix::FinancialStatus* value = new Fix::FinancialStatus();
      getField(value); return value; }
 
    void set(Fix::CorporateAction* value)
    { setField(value); }
    Fix::CorporateAction* get(Fix::CorporateAction* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CorporateAction* getCorporateAction() throw(Fix::FieldNotFound*)
    { Fix::CorporateAction* value = new Fix::CorporateAction();
      getField(value); return value; }
 
    void set(Fix::HaltReason* value)
    { setField(value); }
    Fix::HaltReason* get(Fix::HaltReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::HaltReason* getHaltReason() throw(Fix::FieldNotFound*)
    { Fix::HaltReason* value = new Fix::HaltReason();
      getField(value); return value; }
 
    void set(Fix::InViewOfCommon* value)
    { setField(value); }
    Fix::InViewOfCommon* get(Fix::InViewOfCommon* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::InViewOfCommon* getInViewOfCommon() throw(Fix::FieldNotFound*)
    { Fix::InViewOfCommon* value = new Fix::InViewOfCommon();
      getField(value); return value; }
 
    void set(Fix::DueToRelated* value)
    { setField(value); }
    Fix::DueToRelated* get(Fix::DueToRelated* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DueToRelated* getDueToRelated() throw(Fix::FieldNotFound*)
    { Fix::DueToRelated* value = new Fix::DueToRelated();
      getField(value); return value; }
 
    void set(Fix::BuyVolume* value)
    { setField(value); }
    Fix::BuyVolume* get(Fix::BuyVolume* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BuyVolume* getBuyVolume() throw(Fix::FieldNotFound*)
    { Fix::BuyVolume* value = new Fix::BuyVolume();
      getField(value); return value; }
 
    void set(Fix::SellVolume* value)
    { setField(value); }
    Fix::SellVolume* get(Fix::SellVolume* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SellVolume* getSellVolume() throw(Fix::FieldNotFound*)
    { Fix::SellVolume* value = new Fix::SellVolume();
      getField(value); return value; }
 
    void set(Fix::HighPx* value)
    { setField(value); }
    Fix::HighPx* get(Fix::HighPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::HighPx* getHighPx() throw(Fix::FieldNotFound*)
    { Fix::HighPx* value = new Fix::HighPx();
      getField(value); return value; }
 
    void set(Fix::LowPx* value)
    { setField(value); }
    Fix::LowPx* get(Fix::LowPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LowPx* getLowPx() throw(Fix::FieldNotFound*)
    { Fix::LowPx* value = new Fix::LowPx();
      getField(value); return value; }
 
    void set(Fix::LastPx* value)
    { setField(value); }
    Fix::LastPx* get(Fix::LastPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastPx* getLastPx() throw(Fix::FieldNotFound*)
    { Fix::LastPx* value = new Fix::LastPx();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::Adjustment* value)
    { setField(value); }
    Fix::Adjustment* get(Fix::Adjustment* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Adjustment* getAdjustment() throw(Fix::FieldNotFound*)
    { Fix::Adjustment* value = new Fix::Adjustment();
      getField(value); return value; }
 
  };

  public __gc class TradingSessionStatusRequest : public Message
  {
  public: 
    TradingSessionStatusRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("g"); }
    //
    TradingSessionStatusRequest(
      Fix::TradSesReqID* aTradSesReqID,
      Fix::SubscriptionRequestType* aSubscriptionRequestType )
    : Message(MsgType())
    
    {
      set(aTradSesReqID);
      set(aSubscriptionRequestType);
    }
  
    void set(Fix::TradSesReqID* value)
    { setField(value); }
    Fix::TradSesReqID* get(Fix::TradSesReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesReqID* getTradSesReqID() throw(Fix::FieldNotFound*)
    { Fix::TradSesReqID* value = new Fix::TradSesReqID();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::TradSesMethod* value)
    { setField(value); }
    Fix::TradSesMethod* get(Fix::TradSesMethod* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesMethod* getTradSesMethod() throw(Fix::FieldNotFound*)
    { Fix::TradSesMethod* value = new Fix::TradSesMethod();
      getField(value); return value; }
 
    void set(Fix::TradSesMode* value)
    { setField(value); }
    Fix::TradSesMode* get(Fix::TradSesMode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesMode* getTradSesMode() throw(Fix::FieldNotFound*)
    { Fix::TradSesMode* value = new Fix::TradSesMode();
      getField(value); return value; }
 
    void set(Fix::SubscriptionRequestType* value)
    { setField(value); }
    Fix::SubscriptionRequestType* get(Fix::SubscriptionRequestType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SubscriptionRequestType* getSubscriptionRequestType() throw(Fix::FieldNotFound*)
    { Fix::SubscriptionRequestType* value = new Fix::SubscriptionRequestType();
      getField(value); return value; }
 
  };

  public __gc class TradingSessionStatus : public Message
  {
  public: 
    TradingSessionStatus() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("h"); }
    //
    TradingSessionStatus(
      Fix::TradingSessionID* aTradingSessionID,
      Fix::TradSesStatus* aTradSesStatus )
    : Message(MsgType())
    
    {
      set(aTradingSessionID);
      set(aTradSesStatus);
    }
  
    void set(Fix::TradSesReqID* value)
    { setField(value); }
    Fix::TradSesReqID* get(Fix::TradSesReqID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesReqID* getTradSesReqID() throw(Fix::FieldNotFound*)
    { Fix::TradSesReqID* value = new Fix::TradSesReqID();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::TradSesMethod* value)
    { setField(value); }
    Fix::TradSesMethod* get(Fix::TradSesMethod* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesMethod* getTradSesMethod() throw(Fix::FieldNotFound*)
    { Fix::TradSesMethod* value = new Fix::TradSesMethod();
      getField(value); return value; }
 
    void set(Fix::TradSesMode* value)
    { setField(value); }
    Fix::TradSesMode* get(Fix::TradSesMode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesMode* getTradSesMode() throw(Fix::FieldNotFound*)
    { Fix::TradSesMode* value = new Fix::TradSesMode();
      getField(value); return value; }
 
    void set(Fix::UnsolicitedIndicator* value)
    { setField(value); }
    Fix::UnsolicitedIndicator* get(Fix::UnsolicitedIndicator* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::UnsolicitedIndicator* getUnsolicitedIndicator() throw(Fix::FieldNotFound*)
    { Fix::UnsolicitedIndicator* value = new Fix::UnsolicitedIndicator();
      getField(value); return value; }
 
    void set(Fix::TradSesStatus* value)
    { setField(value); }
    Fix::TradSesStatus* get(Fix::TradSesStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesStatus* getTradSesStatus() throw(Fix::FieldNotFound*)
    { Fix::TradSesStatus* value = new Fix::TradSesStatus();
      getField(value); return value; }
 
    void set(Fix::TradSesStartTime* value)
    { setField(value); }
    Fix::TradSesStartTime* get(Fix::TradSesStartTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesStartTime* getTradSesStartTime() throw(Fix::FieldNotFound*)
    { Fix::TradSesStartTime* value = new Fix::TradSesStartTime();
      getField(value); return value; }
 
    void set(Fix::TradSesOpenTime* value)
    { setField(value); }
    Fix::TradSesOpenTime* get(Fix::TradSesOpenTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesOpenTime* getTradSesOpenTime() throw(Fix::FieldNotFound*)
    { Fix::TradSesOpenTime* value = new Fix::TradSesOpenTime();
      getField(value); return value; }
 
    void set(Fix::TradSesPreCloseTime* value)
    { setField(value); }
    Fix::TradSesPreCloseTime* get(Fix::TradSesPreCloseTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesPreCloseTime* getTradSesPreCloseTime() throw(Fix::FieldNotFound*)
    { Fix::TradSesPreCloseTime* value = new Fix::TradSesPreCloseTime();
      getField(value); return value; }
 
    void set(Fix::TradSesCloseTime* value)
    { setField(value); }
    Fix::TradSesCloseTime* get(Fix::TradSesCloseTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesCloseTime* getTradSesCloseTime() throw(Fix::FieldNotFound*)
    { Fix::TradSesCloseTime* value = new Fix::TradSesCloseTime();
      getField(value); return value; }
 
    void set(Fix::TradSesEndTime* value)
    { setField(value); }
    Fix::TradSesEndTime* get(Fix::TradSesEndTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradSesEndTime* getTradSesEndTime() throw(Fix::FieldNotFound*)
    { Fix::TradSesEndTime* value = new Fix::TradSesEndTime();
      getField(value); return value; }
 
    void set(Fix::TotalVolumeTraded* value)
    { setField(value); }
    Fix::TotalVolumeTraded* get(Fix::TotalVolumeTraded* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TotalVolumeTraded* getTotalVolumeTraded() throw(Fix::FieldNotFound*)
    { Fix::TotalVolumeTraded* value = new Fix::TotalVolumeTraded();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class NewOrderSingle : public Message
  {
  public: 
    NewOrderSingle() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("D"); }
    //
    NewOrderSingle(
      Fix::ClOrdID* aClOrdID,
      Fix::HandlInst* aHandlInst,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide,
      Fix::TransactTime* aTransactTime,
      Fix::OrdType* aOrdType )
    : Message(MsgType())
    
    {
      set(aClOrdID);
      set(aHandlInst);
      set(aSymbol);
      set(aSide);
      set(aTransactTime);
      set(aOrdType);
    }
  
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::Account* value)
    { setField(value); }
    Fix::Account* get(Fix::Account* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Account* getAccount() throw(Fix::FieldNotFound*)
    { Fix::Account* value = new Fix::Account();
      getField(value); return value; }
 
    void set(Fix::NoAllocs* value)
    { setField(value); }
    Fix::NoAllocs* get(Fix::NoAllocs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoAllocs* getNoAllocs() throw(Fix::FieldNotFound*)
    { Fix::NoAllocs* value = new Fix::NoAllocs();
      getField(value); return value; }
 
    void set(Fix::SettlmntTyp* value)
    { setField(value); }
    Fix::SettlmntTyp* get(Fix::SettlmntTyp* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlmntTyp* getSettlmntTyp() throw(Fix::FieldNotFound*)
    { Fix::SettlmntTyp* value = new Fix::SettlmntTyp();
      getField(value); return value; }
 
    void set(Fix::FutSettDate* value)
    { setField(value); }
    Fix::FutSettDate* get(Fix::FutSettDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate* getFutSettDate() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate* value = new Fix::FutSettDate();
      getField(value); return value; }
 
    void set(Fix::HandlInst* value)
    { setField(value); }
    Fix::HandlInst* get(Fix::HandlInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::HandlInst* getHandlInst() throw(Fix::FieldNotFound*)
    { Fix::HandlInst* value = new Fix::HandlInst();
      getField(value); return value; }
 
    void set(Fix::ExecInst* value)
    { setField(value); }
    Fix::ExecInst* get(Fix::ExecInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecInst* getExecInst() throw(Fix::FieldNotFound*)
    { Fix::ExecInst* value = new Fix::ExecInst();
      getField(value); return value; }
 
    void set(Fix::MinQty* value)
    { setField(value); }
    Fix::MinQty* get(Fix::MinQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MinQty* getMinQty() throw(Fix::FieldNotFound*)
    { Fix::MinQty* value = new Fix::MinQty();
      getField(value); return value; }
 
    void set(Fix::MaxFloor* value)
    { setField(value); }
    Fix::MaxFloor* get(Fix::MaxFloor* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxFloor* getMaxFloor() throw(Fix::FieldNotFound*)
    { Fix::MaxFloor* value = new Fix::MaxFloor();
      getField(value); return value; }
 
    void set(Fix::ExDestination* value)
    { setField(value); }
    Fix::ExDestination* get(Fix::ExDestination* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExDestination* getExDestination() throw(Fix::FieldNotFound*)
    { Fix::ExDestination* value = new Fix::ExDestination();
      getField(value); return value; }
 
    void set(Fix::NoTradingSessions* value)
    { setField(value); }
    Fix::NoTradingSessions* get(Fix::NoTradingSessions* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoTradingSessions* getNoTradingSessions() throw(Fix::FieldNotFound*)
    { Fix::NoTradingSessions* value = new Fix::NoTradingSessions();
      getField(value); return value; }
 
    void set(Fix::ProcessCode* value)
    { setField(value); }
    Fix::ProcessCode* get(Fix::ProcessCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ProcessCode* getProcessCode() throw(Fix::FieldNotFound*)
    { Fix::ProcessCode* value = new Fix::ProcessCode();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::PrevClosePx* value)
    { setField(value); }
    Fix::PrevClosePx* get(Fix::PrevClosePx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PrevClosePx* getPrevClosePx() throw(Fix::FieldNotFound*)
    { Fix::PrevClosePx* value = new Fix::PrevClosePx();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::LocateReqd* value)
    { setField(value); }
    Fix::LocateReqd* get(Fix::LocateReqd* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LocateReqd* getLocateReqd() throw(Fix::FieldNotFound*)
    { Fix::LocateReqd* value = new Fix::LocateReqd();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::OrderQty* value)
    { setField(value); }
    Fix::OrderQty* get(Fix::OrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty* getOrderQty() throw(Fix::FieldNotFound*)
    { Fix::OrderQty* value = new Fix::OrderQty();
      getField(value); return value; }
 
    void set(Fix::CashOrderQty* value)
    { setField(value); }
    Fix::CashOrderQty* get(Fix::CashOrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashOrderQty* getCashOrderQty() throw(Fix::FieldNotFound*)
    { Fix::CashOrderQty* value = new Fix::CashOrderQty();
      getField(value); return value; }
 
    void set(Fix::OrdType* value)
    { setField(value); }
    Fix::OrdType* get(Fix::OrdType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdType* getOrdType() throw(Fix::FieldNotFound*)
    { Fix::OrdType* value = new Fix::OrdType();
      getField(value); return value; }
 
    void set(Fix::Price* value)
    { setField(value); }
    Fix::Price* get(Fix::Price* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Price* getPrice() throw(Fix::FieldNotFound*)
    { Fix::Price* value = new Fix::Price();
      getField(value); return value; }
 
    void set(Fix::StopPx* value)
    { setField(value); }
    Fix::StopPx* get(Fix::StopPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StopPx* getStopPx() throw(Fix::FieldNotFound*)
    { Fix::StopPx* value = new Fix::StopPx();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::ComplianceID* value)
    { setField(value); }
    Fix::ComplianceID* get(Fix::ComplianceID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ComplianceID* getComplianceID() throw(Fix::FieldNotFound*)
    { Fix::ComplianceID* value = new Fix::ComplianceID();
      getField(value); return value; }
 
    void set(Fix::SolicitedFlag* value)
    { setField(value); }
    Fix::SolicitedFlag* get(Fix::SolicitedFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SolicitedFlag* getSolicitedFlag() throw(Fix::FieldNotFound*)
    { Fix::SolicitedFlag* value = new Fix::SolicitedFlag();
      getField(value); return value; }
 
    void set(Fix::IOIid* value)
    { setField(value); }
    Fix::IOIid* get(Fix::IOIid* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IOIid* getIOIid() throw(Fix::FieldNotFound*)
    { Fix::IOIid* value = new Fix::IOIid();
      getField(value); return value; }
 
    void set(Fix::QuoteID* value)
    { setField(value); }
    Fix::QuoteID* get(Fix::QuoteID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::QuoteID* getQuoteID() throw(Fix::FieldNotFound*)
    { Fix::QuoteID* value = new Fix::QuoteID();
      getField(value); return value; }
 
    void set(Fix::TimeInForce* value)
    { setField(value); }
    Fix::TimeInForce* get(Fix::TimeInForce* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TimeInForce* getTimeInForce() throw(Fix::FieldNotFound*)
    { Fix::TimeInForce* value = new Fix::TimeInForce();
      getField(value); return value; }
 
    void set(Fix::EffectiveTime* value)
    { setField(value); }
    Fix::EffectiveTime* get(Fix::EffectiveTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EffectiveTime* getEffectiveTime() throw(Fix::FieldNotFound*)
    { Fix::EffectiveTime* value = new Fix::EffectiveTime();
      getField(value); return value; }
 
    void set(Fix::ExpireDate* value)
    { setField(value); }
    Fix::ExpireDate* get(Fix::ExpireDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExpireDate* getExpireDate() throw(Fix::FieldNotFound*)
    { Fix::ExpireDate* value = new Fix::ExpireDate();
      getField(value); return value; }
 
    void set(Fix::ExpireTime* value)
    { setField(value); }
    Fix::ExpireTime* get(Fix::ExpireTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExpireTime* getExpireTime() throw(Fix::FieldNotFound*)
    { Fix::ExpireTime* value = new Fix::ExpireTime();
      getField(value); return value; }
 
    void set(Fix::GTBookingInst* value)
    { setField(value); }
    Fix::GTBookingInst* get(Fix::GTBookingInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::GTBookingInst* getGTBookingInst() throw(Fix::FieldNotFound*)
    { Fix::GTBookingInst* value = new Fix::GTBookingInst();
      getField(value); return value; }
 
    void set(Fix::Commission* value)
    { setField(value); }
    Fix::Commission* get(Fix::Commission* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Commission* getCommission() throw(Fix::FieldNotFound*)
    { Fix::Commission* value = new Fix::Commission();
      getField(value); return value; }
 
    void set(Fix::CommType* value)
    { setField(value); }
    Fix::CommType* get(Fix::CommType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CommType* getCommType() throw(Fix::FieldNotFound*)
    { Fix::CommType* value = new Fix::CommType();
      getField(value); return value; }
 
    void set(Fix::Rule80A* value)
    { setField(value); }
    Fix::Rule80A* get(Fix::Rule80A* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Rule80A* getRule80A() throw(Fix::FieldNotFound*)
    { Fix::Rule80A* value = new Fix::Rule80A();
      getField(value); return value; }
 
    void set(Fix::ForexReq* value)
    { setField(value); }
    Fix::ForexReq* get(Fix::ForexReq* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ForexReq* getForexReq() throw(Fix::FieldNotFound*)
    { Fix::ForexReq* value = new Fix::ForexReq();
      getField(value); return value; }
 
    void set(Fix::SettlCurrency* value)
    { setField(value); }
    Fix::SettlCurrency* get(Fix::SettlCurrency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlCurrency* getSettlCurrency() throw(Fix::FieldNotFound*)
    { Fix::SettlCurrency* value = new Fix::SettlCurrency();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::FutSettDate2* value)
    { setField(value); }
    Fix::FutSettDate2* get(Fix::FutSettDate2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate2* getFutSettDate2() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate2* value = new Fix::FutSettDate2();
      getField(value); return value; }
 
    void set(Fix::OrderQty2* value)
    { setField(value); }
    Fix::OrderQty2* get(Fix::OrderQty2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty2* getOrderQty2() throw(Fix::FieldNotFound*)
    { Fix::OrderQty2* value = new Fix::OrderQty2();
      getField(value); return value; }
 
    void set(Fix::OpenClose* value)
    { setField(value); }
    Fix::OpenClose* get(Fix::OpenClose* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OpenClose* getOpenClose() throw(Fix::FieldNotFound*)
    { Fix::OpenClose* value = new Fix::OpenClose();
      getField(value); return value; }
 
    void set(Fix::CoveredOrUncovered* value)
    { setField(value); }
    Fix::CoveredOrUncovered* get(Fix::CoveredOrUncovered* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CoveredOrUncovered* getCoveredOrUncovered() throw(Fix::FieldNotFound*)
    { Fix::CoveredOrUncovered* value = new Fix::CoveredOrUncovered();
      getField(value); return value; }
 
    void set(Fix::CustomerOrFirm* value)
    { setField(value); }
    Fix::CustomerOrFirm* get(Fix::CustomerOrFirm* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CustomerOrFirm* getCustomerOrFirm() throw(Fix::FieldNotFound*)
    { Fix::CustomerOrFirm* value = new Fix::CustomerOrFirm();
      getField(value); return value; }
 
    void set(Fix::MaxShow* value)
    { setField(value); }
    Fix::MaxShow* get(Fix::MaxShow* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxShow* getMaxShow() throw(Fix::FieldNotFound*)
    { Fix::MaxShow* value = new Fix::MaxShow();
      getField(value); return value; }
 
    void set(Fix::PegDifference* value)
    { setField(value); }
    Fix::PegDifference* get(Fix::PegDifference* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PegDifference* getPegDifference() throw(Fix::FieldNotFound*)
    { Fix::PegDifference* value = new Fix::PegDifference();
      getField(value); return value; }
 
    void set(Fix::DiscretionInst* value)
    { setField(value); }
    Fix::DiscretionInst* get(Fix::DiscretionInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DiscretionInst* getDiscretionInst() throw(Fix::FieldNotFound*)
    { Fix::DiscretionInst* value = new Fix::DiscretionInst();
      getField(value); return value; }
 
    void set(Fix::DiscretionOffset* value)
    { setField(value); }
    Fix::DiscretionOffset* get(Fix::DiscretionOffset* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DiscretionOffset* getDiscretionOffset() throw(Fix::FieldNotFound*)
    { Fix::DiscretionOffset* value = new Fix::DiscretionOffset();
      getField(value); return value; }
 
    void set(Fix::ClearingFirm* value)
    { setField(value); }
    Fix::ClearingFirm* get(Fix::ClearingFirm* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClearingFirm* getClearingFirm() throw(Fix::FieldNotFound*)
    { Fix::ClearingFirm* value = new Fix::ClearingFirm();
      getField(value); return value; }
 
    void set(Fix::ClearingAccount* value)
    { setField(value); }
    Fix::ClearingAccount* get(Fix::ClearingAccount* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClearingAccount* getClearingAccount() throw(Fix::FieldNotFound*)
    { Fix::ClearingAccount* value = new Fix::ClearingAccount();
      getField(value); return value; }
 
  };

  public __gc class ExecutionReport : public Message
  {
  public: 
    ExecutionReport() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("8"); }
    //
    ExecutionReport(
      Fix::OrderID* aOrderID,
      Fix::ExecID* aExecID,
      Fix::ExecTransType* aExecTransType,
      Fix::ExecType* aExecType,
      Fix::OrdStatus* aOrdStatus,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide,
      Fix::LeavesQty* aLeavesQty,
      Fix::CumQty* aCumQty,
      Fix::AvgPx* aAvgPx )
    : Message(MsgType())
    
    {
      set(aOrderID);
      set(aExecID);
      set(aExecTransType);
      set(aExecType);
      set(aOrdStatus);
      set(aSymbol);
      set(aSide);
      set(aLeavesQty);
      set(aCumQty);
      set(aAvgPx);
    }
  
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::SecondaryOrderID* value)
    { setField(value); }
    Fix::SecondaryOrderID* get(Fix::SecondaryOrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecondaryOrderID* getSecondaryOrderID() throw(Fix::FieldNotFound*)
    { Fix::SecondaryOrderID* value = new Fix::SecondaryOrderID();
      getField(value); return value; }
 
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::OrigClOrdID* value)
    { setField(value); }
    Fix::OrigClOrdID* get(Fix::OrigClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigClOrdID* getOrigClOrdID() throw(Fix::FieldNotFound*)
    { Fix::OrigClOrdID* value = new Fix::OrigClOrdID();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::NoContraBrokers* value)
    { setField(value); }
    Fix::NoContraBrokers* get(Fix::NoContraBrokers* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoContraBrokers* getNoContraBrokers() throw(Fix::FieldNotFound*)
    { Fix::NoContraBrokers* value = new Fix::NoContraBrokers();
      getField(value); return value; }
 
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::ExecID* value)
    { setField(value); }
    Fix::ExecID* get(Fix::ExecID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecID* getExecID() throw(Fix::FieldNotFound*)
    { Fix::ExecID* value = new Fix::ExecID();
      getField(value); return value; }
 
    void set(Fix::ExecTransType* value)
    { setField(value); }
    Fix::ExecTransType* get(Fix::ExecTransType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecTransType* getExecTransType() throw(Fix::FieldNotFound*)
    { Fix::ExecTransType* value = new Fix::ExecTransType();
      getField(value); return value; }
 
    void set(Fix::ExecRefID* value)
    { setField(value); }
    Fix::ExecRefID* get(Fix::ExecRefID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecRefID* getExecRefID() throw(Fix::FieldNotFound*)
    { Fix::ExecRefID* value = new Fix::ExecRefID();
      getField(value); return value; }
 
    void set(Fix::ExecType* value)
    { setField(value); }
    Fix::ExecType* get(Fix::ExecType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecType* getExecType() throw(Fix::FieldNotFound*)
    { Fix::ExecType* value = new Fix::ExecType();
      getField(value); return value; }
 
    void set(Fix::OrdStatus* value)
    { setField(value); }
    Fix::OrdStatus* get(Fix::OrdStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdStatus* getOrdStatus() throw(Fix::FieldNotFound*)
    { Fix::OrdStatus* value = new Fix::OrdStatus();
      getField(value); return value; }
 
    void set(Fix::OrdRejReason* value)
    { setField(value); }
    Fix::OrdRejReason* get(Fix::OrdRejReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdRejReason* getOrdRejReason() throw(Fix::FieldNotFound*)
    { Fix::OrdRejReason* value = new Fix::OrdRejReason();
      getField(value); return value; }
 
    void set(Fix::ExecRestatementReason* value)
    { setField(value); }
    Fix::ExecRestatementReason* get(Fix::ExecRestatementReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecRestatementReason* getExecRestatementReason() throw(Fix::FieldNotFound*)
    { Fix::ExecRestatementReason* value = new Fix::ExecRestatementReason();
      getField(value); return value; }
 
    void set(Fix::Account* value)
    { setField(value); }
    Fix::Account* get(Fix::Account* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Account* getAccount() throw(Fix::FieldNotFound*)
    { Fix::Account* value = new Fix::Account();
      getField(value); return value; }
 
    void set(Fix::SettlmntTyp* value)
    { setField(value); }
    Fix::SettlmntTyp* get(Fix::SettlmntTyp* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlmntTyp* getSettlmntTyp() throw(Fix::FieldNotFound*)
    { Fix::SettlmntTyp* value = new Fix::SettlmntTyp();
      getField(value); return value; }
 
    void set(Fix::FutSettDate* value)
    { setField(value); }
    Fix::FutSettDate* get(Fix::FutSettDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate* getFutSettDate() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate* value = new Fix::FutSettDate();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::OrderQty* value)
    { setField(value); }
    Fix::OrderQty* get(Fix::OrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty* getOrderQty() throw(Fix::FieldNotFound*)
    { Fix::OrderQty* value = new Fix::OrderQty();
      getField(value); return value; }
 
    void set(Fix::CashOrderQty* value)
    { setField(value); }
    Fix::CashOrderQty* get(Fix::CashOrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashOrderQty* getCashOrderQty() throw(Fix::FieldNotFound*)
    { Fix::CashOrderQty* value = new Fix::CashOrderQty();
      getField(value); return value; }
 
    void set(Fix::OrdType* value)
    { setField(value); }
    Fix::OrdType* get(Fix::OrdType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdType* getOrdType() throw(Fix::FieldNotFound*)
    { Fix::OrdType* value = new Fix::OrdType();
      getField(value); return value; }
 
    void set(Fix::Price* value)
    { setField(value); }
    Fix::Price* get(Fix::Price* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Price* getPrice() throw(Fix::FieldNotFound*)
    { Fix::Price* value = new Fix::Price();
      getField(value); return value; }
 
    void set(Fix::StopPx* value)
    { setField(value); }
    Fix::StopPx* get(Fix::StopPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StopPx* getStopPx() throw(Fix::FieldNotFound*)
    { Fix::StopPx* value = new Fix::StopPx();
      getField(value); return value; }
 
    void set(Fix::PegDifference* value)
    { setField(value); }
    Fix::PegDifference* get(Fix::PegDifference* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PegDifference* getPegDifference() throw(Fix::FieldNotFound*)
    { Fix::PegDifference* value = new Fix::PegDifference();
      getField(value); return value; }
 
    void set(Fix::DiscretionInst* value)
    { setField(value); }
    Fix::DiscretionInst* get(Fix::DiscretionInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DiscretionInst* getDiscretionInst() throw(Fix::FieldNotFound*)
    { Fix::DiscretionInst* value = new Fix::DiscretionInst();
      getField(value); return value; }
 
    void set(Fix::DiscretionOffset* value)
    { setField(value); }
    Fix::DiscretionOffset* get(Fix::DiscretionOffset* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DiscretionOffset* getDiscretionOffset() throw(Fix::FieldNotFound*)
    { Fix::DiscretionOffset* value = new Fix::DiscretionOffset();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::ComplianceID* value)
    { setField(value); }
    Fix::ComplianceID* get(Fix::ComplianceID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ComplianceID* getComplianceID() throw(Fix::FieldNotFound*)
    { Fix::ComplianceID* value = new Fix::ComplianceID();
      getField(value); return value; }
 
    void set(Fix::SolicitedFlag* value)
    { setField(value); }
    Fix::SolicitedFlag* get(Fix::SolicitedFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SolicitedFlag* getSolicitedFlag() throw(Fix::FieldNotFound*)
    { Fix::SolicitedFlag* value = new Fix::SolicitedFlag();
      getField(value); return value; }
 
    void set(Fix::TimeInForce* value)
    { setField(value); }
    Fix::TimeInForce* get(Fix::TimeInForce* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TimeInForce* getTimeInForce() throw(Fix::FieldNotFound*)
    { Fix::TimeInForce* value = new Fix::TimeInForce();
      getField(value); return value; }
 
    void set(Fix::EffectiveTime* value)
    { setField(value); }
    Fix::EffectiveTime* get(Fix::EffectiveTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EffectiveTime* getEffectiveTime() throw(Fix::FieldNotFound*)
    { Fix::EffectiveTime* value = new Fix::EffectiveTime();
      getField(value); return value; }
 
    void set(Fix::ExpireDate* value)
    { setField(value); }
    Fix::ExpireDate* get(Fix::ExpireDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExpireDate* getExpireDate() throw(Fix::FieldNotFound*)
    { Fix::ExpireDate* value = new Fix::ExpireDate();
      getField(value); return value; }
 
    void set(Fix::ExpireTime* value)
    { setField(value); }
    Fix::ExpireTime* get(Fix::ExpireTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExpireTime* getExpireTime() throw(Fix::FieldNotFound*)
    { Fix::ExpireTime* value = new Fix::ExpireTime();
      getField(value); return value; }
 
    void set(Fix::ExecInst* value)
    { setField(value); }
    Fix::ExecInst* get(Fix::ExecInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecInst* getExecInst() throw(Fix::FieldNotFound*)
    { Fix::ExecInst* value = new Fix::ExecInst();
      getField(value); return value; }
 
    void set(Fix::Rule80A* value)
    { setField(value); }
    Fix::Rule80A* get(Fix::Rule80A* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Rule80A* getRule80A() throw(Fix::FieldNotFound*)
    { Fix::Rule80A* value = new Fix::Rule80A();
      getField(value); return value; }
 
    void set(Fix::LastShares* value)
    { setField(value); }
    Fix::LastShares* get(Fix::LastShares* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastShares* getLastShares() throw(Fix::FieldNotFound*)
    { Fix::LastShares* value = new Fix::LastShares();
      getField(value); return value; }
 
    void set(Fix::LastPx* value)
    { setField(value); }
    Fix::LastPx* get(Fix::LastPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastPx* getLastPx() throw(Fix::FieldNotFound*)
    { Fix::LastPx* value = new Fix::LastPx();
      getField(value); return value; }
 
    void set(Fix::LastSpotRate* value)
    { setField(value); }
    Fix::LastSpotRate* get(Fix::LastSpotRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastSpotRate* getLastSpotRate() throw(Fix::FieldNotFound*)
    { Fix::LastSpotRate* value = new Fix::LastSpotRate();
      getField(value); return value; }
 
    void set(Fix::LastForwardPoints* value)
    { setField(value); }
    Fix::LastForwardPoints* get(Fix::LastForwardPoints* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastForwardPoints* getLastForwardPoints() throw(Fix::FieldNotFound*)
    { Fix::LastForwardPoints* value = new Fix::LastForwardPoints();
      getField(value); return value; }
 
    void set(Fix::LastMkt* value)
    { setField(value); }
    Fix::LastMkt* get(Fix::LastMkt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastMkt* getLastMkt() throw(Fix::FieldNotFound*)
    { Fix::LastMkt* value = new Fix::LastMkt();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::LastCapacity* value)
    { setField(value); }
    Fix::LastCapacity* get(Fix::LastCapacity* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastCapacity* getLastCapacity() throw(Fix::FieldNotFound*)
    { Fix::LastCapacity* value = new Fix::LastCapacity();
      getField(value); return value; }
 
    void set(Fix::LeavesQty* value)
    { setField(value); }
    Fix::LeavesQty* get(Fix::LeavesQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LeavesQty* getLeavesQty() throw(Fix::FieldNotFound*)
    { Fix::LeavesQty* value = new Fix::LeavesQty();
      getField(value); return value; }
 
    void set(Fix::CumQty* value)
    { setField(value); }
    Fix::CumQty* get(Fix::CumQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CumQty* getCumQty() throw(Fix::FieldNotFound*)
    { Fix::CumQty* value = new Fix::CumQty();
      getField(value); return value; }
 
    void set(Fix::AvgPx* value)
    { setField(value); }
    Fix::AvgPx* get(Fix::AvgPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AvgPx* getAvgPx() throw(Fix::FieldNotFound*)
    { Fix::AvgPx* value = new Fix::AvgPx();
      getField(value); return value; }
 
    void set(Fix::DayOrderQty* value)
    { setField(value); }
    Fix::DayOrderQty* get(Fix::DayOrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DayOrderQty* getDayOrderQty() throw(Fix::FieldNotFound*)
    { Fix::DayOrderQty* value = new Fix::DayOrderQty();
      getField(value); return value; }
 
    void set(Fix::DayCumQty* value)
    { setField(value); }
    Fix::DayCumQty* get(Fix::DayCumQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DayCumQty* getDayCumQty() throw(Fix::FieldNotFound*)
    { Fix::DayCumQty* value = new Fix::DayCumQty();
      getField(value); return value; }
 
    void set(Fix::DayAvgPx* value)
    { setField(value); }
    Fix::DayAvgPx* get(Fix::DayAvgPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DayAvgPx* getDayAvgPx() throw(Fix::FieldNotFound*)
    { Fix::DayAvgPx* value = new Fix::DayAvgPx();
      getField(value); return value; }
 
    void set(Fix::GTBookingInst* value)
    { setField(value); }
    Fix::GTBookingInst* get(Fix::GTBookingInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::GTBookingInst* getGTBookingInst() throw(Fix::FieldNotFound*)
    { Fix::GTBookingInst* value = new Fix::GTBookingInst();
      getField(value); return value; }
 
    void set(Fix::TradeDate* value)
    { setField(value); }
    Fix::TradeDate* get(Fix::TradeDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradeDate* getTradeDate() throw(Fix::FieldNotFound*)
    { Fix::TradeDate* value = new Fix::TradeDate();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::ReportToExch* value)
    { setField(value); }
    Fix::ReportToExch* get(Fix::ReportToExch* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ReportToExch* getReportToExch() throw(Fix::FieldNotFound*)
    { Fix::ReportToExch* value = new Fix::ReportToExch();
      getField(value); return value; }
 
    void set(Fix::Commission* value)
    { setField(value); }
    Fix::Commission* get(Fix::Commission* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Commission* getCommission() throw(Fix::FieldNotFound*)
    { Fix::Commission* value = new Fix::Commission();
      getField(value); return value; }
 
    void set(Fix::CommType* value)
    { setField(value); }
    Fix::CommType* get(Fix::CommType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CommType* getCommType() throw(Fix::FieldNotFound*)
    { Fix::CommType* value = new Fix::CommType();
      getField(value); return value; }
 
    void set(Fix::GrossTradeAmt* value)
    { setField(value); }
    Fix::GrossTradeAmt* get(Fix::GrossTradeAmt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::GrossTradeAmt* getGrossTradeAmt() throw(Fix::FieldNotFound*)
    { Fix::GrossTradeAmt* value = new Fix::GrossTradeAmt();
      getField(value); return value; }
 
    void set(Fix::SettlCurrAmt* value)
    { setField(value); }
    Fix::SettlCurrAmt* get(Fix::SettlCurrAmt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlCurrAmt* getSettlCurrAmt() throw(Fix::FieldNotFound*)
    { Fix::SettlCurrAmt* value = new Fix::SettlCurrAmt();
      getField(value); return value; }
 
    void set(Fix::SettlCurrency* value)
    { setField(value); }
    Fix::SettlCurrency* get(Fix::SettlCurrency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlCurrency* getSettlCurrency() throw(Fix::FieldNotFound*)
    { Fix::SettlCurrency* value = new Fix::SettlCurrency();
      getField(value); return value; }
 
    void set(Fix::HandlInst* value)
    { setField(value); }
    Fix::HandlInst* get(Fix::HandlInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::HandlInst* getHandlInst() throw(Fix::FieldNotFound*)
    { Fix::HandlInst* value = new Fix::HandlInst();
      getField(value); return value; }
 
    void set(Fix::MinQty* value)
    { setField(value); }
    Fix::MinQty* get(Fix::MinQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MinQty* getMinQty() throw(Fix::FieldNotFound*)
    { Fix::MinQty* value = new Fix::MinQty();
      getField(value); return value; }
 
    void set(Fix::MaxFloor* value)
    { setField(value); }
    Fix::MaxFloor* get(Fix::MaxFloor* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxFloor* getMaxFloor() throw(Fix::FieldNotFound*)
    { Fix::MaxFloor* value = new Fix::MaxFloor();
      getField(value); return value; }
 
    void set(Fix::OpenClose* value)
    { setField(value); }
    Fix::OpenClose* get(Fix::OpenClose* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OpenClose* getOpenClose() throw(Fix::FieldNotFound*)
    { Fix::OpenClose* value = new Fix::OpenClose();
      getField(value); return value; }
 
    void set(Fix::MaxShow* value)
    { setField(value); }
    Fix::MaxShow* get(Fix::MaxShow* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxShow* getMaxShow() throw(Fix::FieldNotFound*)
    { Fix::MaxShow* value = new Fix::MaxShow();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::FutSettDate2* value)
    { setField(value); }
    Fix::FutSettDate2* get(Fix::FutSettDate2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate2* getFutSettDate2() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate2* value = new Fix::FutSettDate2();
      getField(value); return value; }
 
    void set(Fix::OrderQty2* value)
    { setField(value); }
    Fix::OrderQty2* get(Fix::OrderQty2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty2* getOrderQty2() throw(Fix::FieldNotFound*)
    { Fix::OrderQty2* value = new Fix::OrderQty2();
      getField(value); return value; }
 
    void set(Fix::ClearingFirm* value)
    { setField(value); }
    Fix::ClearingFirm* get(Fix::ClearingFirm* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClearingFirm* getClearingFirm() throw(Fix::FieldNotFound*)
    { Fix::ClearingFirm* value = new Fix::ClearingFirm();
      getField(value); return value; }
 
    void set(Fix::ClearingAccount* value)
    { setField(value); }
    Fix::ClearingAccount* get(Fix::ClearingAccount* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClearingAccount* getClearingAccount() throw(Fix::FieldNotFound*)
    { Fix::ClearingAccount* value = new Fix::ClearingAccount();
      getField(value); return value; }
 
    void set(Fix::MultiLegReportingType* value)
    { setField(value); }
    Fix::MultiLegReportingType* get(Fix::MultiLegReportingType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MultiLegReportingType* getMultiLegReportingType() throw(Fix::FieldNotFound*)
    { Fix::MultiLegReportingType* value = new Fix::MultiLegReportingType();
      getField(value); return value; }
 
  };

  public __gc class DontKnowTrade : public Message
  {
  public: 
    DontKnowTrade() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("Q"); }
    //
    DontKnowTrade(
      Fix::OrderID* aOrderID,
      Fix::ExecID* aExecID,
      Fix::DKReason* aDKReason,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide )
    : Message(MsgType())
    
    {
      set(aOrderID);
      set(aExecID);
      set(aDKReason);
      set(aSymbol);
      set(aSide);
    }
  
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::ExecID* value)
    { setField(value); }
    Fix::ExecID* get(Fix::ExecID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecID* getExecID() throw(Fix::FieldNotFound*)
    { Fix::ExecID* value = new Fix::ExecID();
      getField(value); return value; }
 
    void set(Fix::DKReason* value)
    { setField(value); }
    Fix::DKReason* get(Fix::DKReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DKReason* getDKReason() throw(Fix::FieldNotFound*)
    { Fix::DKReason* value = new Fix::DKReason();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::OrderQty* value)
    { setField(value); }
    Fix::OrderQty* get(Fix::OrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty* getOrderQty() throw(Fix::FieldNotFound*)
    { Fix::OrderQty* value = new Fix::OrderQty();
      getField(value); return value; }
 
    void set(Fix::CashOrderQty* value)
    { setField(value); }
    Fix::CashOrderQty* get(Fix::CashOrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashOrderQty* getCashOrderQty() throw(Fix::FieldNotFound*)
    { Fix::CashOrderQty* value = new Fix::CashOrderQty();
      getField(value); return value; }
 
    void set(Fix::LastShares* value)
    { setField(value); }
    Fix::LastShares* get(Fix::LastShares* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastShares* getLastShares() throw(Fix::FieldNotFound*)
    { Fix::LastShares* value = new Fix::LastShares();
      getField(value); return value; }
 
    void set(Fix::LastPx* value)
    { setField(value); }
    Fix::LastPx* get(Fix::LastPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastPx* getLastPx() throw(Fix::FieldNotFound*)
    { Fix::LastPx* value = new Fix::LastPx();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class OrderCancelReplaceRequest : public Message
  {
  public: 
    OrderCancelReplaceRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("G"); }
    //
    OrderCancelReplaceRequest(
      Fix::OrigClOrdID* aOrigClOrdID,
      Fix::ClOrdID* aClOrdID,
      Fix::HandlInst* aHandlInst,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide,
      Fix::TransactTime* aTransactTime,
      Fix::OrdType* aOrdType )
    : Message(MsgType())
    
    {
      set(aOrigClOrdID);
      set(aClOrdID);
      set(aHandlInst);
      set(aSymbol);
      set(aSide);
      set(aTransactTime);
      set(aOrdType);
    }
  
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::OrigClOrdID* value)
    { setField(value); }
    Fix::OrigClOrdID* get(Fix::OrigClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigClOrdID* getOrigClOrdID() throw(Fix::FieldNotFound*)
    { Fix::OrigClOrdID* value = new Fix::OrigClOrdID();
      getField(value); return value; }
 
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::Account* value)
    { setField(value); }
    Fix::Account* get(Fix::Account* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Account* getAccount() throw(Fix::FieldNotFound*)
    { Fix::Account* value = new Fix::Account();
      getField(value); return value; }
 
    void set(Fix::NoAllocs* value)
    { setField(value); }
    Fix::NoAllocs* get(Fix::NoAllocs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoAllocs* getNoAllocs() throw(Fix::FieldNotFound*)
    { Fix::NoAllocs* value = new Fix::NoAllocs();
      getField(value); return value; }
 
    void set(Fix::SettlmntTyp* value)
    { setField(value); }
    Fix::SettlmntTyp* get(Fix::SettlmntTyp* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlmntTyp* getSettlmntTyp() throw(Fix::FieldNotFound*)
    { Fix::SettlmntTyp* value = new Fix::SettlmntTyp();
      getField(value); return value; }
 
    void set(Fix::FutSettDate* value)
    { setField(value); }
    Fix::FutSettDate* get(Fix::FutSettDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate* getFutSettDate() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate* value = new Fix::FutSettDate();
      getField(value); return value; }
 
    void set(Fix::HandlInst* value)
    { setField(value); }
    Fix::HandlInst* get(Fix::HandlInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::HandlInst* getHandlInst() throw(Fix::FieldNotFound*)
    { Fix::HandlInst* value = new Fix::HandlInst();
      getField(value); return value; }
 
    void set(Fix::ExecInst* value)
    { setField(value); }
    Fix::ExecInst* get(Fix::ExecInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecInst* getExecInst() throw(Fix::FieldNotFound*)
    { Fix::ExecInst* value = new Fix::ExecInst();
      getField(value); return value; }
 
    void set(Fix::MinQty* value)
    { setField(value); }
    Fix::MinQty* get(Fix::MinQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MinQty* getMinQty() throw(Fix::FieldNotFound*)
    { Fix::MinQty* value = new Fix::MinQty();
      getField(value); return value; }
 
    void set(Fix::MaxFloor* value)
    { setField(value); }
    Fix::MaxFloor* get(Fix::MaxFloor* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxFloor* getMaxFloor() throw(Fix::FieldNotFound*)
    { Fix::MaxFloor* value = new Fix::MaxFloor();
      getField(value); return value; }
 
    void set(Fix::ExDestination* value)
    { setField(value); }
    Fix::ExDestination* get(Fix::ExDestination* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExDestination* getExDestination() throw(Fix::FieldNotFound*)
    { Fix::ExDestination* value = new Fix::ExDestination();
      getField(value); return value; }
 
    void set(Fix::NoTradingSessions* value)
    { setField(value); }
    Fix::NoTradingSessions* get(Fix::NoTradingSessions* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoTradingSessions* getNoTradingSessions() throw(Fix::FieldNotFound*)
    { Fix::NoTradingSessions* value = new Fix::NoTradingSessions();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::OrderQty* value)
    { setField(value); }
    Fix::OrderQty* get(Fix::OrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty* getOrderQty() throw(Fix::FieldNotFound*)
    { Fix::OrderQty* value = new Fix::OrderQty();
      getField(value); return value; }
 
    void set(Fix::CashOrderQty* value)
    { setField(value); }
    Fix::CashOrderQty* get(Fix::CashOrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashOrderQty* getCashOrderQty() throw(Fix::FieldNotFound*)
    { Fix::CashOrderQty* value = new Fix::CashOrderQty();
      getField(value); return value; }
 
    void set(Fix::OrdType* value)
    { setField(value); }
    Fix::OrdType* get(Fix::OrdType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdType* getOrdType() throw(Fix::FieldNotFound*)
    { Fix::OrdType* value = new Fix::OrdType();
      getField(value); return value; }
 
    void set(Fix::Price* value)
    { setField(value); }
    Fix::Price* get(Fix::Price* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Price* getPrice() throw(Fix::FieldNotFound*)
    { Fix::Price* value = new Fix::Price();
      getField(value); return value; }
 
    void set(Fix::StopPx* value)
    { setField(value); }
    Fix::StopPx* get(Fix::StopPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StopPx* getStopPx() throw(Fix::FieldNotFound*)
    { Fix::StopPx* value = new Fix::StopPx();
      getField(value); return value; }
 
    void set(Fix::PegDifference* value)
    { setField(value); }
    Fix::PegDifference* get(Fix::PegDifference* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PegDifference* getPegDifference() throw(Fix::FieldNotFound*)
    { Fix::PegDifference* value = new Fix::PegDifference();
      getField(value); return value; }
 
    void set(Fix::DiscretionInst* value)
    { setField(value); }
    Fix::DiscretionInst* get(Fix::DiscretionInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DiscretionInst* getDiscretionInst() throw(Fix::FieldNotFound*)
    { Fix::DiscretionInst* value = new Fix::DiscretionInst();
      getField(value); return value; }
 
    void set(Fix::DiscretionOffset* value)
    { setField(value); }
    Fix::DiscretionOffset* get(Fix::DiscretionOffset* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::DiscretionOffset* getDiscretionOffset() throw(Fix::FieldNotFound*)
    { Fix::DiscretionOffset* value = new Fix::DiscretionOffset();
      getField(value); return value; }
 
    void set(Fix::ComplianceID* value)
    { setField(value); }
    Fix::ComplianceID* get(Fix::ComplianceID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ComplianceID* getComplianceID() throw(Fix::FieldNotFound*)
    { Fix::ComplianceID* value = new Fix::ComplianceID();
      getField(value); return value; }
 
    void set(Fix::SolicitedFlag* value)
    { setField(value); }
    Fix::SolicitedFlag* get(Fix::SolicitedFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SolicitedFlag* getSolicitedFlag() throw(Fix::FieldNotFound*)
    { Fix::SolicitedFlag* value = new Fix::SolicitedFlag();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::TimeInForce* value)
    { setField(value); }
    Fix::TimeInForce* get(Fix::TimeInForce* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TimeInForce* getTimeInForce() throw(Fix::FieldNotFound*)
    { Fix::TimeInForce* value = new Fix::TimeInForce();
      getField(value); return value; }
 
    void set(Fix::EffectiveTime* value)
    { setField(value); }
    Fix::EffectiveTime* get(Fix::EffectiveTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EffectiveTime* getEffectiveTime() throw(Fix::FieldNotFound*)
    { Fix::EffectiveTime* value = new Fix::EffectiveTime();
      getField(value); return value; }
 
    void set(Fix::ExpireDate* value)
    { setField(value); }
    Fix::ExpireDate* get(Fix::ExpireDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExpireDate* getExpireDate() throw(Fix::FieldNotFound*)
    { Fix::ExpireDate* value = new Fix::ExpireDate();
      getField(value); return value; }
 
    void set(Fix::ExpireTime* value)
    { setField(value); }
    Fix::ExpireTime* get(Fix::ExpireTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExpireTime* getExpireTime() throw(Fix::FieldNotFound*)
    { Fix::ExpireTime* value = new Fix::ExpireTime();
      getField(value); return value; }
 
    void set(Fix::GTBookingInst* value)
    { setField(value); }
    Fix::GTBookingInst* get(Fix::GTBookingInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::GTBookingInst* getGTBookingInst() throw(Fix::FieldNotFound*)
    { Fix::GTBookingInst* value = new Fix::GTBookingInst();
      getField(value); return value; }
 
    void set(Fix::Commission* value)
    { setField(value); }
    Fix::Commission* get(Fix::Commission* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Commission* getCommission() throw(Fix::FieldNotFound*)
    { Fix::Commission* value = new Fix::Commission();
      getField(value); return value; }
 
    void set(Fix::CommType* value)
    { setField(value); }
    Fix::CommType* get(Fix::CommType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CommType* getCommType() throw(Fix::FieldNotFound*)
    { Fix::CommType* value = new Fix::CommType();
      getField(value); return value; }
 
    void set(Fix::Rule80A* value)
    { setField(value); }
    Fix::Rule80A* get(Fix::Rule80A* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Rule80A* getRule80A() throw(Fix::FieldNotFound*)
    { Fix::Rule80A* value = new Fix::Rule80A();
      getField(value); return value; }
 
    void set(Fix::ForexReq* value)
    { setField(value); }
    Fix::ForexReq* get(Fix::ForexReq* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ForexReq* getForexReq() throw(Fix::FieldNotFound*)
    { Fix::ForexReq* value = new Fix::ForexReq();
      getField(value); return value; }
 
    void set(Fix::SettlCurrency* value)
    { setField(value); }
    Fix::SettlCurrency* get(Fix::SettlCurrency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlCurrency* getSettlCurrency() throw(Fix::FieldNotFound*)
    { Fix::SettlCurrency* value = new Fix::SettlCurrency();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::FutSettDate2* value)
    { setField(value); }
    Fix::FutSettDate2* get(Fix::FutSettDate2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate2* getFutSettDate2() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate2* value = new Fix::FutSettDate2();
      getField(value); return value; }
 
    void set(Fix::OrderQty2* value)
    { setField(value); }
    Fix::OrderQty2* get(Fix::OrderQty2* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty2* getOrderQty2() throw(Fix::FieldNotFound*)
    { Fix::OrderQty2* value = new Fix::OrderQty2();
      getField(value); return value; }
 
    void set(Fix::OpenClose* value)
    { setField(value); }
    Fix::OpenClose* get(Fix::OpenClose* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OpenClose* getOpenClose() throw(Fix::FieldNotFound*)
    { Fix::OpenClose* value = new Fix::OpenClose();
      getField(value); return value; }
 
    void set(Fix::CoveredOrUncovered* value)
    { setField(value); }
    Fix::CoveredOrUncovered* get(Fix::CoveredOrUncovered* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CoveredOrUncovered* getCoveredOrUncovered() throw(Fix::FieldNotFound*)
    { Fix::CoveredOrUncovered* value = new Fix::CoveredOrUncovered();
      getField(value); return value; }
 
    void set(Fix::CustomerOrFirm* value)
    { setField(value); }
    Fix::CustomerOrFirm* get(Fix::CustomerOrFirm* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CustomerOrFirm* getCustomerOrFirm() throw(Fix::FieldNotFound*)
    { Fix::CustomerOrFirm* value = new Fix::CustomerOrFirm();
      getField(value); return value; }
 
    void set(Fix::MaxShow* value)
    { setField(value); }
    Fix::MaxShow* get(Fix::MaxShow* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaxShow* getMaxShow() throw(Fix::FieldNotFound*)
    { Fix::MaxShow* value = new Fix::MaxShow();
      getField(value); return value; }
 
    void set(Fix::LocateReqd* value)
    { setField(value); }
    Fix::LocateReqd* get(Fix::LocateReqd* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LocateReqd* getLocateReqd() throw(Fix::FieldNotFound*)
    { Fix::LocateReqd* value = new Fix::LocateReqd();
      getField(value); return value; }
 
    void set(Fix::ClearingFirm* value)
    { setField(value); }
    Fix::ClearingFirm* get(Fix::ClearingFirm* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClearingFirm* getClearingFirm() throw(Fix::FieldNotFound*)
    { Fix::ClearingFirm* value = new Fix::ClearingFirm();
      getField(value); return value; }
 
    void set(Fix::ClearingAccount* value)
    { setField(value); }
    Fix::ClearingAccount* get(Fix::ClearingAccount* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClearingAccount* getClearingAccount() throw(Fix::FieldNotFound*)
    { Fix::ClearingAccount* value = new Fix::ClearingAccount();
      getField(value); return value; }
 
  };

  public __gc class OrderCancelRequest : public Message
  {
  public: 
    OrderCancelRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("F"); }
    //
    OrderCancelRequest(
      Fix::OrigClOrdID* aOrigClOrdID,
      Fix::ClOrdID* aClOrdID,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide,
      Fix::TransactTime* aTransactTime )
    : Message(MsgType())
    
    {
      set(aOrigClOrdID);
      set(aClOrdID);
      set(aSymbol);
      set(aSide);
      set(aTransactTime);
    }
  
    void set(Fix::OrigClOrdID* value)
    { setField(value); }
    Fix::OrigClOrdID* get(Fix::OrigClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigClOrdID* getOrigClOrdID() throw(Fix::FieldNotFound*)
    { Fix::OrigClOrdID* value = new Fix::OrigClOrdID();
      getField(value); return value; }
 
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::Account* value)
    { setField(value); }
    Fix::Account* get(Fix::Account* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Account* getAccount() throw(Fix::FieldNotFound*)
    { Fix::Account* value = new Fix::Account();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::OrderQty* value)
    { setField(value); }
    Fix::OrderQty* get(Fix::OrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderQty* getOrderQty() throw(Fix::FieldNotFound*)
    { Fix::OrderQty* value = new Fix::OrderQty();
      getField(value); return value; }
 
    void set(Fix::CashOrderQty* value)
    { setField(value); }
    Fix::CashOrderQty* get(Fix::CashOrderQty* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashOrderQty* getCashOrderQty() throw(Fix::FieldNotFound*)
    { Fix::CashOrderQty* value = new Fix::CashOrderQty();
      getField(value); return value; }
 
    void set(Fix::ComplianceID* value)
    { setField(value); }
    Fix::ComplianceID* get(Fix::ComplianceID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ComplianceID* getComplianceID() throw(Fix::FieldNotFound*)
    { Fix::ComplianceID* value = new Fix::ComplianceID();
      getField(value); return value; }
 
    void set(Fix::SolicitedFlag* value)
    { setField(value); }
    Fix::SolicitedFlag* get(Fix::SolicitedFlag* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SolicitedFlag* getSolicitedFlag() throw(Fix::FieldNotFound*)
    { Fix::SolicitedFlag* value = new Fix::SolicitedFlag();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class OrderCancelReject : public Message
  {
  public: 
    OrderCancelReject() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("9"); }
    //
    OrderCancelReject(
      Fix::OrderID* aOrderID,
      Fix::ClOrdID* aClOrdID,
      Fix::OrigClOrdID* aOrigClOrdID,
      Fix::OrdStatus* aOrdStatus,
      Fix::CxlRejResponseTo* aCxlRejResponseTo )
    : Message(MsgType())
    
    {
      set(aOrderID);
      set(aClOrdID);
      set(aOrigClOrdID);
      set(aOrdStatus);
      set(aCxlRejResponseTo);
    }
  
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::SecondaryOrderID* value)
    { setField(value); }
    Fix::SecondaryOrderID* get(Fix::SecondaryOrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecondaryOrderID* getSecondaryOrderID() throw(Fix::FieldNotFound*)
    { Fix::SecondaryOrderID* value = new Fix::SecondaryOrderID();
      getField(value); return value; }
 
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::OrigClOrdID* value)
    { setField(value); }
    Fix::OrigClOrdID* get(Fix::OrigClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrigClOrdID* getOrigClOrdID() throw(Fix::FieldNotFound*)
    { Fix::OrigClOrdID* value = new Fix::OrigClOrdID();
      getField(value); return value; }
 
    void set(Fix::OrdStatus* value)
    { setField(value); }
    Fix::OrdStatus* get(Fix::OrdStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrdStatus* getOrdStatus() throw(Fix::FieldNotFound*)
    { Fix::OrdStatus* value = new Fix::OrdStatus();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::Account* value)
    { setField(value); }
    Fix::Account* get(Fix::Account* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Account* getAccount() throw(Fix::FieldNotFound*)
    { Fix::Account* value = new Fix::Account();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::CxlRejResponseTo* value)
    { setField(value); }
    Fix::CxlRejResponseTo* get(Fix::CxlRejResponseTo* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CxlRejResponseTo* getCxlRejResponseTo() throw(Fix::FieldNotFound*)
    { Fix::CxlRejResponseTo* value = new Fix::CxlRejResponseTo();
      getField(value); return value; }
 
    void set(Fix::CxlRejReason* value)
    { setField(value); }
    Fix::CxlRejReason* get(Fix::CxlRejReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CxlRejReason* getCxlRejReason() throw(Fix::FieldNotFound*)
    { Fix::CxlRejReason* value = new Fix::CxlRejReason();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class OrderStatusRequest : public Message
  {
  public: 
    OrderStatusRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("H"); }
    //
    OrderStatusRequest(
      Fix::ClOrdID* aClOrdID,
      Fix::Symbol* aSymbol,
      Fix::Side* aSide )
    : Message(MsgType())
    
    {
      set(aClOrdID);
      set(aSymbol);
      set(aSide);
    }
  
    void set(Fix::OrderID* value)
    { setField(value); }
    Fix::OrderID* get(Fix::OrderID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OrderID* getOrderID() throw(Fix::FieldNotFound*)
    { Fix::OrderID* value = new Fix::OrderID();
      getField(value); return value; }
 
    void set(Fix::ClOrdID* value)
    { setField(value); }
    Fix::ClOrdID* get(Fix::ClOrdID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClOrdID* getClOrdID() throw(Fix::FieldNotFound*)
    { Fix::ClOrdID* value = new Fix::ClOrdID();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::Account* value)
    { setField(value); }
    Fix::Account* get(Fix::Account* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Account* getAccount() throw(Fix::FieldNotFound*)
    { Fix::Account* value = new Fix::Account();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
  };

  public __gc class Allocation : public Message
  {
  public: 
    Allocation() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("J"); }
    //
    Allocation(
      Fix::AllocID* aAllocID,
      Fix::AllocTransType* aAllocTransType,
      Fix::NoOrders* aNoOrders,
      Fix::Side* aSide,
      Fix::Symbol* aSymbol,
      Fix::Shares* aShares,
      Fix::AvgPx* aAvgPx,
      Fix::TradeDate* aTradeDate,
      Fix::NoAllocs* aNoAllocs )
    : Message(MsgType())
    
    {
      set(aAllocID);
      set(aAllocTransType);
      set(aNoOrders);
      set(aSide);
      set(aSymbol);
      set(aShares);
      set(aAvgPx);
      set(aTradeDate);
      set(aNoAllocs);
    }
  
    void set(Fix::AllocID* value)
    { setField(value); }
    Fix::AllocID* get(Fix::AllocID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocID* getAllocID() throw(Fix::FieldNotFound*)
    { Fix::AllocID* value = new Fix::AllocID();
      getField(value); return value; }
 
    void set(Fix::AllocTransType* value)
    { setField(value); }
    Fix::AllocTransType* get(Fix::AllocTransType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocTransType* getAllocTransType() throw(Fix::FieldNotFound*)
    { Fix::AllocTransType* value = new Fix::AllocTransType();
      getField(value); return value; }
 
    void set(Fix::RefAllocID* value)
    { setField(value); }
    Fix::RefAllocID* get(Fix::RefAllocID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RefAllocID* getRefAllocID() throw(Fix::FieldNotFound*)
    { Fix::RefAllocID* value = new Fix::RefAllocID();
      getField(value); return value; }
 
    void set(Fix::AllocLinkID* value)
    { setField(value); }
    Fix::AllocLinkID* get(Fix::AllocLinkID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocLinkID* getAllocLinkID() throw(Fix::FieldNotFound*)
    { Fix::AllocLinkID* value = new Fix::AllocLinkID();
      getField(value); return value; }
 
    void set(Fix::AllocLinkType* value)
    { setField(value); }
    Fix::AllocLinkType* get(Fix::AllocLinkType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocLinkType* getAllocLinkType() throw(Fix::FieldNotFound*)
    { Fix::AllocLinkType* value = new Fix::AllocLinkType();
      getField(value); return value; }
 
    void set(Fix::NoOrders* value)
    { setField(value); }
    Fix::NoOrders* get(Fix::NoOrders* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoOrders* getNoOrders() throw(Fix::FieldNotFound*)
    { Fix::NoOrders* value = new Fix::NoOrders();
      getField(value); return value; }
 
    void set(Fix::NoExecs* value)
    { setField(value); }
    Fix::NoExecs* get(Fix::NoExecs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoExecs* getNoExecs() throw(Fix::FieldNotFound*)
    { Fix::NoExecs* value = new Fix::NoExecs();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::Symbol* value)
    { setField(value); }
    Fix::Symbol* get(Fix::Symbol* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Symbol* getSymbol() throw(Fix::FieldNotFound*)
    { Fix::Symbol* value = new Fix::Symbol();
      getField(value); return value; }
 
    void set(Fix::SymbolSfx* value)
    { setField(value); }
    Fix::SymbolSfx* get(Fix::SymbolSfx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SymbolSfx* getSymbolSfx() throw(Fix::FieldNotFound*)
    { Fix::SymbolSfx* value = new Fix::SymbolSfx();
      getField(value); return value; }
 
    void set(Fix::SecurityID* value)
    { setField(value); }
    Fix::SecurityID* get(Fix::SecurityID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityID* getSecurityID() throw(Fix::FieldNotFound*)
    { Fix::SecurityID* value = new Fix::SecurityID();
      getField(value); return value; }
 
    void set(Fix::IDSource* value)
    { setField(value); }
    Fix::IDSource* get(Fix::IDSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::IDSource* getIDSource() throw(Fix::FieldNotFound*)
    { Fix::IDSource* value = new Fix::IDSource();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::MaturityMonthYear* value)
    { setField(value); }
    Fix::MaturityMonthYear* get(Fix::MaturityMonthYear* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityMonthYear* getMaturityMonthYear() throw(Fix::FieldNotFound*)
    { Fix::MaturityMonthYear* value = new Fix::MaturityMonthYear();
      getField(value); return value; }
 
    void set(Fix::MaturityDay* value)
    { setField(value); }
    Fix::MaturityDay* get(Fix::MaturityDay* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::MaturityDay* getMaturityDay() throw(Fix::FieldNotFound*)
    { Fix::MaturityDay* value = new Fix::MaturityDay();
      getField(value); return value; }
 
    void set(Fix::PutOrCall* value)
    { setField(value); }
    Fix::PutOrCall* get(Fix::PutOrCall* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::PutOrCall* getPutOrCall() throw(Fix::FieldNotFound*)
    { Fix::PutOrCall* value = new Fix::PutOrCall();
      getField(value); return value; }
 
    void set(Fix::StrikePrice* value)
    { setField(value); }
    Fix::StrikePrice* get(Fix::StrikePrice* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StrikePrice* getStrikePrice() throw(Fix::FieldNotFound*)
    { Fix::StrikePrice* value = new Fix::StrikePrice();
      getField(value); return value; }
 
    void set(Fix::OptAttribute* value)
    { setField(value); }
    Fix::OptAttribute* get(Fix::OptAttribute* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OptAttribute* getOptAttribute() throw(Fix::FieldNotFound*)
    { Fix::OptAttribute* value = new Fix::OptAttribute();
      getField(value); return value; }
 
    void set(Fix::ContractMultiplier* value)
    { setField(value); }
    Fix::ContractMultiplier* get(Fix::ContractMultiplier* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ContractMultiplier* getContractMultiplier() throw(Fix::FieldNotFound*)
    { Fix::ContractMultiplier* value = new Fix::ContractMultiplier();
      getField(value); return value; }
 
    void set(Fix::CouponRate* value)
    { setField(value); }
    Fix::CouponRate* get(Fix::CouponRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CouponRate* getCouponRate() throw(Fix::FieldNotFound*)
    { Fix::CouponRate* value = new Fix::CouponRate();
      getField(value); return value; }
 
    void set(Fix::SecurityExchange* value)
    { setField(value); }
    Fix::SecurityExchange* get(Fix::SecurityExchange* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityExchange* getSecurityExchange() throw(Fix::FieldNotFound*)
    { Fix::SecurityExchange* value = new Fix::SecurityExchange();
      getField(value); return value; }
 
    void set(Fix::Issuer* value)
    { setField(value); }
    Fix::Issuer* get(Fix::Issuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Issuer* getIssuer() throw(Fix::FieldNotFound*)
    { Fix::Issuer* value = new Fix::Issuer();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuerLen* value)
    { setField(value); }
    Fix::EncodedIssuerLen* get(Fix::EncodedIssuerLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuerLen* getEncodedIssuerLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuerLen* value = new Fix::EncodedIssuerLen();
      getField(value); return value; }
 
    void set(Fix::EncodedIssuer* value)
    { setField(value); }
    Fix::EncodedIssuer* get(Fix::EncodedIssuer* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedIssuer* getEncodedIssuer() throw(Fix::FieldNotFound*)
    { Fix::EncodedIssuer* value = new Fix::EncodedIssuer();
      getField(value); return value; }
 
    void set(Fix::SecurityDesc* value)
    { setField(value); }
    Fix::SecurityDesc* get(Fix::SecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityDesc* getSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::SecurityDesc* value = new Fix::SecurityDesc();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDescLen* value)
    { setField(value); }
    Fix::EncodedSecurityDescLen* get(Fix::EncodedSecurityDescLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDescLen* getEncodedSecurityDescLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDescLen* value = new Fix::EncodedSecurityDescLen();
      getField(value); return value; }
 
    void set(Fix::EncodedSecurityDesc* value)
    { setField(value); }
    Fix::EncodedSecurityDesc* get(Fix::EncodedSecurityDesc* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedSecurityDesc* getEncodedSecurityDesc() throw(Fix::FieldNotFound*)
    { Fix::EncodedSecurityDesc* value = new Fix::EncodedSecurityDesc();
      getField(value); return value; }
 
    void set(Fix::Shares* value)
    { setField(value); }
    Fix::Shares* get(Fix::Shares* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Shares* getShares() throw(Fix::FieldNotFound*)
    { Fix::Shares* value = new Fix::Shares();
      getField(value); return value; }
 
    void set(Fix::LastMkt* value)
    { setField(value); }
    Fix::LastMkt* get(Fix::LastMkt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastMkt* getLastMkt() throw(Fix::FieldNotFound*)
    { Fix::LastMkt* value = new Fix::LastMkt();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::AvgPx* value)
    { setField(value); }
    Fix::AvgPx* get(Fix::AvgPx* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AvgPx* getAvgPx() throw(Fix::FieldNotFound*)
    { Fix::AvgPx* value = new Fix::AvgPx();
      getField(value); return value; }
 
    void set(Fix::Currency* value)
    { setField(value); }
    Fix::Currency* get(Fix::Currency* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Currency* getCurrency() throw(Fix::FieldNotFound*)
    { Fix::Currency* value = new Fix::Currency();
      getField(value); return value; }
 
    void set(Fix::AvgPrxPrecision* value)
    { setField(value); }
    Fix::AvgPrxPrecision* get(Fix::AvgPrxPrecision* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AvgPrxPrecision* getAvgPrxPrecision() throw(Fix::FieldNotFound*)
    { Fix::AvgPrxPrecision* value = new Fix::AvgPrxPrecision();
      getField(value); return value; }
 
    void set(Fix::TradeDate* value)
    { setField(value); }
    Fix::TradeDate* get(Fix::TradeDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradeDate* getTradeDate() throw(Fix::FieldNotFound*)
    { Fix::TradeDate* value = new Fix::TradeDate();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::SettlmntTyp* value)
    { setField(value); }
    Fix::SettlmntTyp* get(Fix::SettlmntTyp* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlmntTyp* getSettlmntTyp() throw(Fix::FieldNotFound*)
    { Fix::SettlmntTyp* value = new Fix::SettlmntTyp();
      getField(value); return value; }
 
    void set(Fix::FutSettDate* value)
    { setField(value); }
    Fix::FutSettDate* get(Fix::FutSettDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::FutSettDate* getFutSettDate() throw(Fix::FieldNotFound*)
    { Fix::FutSettDate* value = new Fix::FutSettDate();
      getField(value); return value; }
 
    void set(Fix::GrossTradeAmt* value)
    { setField(value); }
    Fix::GrossTradeAmt* get(Fix::GrossTradeAmt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::GrossTradeAmt* getGrossTradeAmt() throw(Fix::FieldNotFound*)
    { Fix::GrossTradeAmt* value = new Fix::GrossTradeAmt();
      getField(value); return value; }
 
    void set(Fix::NetMoney* value)
    { setField(value); }
    Fix::NetMoney* get(Fix::NetMoney* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NetMoney* getNetMoney() throw(Fix::FieldNotFound*)
    { Fix::NetMoney* value = new Fix::NetMoney();
      getField(value); return value; }
 
    void set(Fix::OpenClose* value)
    { setField(value); }
    Fix::OpenClose* get(Fix::OpenClose* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::OpenClose* getOpenClose() throw(Fix::FieldNotFound*)
    { Fix::OpenClose* value = new Fix::OpenClose();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
    void set(Fix::NumDaysInterest* value)
    { setField(value); }
    Fix::NumDaysInterest* get(Fix::NumDaysInterest* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NumDaysInterest* getNumDaysInterest() throw(Fix::FieldNotFound*)
    { Fix::NumDaysInterest* value = new Fix::NumDaysInterest();
      getField(value); return value; }
 
    void set(Fix::AccruedInterestRate* value)
    { setField(value); }
    Fix::AccruedInterestRate* get(Fix::AccruedInterestRate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AccruedInterestRate* getAccruedInterestRate() throw(Fix::FieldNotFound*)
    { Fix::AccruedInterestRate* value = new Fix::AccruedInterestRate();
      getField(value); return value; }
 
    void set(Fix::NoAllocs* value)
    { setField(value); }
    Fix::NoAllocs* get(Fix::NoAllocs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoAllocs* getNoAllocs() throw(Fix::FieldNotFound*)
    { Fix::NoAllocs* value = new Fix::NoAllocs();
      getField(value); return value; }
 
  };

  public __gc class AllocationACK : public Message
  {
  public: 
    AllocationACK() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("P"); }
    //
    AllocationACK(
      Fix::AllocID* aAllocID,
      Fix::TradeDate* aTradeDate,
      Fix::AllocStatus* aAllocStatus )
    : Message(MsgType())
    
    {
      set(aAllocID);
      set(aTradeDate);
      set(aAllocStatus);
    }
  
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::AllocID* value)
    { setField(value); }
    Fix::AllocID* get(Fix::AllocID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocID* getAllocID() throw(Fix::FieldNotFound*)
    { Fix::AllocID* value = new Fix::AllocID();
      getField(value); return value; }
 
    void set(Fix::TradeDate* value)
    { setField(value); }
    Fix::TradeDate* get(Fix::TradeDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradeDate* getTradeDate() throw(Fix::FieldNotFound*)
    { Fix::TradeDate* value = new Fix::TradeDate();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::AllocStatus* value)
    { setField(value); }
    Fix::AllocStatus* get(Fix::AllocStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocStatus* getAllocStatus() throw(Fix::FieldNotFound*)
    { Fix::AllocStatus* value = new Fix::AllocStatus();
      getField(value); return value; }
 
    void set(Fix::AllocRejCode* value)
    { setField(value); }
    Fix::AllocRejCode* get(Fix::AllocRejCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocRejCode* getAllocRejCode() throw(Fix::FieldNotFound*)
    { Fix::AllocRejCode* value = new Fix::AllocRejCode();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class SettlementInstructions : public Message
  {
  public: 
    SettlementInstructions() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("T"); }
    //
    SettlementInstructions(
      Fix::SettlInstID* aSettlInstID,
      Fix::SettlInstTransType* aSettlInstTransType,
      Fix::SettlInstRefID* aSettlInstRefID,
      Fix::SettlInstMode* aSettlInstMode,
      Fix::SettlInstSource* aSettlInstSource,
      Fix::AllocAccount* aAllocAccount,
      Fix::TransactTime* aTransactTime )
    : Message(MsgType())
    
    {
      set(aSettlInstID);
      set(aSettlInstTransType);
      set(aSettlInstRefID);
      set(aSettlInstMode);
      set(aSettlInstSource);
      set(aAllocAccount);
      set(aTransactTime);
    }
  
    void set(Fix::SettlInstID* value)
    { setField(value); }
    Fix::SettlInstID* get(Fix::SettlInstID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlInstID* getSettlInstID() throw(Fix::FieldNotFound*)
    { Fix::SettlInstID* value = new Fix::SettlInstID();
      getField(value); return value; }
 
    void set(Fix::SettlInstTransType* value)
    { setField(value); }
    Fix::SettlInstTransType* get(Fix::SettlInstTransType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlInstTransType* getSettlInstTransType() throw(Fix::FieldNotFound*)
    { Fix::SettlInstTransType* value = new Fix::SettlInstTransType();
      getField(value); return value; }
 
    void set(Fix::SettlInstRefID* value)
    { setField(value); }
    Fix::SettlInstRefID* get(Fix::SettlInstRefID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlInstRefID* getSettlInstRefID() throw(Fix::FieldNotFound*)
    { Fix::SettlInstRefID* value = new Fix::SettlInstRefID();
      getField(value); return value; }
 
    void set(Fix::SettlInstMode* value)
    { setField(value); }
    Fix::SettlInstMode* get(Fix::SettlInstMode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlInstMode* getSettlInstMode() throw(Fix::FieldNotFound*)
    { Fix::SettlInstMode* value = new Fix::SettlInstMode();
      getField(value); return value; }
 
    void set(Fix::SettlInstSource* value)
    { setField(value); }
    Fix::SettlInstSource* get(Fix::SettlInstSource* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlInstSource* getSettlInstSource() throw(Fix::FieldNotFound*)
    { Fix::SettlInstSource* value = new Fix::SettlInstSource();
      getField(value); return value; }
 
    void set(Fix::AllocAccount* value)
    { setField(value); }
    Fix::AllocAccount* get(Fix::AllocAccount* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocAccount* getAllocAccount() throw(Fix::FieldNotFound*)
    { Fix::AllocAccount* value = new Fix::AllocAccount();
      getField(value); return value; }
 
    void set(Fix::SettlLocation* value)
    { setField(value); }
    Fix::SettlLocation* get(Fix::SettlLocation* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlLocation* getSettlLocation() throw(Fix::FieldNotFound*)
    { Fix::SettlLocation* value = new Fix::SettlLocation();
      getField(value); return value; }
 
    void set(Fix::TradeDate* value)
    { setField(value); }
    Fix::TradeDate* get(Fix::TradeDate* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradeDate* getTradeDate() throw(Fix::FieldNotFound*)
    { Fix::TradeDate* value = new Fix::TradeDate();
      getField(value); return value; }
 
    void set(Fix::AllocID* value)
    { setField(value); }
    Fix::AllocID* get(Fix::AllocID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::AllocID* getAllocID() throw(Fix::FieldNotFound*)
    { Fix::AllocID* value = new Fix::AllocID();
      getField(value); return value; }
 
    void set(Fix::LastMkt* value)
    { setField(value); }
    Fix::LastMkt* get(Fix::LastMkt* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::LastMkt* getLastMkt() throw(Fix::FieldNotFound*)
    { Fix::LastMkt* value = new Fix::LastMkt();
      getField(value); return value; }
 
    void set(Fix::TradingSessionID* value)
    { setField(value); }
    Fix::TradingSessionID* get(Fix::TradingSessionID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TradingSessionID* getTradingSessionID() throw(Fix::FieldNotFound*)
    { Fix::TradingSessionID* value = new Fix::TradingSessionID();
      getField(value); return value; }
 
    void set(Fix::Side* value)
    { setField(value); }
    Fix::Side* get(Fix::Side* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Side* getSide() throw(Fix::FieldNotFound*)
    { Fix::Side* value = new Fix::Side();
      getField(value); return value; }
 
    void set(Fix::SecurityType* value)
    { setField(value); }
    Fix::SecurityType* get(Fix::SecurityType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecurityType* getSecurityType() throw(Fix::FieldNotFound*)
    { Fix::SecurityType* value = new Fix::SecurityType();
      getField(value); return value; }
 
    void set(Fix::EffectiveTime* value)
    { setField(value); }
    Fix::EffectiveTime* get(Fix::EffectiveTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EffectiveTime* getEffectiveTime() throw(Fix::FieldNotFound*)
    { Fix::EffectiveTime* value = new Fix::EffectiveTime();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::ClientID* value)
    { setField(value); }
    Fix::ClientID* get(Fix::ClientID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientID* getClientID() throw(Fix::FieldNotFound*)
    { Fix::ClientID* value = new Fix::ClientID();
      getField(value); return value; }
 
    void set(Fix::ExecBroker* value)
    { setField(value); }
    Fix::ExecBroker* get(Fix::ExecBroker* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ExecBroker* getExecBroker() throw(Fix::FieldNotFound*)
    { Fix::ExecBroker* value = new Fix::ExecBroker();
      getField(value); return value; }
 
    void set(Fix::StandInstDbType* value)
    { setField(value); }
    Fix::StandInstDbType* get(Fix::StandInstDbType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StandInstDbType* getStandInstDbType() throw(Fix::FieldNotFound*)
    { Fix::StandInstDbType* value = new Fix::StandInstDbType();
      getField(value); return value; }
 
    void set(Fix::StandInstDbName* value)
    { setField(value); }
    Fix::StandInstDbName* get(Fix::StandInstDbName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StandInstDbName* getStandInstDbName() throw(Fix::FieldNotFound*)
    { Fix::StandInstDbName* value = new Fix::StandInstDbName();
      getField(value); return value; }
 
    void set(Fix::StandInstDbID* value)
    { setField(value); }
    Fix::StandInstDbID* get(Fix::StandInstDbID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::StandInstDbID* getStandInstDbID() throw(Fix::FieldNotFound*)
    { Fix::StandInstDbID* value = new Fix::StandInstDbID();
      getField(value); return value; }
 
    void set(Fix::SettlDeliveryType* value)
    { setField(value); }
    Fix::SettlDeliveryType* get(Fix::SettlDeliveryType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlDeliveryType* getSettlDeliveryType() throw(Fix::FieldNotFound*)
    { Fix::SettlDeliveryType* value = new Fix::SettlDeliveryType();
      getField(value); return value; }
 
    void set(Fix::SettlDepositoryCode* value)
    { setField(value); }
    Fix::SettlDepositoryCode* get(Fix::SettlDepositoryCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlDepositoryCode* getSettlDepositoryCode() throw(Fix::FieldNotFound*)
    { Fix::SettlDepositoryCode* value = new Fix::SettlDepositoryCode();
      getField(value); return value; }
 
    void set(Fix::SettlBrkrCode* value)
    { setField(value); }
    Fix::SettlBrkrCode* get(Fix::SettlBrkrCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlBrkrCode* getSettlBrkrCode() throw(Fix::FieldNotFound*)
    { Fix::SettlBrkrCode* value = new Fix::SettlBrkrCode();
      getField(value); return value; }
 
    void set(Fix::SettlInstCode* value)
    { setField(value); }
    Fix::SettlInstCode* get(Fix::SettlInstCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SettlInstCode* getSettlInstCode() throw(Fix::FieldNotFound*)
    { Fix::SettlInstCode* value = new Fix::SettlInstCode();
      getField(value); return value; }
 
    void set(Fix::SecuritySettlAgentName* value)
    { setField(value); }
    Fix::SecuritySettlAgentName* get(Fix::SecuritySettlAgentName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecuritySettlAgentName* getSecuritySettlAgentName() throw(Fix::FieldNotFound*)
    { Fix::SecuritySettlAgentName* value = new Fix::SecuritySettlAgentName();
      getField(value); return value; }
 
    void set(Fix::SecuritySettlAgentCode* value)
    { setField(value); }
    Fix::SecuritySettlAgentCode* get(Fix::SecuritySettlAgentCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecuritySettlAgentCode* getSecuritySettlAgentCode() throw(Fix::FieldNotFound*)
    { Fix::SecuritySettlAgentCode* value = new Fix::SecuritySettlAgentCode();
      getField(value); return value; }
 
    void set(Fix::SecuritySettlAgentAcctNum* value)
    { setField(value); }
    Fix::SecuritySettlAgentAcctNum* get(Fix::SecuritySettlAgentAcctNum* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecuritySettlAgentAcctNum* getSecuritySettlAgentAcctNum() throw(Fix::FieldNotFound*)
    { Fix::SecuritySettlAgentAcctNum* value = new Fix::SecuritySettlAgentAcctNum();
      getField(value); return value; }
 
    void set(Fix::SecuritySettlAgentAcctName* value)
    { setField(value); }
    Fix::SecuritySettlAgentAcctName* get(Fix::SecuritySettlAgentAcctName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecuritySettlAgentAcctName* getSecuritySettlAgentAcctName() throw(Fix::FieldNotFound*)
    { Fix::SecuritySettlAgentAcctName* value = new Fix::SecuritySettlAgentAcctName();
      getField(value); return value; }
 
    void set(Fix::SecuritySettlAgentContactName* value)
    { setField(value); }
    Fix::SecuritySettlAgentContactName* get(Fix::SecuritySettlAgentContactName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecuritySettlAgentContactName* getSecuritySettlAgentContactName() throw(Fix::FieldNotFound*)
    { Fix::SecuritySettlAgentContactName* value = new Fix::SecuritySettlAgentContactName();
      getField(value); return value; }
 
    void set(Fix::SecuritySettlAgentContactPhone* value)
    { setField(value); }
    Fix::SecuritySettlAgentContactPhone* get(Fix::SecuritySettlAgentContactPhone* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::SecuritySettlAgentContactPhone* getSecuritySettlAgentContactPhone() throw(Fix::FieldNotFound*)
    { Fix::SecuritySettlAgentContactPhone* value = new Fix::SecuritySettlAgentContactPhone();
      getField(value); return value; }
 
    void set(Fix::CashSettlAgentName* value)
    { setField(value); }
    Fix::CashSettlAgentName* get(Fix::CashSettlAgentName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashSettlAgentName* getCashSettlAgentName() throw(Fix::FieldNotFound*)
    { Fix::CashSettlAgentName* value = new Fix::CashSettlAgentName();
      getField(value); return value; }
 
    void set(Fix::CashSettlAgentCode* value)
    { setField(value); }
    Fix::CashSettlAgentCode* get(Fix::CashSettlAgentCode* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashSettlAgentCode* getCashSettlAgentCode() throw(Fix::FieldNotFound*)
    { Fix::CashSettlAgentCode* value = new Fix::CashSettlAgentCode();
      getField(value); return value; }
 
    void set(Fix::CashSettlAgentAcctNum* value)
    { setField(value); }
    Fix::CashSettlAgentAcctNum* get(Fix::CashSettlAgentAcctNum* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashSettlAgentAcctNum* getCashSettlAgentAcctNum() throw(Fix::FieldNotFound*)
    { Fix::CashSettlAgentAcctNum* value = new Fix::CashSettlAgentAcctNum();
      getField(value); return value; }
 
    void set(Fix::CashSettlAgentAcctName* value)
    { setField(value); }
    Fix::CashSettlAgentAcctName* get(Fix::CashSettlAgentAcctName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashSettlAgentAcctName* getCashSettlAgentAcctName() throw(Fix::FieldNotFound*)
    { Fix::CashSettlAgentAcctName* value = new Fix::CashSettlAgentAcctName();
      getField(value); return value; }
 
    void set(Fix::CashSettlAgentContactName* value)
    { setField(value); }
    Fix::CashSettlAgentContactName* get(Fix::CashSettlAgentContactName* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashSettlAgentContactName* getCashSettlAgentContactName() throw(Fix::FieldNotFound*)
    { Fix::CashSettlAgentContactName* value = new Fix::CashSettlAgentContactName();
      getField(value); return value; }
 
    void set(Fix::CashSettlAgentContactPhone* value)
    { setField(value); }
    Fix::CashSettlAgentContactPhone* get(Fix::CashSettlAgentContactPhone* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::CashSettlAgentContactPhone* getCashSettlAgentContactPhone() throw(Fix::FieldNotFound*)
    { Fix::CashSettlAgentContactPhone* value = new Fix::CashSettlAgentContactPhone();
      getField(value); return value; }
 
  };

  public __gc class NewOrderList : public Message
  {
  public: 
    NewOrderList() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("E"); }
    //
    NewOrderList(
      Fix::ListID* aListID,
      Fix::BidType* aBidType,
      Fix::TotNoOrders* aTotNoOrders,
      Fix::NoOrders* aNoOrders )
    : Message(MsgType())
    
    {
      set(aListID);
      set(aBidType);
      set(aTotNoOrders);
      set(aNoOrders);
    }
  
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::BidID* value)
    { setField(value); }
    Fix::BidID* get(Fix::BidID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidID* getBidID() throw(Fix::FieldNotFound*)
    { Fix::BidID* value = new Fix::BidID();
      getField(value); return value; }
 
    void set(Fix::ClientBidID* value)
    { setField(value); }
    Fix::ClientBidID* get(Fix::ClientBidID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientBidID* getClientBidID() throw(Fix::FieldNotFound*)
    { Fix::ClientBidID* value = new Fix::ClientBidID();
      getField(value); return value; }
 
    void set(Fix::ProgRptReqs* value)
    { setField(value); }
    Fix::ProgRptReqs* get(Fix::ProgRptReqs* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ProgRptReqs* getProgRptReqs() throw(Fix::FieldNotFound*)
    { Fix::ProgRptReqs* value = new Fix::ProgRptReqs();
      getField(value); return value; }
 
    void set(Fix::BidType* value)
    { setField(value); }
    Fix::BidType* get(Fix::BidType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidType* getBidType() throw(Fix::FieldNotFound*)
    { Fix::BidType* value = new Fix::BidType();
      getField(value); return value; }
 
    void set(Fix::ProgPeriodInterval* value)
    { setField(value); }
    Fix::ProgPeriodInterval* get(Fix::ProgPeriodInterval* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ProgPeriodInterval* getProgPeriodInterval() throw(Fix::FieldNotFound*)
    { Fix::ProgPeriodInterval* value = new Fix::ProgPeriodInterval();
      getField(value); return value; }
 
    void set(Fix::ListExecInstType* value)
    { setField(value); }
    Fix::ListExecInstType* get(Fix::ListExecInstType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListExecInstType* getListExecInstType() throw(Fix::FieldNotFound*)
    { Fix::ListExecInstType* value = new Fix::ListExecInstType();
      getField(value); return value; }
 
    void set(Fix::ListExecInst* value)
    { setField(value); }
    Fix::ListExecInst* get(Fix::ListExecInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListExecInst* getListExecInst() throw(Fix::FieldNotFound*)
    { Fix::ListExecInst* value = new Fix::ListExecInst();
      getField(value); return value; }
 
    void set(Fix::EncodedListExecInstLen* value)
    { setField(value); }
    Fix::EncodedListExecInstLen* get(Fix::EncodedListExecInstLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedListExecInstLen* getEncodedListExecInstLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedListExecInstLen* value = new Fix::EncodedListExecInstLen();
      getField(value); return value; }
 
    void set(Fix::EncodedListExecInst* value)
    { setField(value); }
    Fix::EncodedListExecInst* get(Fix::EncodedListExecInst* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedListExecInst* getEncodedListExecInst() throw(Fix::FieldNotFound*)
    { Fix::EncodedListExecInst* value = new Fix::EncodedListExecInst();
      getField(value); return value; }
 
    void set(Fix::TotNoOrders* value)
    { setField(value); }
    Fix::TotNoOrders* get(Fix::TotNoOrders* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TotNoOrders* getTotNoOrders() throw(Fix::FieldNotFound*)
    { Fix::TotNoOrders* value = new Fix::TotNoOrders();
      getField(value); return value; }
 
    void set(Fix::NoOrders* value)
    { setField(value); }
    Fix::NoOrders* get(Fix::NoOrders* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoOrders* getNoOrders() throw(Fix::FieldNotFound*)
    { Fix::NoOrders* value = new Fix::NoOrders();
      getField(value); return value; }
 
  };

  public __gc class ListStatus : public Message
  {
  public: 
    ListStatus() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("N"); }
    //
    ListStatus(
      Fix::ListID* aListID,
      Fix::ListStatusType* aListStatusType,
      Fix::NoRpts* aNoRpts,
      Fix::ListOrderStatus* aListOrderStatus,
      Fix::RptSeq* aRptSeq,
      Fix::TotNoOrders* aTotNoOrders,
      Fix::NoOrders* aNoOrders )
    : Message(MsgType())
    
    {
      set(aListID);
      set(aListStatusType);
      set(aNoRpts);
      set(aListOrderStatus);
      set(aRptSeq);
      set(aTotNoOrders);
      set(aNoOrders);
    }
  
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::ListStatusType* value)
    { setField(value); }
    Fix::ListStatusType* get(Fix::ListStatusType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListStatusType* getListStatusType() throw(Fix::FieldNotFound*)
    { Fix::ListStatusType* value = new Fix::ListStatusType();
      getField(value); return value; }
 
    void set(Fix::NoRpts* value)
    { setField(value); }
    Fix::NoRpts* get(Fix::NoRpts* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoRpts* getNoRpts() throw(Fix::FieldNotFound*)
    { Fix::NoRpts* value = new Fix::NoRpts();
      getField(value); return value; }
 
    void set(Fix::ListOrderStatus* value)
    { setField(value); }
    Fix::ListOrderStatus* get(Fix::ListOrderStatus* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListOrderStatus* getListOrderStatus() throw(Fix::FieldNotFound*)
    { Fix::ListOrderStatus* value = new Fix::ListOrderStatus();
      getField(value); return value; }
 
    void set(Fix::RptSeq* value)
    { setField(value); }
    Fix::RptSeq* get(Fix::RptSeq* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RptSeq* getRptSeq() throw(Fix::FieldNotFound*)
    { Fix::RptSeq* value = new Fix::RptSeq();
      getField(value); return value; }
 
    void set(Fix::ListStatusText* value)
    { setField(value); }
    Fix::ListStatusText* get(Fix::ListStatusText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListStatusText* getListStatusText() throw(Fix::FieldNotFound*)
    { Fix::ListStatusText* value = new Fix::ListStatusText();
      getField(value); return value; }
 
    void set(Fix::EncodedListStatusTextLen* value)
    { setField(value); }
    Fix::EncodedListStatusTextLen* get(Fix::EncodedListStatusTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedListStatusTextLen* getEncodedListStatusTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedListStatusTextLen* value = new Fix::EncodedListStatusTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedListStatusText* value)
    { setField(value); }
    Fix::EncodedListStatusText* get(Fix::EncodedListStatusText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedListStatusText* getEncodedListStatusText() throw(Fix::FieldNotFound*)
    { Fix::EncodedListStatusText* value = new Fix::EncodedListStatusText();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::TotNoOrders* value)
    { setField(value); }
    Fix::TotNoOrders* get(Fix::TotNoOrders* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TotNoOrders* getTotNoOrders() throw(Fix::FieldNotFound*)
    { Fix::TotNoOrders* value = new Fix::TotNoOrders();
      getField(value); return value; }
 
    void set(Fix::NoOrders* value)
    { setField(value); }
    Fix::NoOrders* get(Fix::NoOrders* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::NoOrders* getNoOrders() throw(Fix::FieldNotFound*)
    { Fix::NoOrders* value = new Fix::NoOrders();
      getField(value); return value; }
 
  };

  public __gc class ListExecute : public Message
  {
  public: 
    ListExecute() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("L"); }
    //
    ListExecute(
      Fix::ListID* aListID,
      Fix::TransactTime* aTransactTime )
    : Message(MsgType())
    
    {
      set(aListID);
      set(aTransactTime);
    }
  
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::ClientBidID* value)
    { setField(value); }
    Fix::ClientBidID* get(Fix::ClientBidID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ClientBidID* getClientBidID() throw(Fix::FieldNotFound*)
    { Fix::ClientBidID* value = new Fix::ClientBidID();
      getField(value); return value; }
 
    void set(Fix::BidID* value)
    { setField(value); }
    Fix::BidID* get(Fix::BidID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BidID* getBidID() throw(Fix::FieldNotFound*)
    { Fix::BidID* value = new Fix::BidID();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class ListCancelRequest : public Message
  {
  public: 
    ListCancelRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("K"); }
    //
    ListCancelRequest(
      Fix::ListID* aListID,
      Fix::TransactTime* aTransactTime )
    : Message(MsgType())
    
    {
      set(aListID);
      set(aTransactTime);
    }
  
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::TransactTime* value)
    { setField(value); }
    Fix::TransactTime* get(Fix::TransactTime* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::TransactTime* getTransactTime() throw(Fix::FieldNotFound*)
    { Fix::TransactTime* value = new Fix::TransactTime();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class ListStatusRequest : public Message
  {
  public: 
    ListStatusRequest() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("M"); }
    //
    ListStatusRequest(
      Fix::ListID* aListID )
    : Message(MsgType())
    
    {
      set(aListID);
    }
  
    void set(Fix::ListID* value)
    { setField(value); }
    Fix::ListID* get(Fix::ListID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::ListID* getListID() throw(Fix::FieldNotFound*)
    { Fix::ListID* value = new Fix::ListID();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };

  public __gc class BusinessMessageReject : public Message
  {
  public: 
    BusinessMessageReject() : Message(MsgType()) {}
    static Fix::MsgType* MsgType() { return new Fix::MsgType("j"); }
    //
    BusinessMessageReject(
      Fix::RefMsgType* aRefMsgType,
      Fix::BusinessRejectReason* aBusinessRejectReason )
    : Message(MsgType())
    
    {
      set(aRefMsgType);
      set(aBusinessRejectReason);
    }
  
    void set(Fix::RefSeqNum* value)
    { setField(value); }
    Fix::RefSeqNum* get(Fix::RefSeqNum* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RefSeqNum* getRefSeqNum() throw(Fix::FieldNotFound*)
    { Fix::RefSeqNum* value = new Fix::RefSeqNum();
      getField(value); return value; }
 
    void set(Fix::RefMsgType* value)
    { setField(value); }
    Fix::RefMsgType* get(Fix::RefMsgType* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::RefMsgType* getRefMsgType() throw(Fix::FieldNotFound*)
    { Fix::RefMsgType* value = new Fix::RefMsgType();
      getField(value); return value; }
 
    void set(Fix::BusinessRejectRefID* value)
    { setField(value); }
    Fix::BusinessRejectRefID* get(Fix::BusinessRejectRefID* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BusinessRejectRefID* getBusinessRejectRefID() throw(Fix::FieldNotFound*)
    { Fix::BusinessRejectRefID* value = new Fix::BusinessRejectRefID();
      getField(value); return value; }
 
    void set(Fix::BusinessRejectReason* value)
    { setField(value); }
    Fix::BusinessRejectReason* get(Fix::BusinessRejectReason* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::BusinessRejectReason* getBusinessRejectReason() throw(Fix::FieldNotFound*)
    { Fix::BusinessRejectReason* value = new Fix::BusinessRejectReason();
      getField(value); return value; }
 
    void set(Fix::Text* value)
    { setField(value); }
    Fix::Text* get(Fix::Text* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::Text* getText() throw(Fix::FieldNotFound*)
    { Fix::Text* value = new Fix::Text();
      getField(value); return value; }
 
    void set(Fix::EncodedTextLen* value)
    { setField(value); }
    Fix::EncodedTextLen* get(Fix::EncodedTextLen* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedTextLen* getEncodedTextLen() throw(Fix::FieldNotFound*)
    { Fix::EncodedTextLen* value = new Fix::EncodedTextLen();
      getField(value); return value; }
 
    void set(Fix::EncodedText* value)
    { setField(value); }
    Fix::EncodedText* get(Fix::EncodedText* value) throw(Fix::FieldNotFound*)
    { getField(value); return value; }
    Fix::EncodedText* getEncodedText() throw(Fix::FieldNotFound*)
    { Fix::EncodedText* value = new Fix::EncodedText();
      getField(value); return value; }
 
  };
}

 