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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.String;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import nz.org.venice.util.Currency;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.ProgressDialog;
import nz.org.venice.ui.ProgressDialogManager;

/**
 * Provides functionality to obtain stock quotes from files. This class
 * implements the QuoteSource interface to allow users to directly use
 * their stock quote files without creating a database.
 *
 * Example:
 * <pre>
 *      EODQuoteRange quoteRange = new EODQuoteRange("CBA");
 *      EODQuoteBundle quoteBundle = new EODQuoteBundle(quoteRange);
 *      try {
 *	    float = quoteBundle.getQuote("CBA", Quote.DAY_OPEN, 0);
 *      }
 *      catch(QuoteNotLoadedException e) {
 *          //...
 *      }
 * </pre>
 *
 * @see Quote
 * @see EODQuoteRange
 * @see EODQuoteBundle
 */
public class FileQuoteSource implements QuoteSource
{
    // Only display this many errors about not being able to load files.
    // This prevents the user from being swamped with error messages.
    private final static int MAXIMUM_ERRORS = 5;

    // Construct a map between TradingDates and file names
    private HashMap dateToURL = null;

    // List of URLs of files containing quotes
    private List fileURLs = null;

    // Buffer first & last trading date in file database
    private TradingDate lastDate = null;
    private TradingDate firstDate = null;

    // Filter to convert data into quote
    private EODQuoteFilter filter;

    /**
     * Creates a new quote source using the list of files specified in the user
     * preferences.
     *
     * @param	format  The format filter to use to parse the quotes
     * @param	fileURLs       List of URL of files
     */
    public FileQuoteSource(String format, List fileURLs) {

	// Set filter to whatever is defined in the preferences to filter
	// to our internal format
	filter = EODQuoteFilterList.getInstance().getFilter(format);

        this.fileURLs = fileURLs;
    }

    // Given a name of a file containing a list of day quotes, return the
    // the day
    private TradingDate getContainedDate(URL fileURL)
	throws IOException {

        InputStreamReader isr = new InputStreamReader(fileURL.openStream());
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();

        // Keep reading each line until we find a valid quote and then
        // return its date
        while(line != null) {
            try {
                EODQuote quote = filter.toEODQuote(line);
                return quote.getDate();
            }
            catch(QuoteFormatException e) {
                line = br.readLine();
            }
        }

        // Couldn't find a legal quote...
	return null;
    }

    // Given a quote range and a file name, return a list of all
    // quotes we are looking for in this file.
    private List getContainedQuotes(URL fileURL,
                                    EODQuoteRange quoteRange) {

        List quotes = new ArrayList();
	String line;

        assert fileURL != null && quoteRange != null;

	try {
            InputStreamReader isr = new InputStreamReader(fileURL.openStream());
	    BufferedReader br = new BufferedReader(isr);
	    line = br.readLine();

	    while(line != null) {
                try {
                    EODQuote quote = filter.toEODQuote(line);

                    // Is this one of the ones we are looking for?
                    if(quoteRange.containsSymbol(quote.getSymbol())) {
                        quotes.add(quote);

                        // If we are only looking for a certain set of
                        // symbols, exit when we have found them
                        if(quoteRange.getType() == EODQuoteRange.GIVEN_SYMBOLS &&
                           quotes.containsAll(quoteRange.getAllSymbols()))
                            break;
                    }
                }
                catch(QuoteFormatException e) {
                    // This is only used for the sample quotes - and they should be valid.
                    assert false;
                }

                line = br.readLine();
	    }
		
	    br.close();

	} catch (IOException e) {
            // This is only a warning message because as long as one file
            // loaded we can continue.	    
	    DesktopManager.showWarningMessage(Locale.getString("ERROR_READING_FROM_FILE",
							       fileURL.getPath()));
	}

        return quotes;
    }

    // Checks that we actually have any quotes files and that we have any quotes
    // in those files. Returns TRUE if we have at least one quote, FALSE otherwise.
    private synchronized boolean checkFiles() {
        if(dateToURL == null) {
            createIndex();

            // Still empty after loading all our quote files?
            if(dateToURL == null) {
                DesktopManager.showErrorMessage(Locale.getString("NO_QUOTES_FOUND"));
                return false;
            }
        }
        return true;
    }

