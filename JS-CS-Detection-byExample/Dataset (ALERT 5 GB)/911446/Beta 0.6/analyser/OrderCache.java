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

package org.mov.analyser;

import java.util.Collections;
import java.util.List;
import java.util.HashMap;

import org.mov.quote.EODQuoteBundle;

/**
 * Caches the order of stock quotes in a quote bundle. The GP allows the user
 * to order the evaluation of stock quotes, by several criteria such as
 * unordered, alphabetically by symbol, volume decreasing etc. This gives
 * priority to certain stocks. For example the user might prefer the GP
 * to concentrate on high volume stocks, so ordering the stocks by
 * volume will give high volume stocks a priority. Since sorting is
 * a slow operation, and the GP traverses over the same day many times,
 * it makes sense to cache the order. Profiling results showed this
 * provides a significant speed increase.
 *
 * @author Andrew Leppard
 * @see OrderComparator
 * @see QuoteRangePage
 */
public class OrderCache {

    // The quote bundle containing the quotes we are ordering
    private EODQuoteBundle quoteBundle;

    // A comparator which orders the quotes
    private OrderComparator orderComparator;

    // The order cache - mapping dates to a list of ordered symbols
    private HashMap dayOrders;

    /**
     * Create a new order cache.
     *
     * @param quoteBundle the quotes to order
     * @param orderComparator the method of ordering
     */
    public OrderCache(EODQuoteBundle quoteBundle, OrderComparator orderComparator) {
        this.quoteBundle = quoteBundle;
        this.orderComparator = orderComparator;

        dayOrders = new HashMap();
    }

    /**
     * Return a list of the given date's ordered symbols.
     *
     * @param dateOffset fast access date offset
     * @return ordered list of symbols
     */
    public List getTodaySymbols(int dateOffset) {

        // Need a way of looking up items in the HashMap without creating an object
        Integer date = new Integer(dateOffset);
        List symbols = (List)dayOrders.get(date);

        // If we haven't cached today's symbols then find the symbols
        // in the quote bundle for today and sort them
        if(symbols == null) {
            symbols = quoteBundle.getSymbols(dateOffset);
            orderComparator.setDateOffset(dateOffset);
            Collections.sort(symbols, orderComparator);
            dayOrders.put(date, symbols);
        }

        return symbols;
    }

    /**
     * Return whether the input order comparator is actually ordered.
     *
     * @return <code>true</code> if the order comparator is ordered
     */
    public boolean isOrdered() {
        return orderComparator.isOrdered();
    }
}
