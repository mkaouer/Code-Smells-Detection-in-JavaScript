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

package org.mov.ui;

import java.text.NumberFormat;

/**
 * Representation of a quote value for display in a table. This class is
 * used by the {@link AbstractTable} class to identify the value type so that
 * it can render the value correctly. A quote value might be the day open,
 * day close, day high etc of the quote.
 *
 * @author Andrew Leppard
 */
public class QuoteFormat implements Comparable {

    // The quote value.
    private double quote;

    // Use NumberFormat to format the value.
    private static NumberFormat format;

    /**
     * Create a new quote value format object.
     *
     * @param quote the quote value.
     */
    public QuoteFormat(double quote) {
        this.quote = quote;
    }

    /**
     * Convert from a quote (in dollars) to string. 
     *
     * @param	quote	the quote
     * @return	the quote string
     */
    public static String quoteToString(double quote) {
        return getNumberFormat().format(quote);
    }

    /**
     * Create a string representation of the quote value.
     *
     * @return string representation of the quote value.
     */
    public String toString() {
        return quoteToString(getQuote());
    }

    /**
     * Return the quote value.
     *
     * @return the quote value.
     */
    public double getQuote() {
        return quote;
    }

    /**
     * Compare two quote values.
     *
     * @param object object to compare to
     * @return	the value <code>0</code> if the objects are equal;
     * <code>1</code> if this object is after the specified
     * object or
     * <code>-1</code> if this object is before the specified
     * object
     */
    public int compareTo(Object object) {
        QuoteFormat format = (QuoteFormat)object;

        if(getQuote() < format.getQuote())
            return -1;
        if(getQuote() > format.getQuote())
            return 1;
        else
            return 0;
    }

    /**
     * Get number format object for this class.
     *
     * @return the number format.
     */
    private static NumberFormat getNumberFormat() {
        // Synchronisation cannot cause issues here. So this code
        // isn't synchronised.
        if(format == null) {
            format = NumberFormat.getInstance();
            format.setMinimumIntegerDigits(1);
            format.setMinimumFractionDigits(5);
            format.setMaximumFractionDigits(5);
        }

        return format;
    }
    
}
