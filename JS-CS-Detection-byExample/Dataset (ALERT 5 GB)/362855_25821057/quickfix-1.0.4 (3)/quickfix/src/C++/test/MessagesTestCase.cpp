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

#include "MessagesTestCase.h"

namespace FIX
{
  using namespace FIX42;
  static UtcTimeStamp create_tm()
  {
    UtcTimeStamp result;
    memset(&result, 0, sizeof(UtcTimeStamp));
    return result;
  }

  void MessageTestCase::identifyType::onRun( Message& )
  {
    try
      {
	assert
	  ( FIX::identifyType("8=FIX.4.2\0019=12\00135=A\001108=30\001"
			      "10=031\001") == "A" );
      }
    catch( std::logic_error& ) { assert(false); }

    try
      {
	assert
	  ( FIX::identifyType("8=FIX.4.2\0019=12\001108=30\00110=031\001")
	    == "A" );
	assert(false);
      }
    catch( std::logic_error& ) { assert(true); }
  }

  void MessageTestCase::setString::onRun( Message& object )
  {
    static const char* strGood =
      "8=FIX.4.2\0019=46\00135=0\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\00110=219\001";
    static const char* strNoChk =
      "8=FIX.4.2\0019=46\00135=0\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\001";
    static const char* strBadChk =
      "8=FIX.4.2\0019=46\00135=0\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\00110=000\001";
    static const char* strBad =
      "8=FIX.4.2\0019=46\00135=0\00134=3\001"
      "49garbled=TW\00152=20000426-12:05:06\00156=ISLD\00110=000\001";
    static const char* strBadHeaderOrder =
      "8=FIX.4.2\00135=0\0019=46\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\00110=219\001";

    assert(object.setString(strGood));
    assert(!object.setString(strNoChk));
    assert(!object.setString(strBadChk));
    assert(!object.setString(strBad));
    assert(!object.setString(strBadHeaderOrder));
  }

  void MessageTestCase::checkSum::onRun( Message& object )
  {
    const std::string str1 =
      "8=FIX.4.2\0019=46\00135=0\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\001";
    const std::string str2 =
      "8=FIX.4.2\0019=46\00135=0\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\00110=000\001";
    std::string::size_type i;
    int chksum;

    for(i = 0L, chksum = 0; i < str1.size(); chksum += (int) str1[i++]) {}

    chksum %= 256;

    object.setString(str2);
    assert( object.checkSum() == chksum );
  }

  void MessageTestCase::repeatingField::onRun( Message& )
  {
    /*Logout logout;
      Text text;
      std::vector<Text> texts;
      logout.add(Text("TEXT1"));
      logout.add(Text("TEXT2"));
      assert(logout.count(text) == 2);
      assert(logout.get(text) == "TEXT1");
      assert(logout.get(texts) == 2);
      assert(texts[0] == "TEXT1");
      assert(texts[1] == "TEXT2");*/
  }

  void MessageTestCase::headerFieldsFirst::onRun( Message& object )
  {
    const std::string str =
      "8=FIX.4.2\0019=95\00135=D\00134=5\00149=ISLD\00155=INTC\001"
      "52=00000000-00:00:00\00156=TW\00111=ID\00121=3\001"
      "40=1\00154=1\00160=00000000-00:00:00\00110=000\001";
    object.setString(str);
    assert(!object.hasValidStructure());
  }

