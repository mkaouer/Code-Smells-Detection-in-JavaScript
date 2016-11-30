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
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import org.mov.util.TradingDate;
import org.mov.util.TradingTime;

/**
 * This class contains all the intra-day stock quotes currently in memory. Its purpose is to
 * cache intra-day stock quotes so that tasks can share copies of the quotes rather than
 * keep duplicate copies.
 * <p>
 * Tasks should not directly call this class, but should go through a {@link IDQuoteBundle}.
 * <p>
 * When tasks access quotes in a quote cache, either directly or via a quote bundle they
 * can access quotes in two ways. The first way is specifying the actual time they
 * are interested in, i.e. a {@link TradingTime}. The other way is specifying a fast
 * access time offset. The fast access time offset is used when lots of quotes have
 * to be queried as fast as possible.
 * <p>
 * The earliest time in the cache has an offset of 0. The next trading time has an
 * offset of 1, the next 2, etc. This is different from the {@link EODQuoteCache} which
 * numbers the latest quote at 0. You can convert to and from fast access times
 * using {@link #timeToOffset} and {@link #offsetToTime}.
 *
 * @author Andrew Leppard
 * @see IDQuote
 * @see IDQuoteBundle
 */
public class IDQuoteCache {

    // Cache is organised by a list of hashmaps, each hashmap
    // corresponds to a trading time. The hashmap's keys are stock symbols.
    private List cache;

    // Time for each cache entry
    private List times;

    // Date of all quotes in cache
    private TradingDate date;

    // Singleton instance of this class
    private static IDQuoteCache instance = null;

    // Listeners to be notified when new intra-day quotes arrive
    private EventListenerList quoteListeners;

    /**
     * This class contains a single intra-day quote in the quote cache. We use
     * this class over the {@link IDQuote} class because it uses less space.
     * This is because due to the way the cache is organised, we do not need to
     * store the date and time of each individual quote. We can also further
     * save space by storing the quotes as floats rather than doubles.
     */
    private class IDQuoteCacheQuote {
        public int day_volume;
        public float day_low;
        public float day_high;
        public float day_open;
        public float day_close;
        public float bid;
        public float ask;

        public IDQuoteCacheQuote(int day_volume, 
                                 float day_low, float day_high, float day_open, float day_close,
                                 float bid, float ask) {
            this.day_volume = day_volume;
            this.day_low = day_low;
            this.day_high = day_high;
            this.day_open = day_open;
            this.day_close = day_close;
            this.bid = bid;
            this.ask = ask;
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
            case(Quote.BID):
                return (double)bid;
            case(Quote.ASK):
                return (double)ask;
            default:
                assert false;
                return 0.0D;
            }
        }

        public IDQuote toQuote(Symbol symbol, TradingDate date, TradingTime time) {
            return new IDQuote(symbol,
                               date,
                               time,
                               day_volume,
                               (double)day_low,
                               (double)day_high,
                               (double)day_open,
                               (double)day_close,
                               (double)bid,
                               (double)ask);
        }

