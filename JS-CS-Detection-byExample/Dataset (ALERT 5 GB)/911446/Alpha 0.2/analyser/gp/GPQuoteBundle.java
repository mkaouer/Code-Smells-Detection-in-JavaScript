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

package org.mov.analyser.gp;

import java.util.Iterator;
import java.util.List;

import org.mov.parser.EvaluationException;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.Quote;
import org.mov.quote.QuoteBundle;
import org.mov.quote.QuoteBundleCache;
import org.mov.quote.QuoteBundleIterator;
import org.mov.quote.QuoteCache;
import org.mov.quote.QuoteRange;
import org.mov.quote.Symbol;
import org.mov.quote.WeekendDateException;
import org.mov.util.TradingDate;

public class GPQuoteBundle implements QuoteBundle {

    private QuoteBundle quoteBundle;
    private int earliestDateOffset;

    public GPQuoteBundle(QuoteBundle quoteBundle, int window) {
        this.quoteBundle = quoteBundle;

        earliestDateOffset = getFirstDateOffset() - window;
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
    public float getQuote(Symbol symbol, int quoteType, int dateOffset)
	throws MissingQuoteException {

        return quoteBundle.getQuote(symbol, quoteType, dateOffset);
    }

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
     * @exception EvaluationException if the script isn't allowed access to the quote.
     */
    public float getQuote(Symbol symbol, int quoteType, int today, int offset)
	throws EvaluationException, MissingQuoteException {
        
        // Trying to access a future quote?
        if(offset > 0)
            throw new EvaluationException("future date");

        // Trying to access a date too far into the past?
        else if(offset < earliestDateOffset)
            throw new EvaluationException("date too far into the past");

        // Date is within range
        else
            return quoteBundle.getQuote(symbol, quoteType, today + offset);
    }

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
        throws MissingQuoteException {
        assert false;
        return 0.0F;
    }

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return  <code>true</code> if this symbol should be in the quote bundle, 
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(Symbol symbol, int dateOffset) {
        return quoteBundle.containsQuote(symbol, dateOffset);
    }

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param date      the date
     * @return  <code>true</code> if this symbol should be in the quote bundle, 
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(Symbol symbol, TradingDate date) {
        return quoteBundle.containsQuote(symbol, date);
    }

    /**
     * Return an iterator over this quote bundle. The iterator will return, in order,
     * all the quotes in this bundle.
     *
     * @return iterator over the quotes
     * @see Quote
     */
    public Iterator iterator() {
        return new QuoteBundleIterator(this);
    }

    /**
     * Return the quote range which specifies this quote bundle.
     *
     * @return the quote range
     */
    public QuoteRange getQuoteRange() {
	return quoteBundle.getQuoteRange();
    }

    /**
     * Set the qutoe range which specifies this quote bundle.
     *
     * @param quoteRange        the new quote range
     */
    public void setQuoteRange(QuoteRange quoteRange) {
        assert false;
    }

    /**
     * Return the first symbol in the quote bundle. 
     * 
     * @return the first symbol
     */
    public Symbol getFirstSymbol() {
        return quoteBundle.getFirstSymbol();
    }

    /**
     * Returns all the symbols in the quote bundle.
     *
     * @return all symbols
     */
    public List getAllSymbols() {
        return quoteBundle.getAllSymbols();
    }

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return all symbols
     */
    public List getSymbols(int dateOffset) {	
        return quoteBundle.getSymbols(dateOffset);
    }

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param date the date
     * @return all symbols
     */
    public List getSymbols(TradingDate date) {
        return quoteBundle.getSymbols(date);
    }

    /**
     * Return the first date in this quote bundle.
     *
     * @return the earliest date
     */
    public TradingDate getFirstDate() {
        return quoteBundle.getFirstDate();
    }

    /**
     * Return the last date in this quote bundle.
     *
     * @return the latest date
     */
    public TradingDate getLastDate() {
        return quoteBundle.getLastDate();
    }

    /**
     * Return the fast access date offset of the first date in this quote bundle
     *
     * @return the first date offset, see {@link QuoteCache}
     */
    public int getFirstDateOffset() {
        return quoteBundle.getFirstDateOffset();
    }

    /**
     * Return the fast access date offset of the last date in this quote bundle
     *
     * @return the first date offset, see {@link QuoteCache}
     */
    public int getLastDateOffset() {
        return quoteBundle.getLastDateOffset();
    }

    /**
     * Convert between a fast access date offset to an actual date.
     *
     * @param dateOffset        fast access date offset, see {@link QuoteCache}
     * @return the date
     */
    public TradingDate offsetToDate(int dateOffset) {
        return quoteBundle.offsetToDate(dateOffset);
    }

    /**
     * Convert between a date and a fast access date offset.
     *
     * @param date the date
     * @return fast access date offset, see {@link QuoteCache}
     */
    public int dateToOffset(TradingDate date) 
        throws WeekendDateException {
        return quoteBundle.dateToOffset(date);
    }
}