  void LogonParseTestCase::getString::onRun( Logon& object )
  {
    try
      {
	EncryptMethod encryptMethod;
	object.get( encryptMethod );
	assert(false);
      }
    catch( std::logic_error& ) {}

    object.set( HeartBtInt(30) );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=12\00135=A\001"
	    "108=30\00110=026\001" );
  }

  void LogonParseTestCase::setString::onRun( Logon& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=12\00135=A\001108=30\00110=026\001") );

    HeartBtInt heartBtInt;
    assert( object.get(heartBtInt) == 30 );
  }

  void TestRequestParseTestCase::getString::onRun( TestRequest& object )
  {
    object.set( TestReqID("23") );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=12\00135=1\001112=23\00110=007\001" );
  }

  void TestRequestParseTestCase::setString::onRun( TestRequest& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=12\00135=1\001112=23\00110=007\001"));

    TestReqID testReqID;
    assert( object.get(testReqID) == "23" );
  }

  void ResendRequestParseTestCase::getString::onRun( ResendRequest& object )
  {
    object.set( BeginSeqNo(1) );
    object.set( EndSeqNo(233) );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=16\00135=2\0017=1\00116=233\00110=184\001" );
  }

  void ResendRequestParseTestCase::setString::onRun( ResendRequest& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=16\00135=2\0017=1\00116=233\00110=184\001"));

    BeginSeqNo beginSeqNo;
    EndSeqNo endSeqNo;
    assert( object.get(beginSeqNo) == 1 );
    assert( object.get(endSeqNo) == 233 );
  }

  void RejectParseTestCase::getString::onRun( Reject& object )
  {
    object.set( RefSeqNum(73) );
    object.set( Text("This Message SUCKS!!!") );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=36\00135=3\00145=73\001"
	    "58=This Message SUCKS!!!\00110=029\001" );
  }

  void RejectParseTestCase::setString::onRun( Reject& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=36\00135=3\00145=73\001"
	    "58=This Message SUCKS!!!\00110=029\001"));

    RefSeqNum refSeqNum;
    Text text;
    assert( object.get(refSeqNum) == 73 );
    assert( object.get(text) == "This Message SUCKS!!!" );
  }

  void SequenceResetParseTestCase::getString::onRun( SequenceReset& object )
  {
    object.set( GapFillFlag(true) );
    object.set( NewSeqNo(88) );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=17\00135=4\00136=88\001123=Y\00110=028\001" );
  }

  void SequenceResetParseTestCase::setString::onRun( SequenceReset& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=17\00135=4\00136=88\001123=Y\00110=028\001"));

    GapFillFlag gapFillFlag;
    NewSeqNo newSeqNo;
    assert( object.get(gapFillFlag) == true );
    assert( object.get(newSeqNo) == 88 );
  }

  void LogoutParseTestCase::getString::onRun( Logout& object )
  {
    object.set( Text("See Ya...") );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=18\00135=5\00158=See Ya...\00110=006\001" );
  }

  void LogoutParseTestCase::setString::onRun( Logout& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=18\00135=5\00158=See Ya...\00110=006\001"));

    Text text;
    assert( object.get(text) == "See Ya..." );
  }

  void NewOrderSingleParseTestCase::getString::onRun( NewOrderSingle& object )
  {
    object.set( ClOrdID("ORDERID") );
    object.set( HandlInst('3') );
    object.set( Symbol("MSFT") );
    object.set( Side('1') );
    object.set( TransactTime(create_tm()) );
    object.set( OrdType('2') );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=60\00135=D\00111=ORDERID\00121=3\00140=2\001"
	    "54=1\00155=MSFT\00160=19000100-00:00:00\00110=225\001" );
  }

  void NewOrderSingleParseTestCase::setString::onRun( NewOrderSingle& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=48\00135=D\00111=ORDERID\00121=3\00140=2\001"
	    "54=1\00155=MSFT\00160=TODAY\00110=028\001"));

    ClOrdID clOrdID;
    HandlInst handlInst;
    Symbol symbol;
    Side side;
    TransactTime transactTime;
    OrdType ordType;
    assert( object.get(clOrdID) == "ORDERID" );
    assert( object.get(handlInst) == '3' );
    assert( object.get(symbol) == "MSFT" );
    assert( object.get(side) == '1' );
    //assert( object.get(transactTime) == 0 );
    assert( object.get(ordType) == '2' );
  }

  void ExecutionReportParseTestCase::getString::onRun
  ( ExecutionReport& object )
  {
    object.set( OrderID("ORDERID") );
    object.set( ExecID("EXECID") );
    object.set( ExecTransType('1') );
    object.set( ExecType('2') );
    object.set( OrdStatus('3') );
    object.set( Symbol("MSFT") );
    object.set( Side('4') );
    object.set( LeavesQty(200) );
    object.set( CumQty(300) );
    object.set( AvgPx(23.4) );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=77\00135=8\0016=23.4\00114=300\001"
	    "17=EXECID\00120=1\00137=ORDERID\00139=3\00154=4\00155=MSFT\001"
	    "150=2\001151=200\00110=052\001" );
  }

  void ExecutionReportParseTestCase::setString::onRun
  ( ExecutionReport& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=77\00135=8\0016=23.4\00114=300\001"
	    "17=EXECID\00120=1\00137=ORDERID\00139=3\00154=4\001"
	    "55=MSFT\001150=2\001151=200\00110=052\001" ));

    OrderID orderID;
    ExecID execID;
    ExecTransType execTransType;
    ExecType execType;
    OrdStatus ordStatus;
    Symbol symbol;
    Side side;
    LeavesQty leavesQty;
    CumQty cumQty;
    AvgPx avgPx;
    assert( object.get(orderID) == "ORDERID" );
    assert( object.get(execID) == "EXECID" );
    assert( object.get(execTransType) == '1' );
    assert( object.get(execType) == '2' );
    assert( object.get(ordStatus) == '3' );
    assert( object.get(symbol) == "MSFT" );
    assert( object.get(side) == '4' );
    assert( object.get(leavesQty) == 200 );
    assert( object.get(cumQty) == 300 );
    assert( object.get(avgPx) == 23.4 );
  }

  void DontKnowTradeParseTestCase::getString::onRun( DontKnowTrade& object )
  {
    object.set( OrderID("ORDERID") );
    object.set( ExecID("EXECID") );
    object.set( DKReason('1') );
    object.set( Symbol("MSFT") );
    object.set( Side('2') );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=45\00135=Q\00117=EXECID\00137=ORDERID\001"
	    "54=2\00155=MSFT\001127=1\00110=195\001" );
  }

  void DontKnowTradeParseTestCase::setString::onRun( DontKnowTrade& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=45\00135=Q\00117=EXECID\00137=ORDERID\001"
	    "54=2\00155=MSFT\001127=1\00110=195\001"));

    OrderID orderID;
    ExecID execID;
    DKReason dKReason;
    Symbol symbol;
    Side side;
    assert( object.get(orderID) == "ORDERID" );
    assert( object.get(execID) == "EXECID" );
    assert( object.get(dKReason) == '1' );
    assert( object.get(symbol) == "MSFT" );
    assert( object.get(side) == '2' );
  }

  void OrderCancelReplaceRequestParseTestCase::getString::onRun
  ( OrderCancelReplaceRequest& object )
  {
    object.set( OrigClOrdID("ORIGINALID") );
    object.set( ClOrdID("CLIENTID") );
    object.set( HandlInst('1') );
    object.set( Symbol("MSFT") );
    object.set( Side('2') );
    object.set( TransactTime(create_tm()) );
    object.set( OrdType('3') );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=75\00135=G\00111=CLIENTID\00121=1\001"
	    "40=3\00141=ORIGINALID\00154=2\00155=MSFT\001"
	    "60=19000100-00:00:00\00110=178\001" );
  }

  void OrderCancelReplaceRequestParseTestCase::setString::onRun
  ( OrderCancelReplaceRequest& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=63\00135=G\00111=CLIENTID\00121=1\001"
	    "40=3\00141=ORIGINALID\00154=2\00155=MSFT\00160=TODAY\001"
	    "10=228\001"));

    OrigClOrdID origClOrdID;
    ClOrdID clOrdID;
    HandlInst handlInst;
    Symbol symbol;
    Side side;
    TransactTime transactTime;
    OrdType ordType;
    assert( object.get(origClOrdID) == "ORIGINALID" );
    assert( object.get(clOrdID) == "CLIENTID" );
    assert( object.get(handlInst) == '1' );
    assert( object.get(symbol) == "MSFT" );
    assert( object.get(side) == '2' );
    assert( object.get(ordType) == '3' );
  }

  void OrderCancelRequestParseTestCase::getString::onRun
  ( OrderCancelRequest& object )
  {
    object.set( OrigClOrdID("ORIGINALID") );
    object.set( ClOrdID("CLIENTID") );
    object.set( Symbol("MSFT") );
    object.set( Side('1') );
    object.set( TransactTime(create_tm()) );

    assert( object.getString()
	    ==
	    "8=FIX.4.2\0019=65\00135=F\00111=CLIENTID\00141=ORIGINALID\001"
	    "54=1\00155=MSFT\00160=19000100-00:00:00\00110=008\001" );
  }

  void OrderCancelRequestParseTestCase::setString::onRun
  ( OrderCancelRequest& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=54\00135=F\00111=CLIENTID\00141=ORIGINALID\001"
	    "54=1\00155=MSFT\00160=TODAY\00110=059\001"));

    OrigClOrdID origClOrdID;
    ClOrdID clOrdID;
    Symbol symbol;
    Side side;
    TransactTime transactTime;
    assert( object.get(origClOrdID) == "ORIGINALID" );
    assert( object.get(clOrdID) == "CLIENTID" );
    assert( object.get(symbol) == "MSFT" );
    assert( object.get(side) == '1' );
  }

  void OrderCancelRejectParseTestCase::getString::onRun
  ( OrderCancelReject& object )
  {
    object.set( OrderID("ORDERID") );
    object.set( ClOrdID("CLIENTID") );
    object.set( OrigClOrdID("ORIGINALID") );
    object.set( OrdStatus('1') );
    object.set( CxlRejResponseTo('2') );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=53\00135=9\00111=CLIENTID\00137=ORDERID\001"
	    "39=1\00141=ORIGINALID\001434=2\00110=229\001" );
  }

  void OrderCancelRejectParseTestCase::setString::onRun
  ( OrderCancelReject& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=53\00135=9\00111=CLIENTID\00137=ORDERID\001"
	    "39=1\00141=ORIGINALID\001434=2\00110=229\001" ));

    OrderID orderID;
    ClOrdID clOrdID;
    OrigClOrdID origClOrdID;
    OrdStatus ordStatus;
    CxlRejResponseTo cxlRejResponseTo;
    assert( object.get(orderID) == "ORDERID" );
    assert( object.get(clOrdID) == "CLIENTID" );
    assert( object.get(origClOrdID) == "ORIGINALID" );
    assert( object.get(ordStatus) == '1' );
    assert( object.get(cxlRejResponseTo) == '2' );
  }

  void OrderStatusRequestParseTestCase::getString::onRun
  ( OrderStatusRequest& object )
  {
    object.set( ClOrdID("CLIENTID") );
    object.set( Symbol("MSFT") );
    object.set( Side('1') );

    assert( object.getString() ==
	    "8=FIX.4.2\0019=30\00135=H\00111=CLIENTID\00154=1\001"
	    "55=MSFT\00110=141\001" );
  }

  void OrderStatusRequestParseTestCase::setString::onRun
  ( OrderStatusRequest& object )
  {
    assert(object.setString
	   ("8=FIX.4.2\0019=30\00135=H\00111=CLIENTID\00154=1\001"
	    "55=MSFT\00110=141\001" ));

    ClOrdID clOrdID;
    Symbol symbol;
    Side side;
    assert( object.get(clOrdID) == "CLIENTID" );
    assert( object.get(symbol) == "MSFT" );
    assert( object.get(side) == '1' );
  }
}