        public boolean equals(int day_volume, float day_low, float day_high,
                              float day_open, float day_close, float bid,
                              float ask) {
            return (day_volume == this.day_volume &&
                    day_low == this.day_low &&
                    day_high == this.day_high &&
                    day_open == this.day_open &&
                    day_close == this.day_close &&
                    bid == this.bid &&
                    ask == this.ask);
        }
    }

    // Class should only be constructed once by this class
    private IDQuoteCache() {
        cache = new ArrayList();
        times = new ArrayList();
        quoteListeners = new EventListenerList();
    }

    /**
     * Create or return the singleton instance of the quote cache.
     *
     * @return  singleton instance of this class
     */
    public static synchronized IDQuoteCache getInstance() {
	if(instance == null)
	    instance = new IDQuoteCache();

        return instance;
    }

    /**
     * Get a quote from the cache.
     *
     * @param symbol    the symbol to load
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME},
     *                  {@link Quote#BID}, {@link Quote#ASK}.
     * @param timeOffset fast access time offset
     * @return the quote
     * @exception QuoteNotLoadedException if the quote was not in the cache
     */
    public double getQuote(Symbol symbol, int quoteType, int timeOffset)
        throws QuoteNotLoadedException {
        
        // Get the quote cache quote for the given symbol + time
        IDQuoteCacheQuote quote = getQuoteCacheQuote(symbol, timeOffset);
        if(quote != null)
            return quote.getQuote(quoteType);
        else
            throw QuoteNotLoadedException.getInstance();
    }

    /**
     * Get a quote from the cache.
     *
     * @param symbol     the symbol to load
     * @param timeOffset fast access time offset
     * @return the quote
     * @exception QuoteNotLoadedException if the quote was not in the cache
     */
    public IDQuote getQuote(Symbol symbol, int timeOffset)
        throws QuoteNotLoadedException {
        
        // Get the quote cache quote for the given symbol + time
        IDQuoteCacheQuote quote = getQuoteCacheQuote(symbol, timeOffset);
        if(quote != null)
            return quote.toQuote(symbol, date, offsetToTime(timeOffset));
        else
            throw QuoteNotLoadedException.getInstance();
    }
    
    // Returns the quote cache object for the given time
    private IDQuoteCacheQuote getQuoteCacheQuote(Symbol symbol, int timeOffset)
        throws QuoteNotLoadedException {

 	// First get the hash map for the given time
        HashMap symbols = getQuotesForTime(timeOffset);
        assert symbols != null;

 	// Second get the quote for the given symbol on the given time
 	return (IDQuoteCacheQuote)symbols.get(symbol);
    }

    // Returns a HashMap containing quotes for the given time
    private HashMap getQuotesForTime(int timeOffset)
 	throws QuoteNotLoadedException {
        
        assert timeOffset >= 0;
        
        if(timeOffset >= cache.size())
 	    throw QuoteNotLoadedException.getInstance();
	
 	HashMap quotesForTime = (HashMap)cache.get(timeOffset);
        
 	if(quotesForTime == null)
 	    throw QuoteNotLoadedException.getInstance();
        
 	return quotesForTime;
    }
    
    /**
     * Load a time slice of intra-day quotes into the cache. Each of these quotes will
     * be given the same fast time offset and thus considerd as a group in time, even
     * if their given times are slightly different.
     *
     * @param quotes list of quotes to cache
     */
    public void load(List quotes) {
        if(quotes.size() > 0) {
            // Get the most recent time of any of the quotes. All the quotes will be
            // assigned this time.
            TradingTime time = getNewestTime(quotes);

            // Get date of first quote
            IDQuote firstQuote = (IDQuote)quotes.get(0);
            TradingDate date = firstQuote.getDate();

            if(this.date == null)
                this.date = date;

            // Convert each quote to a QuoteCacheQuote and add to a Map
            Map map = new HashMap();
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                IDQuote quote = (IDQuote)iterator.next();
                map.put(quote.getSymbol(),
                        new IDQuoteCacheQuote(quote.getDayVolume(),
                                              (float)quote.getDayLow(),
                                              (float)quote.getDayHigh(),
                                              (float)quote.getDayOpen(),
                                              (float)quote.getDayClose(),
                                              (float)quote.getBid(),
                                              (float)quote.getAsk()));
            }

            // Add hash map to cache
            cache.add(map);
            times.add(time);

            // Signal to listeners that there are new quotes
            fireQuotesAdded();
        }
    }

    /**
     * Convert between a time and its fast access time offset. Returns a negative
     * number if the time is not in the cache.
     *
     * @param time the time
     * @return fast access time offset
     */
    public int timeToOffset(TradingTime time) {
        return Collections.binarySearch(times, time);
     }

    /**
     * Convert between a fast access time offset and a time. Returns <code>null</code> if the
     * time is not in the cache.
     *
     * @param timeOffset fast access time offset
     * @return the time
     */
    public TradingTime offsetToTime(int timeOffset) {
 	assert timeOffset >= 0;

        TradingTime time = null;
        
        if(timeOffset < times.size())
            time = (TradingTime)times.get(timeOffset);

        return time;
    }

    /**
     * Return the fast access time offset of the oldest time in the cache.
     *
     * @return the fast access time offset of the oldest time in cache or -1 if there
     *         are no times in the cache.
     */
    public int getFirstTimeOffset() {
        if(times.size() == 0)
            return -1;
        else
            return 0;
    }

    /**
     * Return the fast access time offset of the newest time in the cache.
     *
     * @return the fast access time offset of the oldest time in cache or -1 if there
     *         are no times in the cache.
     */
    public int getLastTimeOffset() {
        return times.size() - 1;
    }
    
    /**
     * Add a listener to listen for new intra-day quotes.
     *
     * @param quoteListener the class to be informed about new intra-day quotes
     */
    public void addQuoteListener(QuoteListener quoteListener) {
        quoteListeners.add(QuoteListener.class, quoteListener);
    }

    /**
     * Remove a listener for new intra-day quotes.
     *
     * @param quoteListener the object to remove
     */
    public void removeQuoteListener(QuoteListener quoteListener) {
        quoteListeners.remove(QuoteListener.class, quoteListener);
    }

    /**
     * Fire a notification to all classes waiting for notification when new quotes
     * have arrived, that the quotes are now available.
     */
    private void fireQuotesAdded() {
        EventListener[] listeners = quoteListeners.getListeners(QuoteListener.class);
        QuoteEvent event = new QuoteEvent(this);

        for(int i = 0; i < listeners.length; i++) {
            QuoteListener listener = (QuoteListener)listeners[i];
            listener.newQuotes(event);
        }
    }

    /**
     * Traverses an array of intra-day quotes and returns the most recent
     * time of any intra-day quotes.
     *
     * @param quotes List of quotes
     * @return the most recent time
     */
    private TradingTime getNewestTime(List quotes) {
        TradingTime time = null;

        for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
            IDQuote quote = (IDQuote)iterator.next();

            if(time == null || quote.getTime().compareTo(time) < 0)
                time = quote.getTime();
        }

        return time;
    }
}
