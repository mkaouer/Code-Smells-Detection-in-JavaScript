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

import org.mov.ui.*;
import org.mov.util.*;

/**
 * This class contains all the stock quotes currently in memory. Its purpose is to
 * cache stock quotes so that tasks do not have to query the database or files whenever they
 * need a quote. While this is a cache it does not control when stock quotes are loaded
 * or freed, that is controlled by {@link QuoteBundleCache}.
 * <p>
 * Tasks should not directly call this class, but should go through a {@link QuoteBundle}.
 * <p>
 * When tasks access quotes in a quote cache, either directly or via a quote bundle they
 * can access quotes in two ways. The first way is specifying the actual date they
 * are interested in, i.e. a {@link TradingDate}. The other way is specifying a fast
 * access date offset. The fast access date offset is used when lots of quotes have
 * to be queried as fast as possible.
 * <p>
 * The latest date in the cache has an offset of 0. The previous trading date
 * (i.e. not a weekend) has offset -1, the previous one to that -2 etc.
 * You can convert to and from fast access dates using {@link #dateToOffset} and
 * {@link #offsetToDate}.
 *
 * @see Quote
 * @see QuoteBundle
 * @see QuoteBundleCache
 */
public class QuoteCache {

    // Cache is organised by a vector of hashmaps, each hashmap
    // corresponds to a trading day. The hashmap's keys are stock symbols.
    private Vector cache = new Vector();

    // Keep list of dates in cache
    private ArrayList dates = new ArrayList();

    // Number of quotes in cache
    private int size = 0;

    // Singleton instance of this class
    private static QuoteCache instance = null;

    // Class should only be constructed once by this class
    private QuoteCache() {
        TradingDate lastDate = 
            QuoteSourceManager.getSource().getLastDate();

        if(lastDate != null)
            addDate(lastDate);
    }

    /**
     * Create or return the singleton instance of the quote cache.
     *
     * @return  singleton instance of this class
     */
    public static synchronized QuoteCache getInstance() {
	if(instance == null)
	    instance = new QuoteCache();

        return instance;
    }

    /**
     * Get a quote from the cache.
     *
     * @param symbol    the symbol to load
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param dateOffset fast access date offset
     * @return the quote
     * @exception QuoteNotLoadedException if the quote was not in the cache
     */
    public float getQuote(String symbol, int quoteType,
			  int dateOffset)
	throws QuoteNotLoadedException {

	// First get the hash map for the given date

	HashMap symbols = getQuotesForDate(dateOffset);
	assert symbols != null;

	// Second get the quote for the given symbol on the given date

	Quote quoteValue = (Quote)symbols.get(symbol);
	if(quoteValue == null)
	    throw new QuoteNotLoadedException();
	
	return quoteValue.getQuote(quoteType);
    }

    /**
     * Return all the symbols in the cache on the given date.
     *
     * @param dateOffset        fast access date offset
     * @return list of symbols
     */
    public Vector getSymbols(int dateOffset) {
        HashMap quotesForDate;

	try {
	    quotesForDate = getQuotesForDate(dateOffset);
	}
	catch(QuoteNotLoadedException e) {
	    // no symbols loaded on date
	    quotesForDate = new HashMap(0);
	}
	
	return new Vector(quotesForDate.keySet());
    }

    /**
     * Return all the symbols in the cache between the given date range
     * (inclusive).
     *
     * @param firstDateOffset fast access offset of first date
     * @param lastDateOffset fast access offset of last date
     * @return list of symbols
     */
    public Vector getSymbols(int firstDateOffset, int lastDateOffset) {
        HashMap symbols = new HashMap();

        // Go through each day, collecting symbols. We put them all in
        // a hashmap to quickly weed out the numerous duplicates.
        for(int date = firstDateOffset; date <= lastDateOffset; date++) {
            Vector datesSymbols = getSymbols(date);

            for(Iterator iterator = datesSymbols.iterator(); iterator.hasNext();) {
                String symbol = (String)iterator.next();

                symbols.put(symbol, symbol);
            }
        }

        return new Vector(symbols.keySet());
    }

    // Returns a HashMap containing quotes for that date
    private HashMap getQuotesForDate(int dateOffset)
	throws QuoteNotLoadedException {

	assert dateOffset <= 0;

	if(dateOffset <= -dates.size())
	    throw new QuoteNotLoadedException();
	
	HashMap quotesForDate = (HashMap)cache.elementAt(-dateOffset);

	if(quotesForDate == null)
	    throw new QuoteNotLoadedException();

	return quotesForDate;
    }

    /**
     * Load the given quote into the cache.
     *
     * @param quote        quote to load
     */
    public void load(Quote quote) {

        // Find the fast date offset for the quote
        int dateOffset;

        try {
            dateOffset = dateToOffset(quote.getDate());
        }
        catch(WeekendDateException e) {
            // If the date falls on a weekend then skip it
            return;
        }

        // Get hash of quotes for that date
        HashMap quotesForDate;

        try {
            quotesForDate = getQuotesForDate(dateOffset);
        }
        catch(QuoteNotLoadedException e) {
            // The dateToOffset() call above should have expanded
            // the quote range so this shouldn't happen
            assert false;

            quotesForDate = new HashMap(0);
        }

        // Put stock in map and remove symbol and date to reduce memory
        // (they are our indices so we already know them)
        Object previousQuote = quotesForDate.put(quote.getSymbol(), quote);
        quote.setSymbol(null);
        quote.setDate(null);

        // If the quote wasn't already there then increase size counter
        if(previousQuote == null)
            size++;
    }

