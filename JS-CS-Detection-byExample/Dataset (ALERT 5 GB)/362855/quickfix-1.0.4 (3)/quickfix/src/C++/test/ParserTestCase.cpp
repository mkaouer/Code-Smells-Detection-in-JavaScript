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

#include "ParserTestCase.h"
#include <string>
#include <sstream>

namespace FIX
{
  bool ParserTestCase::readFixMessage::onSetup( Parser*& pObject )
  {
    m_fixMsg1 = "8=FIX.4.2\0019=12\00135=A\001108=30\00110=31\001";
    m_fixMsg2 = "8=FIX.4.2\0019=17\00135=4\00136=88\001123=Y\00110=34\001";
    m_partFixMsg1 = "8=FIX.4.2\0019=17\00135=4\00136=";
    m_partFixMsg2 = "88\001123=Y\00110=34\001";

    m_pStream = new std::stringstream(m_fixMsg1 + m_fixMsg2 + m_partFixMsg1);

    pObject = new Parser(*m_pStream);
    return true;
  }

  void ParserTestCase::readFixMessage::onRun( Parser& object )
  {
    std::string fixMsg1;
    assert(object.readFixMessage(fixMsg1));
    assert( fixMsg1 == m_fixMsg1 );

    std::string fixMsg2;
    assert(object.readFixMessage(fixMsg2));
    assert( fixMsg2 == m_fixMsg2 );

    std::string partFixMsg;
    assert(!object.readFixMessage(partFixMsg));
    std::stringstream finishStream(m_partFixMsg2);
    object.setStream(finishStream);
    assert(object.readFixMessage(partFixMsg));
    assert( partFixMsg == (m_partFixMsg1 + m_partFixMsg2) );
  }

  bool ParserTestCase::readBadFixMessage::onSetup( Parser*& pObject )
  {
    m_fixMsg1 =
      "8=FIX.4.2\0019=15\00135=0\00134=2\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\00110=217\001";
    m_fixMsg2 =
      "8=FIX.4.2\0019=45\00135=0\00134=3\00149=TW\001"
      "52=20000426-12:05:06\00156=ISLD\00110=218\001";

    m_pStream = new std::stringstream(m_fixMsg1 + m_fixMsg2);

    pObject = new Parser(*m_pStream);
    return true;
  }

  void ParserTestCase::readBadFixMessage::onRun( Parser& object )
  {
    try
      {
	      std::string fixMsg1;
	      object.readFixMessage(fixMsg1);
	      assert(false);
      }
    catch( MessageParseError& ){}

    try
      {
	      std::string fixMsg2;
	      assert(object.readFixMessage(fixMsg2));
	      assert(fixMsg2 == m_fixMsg2);
      }
    catch( MessageParseError& ) { assert(false); }
  }
}
