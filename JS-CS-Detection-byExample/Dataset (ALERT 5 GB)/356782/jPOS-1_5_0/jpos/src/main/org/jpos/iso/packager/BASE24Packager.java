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

package org.jpos.iso.packager;

import org.jpos.iso.IFA_AMOUNT;
import org.jpos.iso.IFA_BINARY;
import org.jpos.iso.IFA_BITMAP;
import org.jpos.iso.IFA_LLCHAR;
import org.jpos.iso.IFA_LLLCHAR;
import org.jpos.iso.IFA_LLLNUM;
import org.jpos.iso.IFA_LLNUM;
import org.jpos.iso.IFA_NUMERIC;
import org.jpos.iso.IF_CHAR;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOFieldPackager;

/**
 * BASE24 Packager made from ISO 8583 v1987 ASCII Packager
 *
 * @author Mike Trank <mike@netcomsa.com>
 * @version $Id$
 * @see ISOPackager
 * @see ISOBasePackager
 * @see ISOComponent
 */
 /*
 * This is almost the same as ISO87A packager, just a few of the 
 *  field lengths are different, and I changed some stuff in the 
 *  private fields from S-121 to S-128 that ACI BASE24 uses.
 *  There are notes where the comments are........ Mike.
 */
public class BASE24Packager extends ISOBasePackager {
    protected ISOFieldPackager fld[] = {
            new IFA_NUMERIC (  4, "MESSAGE TYPE INDICATOR"),
            new IFA_BITMAP  ( 16, "BIT MAP"),
            new IFA_LLNUM   ( 19, "PAN - PRIMARY ACCOUNT NUMBER"),
            new IFA_NUMERIC (  6, "PROCESSING CODE"),
            new IFA_NUMERIC ( 12, "AMOUNT, TRANSACTION"),
            new IFA_NUMERIC ( 12, "AMOUNT, SETTLEMENT"),
            new IFA_NUMERIC ( 12, "AMOUNT, CARDHOLDER BILLING"),
            new IFA_NUMERIC ( 10, "TRANSMISSION DATE AND TIME"),
            new IFA_NUMERIC (  8, "AMOUNT, CARDHOLDER BILLING FEE"),
            new IFA_NUMERIC (  8, "CONVERSION RATE, SETTLEMENT"),
            new IFA_NUMERIC (  8, "CONVERSION RATE, CARDHOLDER BILLING"),
            new IFA_NUMERIC (  6, "SYSTEM TRACE AUDIT NUMBER"),
            new IFA_NUMERIC (  6, "TIME, LOCAL TRANSACTION"),
            new IFA_NUMERIC (  4, "DATE, LOCAL TRANSACTION"),
            new IFA_NUMERIC (  4, "DATE, EXPIRATION"),
            new IFA_NUMERIC (  4, "DATE, SETTLEMENT"),
            new IFA_NUMERIC (  4, "DATE, CONVERSION"),
            new IFA_NUMERIC (  4, "DATE, CAPTURE"),
            new IFA_NUMERIC (  4, "MERCHANTS TYPE"),
            new IFA_NUMERIC (  3, "ACQUIRING INSTITUTION COUNTRY CODE"),
            new IFA_NUMERIC (  3, "PAN EXTENDED COUNTRY CODE"),
            new IFA_NUMERIC (  3, "FORWARDING INSTITUTION COUNTRY CODE"),
            new IFA_NUMERIC (  3, "POINT OF SERVICE ENTRY MODE"),
            new IFA_NUMERIC (  3, "CARD SEQUENCE NUMBER"),
            new IFA_NUMERIC (  3, "NETWORK INTERNATIONAL IDENTIFIEER"),
            new IFA_NUMERIC (  2, "POINT OF SERVICE CONDITION CODE"),
            new IFA_NUMERIC (  2, "POINT OF SERVICE PIN CAPTURE CODE"),
            new IFA_NUMERIC (  1, "AUTHORIZATION IDENTIFICATION RESP LEN"),
            new IFA_AMOUNT  (  9, "AMOUNT, TRANSACTION FEE"),
            new IFA_AMOUNT  (  9, "AMOUNT, SETTLEMENT FEE"),
            new IFA_AMOUNT  (  9, "AMOUNT, TRANSACTION PROCESSING FEE"),
            new IFA_AMOUNT  (  9, "AMOUNT, SETTLEMENT PROCESSING FEE"),
            new IFA_LLNUM   ( 11, "ACQUIRING INSTITUTION IDENT CODE"),
            new IFA_LLNUM   ( 11, "FORWARDING INSTITUTION IDENT CODE"),
            new IFA_LLCHAR  ( 28, "PAN EXTENDED"),
            new IFA_LLNUM   ( 37, "TRACK 2 DATA"),
            new IFA_LLLCHAR (104, "TRACK 3 DATA"),
            new IF_CHAR     ( 12, "RETRIEVAL REFERENCE NUMBER"),
            new IF_CHAR     (  6, "AUTHORIZATION IDENTIFICATION RESPONSE"),
            new IF_CHAR     (  2, "RESPONSE CODE"),
            new IF_CHAR     (  3, "SERVICE RESTRICTION CODE"),
// Mike changed the following field to 16 bytes from 8 in ISO87A
            new IF_CHAR     ( 16, "CARD ACCEPTOR TERMINAL IDENTIFICACION"),
            new IF_CHAR     ( 15, "CARD ACCEPTOR IDENTIFICATION CODE" ),
            new IF_CHAR     ( 40, "CARD ACCEPTOR NAME/LOCATION"),
// Mike changed the folowing field to 27 from 25 in ISO87A
            new IFA_LLCHAR  ( 25, "ADITIONAL RESPONSE DATA"),
            new IFA_LLCHAR  ( 76, "TRACK 1 DATA"),
            new IFA_LLLCHAR (999, "ADITIONAL DATA - ISO"),
            new IFA_LLLCHAR (999, "ADITIONAL DATA - NATIONAL"),
            new IFA_LLLCHAR (999, "ADITIONAL DATA - PRIVATE"),
            new IF_CHAR     (  3, "CURRENCY CODE, TRANSACTION"),
            new IF_CHAR     (  3, "CURRENCY CODE, SETTLEMENT"),
            new IF_CHAR     (  3, "CURRENCY CODE, CARDHOLDER BILLING"   ),
            new IFA_BINARY  (  8, "PIN DATA"   ),
            new IFA_NUMERIC ( 16, "SECURITY RELATED CONTROL INFORMATION"),
            new IFA_LLLCHAR (120, "ADDITIONAL AMOUNTS"),
            new IFA_LLLCHAR (999, "RESERVED ISO"),
            new IFA_LLLCHAR (999, "RESERVED ISO"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL"),
            new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
            new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
            new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
            new IFA_LLLCHAR (999, "RESERVED PRIVATE"),
// mike changed the following to 16 from 8 in ISO87A
            new IFA_BINARY  ( 16, "MESSAGE AUTHENTICATION CODE FIELD"),
            new IFA_BINARY  (  1, "BITMAP, EXTENDED"),
            new IFA_NUMERIC (  1, "SETTLEMENT CODE"),
            new IFA_NUMERIC (  2, "EXTENDED PAYMENT CODE"),
            new IFA_NUMERIC (  3, "RECEIVING INSTITUTION COUNTRY CODE"),
            new IFA_NUMERIC (  3, "SETTLEMENT INSTITUTION COUNTRY CODE"),
            new IFA_NUMERIC (  3, "NETWORK MANAGEMENT INFORMATION CODE"),
            new IFA_NUMERIC (  4, "MESSAGE NUMBER"),
            new IFA_NUMERIC (  4, "MESSAGE NUMBER LAST"),
            new IFA_NUMERIC (  6, "DATE ACTION"),
            new IFA_NUMERIC ( 10, "CREDITS NUMBER"),
            new IFA_NUMERIC ( 10, "CREDITS REVERSAL NUMBER"),
            new IFA_NUMERIC ( 10, "DEBITS NUMBER"),
            new IFA_NUMERIC ( 10, "DEBITS REVERSAL NUMBER"),
            new IFA_NUMERIC ( 10, "TRANSFER NUMBER"),
            new IFA_NUMERIC ( 10, "TRANSFER REVERSAL NUMBER"),
            new IFA_NUMERIC ( 10, "INQUIRIES NUMBER"),
            new IFA_NUMERIC ( 10, "AUTHORIZATION NUMBER"),
            new IFA_NUMERIC ( 12, "CREDITS, PROCESSING FEE AMOUNT"),
            new IFA_NUMERIC ( 12, "CREDITS, TRANSACTION FEE AMOUNT"),
            new IFA_NUMERIC ( 12, "DEBITS, PROCESSING FEE AMOUNT"),
            new IFA_NUMERIC ( 12, "DEBITS, TRANSACTION FEE AMOUNT"),
            new IFA_NUMERIC ( 16, "CREDITS, AMOUNT"),
            new IFA_NUMERIC ( 16, "CREDITS, REVERSAL AMOUNT"),
            new IFA_NUMERIC ( 16, "DEBITS, AMOUNT"),
            new IFA_NUMERIC ( 16, "DEBITS, REVERSAL AMOUNT"),
            new IFA_NUMERIC ( 42, "ORIGINAL DATA ELEMENTS"),
            new IF_CHAR     (  1, "FILE UPDATE CODE"),
            new IF_CHAR     (  2, "FILE SECURITY CODE"),
            new IF_CHAR     (  6, "RESPONSE INDICATOR"),
            new IF_CHAR     (  7, "SERVICE INDICATOR"),
            new IF_CHAR     ( 42, "REPLACEMENT AMOUNTS"),
            new IFA_BINARY  ( 16, "MESSAGE SECURITY CODE"),
            new IFA_AMOUNT  ( 17, "AMOUNT, NET SETTLEMENT"),
            new IF_CHAR     ( 25, "PAYEE"),
            new IFA_LLNUM   ( 11, "SETTLEMENT INSTITUTION IDENT CODE"),
            new IFA_LLNUM   ( 11, "RECEIVING INSTITUTION IDENT CODE"),
            new IFA_LLCHAR  ( 17, "FILE NAME"),
            new IFA_LLCHAR  ( 28, "ACCOUNT IDENTIFICATION 1"),
            new IFA_LLCHAR  ( 28, "ACCOUNT IDENTIFICATION 2"),
            new IFA_LLLCHAR (100, "TRANSACTION DESCRIPTION"),
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED ISO USE"), 
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"   ),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"  ),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFA_LLLCHAR (999, "RESERVED NATIONAL USE"),
            new IFA_LLLCHAR (999, "RESERVED PRIVATE USE"),
            new IFA_LLLCHAR (23, "S-121 BASE24-POS AUTH INDICATORS"),
/* S-122 is described inacurately ( or at least inconsistently ) in the 
 *  Base 24book. It is described as a maximum 11-byte varible numeric
 *  field but it is actually maximum 14-bytes, taking into account
 *  the 3 digit field-width part. */
            new IFA_LLLNUM (14, "S-122 CARD ISSUER IDENTIFICATION CODE"),
            new IFA_LLLCHAR (153, "S-123 CRYPTOGRAFIC SERVICE MESSAGE"),
            new IFA_LLLCHAR (12, "S-124 DEPOSIT TYPE OR BATCH/SHIFT DATA"),
            new IFA_LLLCHAR (15, "S-125 ATM ACCT INDICATOR OR POS SETTLEMENT DATA"),
            new IFA_LLLCHAR (999, "S-126 ATM ADDIC. DATA OR POS PRE-AUTH/CHARGEBACK"),
            new IFA_LLCHAR (99, "BASE24-POS User Data"),
            new IFA_NUMERIC (16, "MAC 2")
        };
    public BASE24Packager() {
        super();
        setFieldPackager(fld);
    }
}
