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
 * When a task requires stock quotes, it should create an instance of this class which represents
 * all the task's required quotes. The task can then access quotes from this class,
 * which in turn reads its stock quotes from a global quote cache - {@link QuoteCache}.
 * <p>
 * The purpose of this class is therefore to group together a set of quotes that are
 * needed by a single task. This grouping allows the quotes to be loaded in at one time,
 * which is much faster than loading them in quote by quote. 
 * <p>
 * Also by placing a set of quotes in a bundle it simplifies caching. Caching is performed
 * by {@link QuoteBundleCache}.
 *
 * Example:
 * <pre>
 *      QuoteRange quoteRange = new QuoteRange("CBA");
 *      QuoteBundle quoteBundle = new QuoteBundle(quoteRange);
 *      try {
 *	    float = quoteBundle.getQuote("CBA", Quote.DAY_OPEN, 0);
 *      }
 *      catch(QuoteNotLoadedException e) {
 *          //...
 *      }
 * </pre>
 *
 * If the quote bundle is going to be passed to the <i>Gondola</i> language, it should
 * be a {@link ScriptQuoteBundle}.
 *
 * @see ScriptQuoteBundle
 * @see Quote
 * @see QuoteRange
 * @see QuoteBundleCache
 * @see QuoteCache
 */
public class QuoteBundle {

    // Quotes contained in this bundle
    private QuoteRange quoteRange;

    /** For speed reasons, keep reference to the global quote cache */
    protected QuoteCache quoteCache;

    /** For speed reasons, keep reference to the global quote bundle cache */
    protected QuoteBundleCache quoteBundleCache;

    // Start and end date offsets (marked as 1 which indicates an illegal
    // date offset, date offsets start from 0 and go down).
    private int firstDateOffset = 1;
    private int lastDateOffset = 1;

    /**
     * Create a new quote bundle that represents the quotes in the given
     * quote range.
     *
     * @param quoteRange      the quote range
     */
    public QuoteBundle(QuoteRange quoteRange) {
	this.quoteRange = quoteRange;

	quoteCache = QuoteCache.getInstance();
	quoteBundleCache = QuoteBundleCache.getInstance();

	// Load it in now if its not already
	quoteBundleCache.load(this);
    }

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

                // Still not in cache! Should this quote even be in this bundle?
                assert containsQuote(symbol, dateOffset);

