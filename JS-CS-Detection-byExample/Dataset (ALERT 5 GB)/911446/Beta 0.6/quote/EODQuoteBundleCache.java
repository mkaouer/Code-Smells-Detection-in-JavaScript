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
import java.util.Iterator;
import java.util.List;

import org.mov.prefs.PreferencesManager;

/**
 * This class is responsible for caching quote bundles. When a {@link EODQuoteBundle} is
 * created it tries to load itself into memory by calling the <code>load()</code> method in
 * this class. When this method is called, this class will make sure that all of its quotes 
 * are loaded in the {@link EODQuoteCache}. If not, it will try and load only the quotes not 
 * present in the quote cache.
 * <p>
 * When this class loads quotes into the quote cache, it will check to make sure the
 * quote cache hasn't got too big. If it has, it will free up the quotes used only by
 * the oldest quote caches.
 * <p>
 * Its possible that the oldest quote bundle is still in use, in which case when the
 * quote bundle tries to access the cache it might not find its quote.
 * If thats the case, it will call <code>load()</code> again to reload the quotes.
 *
 * @see EODQuote
 * @see EODQuoteBundle
 * @see EODQuoteCache
 * @see Symbol
 */
public class EODQuoteBundleCache {

    // Singleton instance of class
    private static EODQuoteBundleCache instance = null;

    // Loaded quote bundle stack
    private List loadedQuoteBundles;

    // For speed reasons keep copy of quote cache instance
    private EODQuoteCache quoteCache;

    // Class should only be constructed once by this class
    private EODQuoteBundleCache() {
	quoteCache = EODQuoteCache.getInstance();
	loadedQuoteBundles = Collections.synchronizedList(new ArrayList());
    }

    /**
     * Create or return the singleton instance of the quote bundle cache.
     *
     * @return  singleton instance of this class
     */
    public static synchronized EODQuoteBundleCache getInstance() {
	if(instance == null)
	    instance = new EODQuoteBundleCache();

        return instance;
    }

    /**
     * Expand the given quote bundle to include quotes from the new quote range.
     * When this function is called, the quote bundle should still have its old
     * quote range. It will be updated by this function.
     *
     * @param quoteBundle        the quote bundle to expand
     * @param expandedQuoteRange the quote bundles new quote range
     */
    public void expand(EODQuoteBundle quoteBundle, EODQuoteRange expandedQuoteRange) {
        // If the quote bundle is already loaded, then clip the expanded quote range
        // so we don't try and re-load any of the load symbols.
        if(isLoaded(quoteBundle)) 
            expandedQuoteRange = quoteBundle.getQuoteRange().clip(expandedQuoteRange);

        // Otherwise place the quote bundle in the list as we will now load it.
        else
            loadedQuoteBundles.add(quoteBundle);

        // Load the quotes from the expanded quote bundle
        forceLoad(expandedQuoteRange);

        // Update quote range
        quoteBundle.setQuoteRange(expandedQuoteRange);
    }

    /** 
     * Make sure all the quotes needed by the quote bundle are in the quote cache.
     *
     * @param quoteBundle       the quote bundle to load
     * @see EODQuoteCache
     */
    public void load(EODQuoteBundle quoteBundle) {
	if(!isLoaded(quoteBundle)) {
            loadedQuoteBundles.add(quoteBundle);

            if(!forceLoad(quoteBundle.getQuoteRange()))
                loadedQuoteBundles.remove(quoteBundle);
        }
    }
    
    // Make sure all the quotes in the given quote range into the quote cache.
    private boolean forceLoad(EODQuoteRange quoteRange) {
        // Go thorugh all loaded quote bundles to reduce the
        // possibility of reloading quotes that are already in
        // the cache
        synchronized(loadedQuoteBundles) {
            Iterator iterator = loadedQuoteBundles.iterator();
        
            while(iterator.hasNext() && !quoteRange.isEmpty()) {
                EODQuoteBundle traverse = (EODQuoteBundle)iterator.next();            

                // Don't check against last entry - that one contains the
                // new quote bundle
                if(iterator.hasNext())
                    quoteRange = traverse.getQuoteRange().clip(quoteRange);
            }
        }
        
        if(!quoteRange.isEmpty()) { 
            // Load the quote range into the quote cache. Return immediately if
            // we couldn't load it.
            if(!QuoteSourceManager.getSource().loadQuoteRange(quoteRange))
                return false;
           
            // If the quote cache has too many quotes then keep
            // freeing the oldest bundle - but don't free the
            // newest bundle, since that is the one we are loading.
            int maximumCachedQuotes = PreferencesManager.loadMaximumCachedQuotes();

            synchronized(loadedQuoteBundles) {
                while(quoteCache.size() > maximumCachedQuotes &&
                      loadedQuoteBundles.size() > 1) {
                    free((EODQuoteBundle)loadedQuoteBundles.get(0));
                }
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
    public boolean isLoaded(EODQuoteBundle quoteBundle) {
        return loadedQuoteBundles.contains(quoteBundle);
    }

    /**
     * Remove the quote bundle from the quote bundle cache and remove all of its
     * quotes that aren't used by other quote bundles from the quote cache.
     * Quote bundles never need to call this optional method.
     *
     * @param quoteBundle       the quote bundle to free
     */
    private void free(EODQuoteBundle quoteBundle) {
	// Now traverse each quote in bundle, check to see if its used by any other
	// bundle. If not, then free.
	int firstDateOffset = quoteBundle.getFirstOffset();
	int lastDateOffset = quoteBundle.getLastOffset();

	for(int dateOffset = firstDateOffset; dateOffset <= lastDateOffset; dateOffset++) {

	    List symbols = quoteBundle.getSymbols(dateOffset);
	    Iterator iterator = symbols.iterator();

	    while(iterator.hasNext()) {	    
		Symbol symbol = (Symbol)iterator.next();

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
    private boolean isQuoteUsedElsewhere(Symbol symbol, int dateOffset) {
	Iterator iterator = loadedQuoteBundles.iterator();

        // Don't check first quote bundle.
        if(iterator.hasNext())
            iterator.next();

	while(iterator.hasNext()) {
	    EODQuoteBundle quoteBundle = (EODQuoteBundle)iterator.next();

	    if(quoteBundle.containsQuote(symbol, dateOffset))
		return true;
	}

	return false;
    }
}
