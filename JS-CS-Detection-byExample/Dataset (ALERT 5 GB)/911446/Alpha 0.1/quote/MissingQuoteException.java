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

package org.mov.quote;

/**
 * This exception represents the error condition that occurs when the caller
 * is trying to retrieve a quote that is not present. This exception would
 * occur if a caller was trying to retrieve a quote for a public holiday for
 * example.
 */
public class MissingQuoteException extends java.lang.Throwable {

    /**
     * Create an exception that represents the error condition that occurs
     * when the caller is trying to retrieve a quote that is not present.
     */
    public MissingQuoteException() {
        // nothing to do
    }
}
