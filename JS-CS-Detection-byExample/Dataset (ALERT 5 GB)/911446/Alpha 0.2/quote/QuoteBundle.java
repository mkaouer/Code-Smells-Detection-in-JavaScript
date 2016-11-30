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

import java.util.Iterator;
import java.util.List;

import org.mov.analyser.gp.GPQuoteBundle;
import org.mov.parser.EvaluationException;
import org.mov.util.TradingDate;

/** 
 * This interface provides a set of functions to allow Venice to access stock quotes
 * and entities that can be treated like stock quotes, e.g. user indices and portfolios.
 * Typically the caller will want to use the {@link ScriptQuoteBundle} implementation
 * which allows dynamic resizing if the user tries to access older quotes not in the
 * quote bundle.
 *
 * @see GPQuoteBundle
 * @see ScriptQuoteBundle
 * @see Quote
 * @see QuoteRange
 * @see QuoteBundleCache
 * @see QuoteCache
 * @see Symbol
 */
public interface QuoteBundle {

    /** 
     * Get a stock quote. 
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return the quote
     * @exception MissingQuoteException if the quote was not found
     */
    public float getQuote(Symbol symbol, int quoteType, int dateOffset)
	throws MissingQuoteException;

    /** 
     * Get a stock quote. This function has been primarily created for Gondola
     * scripts. It passes in the current date and the date offset so that
     * specialised QuoteBundle implementations such as {@link GPQuoteBundle} can prevent the GP
     * accessing 'future' dates.
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param today fast access date offset of current date, see {@link QuoteCache}
     * @param offset offset from current date
     * @return the quote
     * @exception EvaluationException if the script isn't allow access to the quote.
     */
    public float getQuote(Symbol symbol, int quoteType, int today, int offset)
	throws EvaluationException, MissingQuoteException;

    /** 
     * Get a stock quote. 
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param date the date
     * @return the quote
     * @exception MissingQuoteException if the quote was not found
     */
    public float getQuote(Symbol symbol, int quoteType, TradingDate date) 
	throws MissingQuoteException;

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return  <code>true</code> if this symbol should be in the quote bundle, 
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(Symbol symbol, int dateOffset);

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param date      the date
     * @return  <code>true</code> if this symbol should be in the quote bundle, 
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(Symbol symbol, TradingDate date);

    /**
     * Return an iterator over this quote bundle. The iterator will return, in order,
     * all the quotes in this bundle.
     *
     * @return iterator over the quotes
     * @see Quote
     */
    public Iterator iterator();

    /**
     * Return the quote range which specifies this quote bundle.
     *
     * @return the quote range
     */
    public QuoteRange getQuoteRange();

    /**
     * Set the qutoe range which specifies this quote bundle.
     *
     * @param quoteRange        the new quote range
     */
    public void setQuoteRange(QuoteRange quoteRange);

    /**
     * Return the first symbol in the quote bundle. 
     * 
     * @return the first symbol
     */
    public Symbol getFirstSymbol();

    /**
     * Returns all the symbols in the quote bundle.
     *
     * @return all symbols
     */
    public List getAllSymbols();

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return all symbols
     */
    public List getSymbols(int dateOffset);

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param date the date
     * @return all symbols
     */
    public List getSymbols(TradingDate date);

    /**
     * Return the first date in this quote bundle.
     *
     * @return the earliest date
     */
    public TradingDate getFirstDate();

    /**
     * Return the last date in this quote bundle.
     *
     * @return the latest date
     */
    public TradingDate getLastDate();

    /**
     * Return the fast access date offset of the first date in this quote bundle
     *
     * @return the first date offset, see {@link QuoteCache}
     */
    public int getFirstDateOffset();

    /**
     * Return the fast access date offset of the last date in this quote bundle
     *
     * @return the first date offset, see {@link QuoteCache}
     */
    public int getLastDateOffset();

    /**
     * Convert between a fast access date offset to an actual date.
     *
     * @param dateOffset        fast access date offset, see {@link QuoteCache}
     * @return the date
     */
    public TradingDate offsetToDate(int dateOffset);

    /**
     * Convert between a date and a fast access date offset.
     *
     * @param date the date
     * @return fast access date offset, see {@link QuoteCache}
     */
    public int dateToOffset(TradingDate date)
        throws WeekendDateException;
}

