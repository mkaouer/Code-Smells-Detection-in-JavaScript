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

import java.util.ArrayList;
import java.util.List;

import org.mov.parser.EvaluationException;
import org.mov.util.TradingDate;

/**
 * When a task requires intra-day stock quotes, it should create an instance of this class
 * which represents all the task's required quotes. The task can then access quotes
 * from this class, which in turn reads its stock quotes from the global intra-day quote
 * cache - {@link IDQuoteCache}. To be notified of new intra-day quotes call
 * {@link IDQuoteCache#addQuoteListener}.
 * <p>
 * Example:
 * <pre>
 *      IDQuoteBundle quoteBundle = new IDQuoteBundle("CBA");
 *      try {
 *	    double = quoteBundle.getQuote("CBA", Quote.ASK, 0);
 *      }
 *      catch(QuoteNotLoadedException e) {
 *          //...
 *      }
 * </pre>
 *
 * @author Andrew Leppard
 * @see IDQuote
 * @see IDQuoteCache
 * @see EODQuoteBundle
 * @see Symbol
 */
public class IDQuoteBundle implements QuoteBundle {

    // Current date of all intra-day quotes
    private TradingDate date;

    // List of symbols in quote bundle
    private List symbols;

    // Quick references to remove need to getInstance() calls
    private static IDQuoteCache quoteCache = IDQuoteCache.getInstance();
    private static IDQuoteSync quoteSync = IDQuoteSync.getInstance();

    /**
     * Create a new intra-day quote bundle containing all today's quotes for
     * the given symbols.
     *
     * @param symbols the quote symbols
     */
    public IDQuoteBundle(List symbols) {
        this.symbols = symbols;

        // TODO: This isn't correct. Our latest quotes might be yesterday's.
        this.date = new TradingDate();
        
        // Tell the quote sync to download intra-day quotes for these symbols
        quoteSync.addSymbols(symbols);
    }

    public double getQuote(Symbol symbol, int quoteType, int now, int timeOffset)
	throws EvaluationException, MissingQuoteException {

        return getQuote(symbol, quoteType, now + timeOffset);
    }

    public double getQuote(Symbol symbol, int quoteType, int timeOffset)
	throws MissingQuoteException {

        double quote;

        try {
            quote = quoteCache.getQuote(symbol, quoteType, timeOffset);
        }
        catch(QuoteNotLoadedException e) {
            // Expand the quote range to include the symbol if we don't already have it
            if(!symbols.contains(symbol)) {
                symbols.add(symbol);
                quoteSync.addSymbols(symbols);
            }

            // The symbol won't be available until next polling period
            throw MissingQuoteException.getInstance();
        }

        return quote;
    }

    public Quote getQuote(Symbol symbol, int timeOffset)
        throws MissingQuoteException {

        Quote quote;

        try {
            quote = quoteCache.getQuote(symbol, timeOffset);
        }
        catch(QuoteNotLoadedException e) {
            // Expand the quote range to include the symbol if we don't already have it
            if(!symbols.contains(symbol)) {
                symbols.add(symbol);
                quoteSync.addSymbols(symbols);
            }

            // The symbol won't be available until next polling period
            throw MissingQuoteException.getInstance();            
        }

        return quote;
    }

    public TradingDate offsetToDate(int timeOffset) {
        // The entire quote bundle is on the same date
        return date;
    }

    /**
     * Retrieve the fast access offset from the given quote.
     *
     * @param quote quote
     * @return fast access offset
     */
    public int getOffset(Quote quote) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the fast access offset for the earliest quote in the bundle.
     *
     * @return fast access offset
     */
    public int getFirstOffset() {
        return quoteCache.getFirstTimeOffset();
    }

    /**
     * Return the fast access offset for the latest quote in the bundle.
     *
     * @return fast access offset
     */
    public int getLastOffset() {
        return quoteCache.getLastTimeOffset();
    }
}
