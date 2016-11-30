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
 * Provides an interface for converting to/from a text string containing a
 * single intra-day quote from/to the internal stock quote object.
 *
 * @author Andrew Leppard
 */
public interface IDQuoteFilter {

    /**
     * Return the name of the filter.
     *
     * @return	the name of the filter
     */
    public String getName();

    /**
     * Parse the given text string and returns the stock quote or null
     * if it did not contain a valid quote.
     *
     * @param	quoteLine	a single line of text containing a quote.
     * @return	the stock quote
     * @exception QuoteFormatException if the quote could not be parsed
     */
    public IDQuote toIDQuote(String quoteLine) throws QuoteFormatException;

    /**
     * Convert the given stock quote to a string line.
     *
     * @param	quote	a stock quote
     * @return	string version of the quote
     */
    public String toString(IDQuote quote);
}