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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.org.venice.analyser.gp.GPQuoteBundle;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.util.TradingDate;

/**
 * When a task requires end-of-day stock quotes, it should create an instance of this class 
 * which represents all the task's required quotes. The task can then access quotes from 
 * this class, which in turn reads its stock quotes from the global end-of-day quote cache -
 * {@link EODQuoteCache}.
 * <p>
 * The purpose of this class is therefore to group together a set of quotes that are
 * needed by a single task. This grouping allows the quotes to be loaded in at one time,
 * which is much faster than loading them in quote by quote.
 * <p>
 * Also by placing a set of quotes in a bundle it simplifies caching. Caching is performed
 * by {@link EODQuoteBundleCache}.
 *
 * Example:
 * <pre>
 *      EODQuoteRange quoteRange = new QuoteRange("CBA");
 *      EODQuoteBundle quoteBundle = new EODQuoteBundle(quoteRange);
 *      try {
 *	    double = quoteBundle.getQuote("CBA", Quote.DAY_OPEN, 0);
 *      }
 *      catch(QuoteNotLoadedException e) {
 *          //...
 *      }
 * </pre>
 *
 * @author Andrew Leppard
 * @see GPQuoteBundle
 * @see EODQuote
 * @see EODQuoteRange
 * @see EODQuoteBundleCache
 * @see EODQuoteCache
 * @see IDQuoteBundle
 * @see Symbol
 */
public class EODQuoteBundle implements QuoteBundle {

    // Quotes contained in this bundle
    private EODQuoteRange quoteRange;

    /** For speed reasons, keep reference to the global quote cache */
    protected EODQuoteCache quoteCache;

    /** For speed reasons, keep reference to the global quote bundle cache */
    protected EODQuoteBundleCache quoteBundleCache;

    // Start and end date offsets (marked as 1 which indicates an illegal
    // date offset, date offsets start from 0 and go down).
    private int firstDateOffset = 1;
    private int lastDateOffset = 1;

    /**
     * Create a new end-of-day quote bundle that represents the quotes in the given
     * quote range.
     *
     * @param quoteRange      the quote range
     */
    public EODQuoteBundle(EODQuoteRange quoteRange) {
	this.quoteRange = quoteRange;

	quoteCache = EODQuoteCache.getInstance();
	quoteBundleCache = EODQuoteBundleCache.getInstance();

	// Load it in now if its not already
	quoteBundleCache.load(this);
    }

    /**
     * Create a new end-of-day quote bundle with the same quote range as the given
     * quote bundle.
     *
     * @param quoteBundle      the quote bundle to copy
     */
    public EODQuoteBundle(EODQuoteBundle quoteBundle) {
        this(quoteBundle.getQuoteRange());
    }

    /**
     * Get a stock quote. If the stock is earlier than the first date in the bundle, the
     * bundle will be expand to include the new date given. If the stock symbol is not
     * included in the original symbol list, the quote bundle will be expanded to include
     * it.
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param dateOffset fast access date offset, see {@link EODQuoteCache}
     * @return the quote
     * @exception MissingQuoteException if the quote was not found
     */
    public double getQuote(Symbol symbol, int quoteType, int dateOffset)
	throws MissingQuoteException {
        
        boolean foundQuote = false;
	double quote = 0.0D;

        // First try the quote cache.
        try {
            quote = quoteCache.getQuote(symbol, quoteType, dateOffset);
            foundQuote = true;
        }
        catch(QuoteNotLoadedException e) {
            // Ignore
        }

        // If the quote is not in the quote cache, perhaps the quote bundle is not loaded.
        if(!foundQuote && tryReload()) {
            try {
                quote = quoteCache.getQuote(symbol, quoteType, dateOffset);
                foundQuote = true;
            }
            catch(QuoteNotLoadedException e2) {
                // Ignore
            }
        }

        // If the quote is still not in the quote cache, perhaps the quote bundle
        // does not contain the quote. Try expand the quote bundle.
        if(!foundQuote && tryExpand(symbol, dateOffset)) {
            try {
                quote = quoteCache.getQuote(symbol, quoteType, dateOffset);
                foundQuote = true;
            }
            catch(QuoteNotLoadedException e3) {
                // Ignore
            }
        }

        // If we still don't have the quote. Give up.
        if(!foundQuote)
            throw MissingQuoteException.getInstance();

        return quote;
    }