    // Ensure that the date to file map has been created. Also make sure the latest
    // and earliest quote dates have been stored. This allows us speed up various
    // functions.
    private void createIndex() {
        Thread thread = Thread.currentThread();

        // Create map
        dateToURL = new HashMap();
        
        TradingDate date;
        
        // Make sure we don't pop up 1000 error messages if all the files
        // have been moved :)
        int errorCount = 0;
        
        // Indexing might take a while
        ProgressDialog p = ProgressDialogManager.getProgressDialog();
        p.setMaster(true);
        p.setMaximum(fileURLs.size());
	p.setProgress(0);
        p.setNote(Locale.getString("INDEXING_FILES"));
        p.show(Locale.getString("INDEXING_FILES"));
        
        for(Iterator iterator = fileURLs.iterator(); iterator.hasNext();) {
            URL fileURL = (URL)iterator.next();
            
            try {
                date = getContainedDate(fileURL);
                
                if(date != null) {
                    // Buffer the first and last quote dates
                    if(lastDate == null || date.after(lastDate))
                        lastDate = date;
                    if(firstDate == null || date.before(firstDate))
                        firstDate = date;
                    
                    // Associate this date with this file
                    dateToURL.put(date, fileURL);
                }		
                else {
                    if(errorCount++ < MAXIMUM_ERRORS) {
                        // These messages are only warning messages because we
                        // can continue.
                        String message = Locale.getString("NO_QUOTES_FOUND_IN_FILE",
							  fileURL.getPath());
                        DesktopManager.showWarningMessage(message);
                    }
                }
                
            } catch (IOException e) {
                if(errorCount++ < MAXIMUM_ERRORS) {
                    // These messages are only warning messages because we
                    // can continue.
                    String message = Locale.getString("ERROR_READING_FROM_FILE",
						      fileURL.getPath());
                    DesktopManager.showWarningMessage(message);
                }
            }
            
            p.increment();
        }
        
        ProgressDialogManager.closeProgressDialog(p);

        // Nuke the hash if it is empty
        if(dateToURL.isEmpty())
            dateToURL = null;
    }

    /**
     * Returns the company name associated with the given symbol. Not
     * implemented for the file quote source.
     *
     * @param	symbol	the stock symbol.
     * @return	always an empty string.
     */
    public String getSymbolName(Symbol symbol) {
	return null;
    }

    /**
     * Returns the symbol associated with the given company. Not
     * implemented for the file quote source.
     *
     * @param	partialCompanyName	a partial company name.
     * @return	always an empty string.
     */
    public Symbol getSymbol(String partialCompanyName) {
	return null;
    }

    /**
     * Returns whether we have any quotes for the given symbol.
     *
     * @param	symbol	the symbol we are searching for.
     * @return	whether the symbol was found or not.
     */
    public boolean symbolExists(Symbol symbol) {

        if(checkFiles()) {
            // Iterate through all files until we find one containing the
            // symbol name we are looking for
            for(Iterator iterator = dateToURL.keySet().iterator(); iterator.hasNext();) {
                TradingDate date = (TradingDate)iterator.next();
                List quotes = getContainedQuotes(getURLForDate(date),
                                                 new EODQuoteRange(symbol));
                if(quotes.size() > 0)
                    return true; // found!
            }
        }

	return false;
    }

    /**
     * Return the earliest date we have any stock quotes for.
     *
     * @return	the oldest quote date
     */
    public TradingDate getFirstDate() {
        checkFiles();

        return firstDate;
    }

    /**
     * Return the latest date we have any stock quotes for.
     *
     * @return	the most recent quote date.
     */
    public TradingDate getLastDate() {
        checkFiles();

	return lastDate;
    }

    /**
     * Returns the file URL that contains quotes for the given date.
     *
     * @param	date	the given date
     * @return	the file URL containing quotes for this date
     */
    public URL getURLForDate(TradingDate date) {
        if(checkFiles()) 
            return (URL)dateToURL.get(date);
        else
            return null;
    }

    /**
     * Returns whether the source contains any quotes for the given date.
     *
     * @param date the date
     * @return wehther the source contains the given date
     */
    public boolean containsDate(TradingDate date) {
        if(checkFiles()) {
            // If we have a file - we'll assume we also have quotes
            URL fileURL = getURLForDate(date);
            return fileURL != null;
        }
        else
            return false;
    }

    /**
     * Return all the dates which we have quotes for (SLOW0.
     *
     * @return a list of dates
     */
    public List getDates() {
        if(checkFiles()) 
            return new ArrayList(dateToURL.keySet());
        else
            return new ArrayList();
    }

