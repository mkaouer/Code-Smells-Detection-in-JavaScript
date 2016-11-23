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

class Reflector < Array

  def identifyMessage(message)
    if [?I, ?E, ?R, ?i, ?e].include?(message[0]) 
      return message[0]
    else 
      return ?X
    end
  end

  def processFile(messages)
    lineNum = 0
    messages.each_line do
      | line |
      lineNum += 1
      line.chomp!
      if line.empty? then
      elsif (/^[IERie]\d{1},/ === line) != nil then
	cid = line[1].to_i - 48
	body = fixify!(timify!(line[3, line.length]))
      else
	cid = 1
	body = fixify!(timify!(line[1, line.length]))
      end

      if line.empty?
      elsif line[0] == ?\#
      elsif identifyMessage(line) == ?I
	initiateAction(body, cid)
      elsif identifyMessage(line) == ?E
	expectedAction(body, cid)
      elsif identifyMessage(line) == ?R
	responseAction(body, cid)
      elsif identifyMessage(line) == ?i
	if body == "CONNECT"
	  connectAction(cid)
	elsif body == "DISCONNECT"
	  disconnectAction(cid)
	else errorAction(lineNum, line)
	end
      elsif identifyMessage(line) == ?e
	if body == "CONNECT"
	  waitConnectAction(cid)
	elsif body == "DISCONNECT"
	  waitDisconnectAction(cid)
	else
	  errorAction(lineNum, line)
	end
      else
	errorAction(lineNum, line)
      end
    end
  end

  def fixify!(message)
    hasLength = (message =~ /[\001]9=.*?[\001]/)
    length = ""

    head = message.slice!(/^8=.*?[\001]/)

    if head == nil
      return message
    end

    checksum = message.slice!(/10=.*[\001]$/)
    if hasLength == nil
      length = "9=" + message.length.to_s + "\001"
    end
    
    if checksum == nil
      checksum = "10=" + (head + length + message).sum(8).to_s + "\001"
    end

    message.replace(head + length + message + checksum)
    return message
  end

  def timify!(message)
    copy = ""
    copy.replace(message)
    t = Time.new
    t = t.gmtime

    strtime = t.strftime("%Y%m%d-%H:%M:%S")
    message.sub!("<TIME>", strtime)
    if( message != copy )
      return timify!(message)
    end

    pos1 = /\<TIME[+-]\d+\>/ =~ message
    pos2 = /\>/ =~ message
    if( pos1 != nil )
      op = message[pos1 + 5]
      num = message.slice(pos1+6..pos2-1)
      if( op == ?+ )
	t += num.to_i
      else
	t -= num.to_i
      end

      strtime = t.strftime("%Y%m%d-%H:%M:%S")
      exp = "<TIME[" + op.chr + "]" + num + ">"
      message.sub!(exp, strtime)
      if( message != copy )
	return timify!(message)
      end
    end

    return message
  end

end

require 'runit/testcase'

