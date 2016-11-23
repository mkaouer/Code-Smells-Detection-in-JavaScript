/*
 * Copyright (c) 2000 jPOS.org.  All rights reserved.
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
 *    "This product includes software developed by the jPOS project 
 *    (http://www.jpos.org/)". Alternately, this acknowledgment may 
 *    appear in the software itself, if and wherever such third-party 
 *    acknowledgments normally appear.
 *
 * 4. The names "jPOS" and "jPOS.org" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission. For written permission, please contact 
 *    license@jpos.org.
 *
 * 5. Products derived from this software may not be called "jPOS",
 *    nor may "jPOS" appear in their name, without prior written
 *    permission of the jPOS project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  
 * IN NO EVENT SHALL THE JPOS PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the jPOS Project.  For more
 * information please see <http://www.jpos.org/>.
 */

package org.jpos.iso.channel;

import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.jpos.iso.*;
import org.jpos.iso.packager.XMLPackager;

/**
 * Implements an ISOChannel suitable to be used to connect to an X.25 PAD. 
 * It waits a limited amount of time to decide when a packet is ready
 * to be unpacked.
 *
 * @author  <a href="mailto:apr@cs.com.uy">Alejandro P. Revilla</a>
 * @version $Id$
 *
 * @see ISOMsg
 * @see ISOException
 * @see ISOChannel
 */
public class PADChannel extends BaseChannel {
    BufferedReader reader = null;
    protected byte[] header;
    /**
     * No-args constructor
     */
    public PADChannel () {
        super();
    }
    /**
     * Constructs client ISOChannel
     * @param host  server TCP Address
     * @param port  server port number
     * @param p     an ISOPackager
     * @see ISOPackager
     */
    public PADChannel (String host, int port, ISOPackager p) {
        super(host, port, p);
    }
    /**
     * Construct server ISOChannel
     * @param p     an ISOPackager
     * @see ISOPackager
     * @exception IOException
     */
    public PADChannel (ISOPackager p) throws IOException {
        super(p);
    }
    /**
     * constructs a server ISOChannel associated with a Server Socket
     * @param p     an ISOPackager
     * @param serverSocket where to accept a connection
     * @exception IOException
     * @see ISOPackager
     */
    public PADChannel (ISOPackager p, ServerSocket serverSocket) 
        throws IOException
    {
        super(p, serverSocket);
    }
    /**
     * @return a byte array with the received message
     * @exception IOException
     */
    protected byte[] streamReceive() throws IOException {
	int c, k=0, len = 1;
	Vector v = new Vector();

	c = serverIn.read();
	if (c == -1)
	    throw new EOFException ("connection closed");
	byte[] b = new byte[1];
	b[0] = (byte) c;
	v.addElement (b);

	// Wait for packets until timeout
	while ((c = serverIn.available()) > 0) {
	    b = new byte[c];
	    if (serverIn.read (b) != c)
		throw new EOFException ("connection closed");
	    v.addElement (b);
	    len += c;
	    try {
		Thread.sleep (50);
	    } catch (InterruptedException e) { }
	}

	byte[] d = new byte[len];
	for (int i=0; i<v.size(); i++) {
	    b = (byte[]) v.elementAt(i);
	    System.arraycopy (b, 0, d, k, b.length);
	    k += b.length;
	}
	return d;
    }
    protected void connect (Socket socket) throws IOException {
	super.connect (socket);
	reader = new BufferedReader (new InputStreamReader (serverIn));
    }
    public void disconnect () throws IOException {
	super.disconnect ();
	reader = null;
    }
    protected int getHeaderLength() { 
        return header != null ? header.length : 0;
    }
    public void setHeader (byte[] header) {
	this.header = header;
    }
    /**
     * @param header Hex representation of header
     */
    public void setHeader (String header) {
	setHeader (
	    ISOUtil.hex2byte (header.getBytes(), 0, header.getBytes().length)
	);
    }
    public byte[] getHeader () {
	return header;
    }
    protected void sendMessageHeader(ISOMsg m, int len) throws IOException { 
	if (m.getHeader() != null)
            serverOut.write(m.getHeader());
        else if (header != null) 
            serverOut.write(header);
    }
}
