/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/

package nz.org.venice.quote;

import java.lang.Throwable;

/**
 * This exception represents the error condition that occurs when the caller
 * is trying to retrieve a quote that is not present. This exception would
 * occur if a caller was trying to retrieve a quote for a public holiday for
 * example.
 */
public class MissingQuoteException extends Throwable {

    private static MissingQuoteException instance = new MissingQuoteException();

    private MissingQuoteException() {
        // nothing to do
    }

    /**
     * Return the singleton instance of this exception. There is only a single
     * instance of this exception defined. The reason is that this exception
     * is thrown around a lot so needs to be fast - and the stack trace is
     * not important. See "Java Performance Tuning" for an explanation.
     */
    public static synchronized MissingQuoteException getInstance() {
        return instance;
    }
}