class ReflectorTestCase < RUNIT::TestCase

  def test_identifyMessage
    reflector = Reflector.new
    message = "I8=FIX42"
    assert(reflector.identifyMessage(message) == ?I)    
    message = "E8=FIX42"
    assert(reflector.identifyMessage(message) == ?E)
    message = "R8=FIX42"
    assert(reflector.identifyMessage(message) == ?R)
    message = "8=FIX42"
    assert(reflector.identifyMessage(message) == ?X)
    message = "iACTION"
    assert(reflector.identifyMessage(message) == ?i)
    message = "eACTION"
    assert(reflector.identifyMessage(message) == ?e)
  end

  def test_fixify_bang
    reflector = Reflector.new

    str = "8=FIX.4.235=A34=149=TW52=20000426-12:05:06" + 
      "56=ISLD98=0108=30"
    reflector.fixify!(str)
    assert_equals("8=FIX.4.29=5735=A34=149=TW52=20000426-12:05:0656=ISLD98=0108=3010=5", str)
    
    str = "8=FIX.4.29=5735=A34=149=TW52=20000426-12:05:06" +
      "56=ISLD98=0108=3010=5"
    reflector.fixify!(str)
    assert_equals("8=FIX.4.29=5735=A34=149=TW52=20000426-12:05:0656=ISLD98=0108=3010=5", str)
  end

  def test_timify_bang
    reflector = Reflector.new
    
    str = "8=FIX.4.29=5735=A34=149=TW52=20011010-10:10:1056=ISLD98=0108=3010=5"
    reflector.timify!(str)
    assert_equals("8=FIX.4.29=5735=A34=149=TW52=20011010-10:10:1056=ISLD98=0108=3010=5", str)

    str = "8=FIX.4.29=5735=A34=149=TW52=<TIME>56=ISLD98=0" + 
      "108=3010=5"
    reflector.timify!(str)
    match = (/8=FIX.4.29=5735=A34=149=TW52=\d{8}-\d{2}:\d{2}:\d{2}56=ISLD98=0108=3010=5/ === str)
    assert(match != nil)

    str = "8=FIX.4.29=5735=A34=149=TW52=<TIME>56=ISLD" +
      "122=<TIME>98=0108=3010=5"
    reflector.timify!(str)
    match = (/8=FIX.4.29=5735=A34=149=TW52=\d{8}-\d{2}:\d{2}:\d{2}56=ISLD122=\d{8}-\d{2}:\d{2}:\d{2}98=0108=3010=5/ === str)
    assert(match != nil)

    str = "8=FIX.4.29=5735=A34=149=TW52=<TIME+9>56=ISLD98=0" + 
      "108=3010=5"
    reflector.timify!(str)
    match = (/8=FIX.4.29=5735=A34=149=TW52=\d{8}-\d{2}:\d{2}:\d{2}56=ISLD98=0108=3010=5/ === str)
    assert(match != nil)
  end

  def test_identifyFile
    reflector = Reflector.new
    messages = "E8=1\nR8=2\n\nR8=3\nE8=4\n#foo\nE8=5\nE8=6\nI8=7\niCONNECT\neDISCONNECT\neCONNECT\niDISCONNECT\nE2,8=8\n"
    cum = ""
  
    def reflector.ini=(i)
      @ini = i
    end
    def reflector.ini
      return @ini
    end

    def reflector.cum=(c)
      @cum = c
    end
    def reflector.cum
      return @cum
    end

    def reflector.exp=(e)
      @exp = e
    end
    def reflector.exp
      return @exp
    end

    def reflector.rsp=(r)
      @rsp = r
    end
    def reflector.rsp
      return @rsp
    end

    def reflector.icon=(i)
      @icon = i
    end
    def reflector.icon
      return @icon
    end

    def reflector.idis=(i)
      @idis = i
    end
    def reflector.idis
      return @idis
    end

    def reflector.econ=(e)
      @econ = e
    end
    def reflector.econ
      return @econ
    end

    def reflector.edis=(e)
      @edis = e
    end
    def reflector.edis
      return @edis
    end

    def reflector.err=(e)
      @err = e
    end
    def reflector.err
      return @err
    end

    reflector.ini = ""; reflector.cum = ""
    reflector.exp = ""; reflector.rsp = ""
    reflector.icon = ""; reflector.idis = ""
    reflector.econ = ""; reflector.edis = ""
    reflector.err = ""

    def reflector.initiateAction(msg, cid)
      @cum += cid.to_s + "," + msg + "|"
      @ini += cid.to_s + "," + msg + "|"
    end

    def reflector.expectedAction(msg, cid)
      @cum += cid.to_s + "," + msg + "|"
      @exp += cid.to_s + "," + msg + "|"
    end
    
    def reflector.responseAction(msg, cid)
      @cum += cid.to_s + "," + msg + "|"
      @rsp += cid.to_s + "," + msg + "|"
    end

    def reflector.connectAction(cid)
      @cum += cid.to_s + "," + "iCONNECT" + "|"
      @icon += cid.to_s + "," + "iCONNECT" + "|"
    end

    def reflector.disconnectAction(cid)
      @cum += cid.to_s + "," + "iDISCONNECT" + "|"
      @idis += cid.to_s + "," + "iDISCONNECT" + "|"
    end

    def reflector.waitConnectAction(cid)
      @cum += cid.to_s + "," + "eCONNECT" + "|"
      @econ += cid.to_s + "," + "eCONNECT" + "|"
    end

    def reflector.waitDisconnectAction(cid)
      @cum += cid.to_s + "," + "eDISCONNECT" + "|"
      @edis += cid.to_s + "," + "eDISCONNECT" + "|"
    end

    def reflector.errorAction(msg)
      @cum += msg + "|"
      @err += msg + "|"
    end

    reflector.processFile(messages)

    assert_equals("1,8=1|1,8=2|1,8=3|1,8=4|1,8=5|1,8=6|1,8=7|" +
		  "1,iCONNECT|1,eDISCONNECT|1,eCONNECT|1,iDISCONNECT|2,8=8|", 
		  reflector.cum)
    assert_equals("1,8=7|", reflector.ini)
    assert_equals("1,8=1|1,8=4|1,8=5|1,8=6|2,8=8|", reflector.exp)
    assert_equals("1,8=2|1,8=3|", reflector.rsp)
    assert_equals("1,iCONNECT|", reflector.icon)
    assert_equals("1,iDISCONNECT|", reflector.idis)
    assert_equals("1,eCONNECT|", reflector.econ)
    assert_equals("1,eDISCONNECT|", reflector.edis)
  end
end
