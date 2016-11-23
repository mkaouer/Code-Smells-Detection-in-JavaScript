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

package org.jpos.iso.filter;

import java.io.*;
import org.jpos.iso.*;
import org.jpos.iso.packager.*;
import org.xml.sax.SAXException;
import org.apache.xalan.xpath.XPathException;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xpath.XString;


import org.jpos.util.LogEvent;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOFilter.VetoException;

/**
 * Implements ISOFilter by means of XSL-Transformations
 * @author <a href="mailto:apr@cs.com.uy">Alejandro P. Revilla</a>
 * @version $Revision$ $Date$
 */
public class XSLTFilter implements ISOFilter, Configurable {
    boolean reread;
    String xsltfile;
    XSLTProcessor processor;
    XSLTInputSource xslt;
    XMLPackager packager;

    /**
     * Default noargs constructor
     * @throws SAXException
     * @throws ISOException
     */
    public XSLTFilter () throws ISOException, SAXException  {
	super();
	packager = new XMLPackager();
	processor = XSLTProcessorFactory.getProcessor();
        xsltfile  = null;
	reread    = true;
    }

    /**
     * @param xsltfile XSL Transformation file
     * @param reread true if you want XSLT file re-read from disk
     * @throws SAXException
     * @throws ISOException
     */
    public XSLTFilter (String xsltfile, boolean reread) 
	throws ISOException, SAXException
    {
	this();
	this.xsltfile = xsltfile;
	this.reread   = reread;
	xslt = new XSLTInputSource(xsltfile);
    }

   /**
    * <ul>
    *  <li>xsltfile - source XSL-T file
    *  <li>reread   - something != "no" will re-read source file
    * </ul>
    * @param cfg new ConfigurationFile
    */
    public void setConfiguration (Configuration cfg) 
	throws ConfigurationException
    {
	try {
	    xslt     = new XSLTInputSource(cfg.get("xsltfile"));
	    String s = cfg.get ("reread");
	    reread   =  (s == null || s.equals ("no"));
	} catch (Exception e) {
	    throw new ConfigurationException (e);
	}
    }

    /**
     * @param channel current ISOChannel instance
     * @param m ISOMsg to filter
     * @param evt LogEvent
     * @return an ISOMsg (possibly parameter m)
     * @throws VetoException
     */
    public ISOMsg filter (ISOChannel channel, ISOMsg m, LogEvent evt) 
	throws VetoException
    {
	try {
	    m.setPackager (packager);
	    ByteArrayOutputStream os = new ByteArrayOutputStream();

	    if (reread || xslt == null)
		xslt = new XSLTInputSource(xsltfile);

	    processor.process(
		new XSLTInputSource(new ByteArrayInputStream (m.pack())),
		xslt, 
		new XSLTResultTarget(os)
	    );
	    m.unpack (os.toByteArray());
	} catch (SAXException e) {
	    throw new VetoException(e);
	} catch (ISOException e) {
	    throw new VetoException(e);
	}
	return m;
    }
}