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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.mov.util.TradingDate;
import org.mov.util.TradingDateComparator;

/**
 * This class contains all the end-of-day stock quotes currently in memory. Its purpose is to
 * cache end-of-day stock quotes so that tasks do not have to query the database or files 
 * whenever they need a quote. While this is a cache it does not control when stock quotes are
 * loaded or freed, that is controlled by {@link EODQuoteBundleCache}.
 * <p>
 * Tasks should not directly call this class, but should go through a {@link EODQuoteBundle}.
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
 * @author Andrew Leppard
 * @see EODQuote
 * @see EODQuoteBundle
 * @see EODQuoteBundleCache
 */
public class EODQuoteCache {

    // Cache is organised by a list of hashmaps, each hashmap
    // corresponds to a trading day. The hashmap's keys are stock symbols.
    private List cache;

    // Keep list of dates in cache
    private List dates;

    // Number of quotes in cache
    private int size = 0;

    // Singleton instance of this class
    private static EODQuoteCache instance = null;

    private class EODQuoteCacheQuote {
        // Floats have more than enough precision to hold quotes. So we
        // store them as floats rather than doubles to reduce memory.
        public int day_volume;
        public float day_low;
        public float day_high;
        public float day_open;
        public float day_close;

        public EODQuoteCacheQuote(int day_volume, float day_low, float day_high,
                                  float day_open, float day_close) {
            this.day_volume = day_volume;
            this.day_low = day_low;
            this.day_high = day_high;
            this.day_open = day_open;
            this.day_close = day_close;
        }

        public double getQuote(int quote) {
            switch(quote) {
            case(Quote.DAY_OPEN):
                return (double)day_open;
            case(Quote.DAY_CLOSE):
                return (double)day_close;
            case(Quote.DAY_LOW):
                return (double)day_low;
            case(Quote.DAY_HIGH):
                return (double)day_high;
            case(Quote.DAY_VOLUME):
                return (double)day_volume;
            default:
                assert false;
                return 0.0D;
            }
        }

        public EODQuote toQuote(Symbol symbol, TradingDate date) {
            return new EODQuote(symbol,
                                date,
                                day_volume,
                                (double)day_low,
                                (double)day_high,
                                (double)day_open,
                                (double)day_close);
        }

        public boolean equals(int day_volume, float day_low, float day_high,
                              float day_open, float day_close) {
            return (day_volume == this.day_volume &&
                    day_low == this.day_low &&
                    day_high == this.day_high &&
                    day_open == this.day_open &&
                    day_close == this.day_close);
        }
    }

    // Class should only be constructed once by this class
    private EODQuoteCache() {
        cache = new ArrayList();
        dates = new ArrayList();

        TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();

        if(lastDate != null)
            addDate(lastDate);
    }

    /**
     * Create or return the singleton instance of the quote cache.
     *
     * @return  singleton instance of this class
     */
    public static synchronized EODQuoteCache getInstance() {
	if(instance == null)
	    instance = new EODQuoteCache();

        return instance;
    }

