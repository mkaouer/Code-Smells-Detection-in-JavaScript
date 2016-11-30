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

import java.util.*;

import org.mov.util.*;

/** 
 * This class is identical to {@link QuoteBundle} except that it allows the caller
 * to query quotes that occur earlier than the first date in the quote bundle. If
 * this occurs, this class will expand the bundle to include the date given.
 * <p>
 * This is useful for <i>Gondola</i> scripts which don't specify their earliest needed quote
 * range in advance - but always specify their latest.
 */
public class ScriptQuoteBundle extends QuoteBundle {

    /**
     * Create a new quote bundle that represents the quotes in the given
     * quote range.
     *
     * @param quoteRange      the quote range
     */
    public ScriptQuoteBundle(QuoteRange quoteRange) {
        super(quoteRange);
    }

    /** 
     * Get a stock quote. If the stock is earlier than the first date in the bundle, the
     * bundle will be expand to include the new date given.
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return the quote
     * @exception MissingQuoteException if the quote was not found
     */
    public float getQuote(String symbol, int quoteType, int dateOffset)
	throws MissingQuoteException {
     
	float quote;

        try {
            quote = quoteCache.getQuote(symbol, quoteType, dateOffset);
        }
        catch(QuoteNotLoadedException e) {
            
            // If we couldn't load the quote - maybe the bundle isn't laoded?
            try {
                quote = tryReload(symbol, quoteType, dateOffset);
            }
            catch(QuoteNotLoadedException e2) {

                // If the quote is still null, maybe we need to expand the bundle?
                // First check to make sure the new date is older than any date in
                // the cache
                if(getFirstDate() != null && dateOffset < getFirstDateOffset()) {
                    try {
                        quote = tryExpand(symbol, quoteType, dateOffset);
                    }
                    catch(QuoteNotLoadedException e3) {
                        
                        // We tried everyting - we just don't have it
                        throw new MissingQuoteException();
                    }
                }
                else 
                    throw new MissingQuoteException();
            
            }
        }

        return quote;
    }

    // Try to expand the quote bundle so that it includes the current date.
    // Now reload the quote from the cache and return.
    private float tryExpand(String symbol, int quoteType, int dateOffset) 
        throws QuoteNotLoadedException {

        QuoteRange expandedQuoteRange = (QuoteRange)getQuoteRange().clone();
        TradingDate date = quoteCache.offsetToDate(dateOffset);
        expandedQuoteRange.setFirstDate(date);
        quoteBundleCache.expand(this, expandedQuoteRange);

        // Now try loading the quote again!
        return quoteCache.getQuote(symbol, quoteType, dateOffset); 
    }
}
