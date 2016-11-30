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

import nz.org.venice.prefs.PreferencesManager;

/**
 * Returns the singleton reference to the quote source that the user
 * has selected in their preferences. This class will also be
 * updated when the user preferences has changed so the return quote source
 * will always be update to date.
 *
 * Example:
 * <pre>
 *	List quotes = QuoteSourceManager.getSource().getQuotesForSymbol("CBA");
 * </pre>
 * 
 * @author Andrew Leppard
 * @see QuoteSource
 */
public class QuoteSourceManager {
    
    // Singleton instance of QuoteSource class
    private static QuoteSource sourceInstance = null;

    private QuoteSourceManager() {
        // declared here so constructor is not public
    }
        
    /**
     * Set the quote source to be the given quote source. This function was
     * written for import. During import we set the quote source to be the
     * source source for the import. Then after the import we flush the
     * source and let it return to whatever the user selected.
     *
     * @param source the new quote source
     */
    public static void setSource(QuoteSource source) {
        if(sourceInstance != null)
            sourceInstance.shutdown();

        sourceInstance = source;
    }
    
    /**
     * The user has changed their quote source preferences, flush singleton
     * reference and create new instance. 
     */
    public static void flush() {
        if(sourceInstance != null) {
            sourceInstance.shutdown();
            sourceInstance = null;
        }
    }
    
    /**
     * Shutdown the quote source. Some quote sources require a shut down operation
     * to ensure that they are properly closed.
     */
    public static void shutdown() {
        if(sourceInstance != null)
            sourceInstance.shutdown();
    }

    /** 
     * Creates and returns singleton instance of quote source which
     * user has selected in the Preferences->Quote Source page.
     *
     * @return quote source
     */
    public static synchronized QuoteSource getSource() {
        if(sourceInstance == null) {
            int quoteSource = PreferencesManager.getQuoteSource();
            
            if(quoteSource == PreferencesManager.DATABASE)
                sourceInstance = QuoteSourceFactory.createDatabaseQuoteSource();
            else if(quoteSource == PreferencesManager.INTERNAL)
                sourceInstance = QuoteSourceFactory.createInternalQuoteSource();
            else {
                assert quoteSource == PreferencesManager.SAMPLES;
                sourceInstance = QuoteSourceFactory.createSamplesQuoteSource();
            }
        }
        return sourceInstance;
    }
}