    /**
     * Returns whether this class has been instantiated yet. This is
     * used by the tuning page, which needs to know the number of
     * quotes in the cache. But it doesn't want to be the first
     * to instantiate the cache, because that would cause it to
     * access the quote source.. which might not be set up at that
     * stage.
     *
     * @return <code>true</code> if this class has been instantiated.
     */
    public static boolean isInstantiated() {
        return (instance != null);
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
    public double getQuote(Symbol symbol, int quoteType, int dateOffset)
	throws QuoteNotLoadedException {

	// Get the quote cache quote for the given symbol + date
	EODQuoteCacheQuote quote = getQuoteCacheQuote(symbol, dateOffset);

	if(quote != null)
            return quote.getQuote(quoteType);
        else
	    throw QuoteNotLoadedException.getInstance();
    }


    /**
     * Get a quote from the cache.
     *
     * @param symbol     the symbol to load
     * @param dateOffset fast access date offset
     * @return the quote
     * @exception QuoteNotLoadedException if the quote was not in the cache
     */
    public EODQuote getQuote(Symbol symbol, int dateOffset)
	throws QuoteNotLoadedException {

	// Get the quote cache quote for the given symbol + date
	EODQuoteCacheQuote quote = getQuoteCacheQuote(symbol, dateOffset);

	if(quote != null)
            return quote.toQuote(symbol, offsetToDate(dateOffset));
        else
	    throw QuoteNotLoadedException.getInstance();
    }

    /**
     * Return all the symbols in the cache on the given date.
     *
     * @param dateOffset        fast access date offset
     * @return list of symbols
     */
    public List getSymbols(int dateOffset) {
        HashMap quotesForDate;

	try {
	    quotesForDate = getQuotesForDate(dateOffset);
	}
	catch(QuoteNotLoadedException e) {
	    // no symbols loaded on date
	    quotesForDate = new HashMap(0);
	}
	
	return new ArrayList(quotesForDate.keySet());
    }

    /**
     * Return all the symbols in the cache between the given date range
     * (inclusive).
     *
     * @param firstDateOffset fast access offset of first date
     * @param lastDateOffset fast access offset of last date
     * @return list of symbols
     */
    public List getSymbols(int firstDateOffset, int lastDateOffset) {
        HashMap allSymbols = new HashMap();

        // Go through each day, collecting symbols. We put them all in
        // a hashmap to quickly weed out the numerous duplicates. We
        // don't call getSymbols() for each day because unrolling the
        // call is much, much faster.
        for(int dateOffset = firstDateOffset; dateOffset <= lastDateOffset; dateOffset++) {
            try {
                HashMap todaySymbols = getQuotesForDate(dateOffset);
                allSymbols.putAll(todaySymbols);
            }
            catch(QuoteNotLoadedException e) {
                // no symbols loaded on date
            }
        }

        return new ArrayList(allSymbols.keySet());
    }

    /**
     * Return whether we currently have any quotes for the given symbol on the given date
     *
     * @param symbol symbol
     * @param dateOffset fast access date offset
     * @return <code>TRUE</code> if we have the quote
     */
    public boolean containsQuote(Symbol symbol, int dateOffset) {
	assert dateOffset <= 0;

	if(dateOffset > -dates.size()) {
            HashMap symbols = (HashMap)cache.get(-dateOffset);

            if(symbols != null) {
                EODQuoteCacheQuote quote = (EODQuoteCacheQuote)symbols.get(symbol);
                if (quote != null)
                    return true;
            }
        }
        return false;
    }

    // Returns the quote cache object for the given date
    private EODQuoteCacheQuote getQuoteCacheQuote(Symbol symbol, int dateOffset)
        throws QuoteNotLoadedException {

	// First get the hash map for the given date
	HashMap symbols = getQuotesForDate(dateOffset);
	assert symbols != null;

	// Second get the quote for the given symbol on the given date
	return  (EODQuoteCacheQuote)symbols.get(symbol);
    }

    // Returns a HashMap containing quotes for that date
    private HashMap getQuotesForDate(int dateOffset)
	throws QuoteNotLoadedException {

	assert dateOffset <= 0;

	if(dateOffset <= -dates.size())
	    throw QuoteNotLoadedException.getInstance();
	
	HashMap quotesForDate = (HashMap)cache.get(-dateOffset);

	if(quotesForDate == null)
	    throw QuoteNotLoadedException.getInstance();

	return quotesForDate;
    }

    /**
     * Load the given quote into the cache.
     *
     * @param quote the quote
     */
    public void load(EODQuote quote) {
        load(quote.getSymbol(),
             quote.getDate(),
             quote.getDayVolume(),
             (float)quote.getDayLow(),
             (float)quote.getDayHigh(),
             (float)quote.getDayOpen(),
             (float)quote.getDayClose());
    }

    /**
     * Load the given quote into the cache.
     *
     * @param symbol symbol of quote
     * @param date   quote date
     * @param day_volume day volume
     * @param day_low day low
     * @param day_high day high
     * @param day_open day open
     * @param day_close day close
     */
    public synchronized void load(Symbol symbol, TradingDate date, int day_volume, float day_low,
                                  float day_high, float day_open, float day_close) {

        // Find the fast date offset for the quote
        int dateOffset;

        try {
            dateOffset = dateToOffset(date);
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

        // Lots of stocks don't change between days, so check to see if
        // this stock's quote is identical to yesterdays. If so then
        // just use that
        EODQuoteCacheQuote yesterdayQuote = null;
        EODQuoteCacheQuote todayQuote = null;

        try {
            yesterdayQuote = getQuoteCacheQuote(symbol, dateOffset - 1);
        }
        catch(QuoteNotLoadedException e) {
            // OK
        }

        if(yesterdayQuote != null &&
           yesterdayQuote.equals(day_volume, day_low, day_high, day_open, day_close))
            todayQuote = yesterdayQuote;
        else
            todayQuote = new EODQuoteCacheQuote(day_volume, day_low, day_high,
                                                day_open, day_close);

        // Put stock in map and remove symbol and date to reduce memory
        // (they are our indices so we already know them)
        Object previousQuote = quotesForDate.put(symbol, todayQuote);

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
    public synchronized void free(Symbol symbol, int dateOffset) {

	try {
	    HashMap quotesForDate = getQuotesForDate(dateOffset);
	    Object quote = quotesForDate.remove(symbol);

	    // If we actually deleted a quote, then reduce our quote counter.
	    // We have to check that we actually did remove something from
	    // the cache, so that our size count is correct. Its OK for the caller
            // to try to delete a quote that's not in the cache - if it wasn't
            // then the quote bundles would have to keep track of holidays etc...
	    if(quote != null) {
		size--;

                // If the hashmap is empty then resize it to the minimum size.
                // Otherwise we may have 1,000s of large hash maps taking up
                // a *LOT* of memory.
                if(quotesForDate.isEmpty())
                    cache.set(-dateOffset, new HashMap());
            }

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


