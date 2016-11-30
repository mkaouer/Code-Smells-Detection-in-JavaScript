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

import java.util.*;

import nz.org.venice.util.*;

/**
 * Iterator for traversing EODQuotes in an EODQuoteBundle. This iterator allows the user to
 * traverse each quote in any quote bundle. The quotes will be pulled out in date then
 * symbol order. To get an interator for a EODQuoteBundle, use the
 * {@link EODQuoteBundle#iterator} method.
 *
 * @author Andrew Leppard
 * @see EODQuote
 * @see EODQuoteBundle
 */
public class EODQuoteBundleIterator implements Iterator {

    private Iterator symbolsIterator;
    private EODQuoteBundle quoteBundle;

    private TradingDate nextDate;
    private Symbol nextSymbol;

    private boolean isMore;

    /**
     * Create a new iterator over the given quote bundle.
     */
    public EODQuoteBundleIterator(EODQuoteBundle quoteBundle) {
        this.quoteBundle = quoteBundle;

        nextDate = quoteBundle.getFirstDate();

        List symbols = quoteBundle.getSymbols(nextDate);
        symbolsIterator = symbols.iterator();

        // go to next quote
        isMore = true;
        findNext();
    }

    // Locate the next quote in the bundle and set the flag isMore to
    // indicate whether they are anymore quotes.
    private void findNext() {

        // Make sure we haven't already decided there isn't more. There might
        // not be, but we assume there are.
        assert isMore;

        boolean found = false;

        while(!found && isMore) {

            // Is there anymore symbols for this date?
            if(symbolsIterator.hasNext())
                nextSymbol = (Symbol)symbolsIterator.next();
            
            // No, try the next date.. and the next date...
            else {
                nextDate = nextDate.next(1);

                while(nextDate.compareTo(quoteBundle.getLastDate()) <= 0) {
                    List symbols = quoteBundle.getSymbols(nextDate);
                    
                    // Are there symbols in the cache?
                    if(symbols.size() > 0) {
                        symbolsIterator = symbols.iterator();
                        nextSymbol = (Symbol)symbolsIterator.next();
                        break;
                    }
                    nextDate = nextDate.next(1);
                }

                if(nextDate.compareTo(quoteBundle.getLastDate()) > 0) {
                    isMore = false;
                    break;
                }
            }

            // Check that the quote is actually in the bundle. When we load
            // a quote over all the ranges in the cache, it might lie and say
            // it starts on a certain date (i.e. the first date in the cache),
            // but it might not have any quotes until a much later date.
            try {
                double volume = 
                    quoteBundle.getQuote(nextSymbol, Quote.DAY_VOLUME, nextDate);
                found = true;
            }
            catch(MissingQuoteException e) {
                found = false;
            }
        }
    }

    /**
     * Return the next Quote in the EODQuoteBundle.
     *
     * @return quote the next quote
     */
    public Object next() {
        if(hasNext()) {
            int dateOffset;

            try {
                dateOffset = EODQuoteCache.getInstance().dateToOffset(nextDate);
            }
            catch(WeekendDateException e) {
                // hasNext() should have sorted this out
                assert false;
                return null;
            }

            try {
                Quote quote = quoteBundle.getQuote(nextSymbol, dateOffset);
                findNext();

                return (Object)quote;
            }
            catch(MissingQuoteException e) {
                // hasNext() should have sorted this out
                assert false;
                return null;
            }
        }
        else
            throw new NoSuchElementException();
    }

    /**
     * Return whether the EODQuoteBundle has anymore Quotes.
     *
     * @return whether there are anymore quotes
     */
    public boolean hasNext() {
        return isMore;
    }

    /**
     * Removing Quotes from the EODQuoteBundle is not supported.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