    /**
     * Remove the given quote from the cache. It's OK if the quote isn't loaded.
     *
     * @param symbol the symbol of the quote to remove
     * @param dateOffset the fast access date offset of the quote to remove
     */
    public void free(String symbol, int dateOffset) {

	try {
	    HashMap quotesForDate = getQuotesForDate(dateOffset);
	
	    Quote quote = (Quote)quotesForDate.remove(symbol);

	    // If we actually deleted a quote, then reduce our quote counter.
	    // We have to check that we actually did remove something from
	    // the cache, so that our size count is correct. Its OK for the caller
            // to try to delete a quote that's not in the cache - if it wasn't
            // then the quote bundles would have to keep track of holidays etc...
	    if(quote != null)
		size--;

	    assert size >= 0;
	}
	catch(QuoteNotLoadedException e) {
	    // This means we've never had any quotes on the given date that
	    // the caller was trying to free. This sounds like something
	    // wonky is going on.
	    assert false;
	}
    }

    /**
     * Convert between a date and its fast access date offset.
     *
     * @param date the date
     * @return fast access date offset
     * @exception WeekendDateException if the date is on a weekend (there are no
     *            fast access date offsets for weekend dates)
     */
    public int dateToOffset(TradingDate date)
	throws WeekendDateException {

        TradingDateComparator comparator =
            new TradingDateComparator(TradingDateComparator.BACKWARDS);

	int dateOffset = -Collections.binarySearch(dates, date, comparator);

	// If the date isn't yet in the cache because its too old, then binary search
	// will return the negative size of dates. 
        // If the date isn't yet in the cache because its too new, then binary search
        // will return 1. 
        // In either case expand the cache.
	if(dateOffset > dates.size() || dateOffset == 1) {
	    expandToDate(date);
	    dateOffset = -Collections.binarySearch(dates, date, comparator);
	}

	// Only possible reason date isn't in cache now is because it falls
	// on a weekend or its a newer date than what is in the cache.
	if(dateOffset > 0)
	    throw new WeekendDateException();

        return dateOffset;
    }

    /**
     * Convert between a fast access date offset and a date.
     *
     * @param dateOffset fast access date offset
     * @return the date
     */
    public TradingDate offsetToDate(int dateOffset) {

	assert dateOffset <= 0;

	// If the date isn't in the cache then expand it
	while(dateOffset <= -dates.size()) {
	    TradingDate date = getFirstDate().previous(1);
	    addDate(date);
	}

	return (TradingDate)dates.get(-dateOffset);
    }

    /**
     * Return the number of quotes in the cache.
     *
     * @return the cache size
     */
    public int size() {
	return size;
    }

    /**
     * Get the oldest date in the cache.
     *
     * @return the oldest date in cache or <code>null</code> if the cache is empty.
     */
    public TradingDate getFirstDate() {
        if(dates.size() > 0)
            return (TradingDate)dates.get(dates.size() - 1);
        else
            return null;
    }

    /**
     * Get the newest date in the cache.
     *
     * @return the newest date in cache or <code>null</code> if the cache is empty.
     */
    public TradingDate getLastDate() {
        if(dates.size() > 0)
            return (TradingDate)dates.get(0);
        else
            return null;
    }

    /**
     * Get the fast access offset of the oldest date in the cache.
     *
     * @return the fast access offset of oldest date in cache or +1 if there
     *         are no dates in the cache.
     */
    public int getFirstDateOffset() {
        return -(dates.size() - 1);
    }

    // Add one date to cache. The date should be one trading day older than the 
    // oldest date in the cache.
    private void addDate(TradingDate date) {       
	// Create a map with 0 initial capacity. I.e. we create an empty one
	// because we might not even use it
	HashMap map = new HashMap(0);
	cache.add(map);
	dates.add(date);	
    }

    // This function is used to insert a date into the cache that is newer
    // (i.e. more recent) than any other dates in the cache. It's pretty
    // slow as it needs to shift two arrays but it's only used for import
    // so it doesn't matter
    private void insertDate(TradingDate date) {
	// Create a map with 0 initial capacity. I.e. we create an empty one
	// because we might not even use it
	HashMap map = new HashMap(0);
	cache.add(0, map);
	dates.add(0, date);       
    }

    // Expand the quote cache to encompass the given date
    private void expandToDate(TradingDate date) {

        assert date != null;

        TradingDate firstDate = getFirstDate();
        TradingDate lastDate = getLastDate();

        // There are four cases to consider, first there are no dates
        // in the cache
        if(firstDate == null)
            addDate(date);

        // Second is that the new date to add is before the first date
        // in our cache. This is common and we can handle this quickly
        else if(date.before(firstDate)) {
            while(date.before(firstDate)) {
                firstDate = firstDate.previous(1);
                addDate(firstDate);
            }
        }

        // The third case is that this date is newer than our newest
        // date. This can only happen when quotes are imported into the
        // system while Venice is running. This code is slow but we don't care.
        else if(date.after(lastDate)) {
            while(date.after(lastDate)) {
                lastDate = lastDate.next(1);
                insertDate(lastDate);
            }
        }
    
        // The remaining case is the date is already in our range...
    }
}


