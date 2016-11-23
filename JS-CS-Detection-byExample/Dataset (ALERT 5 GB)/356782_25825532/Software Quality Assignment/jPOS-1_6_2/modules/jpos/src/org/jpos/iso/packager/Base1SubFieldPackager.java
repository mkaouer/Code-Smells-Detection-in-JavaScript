/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2008 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.iso.packager;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOBitMap;
import org.jpos.iso.ISOBitMapPackager;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFieldPackager;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

/**
 * ISO 8583 v1987 BINARY Packager 
 * customized for VISA Base1 subfields
 * 
 *
 * @author <a href="mailto:eoin.flood@orbiscom.com">Eoin Flood</a>
 * @version $Id$
 * @see ISOPackager
 * @see ISOBasePackager
 * @see ISOComponent
 */

public class Base1SubFieldPackager extends ISOBasePackager 
{
    // These methods are identical to ISOBasePackager
    // except that fld[1] has been replaced with fld[0]
    // and a secondard bitmap is not allowed

    protected boolean emitBitMap()
    {
        return (fld[0] instanceof ISOBitMapPackager);
    }

    protected int getFirstField()
    {
        return (fld[0] instanceof ISOBitMapPackager) ? 1 : 0;
    }

    protected ISOFieldPackager getBitMapfieldPackager() 
    {
        return fld[0];
    }

    /**
     * Unpack a packed subfield into
     * its corresponding ISOComponent
     */

    public int unpack (ISOComponent m, byte[] b) throws ISOException 
    {
        LogEvent evt = new LogEvent (this, "unpack");
        try 
        {
            if (m.getComposite() != m) 
                throw new ISOException ("Can't call packager on non Composite");
            if (logger != null)  // save a few CPU cycle if no logger available
                evt.addMessage (ISOUtil.hexString (b));

            int consumed=0;
            ISOBitMap bitmap = new ISOBitMap (-1);

            BitSet bmap = null;
            int maxField = fld.length;
            if (emitBitMap()) 
            {
                consumed += getBitMapfieldPackager().unpack(bitmap,b,consumed);
                bmap = (BitSet) bitmap.getValue();
                m.set (bitmap);
                maxField = bmap.size();
            }
            for (int i=getFirstField(); i<maxField && consumed < b.length; i++) 
            {
                if (bmap == null || bmap.get(i)) 
                {
                    ISOComponent c = fld[i].createComponent(i);
                    consumed += fld[i].unpack (c, b, consumed);
                    m.set(c);
                }
            }
            if (b.length != consumed) 
            {
                evt.addMessage (
                "WARNING: unpack len=" +b.length +" consumed=" +consumed);
            }
            return consumed;
        } 
        catch (ISOException e) 
        {
            evt.addMessage (e);
            throw e;
        } 
        finally 
        {
            Logger.log (evt);
        }
    }

    /**
     * Pack the subfield into a byte array
     */ 

    public byte[] pack (ISOComponent m) throws ISOException 
    {
        LogEvent evt = new LogEvent (this, "pack");
        try 
        {
            ISOComponent c;
            Vector v = new Vector();
            Hashtable fields = m.getChildren();
            int len = 0;
            byte[] b;

            if (emitBitMap()) 
            {
                // BITMAP (-1 in HashTable)
                c = (ISOComponent) fields.get (new Integer (-1));
                b = getBitMapfieldPackager().pack(c);
                len += b.length;
                v.addElement (b);
            }

            for (int i=getFirstField(); i<=m.getMaxField(); i++) 
            {
                if ((c = (ISOComponent) fields.get (new Integer (i))) != null) 
                {
                    try 
                    {
                        b = fld[i].pack(c);
                        len += b.length;
                        v.addElement (b);
                    } 
                    catch (Exception e) 
                    {
                        evt.addMessage ("error packing field "+i);
                        evt.addMessage (c);
                        evt.addMessage (e);
                    }
                }
            }
            int k = 0;
            byte[] d = new byte[len];
            for (int i=0; i<v.size(); i++) 
            {
                b = (byte[]) v.elementAt(i);
                for (int j=0; j<b.length; j++)
                d[k++] = b[j];
            }
            if (logger != null)  // save a few CPU cycle if no logger available
                evt.addMessage (ISOUtil.hexString (d));
            return d;
        } 
        catch (ISOException e) 
        {
            evt.addMessage (e);
            throw e;
        } 
        finally 
        {
            Logger.log(evt);
        }
    }
}