    public Quote getQuote(Symbol symbol, int dateOffset)
	throws MissingQuoteException {
        
        Quote quote = null;

        // First try the quote cache.
        try {
            quote = quoteCache.getQuote(symbol, dateOffset);
        }
        catch(QuoteNotLoadedException e) {
            // Ignore
        }

        // If the quote is not in the quote cache, perhaps the quote bundle is not loaded.
        if(quote == null && tryReload()) {
            try {
                quote = quoteCache.getQuote(symbol, dateOffset);
            }
            catch(QuoteNotLoadedException e2) {
                // Ignore
            }
        }

        // If the quote is still not in the quote cache, perhaps the quote bundle
        // does not contain the quote. Try expand the quote bundle.
        if(quote == null && tryExpand(symbol, dateOffset)) {
            try {
                quote = quoteCache.getQuote(symbol, dateOffset);
            }
            catch(QuoteNotLoadedException e3) {
                // Ignore
            }
        }

        // If we still don't have the quote. Give up.
        if(quote == null)
            throw MissingQuoteException.getInstance();

        return quote;
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
     * @param today fast access date offset of current date, see {@link EODQuoteCache}
     * @param offset offset from current date
     * @return the quote
     * @exception EvaluationException if the script isn't allow access to the quote.
     * @exception MissingQuoteException if the quote was not found
     */
    public double getQuote(Symbol symbol, int quoteType, int today, int offset)
	throws EvaluationException, MissingQuoteException {

        return getQuote(symbol, quoteType, today + offset);
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
    public double getQuote(Symbol symbol, int quoteType, TradingDate date)
	throws MissingQuoteException {
	
	double quote;

	try {
	    quote = getQuote(symbol, quoteType, quoteCache.dateToOffset(date));
	}
	catch(WeekendDateException e) {
	    throw MissingQuoteException.getInstance();
	}

	return quote;
    }

    /**
     * Get a stock quote nearest to the given offset.      
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param dateOffset fast access date offset, see {@link EODQuoteCache}
     * @return the quote nearest to the given dateOffset
     * @exception MissingQuoteException if the quote was not found
     */
    public double getNearestQuote(Symbol symbol, int quoteType, int dateOffset)
	throws MissingQuoteException {
        
        boolean foundQuote = false;
	double quote = 0.0D;

        // First try the quote cache.
        try {
            quote = quoteCache.getQuote(symbol, quoteType, dateOffset);
            foundQuote = true;
        }
        catch(QuoteNotLoadedException e) {
            // Ignore
        }

        // If the quote is not in the quote cache, perhaps the quote bundle is not loaded.
        if(!foundQuote && tryReload()) {
            try {
                quote = quoteCache.getQuote(symbol, quoteType, dateOffset);
                foundQuote = true;
            }
            catch(QuoteNotLoadedException e2) {
                // Ignore
            }
        }

	// If the quote is still not in the quote cache, find an earlier quote,
	// if there is one.	
        if(dateOffset > getFirstOffset()) {	    
	    //Search the cache until a quote is found.
	    while (!quoteCache.containsQuote(symbol, dateOffset) && 
		   dateOffset >= getFirstOffset()) {		
		dateOffset--;
	    }
	    
	    if (dateOffset >= getFirstOffset()) {
		quote = getQuote(symbol, quoteType, dateOffset);
		foundQuote = true;
	    } else {
		//If the loop finishes with dateOffset passing the
		//first offset, there are no more quotes
		foundQuote = false;
	    }
	}
	      
        // If we still don't have the quote. Give up.
        if(!foundQuote)
            throw MissingQuoteException.getInstance();

        return quote;
    }

    /**
     * Return whether the given quote should be in this quote bundle.
     *
     * @param symbol    the symbol
     * @param dateOffset fast access date offset, see {@link EODQuoteCache}
     * @return  <code>true</code> if this symbol should be in the quote bundle,
     *          <code>false</code> otherwise
     */
    public boolean containsQuote(Symbol symbol, int dateOffset) {

	if(getQuoteRange().containsSymbol(symbol) &&
	   dateOffset >= getFirstOffset() &&
	   dateOffset <= getLastOffset())
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
    public boolean containsQuote(Symbol symbol, TradingDate date) {

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
        return new EODQuoteBundleIterator(this);
    }

    /**
     * Return the quote range which specifies this quote bundle.
     *
     * @return the quote range
     */
    public EODQuoteRange getQuoteRange() {
	return quoteRange;
    }

    /**
     * Set the qutoe range which specifies this quote bundle.
     *
     * @param quoteRange        the new quote range
     */
    public void setQuoteRange(EODQuoteRange quoteRange) {
        this.quoteRange = quoteRange;

        // Clear buffered start/end date offsets
        firstDateOffset = lastDateOffset = 1;
    }

    /**
     * Return the first symbol in the quote bundle.
     *
     * @return the first symbol
     */
    public Symbol getFirstSymbol() {
        int dateOffset = getFirstOffset();

        // Loop through each day looking for any symbols
        while(dateOffset <= getLastOffset()) {
            List symbols = getSymbols(dateOffset++);

            if(symbols.size() > 0)
                return (Symbol)symbols.get(0);
        }

        // If we got here there are no symbols in the bundle.
        assert false;
        return null;
    }

    // Returns all the symbols in the quote bundle between the two dates
    private List getSymbols(int firstDateOffset, int lastDateOffset) {
	// To get list of symbols - the quote bundle *must* be loaded!
	if(!quoteBundleCache.isLoaded(this))
	    quoteBundleCache.load(this);

	if(getQuoteRange().getType() == EODQuoteRange.GIVEN_SYMBOLS) {
            // We can't just call getQuoteRange().getAllSymbols() because
            // for the given quote range it is possible we don't have any
            // quotes for them. So make sure all the given symbols are
            // present in the cache for the given range.
            List presentSymbols = new ArrayList();
            List allSymbols = quoteCache.getSymbols(firstDateOffset, lastDateOffset);
            List expectedSymbols = getQuoteRange().getAllSymbols();

	    // Weed out ones that aren't ours
            for(Iterator iterator = expectedSymbols.iterator(); iterator.hasNext();) {
		Symbol symbol = (Symbol)iterator.next();

		if(allSymbols.contains(symbol))
                    presentSymbols.add(symbol);
	    }

	    return presentSymbols;
	}
		
	else if(getQuoteRange().getType() == EODQuoteRange.ALL_SYMBOLS) {
	    return quoteCache.getSymbols(firstDateOffset, lastDateOffset);
	}
	
	else if(getQuoteRange().getType() == EODQuoteRange.ALL_ORDINARIES) {
	
	    List ourSymbols = new ArrayList();
	    List symbols = quoteCache.getSymbols(firstDateOffset, lastDateOffset);
	
	    // Weed out ones that aren't ours
	    Iterator iterator = symbols.iterator();
	    while(iterator.hasNext()) {
		Symbol symbol = (Symbol)iterator.next();
		
		if(!QuoteSourceManager.getSource().isMarketIndex(symbol))
		    ourSymbols.add(symbol);
	    }
	
	    return ourSymbols;
	}
	
	else {
	    assert getQuoteRange().getType() == EODQuoteRange.MARKET_INDICES;
	
	    List ourSymbols = new ArrayList();
	    List symbols = quoteCache.getSymbols(firstDateOffset, lastDateOffset);
	
	    // Weed out ones that aren't ours
	    Iterator iterator = symbols.iterator();
	    while(iterator.hasNext()) {
		Symbol symbol = (Symbol)iterator.next();
		
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
    public List getAllSymbols() {
        return getSymbols(getFirstOffset(), getLastOffset());
    }

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param dateOffset fast access date offset, see {@link EODQuoteCache}
     * @return all symbols
     */
    public List getSymbols(int dateOffset) {	
        return getSymbols(dateOffset, dateOffset);
    }

    /**
     * Returns all the symbols listed in this quote bundle for the given date.
     *
     * @param date the date
     * @return all symbols
     */
    public List getSymbols(TradingDate date) {
	try {
	    return getSymbols(quoteCache.dateToOffset(date));
	}
	catch(WeekendDateException e) {
	    return new ArrayList();
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
     * Return the fast access date offset for the earliest quote in the bundle.
     *
     * @return fast access date offset
     * @see EODQuoteCache
     */
    public int getFirstOffset() {
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
		    firstDateOffset = quoteCache.dateToOffset(firstDate);
		}
		catch(WeekendDateException e2) {
		    assert false;
		}
	    }
	}

	return firstDateOffset;
    }

    /**
     * Return the fast access date offset for the latest quote in the bundle.
     *
     * @return fast access date offset
     * @see EODQuoteCache
     */
    public int getLastOffset() {
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
     * @param dateOffset        fast access date offset, see {@link EODQuoteCache}
     * @return the date
     */

    public TradingDate offsetToDate(int dateOffset) {
        return quoteCache.offsetToDate(dateOffset);
    }

    /**
     * Convert between a date and a fast access date offset.
     *
     * @param date the date
     * @return fast access date offset, see {@link EODQuoteCache}
     */
    public int dateToOffset(TradingDate date)
        throws WeekendDateException {
        return quoteCache.dateToOffset(date);
    }

    /**
     * Retrieve the fast access offset from the given quote.
     *
     * @param quote quote
     * @return fast access offset
     * @exception WeekendDateException if the date falls on a weekend.
     */
    public int getOffset(Quote quote)
        throws WeekendDateException {

        return dateToOffset(quote.getDate());
    }

    public String toString() {
        return quoteRange.toString();
    }

    /**
     * If we know the given quote is not in the quote cache, this function will reload this
     * quote bundle.
     *
     * @return <code>true</code> if the quote bundle was reloaded, <code>false</code> otherwise.
     */
    private boolean tryReload() {
        boolean success = false;

        // Perhaps our quote packet is not loaded - if so load
        if(!quoteBundleCache.isLoaded(this)) {
            quoteBundleCache.load(this);
            success = true;
        }

        return success;
    }

    /**
     * Try to expand the new quote bundle so that it include a new
     * date and/or a new symbol. This function is passed the 
     *
     * @param symbol possibly new symbol to include
     * @param dateOffset possibly new date to include
     * @return <code>true</code> if the quote range was expanded, or
     *         <code>false</code> if the quote range already includes
     *         the symbol and date range.
     */
    private boolean tryExpand(Symbol symbol, int dateOffset) {
        boolean success = false;
        EODQuoteRange expandedQuoteRange = (EODQuoteRange)getQuoteRange().clone();
        
        // We can expand a quote range by expanding it to cover an older date
        if(getQuoteRange().getFirstDate() != null && dateOffset < getFirstOffset()) {

            TradingDate date = quoteCache.offsetToDate(dateOffset);
            expandedQuoteRange.setFirstDate(date);
            success = true;
        }
        
        // Expand a list of symbols to include another
        if(getQuoteRange().getType() == EODQuoteRange.GIVEN_SYMBOLS &&
           !getQuoteRange().containsSymbol(symbol)) {
            
            expandedQuoteRange.addSymbol(symbol);
            success = true;
        }   

        // Load expanded quote cache
        if(success)
            quoteBundleCache.expand(this, expandedQuoteRange);

        return success;
    }
}

