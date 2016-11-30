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

/**
 * This class is responsible for caching quote bundles. When a {@link QuoteBundle} is created it
 * tries to load itself into memory by calling the <code>load()</code> method in this class.
 * When this method is called, this class will make sure that all of its quotes are loaded
 * in the {@link QuoteCache}. If not, it will try and load only the quotes not present
 * in the quote cache.
 * <p>
 * When this class loads quotes into the quote cache, it will check to make sure the
 * quote cache hasn't got too big. If it has, it will free up the quotes used only by
 * the oldest quote caches.
 * <p>
 * Its possible that the oldest quote bundle is still in use, in which case when the
 * quote bundle tries to access the cache it might not find its quote.
 * If thats the case, it will call <code>load()</code> again to reload the quotes.
 *
 * @see Quote
 * @see QuoteBundle
 * @see QuoteCache
 */
public class QuoteBundleCache {

    // Singleton instance of class
    private static QuoteBundleCache instance = null;

    // Loaded quote bundle stack
    private Vector loadedQuoteBundles;

    // For speed reasons keep copy of quote cache instance
    private QuoteCache quoteCache;

    // TEMPORARY. Should be set in preferences. Maximum quotes that the cache
    // can have before we throw some old quote bundles away.
    private final static int MAX_QUOTES = 100000;

    // Class should only be constructed once by this class
    private QuoteBundleCache() {
	quoteCache = QuoteCache.getInstance();
	loadedQuoteBundles = new Vector();
    }

    /**
     * Create or return the singleton instance of the quote bundle cache.
     *
     * @return  singleton instance of this class
     */
    public static synchronized QuoteBundleCache getInstance() {
	if(instance == null)
	    instance = new QuoteBundleCache();

        return instance;
    }

    /**
     * Expand the given quote bundle to include quotes from the new quote range.
     * When this function is called, the quote bundle should still have its old
     * quote range. It will be updated by this function.
     *
     * @param quoteBundle the quote bundle to expand
     * @param expandedQuoteRange        the quote bundles new quote range
     */
    public void expand(QuoteBundle quoteBundle, QuoteRange expandedQuoteRange) {

        // Check dates for expansion
        QuoteRange oldQuoteRange = quoteBundle.getQuoteRange();
        assert expandedQuoteRange.getFirstDate() != null;
        assert expandedQuoteRange.getFirstDate().before(oldQuoteRange.getFirstDate()) ||
               expandedQuoteRange.getLastDate().after(oldQuoteRange.getLastDate());

        // Load the quotes from the expanded quote bundle
        load(expandedQuoteRange);

        // If the quote bundle isn't in our loaded list, put it there
        if(!isLoaded(quoteBundle)) 
            loadedQuoteBundles.add(quoteBundle);

        // Update quote range
        quoteBundle.setQuoteRange(expandedQuoteRange);
    }

    /** 
     * Make sure all the quotes needed by the quote bundle are in the quote cache.
     *
     * @param quoteBundle       the quote bundle to load
     * @see QuoteCache
     */
    public void load(QuoteBundle quoteBundle) {
        // If the bundle isn't loaded, then load it. If we successfully loaded it
        // then add it to the list.
	if(!isLoaded(quoteBundle) && load(quoteBundle.getQuoteRange()))
            loadedQuoteBundles.add(quoteBundle);
    }
    
    // Make sure all the quotes in the given quote range into the quote cache.
    private boolean load(QuoteRange quoteRange) {
        // Go thorugh all loaded quote bundles to reduce the
        // possibility of reloading quotes that are already in
        // the cache
        Iterator iterator = loadedQuoteBundles.iterator();
        
        while(iterator.hasNext()) {
            QuoteBundle traverse = (QuoteBundle)iterator.next();            
            quoteRange = traverse.getQuoteRange().clip(quoteRange);

            // If the quote range is null it means that the range
            // contains no quotes, meaning its entirely contained
            // by already loaded quote ranges
            if(quoteRange == null)
                break;
        }
        
        if(quoteRange != null) { 
            // Load the quote range into the quote cache. Return immediately if
            // we couldn't load it.
            if(!QuoteSourceManager.getSource().loadQuoteRange(quoteRange))
                return false;
            
            // If the quote cache has too many quotes then keep
            // freeing the oldest bundle
            while(quoteCache.size() > MAX_QUOTES &&
                  loadedQuoteBundles.size() > 0) {
                free((QuoteBundle)loadedQuoteBundles.firstElement());
            }
        }

        return true;
    }

    /**
     * Return whether the given quote bundle is loaded or not.
     *
     * @param quoteBundle       the quote bundle to check
     * @return <code>true</code> if the quote bundle is loaded
     */
    public boolean isLoaded(QuoteBundle quoteBundle) {
	// Check to see if its loaded - if it is make it the most recent one
	int index = loadedQuoteBundles.indexOf(quoteBundle);

	// Not loaded
	if(index == -1) 
	    return false;

	// Make sure its at the top of the stack (if its not already there)
	if(index != (loadedQuoteBundles.size() - 1)) {
	    loadedQuoteBundles.remove(index);
	    loadedQuoteBundles.add(quoteBundle);
	}

	return true;
    }

    /**
     * Remove the quote bundle from the quote bundle cache and remove all of its
     * quotes that aren't used by other quote bundles from the quote cache.
     * Quote bundles never need to call this optional method.
     *
     * @param quoteBundle       the quote bundle to free
     */
    public void free(QuoteBundle quoteBundle) {
	// Now traverse each quote in bundle, check to see if its used by any other
	// bundle. If not, then free.
	int firstDateOffset = quoteBundle.getFirstDateOffset();
	int lastDateOffset = quoteBundle.getLastDateOffset();

	for(int dateOffset = firstDateOffset; 
	    dateOffset <= lastDateOffset; dateOffset++) {

	    Vector symbols = quoteBundle.getSymbols(dateOffset);
	    Iterator iterator = symbols.iterator();

	    while(iterator.hasNext()) {
	    
		String symbol = (String)iterator.next();

		if(!isQuoteUsedElsewhere(symbol, dateOffset))
		    quoteCache.free(symbol, dateOffset);
	    }
	}

	// Finally remove from list of loaded quote bundles. We can't do this
        // earlier because when we call getSymbols() on the QuoteBundle it
        // will check to make sure its loaded. If we removed it earlier, it
        // would then request to be loaded again! 
	loadedQuoteBundles.remove(quoteBundle);
    }

    // Checks all loaded quote bundles (except the first one!) to see whether
    // it contains the given symbol quote on the given date. The first one isn't
    // checked since that is the one that is about to be free'd and will always
    // contain the given quote.
    private boolean isQuoteUsedElsewhere(String symbol, int dateOffset) {
	Iterator iterator = loadedQuoteBundles.iterator();

        // Don't check first quote bundle.
        if(iterator.hasNext())
            iterator.next();

	while(iterator.hasNext()) {
	    QuoteBundle quoteBundle = (QuoteBundle)iterator.next();

	    if(quoteBundle.containsQuote(symbol, dateOffset))
		return true;
	}

	return false;
    }
}
