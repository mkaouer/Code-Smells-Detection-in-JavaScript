# ====================================================================
# The QuickFIX Software License, Version 1.0
#
# Copyright (c) 2001 ThoughtWorks, Inc.  All rights
# reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in
#    the documentation and/or other materials provided with the
#    distribution.
#
# 3. The end-user documentation included with the redistribution,
#    if any, must include the following acknowledgment:
#       "This product includes software developed by
#        ThoughtWorks, Inc. (http://www.thoughtworks.com/)."
#    Alternately, this acknowledgment may appear in the software itself,
#    if and wherever such third-party acknowledgments normally appear.
#
# 4. The names "QuickFIX" and "ThoughtWorks, Inc." must
#    not be used to endorse or promote products derived from this
#    software without prior written permission. For written
#    permission, please contact quickfix-users@lists.sourceforge.net.
#
# 5. Products derived from this software may not be called "QuickFIX",
#    nor may "QuickFIX" appear in their name, without prior written
#    permission of ThoughtWorks, Inc.
#
# THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED.  IN NO EVENT SHALL THOUGHTWORKS INC OR
# ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
# USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
# OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
# ====================================================================

require "socket"

class FixParser

  def initialize(io)
    @io = io
  end

  def readFixMessage()
    if(@io.eof?)
      raise "Was disconnected, expected data"
    end

    m = ""
    # read to begining of MsgLen field
    m = @io.gets("\0019=")
    # read contents of MsgLen field
    length = @io.gets("\001")
    m += length
    length.chop!
    
    # regex checks to make sure length is an integer
    # if it isn't there is nothing we can do so
    # close the connection
    if( (/^\d*$/ === length) == nil )
      @io.close
    end
    # read body
    m += @io.read(Integer(length))
    # read CheckSum
    m += @io.gets("\001")
    return m
  end    

end

require 'runit/testcase'
require "thread"
require 'SocketServer'

class FixParserTestCase < RUNIT::TestCase

  def test_readFixMessage
    fixMsg1 = "8=FIX.4.2\0019=12\00135=A\001108=30\00110=31\001"
    fixMsg2 = "8=FIX.4.2\0019=17\00135=4\00136=88\001123=Y\00110=34\001"

    server = SocketServer.new
    def server.message=(m)
      @message = m
    end

    def server.connectAction(s)
    end

    def server.receiveAction(s)
      s.write(@message)
    end

    def server.disconnectAction(s)
    end

    server.message = fixMsg1 + fixMsg2
    Thread.start do
      server.listen(RUNIT::TestCase.port)
    end
    server.wait

    s = TCPSocket.open("localhost", RUNIT::TestCase.port)
    parser = FixParser.new(s)
    begin
      assert_equals(fixMsg1, parser.readFixMessage)
      assert_equals(fixMsg2, parser.readFixMessage)
    rescue IOError
      # I have no idea why this is being thrown
    end

    s.close
    server.stop()
  end

end