    /**
     * Load the given quote range into the quote cache.
     *
     * @param	quoteRange	the range of quotes to load
     * @return  <code>TRUE</code> if the operation suceeded
     * @see EODQuote
     * @see EODQuoteCache
     */
    public boolean loadQuoteRange(EODQuoteRange quoteRange) {

        if(checkFiles()) {
            // This needs to be before the progress dialog otherwise
            // we might end up (during an import) trying to open 3
            // progress dialogs within one thread which is illegal.
            EODQuoteCache quoteCache = EODQuoteCache.getInstance();

            // This query might take a while...
            Thread thread = Thread.currentThread();
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            progress.setNote(Locale.getString("LOADING_QUOTES"));
            progress.setIndeterminate(true);
            
            // Work out date range in quote range
            TradingDate firstDate = quoteRange.getFirstDate();
            TradingDate lastDate = quoteRange.getLastDate();
            
            // ... all dates?
            if(firstDate == null) {
                firstDate = this.firstDate;
                lastDate = this.lastDate;
            }
            
            List dates = TradingDate.dateRangeToList(firstDate, lastDate);
            
            // If there are multiple dates, set the progress indicator
            // to indicate the date we are on. Otherwise set it to
            // indeterminate.
            if(dates.size() > 1) {
                progress.setMaximum(dates.size());
                progress.setProgress(0);
                progress.setIndeterminate(false);
            }
            else {
                progress.setIndeterminate(true);
            }
            
            for(Iterator iterator = dates.iterator(); iterator.hasNext();) {
                TradingDate date = (TradingDate)iterator.next();
                
                // Load all quotes from the file
                URL fileURL = getURLForDate(date);
                
                if(fileURL != null) {
                    List quotes = getContainedQuotes(fileURL, quoteRange);
                                        
                    // Load quotes into cache
                    for(Iterator quoteIterator = quotes.iterator(); quoteIterator.hasNext();) {
                        EODQuote quote = (EODQuote)quoteIterator.next();
                        quoteCache.load(quote);
                    }
                }
                
                if(thread.isInterrupted())
                    break;
                
                if(dates.size() > 1)
                    progress.increment();
            }
            
            ProgressDialogManager.closeProgressDialog(progress);
            
            return !thread.isInterrupted();
        }
        else
            return false;
    }

    /**
     * Is the given symbol a market index?
     *
     * @param	symbol to test
     * @return	yes or no
     */
    public boolean isMarketIndex(Symbol symbol) {
	
	if (PreferencesManager.isMarketIndex(symbol)) {
	    return true;
	} else {
	    return false;
	}

	/* Previous version; guaranteed for ASX, not for DAX or 
	   anything else.
	// HACK. It needs to keep a table which maintains a flag
        // for whether a symbol is an index or not.

	if(symbol.length() == 3 && symbol.charAt(0) == 'X')
	    return true;
	else
	    return false;
	*/
    }

    /**
     * Return the advance/decline for the given date. This returns the number
     * of all ordinary stocks that rose (day close > day open) - the number of all
     * ordinary stocks that fell.
     *
     * @param date the date
     * @exception throws MissingQuoteException if the date wasn't in the source
     */
    public int getAdvanceDecline(TradingDate date)
        throws MissingQuoteException {

        if(checkFiles()) {

            URL fileURL = getURLForDate(date);
            
            if(fileURL == null)
                throw MissingQuoteException.getInstance();
            
            // Get all ordinaries for that date
            EODQuoteRange quoteRange = new EODQuoteRange(EODQuoteRange.ALL_ORDINARIES, date);
            List quotes = getContainedQuotes(fileURL, quoteRange);
            
            int advanceDecline = 0;
            
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                EODQuote quote = (EODQuote)iterator.next();
                
                if(quote.getDayClose() > quote.getDayOpen())
                    advanceDecline++;
                else if(quote.getDayClose() < quote.getDayOpen())
                    advanceDecline--;
            }
            
            return advanceDecline;
        }
        else
            return 0;
    }
    
    public HashMap getAdvanceDecline(TradingDate firstDate, TradingDate lastDate)
        throws MissingQuoteException {

	HashMap advanceDeclines = new HashMap(); 
	TradingDate date = firstDate;
	while (date.before(lastDate)) {
	    int advanceDeclineValue = getAdvanceDecline(date);
	    advanceDeclines.put(date, new Integer(advanceDeclineValue));
	    date = date.next(1);
	}

	return advanceDeclines;
    }

    /**
     * Return all the stored exchange rates between the two currencies.
     *
     * @param sourceCurrency the currency to convert from
     * @param destinationCurrency the currency to convert to
     * @return the exchange rate being the number of destinationCurrency that you can buy per
     *         sourceCurrency
     */
    public List getExchangeRates(Currency sourceCurrency, Currency destinationCurrency) {
        // We do not store the exchange rates in files
        return new ArrayList();
    }

    public void shutdown() {
        // nothing to do
    }

    /**
     * Reset the first and last dates, forcing this quotesource to reload
     * data from the filesystem.
     */
    public void cacheExpiry() {
	firstDate = null;
	lastDate = null;
    }
}