                // We tried everyting - we just don't have it
                throw new MissingQuoteException();
            }
        }

	return quote;
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
    public float getQuote(String symbol, int quoteType, TradingDate date) 
	throws MissingQuoteException {
	
	float quote;

	try {
	    quote = getQuote(symbol, quoteType, quoteCache.dateToOffset(date));
	}
	catch(WeekendDateException e) {
	    throw new MissingQuoteException();
	}

	return quote;
    }

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return  <code>true</code> if this symbol should be in the quote bundle, 
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(String symbol, int dateOffset) {

	if(getQuoteRange().containsSymbol(symbol) && 
	   dateOffset >= getFirstDateOffset() &&
	   dateOffset <= getLastDateOffset())
	    return true;
	else
	    return false;
    }

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param date      the date
     * @return  <code>true</code> if this symbol should be in the quote bundle, 
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(String symbol, TradingDate date) {

	try {
	    return containsQuote(symbol, quoteCache.dateToOffset(date));
	}
	catch(WeekendDateException e) {
	    // There are no quotes on a weekend.
	    return false;
	}
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
	return quoteRange;
    }

    /**
     * Set the qutoe range which specifies this quote bundle.
     *
     * @param quoteRange        the new quote range
     */
    public void setQuoteRange(QuoteRange quoteRange) {
        this.quoteRange = quoteRange;

        // Clear buffered start/end date offsets
        firstDateOffset = lastDateOffset = 1;
    }

    /**
     * Return the first symbol in the quote bundle. 
     * 
     * @return the first symbol
     */
    public String getFirstSymbol() {
        // This will fail if the first date in the bundle is on a public holiday or
        // weekend. Thats why its so important to fix the start/end dates!
    	Vector symbols = getSymbols(getFirstDate());
    	
    	assert symbols.size() > 0;
    	return (String)symbols.firstElement();
    }
    
    // Returns all the symbols in the quote bundle between the two dates
    private Vector getSymbols(int firstDateOffset, int lastDateOffset) {
	if(getQuoteRange().getType() == QuoteRange.GIVEN_SYMBOLS) {
	    return getQuoteRange().getAllSymbols();
	}
	
	// To get list of symbols - the quote bundle *must* be loaded!
	if(!quoteBundleCache.isLoaded(this))
	    quoteBundleCache.load(this);
	
	if(getQuoteRange().getType() == QuoteRange.ALL_SYMBOLS) {
	    return quoteCache.getSymbols(firstDateOffset, lastDateOffset);
	}
	
	else if(getQuoteRange().getType() == QuoteRange.ALL_ORDINARIES) {
	    
	    Vector ourSymbols = new Vector();
	    Vector symbols = quoteCache.getSymbols(firstDateOffset, lastDateOffset);
	    
	    // Weed out ones that aren't ours
	    Iterator iterator = symbols.iterator();
	    while(iterator.hasNext()) {
		String symbol = (String)iterator.next();
		
		if(!QuoteSourceManager.getSource().isMarketIndex(symbol))
		    ourSymbols.add(symbol);
	    }
	    
	    return ourSymbols;
	}
	
	else {
	    assert getQuoteRange().getType() == QuoteRange.MARKET_INDICES;
	    
	    Vector ourSymbols = new Vector();
	    Vector symbols = quoteCache.getSymbols(firstDateOffset, lastDateOffset);
	    
	    // Weed out ones that aren't ours
	    Iterator iterator = symbols.iterator();
	    while(iterator.hasNext()) {
		String symbol = (String)iterator.next();
		
		if(QuoteSourceManager.getSource().isMarketIndex(symbol))
		    ourSymbols.add(symbol);
	    }
	    
	    return ourSymbols;
	}
    }

    /**
     * Returns all the symbols in the quote bundle.
     *
     * @return all symbols
     */
    public Vector getAllSymbols() {
        return getSymbols(getFirstDateOffset(), getLastDateOffset());
    }

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return all symbols
     */
    public Vector getSymbols(int dateOffset) {	
        return getSymbols(dateOffset, dateOffset);
    }

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param date the date
     * @return all symbols
     */
    public Vector getSymbols(TradingDate date) {
	try {
	    return getSymbols(quoteCache.dateToOffset(date));
	}
	catch(WeekendDateException e) {
	    return new Vector();
	}
    }

    /**
     * Return the first date in this quote bundle.
     *
     * @return the earliest date
     */
    public TradingDate getFirstDate() {
	if(quoteRange.getFirstDate() != null)
	    return quoteRange.getFirstDate();
	else
	    return QuoteSourceManager.getSource().getFirstDate();
    }

    /**
     * Return the last date in this quote bundle.
     *
     * @return the latest date
     */
    public TradingDate getLastDate() {
	if(quoteRange.getLastDate() != null)
	    return quoteRange.getLastDate();
	else
	    return QuoteSourceManager.getSource().getLastDate();
    }

    /**
     * Return the fast access date offset of the first date in this quote bundle
     *
     * @return the first date offset, see {@link QuoteCache}
     */
    public int getFirstDateOffset() {
	if(firstDateOffset == 1) {
	    try {
		firstDateOffset = quoteCache.dateToOffset(getFirstDate());
	    }
	    catch(WeekendDateException e) {
		// Whoops this quote bundle starts on a weekend! Move it
		// to the next date if we have one, otherwise put it after
		// the last date in the database (to indicate the quote bundle is empty).
		TradingDate firstDate = getFirstDate();

		if(!firstDate.equals(getLastDate())) 
		    firstDate = firstDate.next(1);
		else
		    firstDate = QuoteSourceManager.getSource().getFirstDate().next(1);

		// Now its definitely not on a weekend...
		try {
		    firstDateOffset = quoteCache.dateToOffset(getFirstDate());
		}
		catch(WeekendDateException e2) {
		    assert false; 
		}
	    }
	}

	return firstDateOffset;
    }

    /**
     * Return the fast access date offset of the last date in this quote bundle
     *
     * @return the first date offset, see {@link QuoteCache}
     */
    public int getLastDateOffset() {
	if(lastDateOffset == 1) {
	    try {
		lastDateOffset = quoteCache.dateToOffset(getLastDate());
	    }
	    catch(WeekendDateException e) {
		// Whoops this quote bundle ends on a weekend! Move it
		// to the previous date if we have one, otherwise put it after
		// the last date in the database (to indicate the quote bundle is empty).
		TradingDate lastDate = getLastDate();

		if(!lastDate.equals(getFirstDate()))
		    lastDate = lastDate.previous(1);
		else
		    lastDate = QuoteSourceManager.getSource().getLastDate().next(1);

		// Now its definitely not on a weekend...
		try {
		    lastDateOffset = quoteCache.dateToOffset(getLastDate());
		}
		catch(WeekendDateException e2) {
		    assert false; 
		}
	    }
	}

	return lastDateOffset;
    }

    /**
     * Convert between a fast access date offset to an actual date.
     *
     * @param dateOffset        fast access date offset, see {@link QuoteCache}
     * @return the date
     */
    public TradingDate offsetToDate(int dateOffset) {
        return quoteCache.offsetToDate(dateOffset);
    }

    /**
     * Convert between a date and a fast access date offset.
     *
     * @param date the date
     * @return fast access date offset, see {@link QuoteCache}
     */
    public int dateToOffset(TradingDate date) 
        throws WeekendDateException {
        return quoteCache.dateToOffset(date);
    }

    /**
     * Free the quote bundle. This will remove the quote bundle from the 
     * {@link QuoteBundleCache}. This method is optional - quote bundles do not need
     * to call this when they are done. 
     */
    public void free() {
	quoteBundleCache.free(this);
    }    

    /**
     * If we know the given quote is not in the quote cache, this function will reload this
     * quote bundle (if its not already loaded) and return the given quote. Or
     * throw an exception if its still not in the quote cache.
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param dateOffset fast access date offset, see {@link QuoteCache}
     * @return the quote
     * @exception QuoteNotLoaded if the quote was not found
     */
    protected float tryReload(String symbol, int quoteType, int dateOffset) 
        throws QuoteNotLoadedException {

        // Perhaps our quote packet is not loaded - if so load
        if(!quoteBundleCache.isLoaded(this)) {
            quoteBundleCache.load(this);
            
            return quoteCache.getQuote(symbol, quoteType, dateOffset);
        }                

        throw new QuoteNotLoadedException();
    }
}

