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

package org.jpos.ui.factory;

import java.util.Observer;
import java.util.Observable;
import javax.swing.*;
import javax.swing.border.*;
import org.jdom.Element;

import org.jpos.iso.*;
import org.jpos.iso.gui.*;
import org.jpos.iso.packager.*;
import org.jpos.iso.channel.*;
import org.jpos.ui.UI;
import org.jpos.ui.UIFactory;
import org.jpos.util.NameRegistrar;

/**
 * @author Alejandro Revilla
 *
 * Creates an ISOMeter component
 * i.e:
 * <pre>
 *  &lt;iso-meter idref="id" scroll="true|false" refresh="nnn"/&gt
 * </pre>
 * @see org.jpos.ui.UIFactory
 */

public class ISOMeterFactory implements UIFactory {
    public JComponent create (UI ui, Element e) {
        ISOChannelPanel icp = null;
        try {
            Object obj = (Object) 
                NameRegistrar.get (e.getAttributeValue ("idref"));

            if (obj instanceof ISOChannel) {
                icp = new ISOChannelPanel ((ISOChannel) obj, e.getText ());
            } else if (obj instanceof Observable) {
                icp = new ISOChannelPanel (e.getText());
                ((Observable)obj).addObserver (icp);
            }
            ISOMeter meter = icp.getISOMeter ();
            if ("false".equals (e.getAttributeValue ("scroll")))
                meter.setScroll (false);
            String refresh = e.getAttributeValue ("refresh");
            if (refresh != null)
                meter.setRefresh (Integer.parseInt (refresh));
        } catch (Exception ex) {
            ex.printStackTrace ();
            return new JLabel (ex.getMessage());
        }
        return icp;
    }
}

