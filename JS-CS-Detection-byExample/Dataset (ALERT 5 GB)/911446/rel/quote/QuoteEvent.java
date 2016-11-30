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

import java.util.EventObject;
import java.util.List;

/**
 * Representation of an event indicating that a new intra-day
 * quote has been downloaded.
 *
 * @author Andrew Leppard
 * @see IDQuoteCache
 * @see QuoteEvent
 */
public class QuoteEvent extends EventObject {

    /**
     * Create a new quote event based on the given module.
     *
     * @param quoteCache The intra-day quote cache
     */
    public QuoteEvent(IDQuoteCache quoteCache) {
        super(quoteCache);
    }
}