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

import org.mov.parser.EvaluationException;

import org.mov.util.TradingDate;

/**
 * This class provides a generic interface which can be used to access a bundle
 * of either end-of-day or intra-day quotes.
 *
 * @author Andrew Leppard
 */

public interface QuoteBundle {

    /**
     * Get a stock quote. This function has been primarily created for Gondola
     * scripts. It passes in the current date and the date offset so that
     * specialised QuoteBundle implementations can prevent the GP accessing 'future' dates.
     *
     * @param symbol    the stock symbol
     * @param quoteType the quote type, e.g. {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param now       fast access offset of current quote, for end-of-day quotes this
     *                  is the fast access date offset (see {@link EODQuoteCache}). For
     *                  intra-day quotes, it is the fast access time offset (see
     *                  {@link IDQuoteCache}).
     * @param offset    modifier offset to fast access offset
     * @return the quote
     * @exception EvaluationException if the script isn't allow access to the quote.
     * @exception MissingQuoteException if the quote was not found
     */
    public double getQuote(Symbol symbol, int quoteType, int now, int offset)
	throws EvaluationException, MissingQuoteException;


    /**
     * Get a stock quote. If the stock is earlier than the first date in the bundle, the
     * bundle will be expand to include the new date given. If the stock symbol is not
     * included in the original symbol list, the quote bundle will be expanded to include
     * it.
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param offset    fast access offset of current quote, for end-of-day quotes this
     *                  is the fast access date offset (see {@link EODQuoteCache}). For
     *                  intra-day quotes, it is the fast access time offset (see
     *                  {@link IDQuoteCache}).
     * @return the quote
     * @exception MissingQuoteException if the quote was not found
     */
    public double getQuote(Symbol symbol, int quoteType, int offset)
	throws MissingQuoteException;

    /**
     * Get a stock quote. If the stock is earlier than the first date in the bundle, the
     * bundle will be expand to include the new date given. If the stock symbol is not
     * included in the original symbol list, the quote bundle will be expanded to include
     * it.
     *
     * @param symbol  the stock symbol
     * @param offset  fast access offset of current quote, for end-of-day quotes this
     *                is the fast access date offset (see {@link EODQuoteCache}). For
     *                intra-day quotes, it is the fast access time offset (see
     *                {@link IDQuoteCache}).
     * @return the quote
     * @exception MissingQuoteException if the quote was not found
     */
    public Quote getQuote(Symbol symbol, int offset)
        throws MissingQuoteException;

    /**
     * Convert between a fast access offset to an actual date. Intra-day quotes
     * will return the same date for each offset because the offset refers
     * to the time within that date. End-of-day quotes will return a different
     * date for each fast access offset.
     *
     * @param offset  fast access offset, see {@link EODQuoteCache}
     * @return the date
     */
    public TradingDate offsetToDate(int offset);

    /**
     * Return the fast access offset from the given quote.
     *
     * @param quote quote
     * @return fast access offset
     */
    public int getOffset(Quote quote)
        throws WeekendDateException;

    /**
     * Return the fast access offset for the earliest quote in the bundle.
     *
     * @return fast access offset
     */
    public int getFirstOffset();

    /**
     * Return the fast access offset for the latest quote in the bundle.
     *
     * @return fast access offset
     */
    public int getLastOffset();



}
