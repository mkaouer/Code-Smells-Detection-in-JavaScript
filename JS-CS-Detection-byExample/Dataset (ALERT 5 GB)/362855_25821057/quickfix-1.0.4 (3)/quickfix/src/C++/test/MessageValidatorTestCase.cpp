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
#pragma warning( disable : 4503 4355 4786 )
#include "stdafx.h"
#else
#include "config.h"
#endif

#include "MessageValidatorTestCase.h"
#include "Messages.h"
#include "Values.h"

namespace FIX
{
  USER_DEFINE_STRING(TooHigh, 501);

  bool MessageValidatorTestCase::checkValidTagNumber::onSetup
  ( MessageValidator*& pObject )
  {
    m_object.setVersion(BeginString_FIX40);
    m_object.setLastField(500);
    m_object.addMsgType(MsgType_TestRequest);
    m_object.addMsgField(MsgType_TestRequest, FIELD::TestReqID);
    pObject = &m_object; return true;
  }

  void MessageValidatorTestCase::checkValidTagNumber::onRun
  ( MessageValidator& object )
  {
    TestReqID testReqID("1");

    FIX40::TestRequest message(testReqID);

    message.setField(TooHigh("value"));

    try{ object.validate(message); assert(false); }
    catch(InvalidTagNumber&) {}
  }

  void MessageValidatorTestCase::checkHasValue::onRun
  ( MessageValidator& object )
  {
    TestReqID testReqID("");

    FIX42::TestRequest message(testReqID);

    try{ object.validate(message); assert(false); }
    catch(NoTagValue&) {}
  }

  bool MessageValidatorTestCase::checkIsInMessage::onSetup
  ( MessageValidator*& pObject )
  {
    m_object.setVersion(BeginString_FIX40);
    m_object.setLastField(500);
    m_object.addMsgType(MsgType_TestRequest);
    m_object.addMsgField(MsgType_TestRequest, FIELD::TestReqID);
    pObject = &m_object; return true;
  }

  void MessageValidatorTestCase::checkIsInMessage::onRun
  ( MessageValidator& object )
  {
    TestReqID testReqID("1");

    FIX40::TestRequest message(testReqID);
    try{ object.validate(message); }
    catch(TagNotDefinedForMessage&) { assert(false); }

    message.setField(Symbol("MSFT"));
    try{ object.validate(message); assert(false); }
    catch(TagNotDefinedForMessage&) {}
  }

  bool MessageValidatorTestCase::checkHasRequired::onSetup
  ( MessageValidator*& pObject )
  {
    m_object.setVersion(BeginString_FIX40);
    m_object.setLastField(500);
    m_object.addMsgType(MsgType_TestRequest);
    m_object.addMsgField(MsgType_TestRequest, FIELD::TestReqID);
    m_object.addRequiredField(MsgType_TestRequest, FIELD::TestReqID);

    pObject = &m_object; return true;
  }

  void MessageValidatorTestCase::checkHasRequired::onRun
  ( MessageValidator& object )
  {
    FIX40::TestRequest message;
    try{ object.validate(message); assert(false); }
    catch(RequiredTagMissing&) {}

    message.setField(TestReqID("1"));
    try{ object.validate(message); }
    catch(TagNotDefinedForMessage&) { assert(false); }
  }

  bool MessageValidatorTestCase::checkValidFormat::onSetup
  ( MessageValidator*& pObject )
  {
    m_object.setVersion(BeginString_FIX40);
    m_object.setLastField(500);
    m_object.addMsgType(MsgType_TestRequest);
    m_object.addMsgField(MsgType_TestRequest, FIELD::TestReqID);
    m_object.addField(FIELD::TestReqID, TYPE::Int);

    pObject = &m_object; return true;
  }

  void MessageValidatorTestCase::checkValidFormat::onRun
  ( MessageValidator& object )
  {
    FIX40::TestRequest message;
    message.setField(TestReqID("+200"));
    try{ object.validate(message); assert(false); }
    catch(IncorrectDataFormat&) {}
  }

  bool MessageValidatorTestCase::checkValue::onSetup
  ( MessageValidator*& pObject )
  {
    m_object.setVersion(BeginString_FIX40);
    m_object.setLastField(500);
    m_object.addMsgType(MsgType_NewOrderSingle);
    m_object.addMsgField(MsgType_NewOrderSingle, FIELD::OrdType);
    m_object.addField(FIELD::OrdType, TYPE::Char);
    m_object.addValue(FIELD::OrdType, '1');

    pObject = &m_object; return true;
  }

  void MessageValidatorTestCase::checkValue::onRun
  ( MessageValidator& object )
  {
    FIX40::NewOrderSingle message;
    message.setField(OrdType('1'));
    try{ object.validate(message); }
    catch(IncorrectTagValue&) { assert(false); }

    message.setField(OrdType('2'));
    try{ object.validate(message); assert(false); }
    catch(IncorrectTagValue&) {}
  }

  bool MessageValidatorTestCase::readFromFile::onSetup
  ( MessageValidator*& pObject )
  {
    pObject = new MessageValidator("spec/FIX42.xml");
    return true;
  }

  void MessageValidatorTestCase::readFromFile::onRun
  ( MessageValidator& object )
  {
    assert(object.getVersion() == "FIX.4.2");

    ClOrdID clOrdID("1");
    HandlInst handlInst('1');
    Symbol symbol("DELL");
    Side side(Side_BUY);
    TransactTime transactTime;
    OrdType ordType(OrdType_LIMIT);

    FIX42::NewOrderSingle order(clOrdID, handlInst, symbol, side,
				transactTime, ordType);

    try{ object.validate(order); }
    catch(TagNotDefinedForMessage&) { assert(false); }

    order.setField(ExecType(ExecType_NEW));
    try{ object.validate(order); assert(false); }
    catch(TagNotDefinedForMessage&) {}

    TestReqID testReqID("id");
    FIX42::TestRequest testRequest(testReqID);
    try{ object.validate(testRequest); }
    catch(TagNotDefinedForMessage&) { assert(false); }

    Message message;
    message.setString("8=FIX.4.2\0019=68\00135=D\00111=ORDERID\00121=3\00140=2\00138=+200\00154=1\00155=MSFT\00160=19000100-00:00:00\00110=79\001");
    try{ object.validate(message); assert(false); }
    catch(IncorrectDataFormat&) {}

    message.setString("8=FIX.4.2\0019=68\00135=D\00111=ORDERID\00121=4\00140=2\00138=200\00154=1\00155=MSFT\00160=19000100-00:00:00\00110=79\001");
    try{ object.validate(message); assert(false); }
    catch(IncorrectTagValue&) {}
  }
}
