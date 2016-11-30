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

import java.io.*;
import java.util.*;

import org.mov.util.*;
import org.mov.ui.DesktopManager;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;

/**
 * Provides functionality to obtain stock quotes from files. This class
 * implements the QuoteSource interface to allow users to directly use
 * their stock quote files without creating a database.
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
 * @see Quote
 * @see QuoteRange
 * @see QuoteBundle
 */
public class FileQuoteSource implements QuoteSource
{
    // Construct a map between TradingDates and file names
    private HashMap dateToFile = null;

    // List of files containing quotes
    private Vector fileNames = null;

    // Buffer first & last trading date in file database
    private TradingDate lastDate = null;
    private TradingDate firstDate = null;

    // Filter to convert data into quote
    private QuoteFilter filter;

    // When reading in the first quote - don't read the whole file,
    // we don't need it. Make sure we read in this amount and no more.
    // (Two lines).
    private final static int ONE_LINE_BUFFER_SIZE = 160;

    // Given a name of a file containing a list of day quotes, return the
    // the day
    private TradingDate getContainedDate(String fileName)
	throws java.io.IOException {

	TradingDate date = null;

        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr, ONE_LINE_BUFFER_SIZE);
        Quote quote = filter.toQuote(br.readLine());
        if(quote != null)
            date = quote.getDate();
        br.close();

	return date;
    }

    // Given a quote range and a file name, return a vector of all
    // quotes we are looking for in this file.
    private Vector getContainedQuotes(String fileName,
                                      QuoteRange quoteRange) {

        Vector quotes = new Vector();
	String line;

        assert fileName != null && quoteRange != null;

	try {
	    FileReader fr = new FileReader(fileName);
	    BufferedReader br = new BufferedReader(fr);
	    line = br.readLine();

	    while(line != null) {
		Quote quote = filter.toQuote(line);

                // Legal quote?
                if(quote != null) {

                    assert quote.getSymbol() != null;

                    // Is this one of the ones we are looking for?
                    if(quoteRange.containsSymbol(quote.getSymbol().toLowerCase())) {
                        quotes.add(quote);

                        // If we are only looking for a certain set of
                        // symbols, exit when we have found them
                        if(quoteRange.getType() == QuoteRange.GIVEN_SYMBOLS &&
                           quotes.containsAll(quoteRange.getAllSymbols()))
                            break;
                    }
                }

                line = br.readLine();
	    }
		
	    br.close();

	} catch (java.io.IOException ioe) {
	    DesktopManager.showErrorMessage("Can't load " + fileName);
	}

        return quotes;
    }

    /**
     * Creates a new quote source using the list of files specified in the user
     * preferences.
     *
     * @param	format	The format filter to use to parse the quotes
     * @param	fileNames	Vector of file names
     */
    public FileQuoteSource(String format, Vector fileNames) {

	// Set filter to whatever is defined in the preferences to filter
	// to our internal format
	filter = QuoteFilterList.getInstance().getFilter(format);

        this.fileNames = fileNames;
    }

    // Checks that we actually have any quotes files and that we have any quotes
    // in those files. Returns TRUE if we have at least one quote, FALSE otherwise.
    private synchronized boolean checkFiles() {
        if(dateToFile == null) {
            createIndex();

            // Still empty after loading all our quote files?
            if(dateToFile == null) {
                DesktopManager.showErrorMessage("Venice couldn't find any quotes.\n" +
                                                "You can import quotes using the import\n" +
                                                "quote tool under the File menu.");
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
        dateToFile = new HashMap();
        
        TradingDate date;
        
        // Make sure we don't pop up 1000 error messages if all the files
        // have been moved :)
        int errorCount = 0;
        
        // Indexing might take a while
        ProgressDialog p = ProgressDialogManager.getProgressDialog();
        p.setMaster(true);
        p.setMaximum(fileNames.size());
        p.setNote("Indexing files");
        p.show("Indexing files");
        
        Iterator iterator = fileNames.iterator();
        String fileName;
        
        while(iterator.hasNext()) {
            
            fileName = (String)iterator.next();
            
            try {
                date = getContainedDate(fileName);
                
                if(date != null) {
                    // Buffer the first and last quote dates
                    if(lastDate == null || date.after(lastDate))
                        lastDate = date;
                    if(firstDate == null || date.before(firstDate))
                        firstDate = date;
                    
                    // Associate this date with this file
                    dateToFile.put(date, fileName);
                }		
                else {
                    if(errorCount < 5) {
                        DesktopManager.
                            showErrorMessage("No quotes found in " +
                                             fileName);
                        errorCount++;
                    }
                }
                
            } catch (java.io.IOException ioe) {
                if(errorCount < 5) {
                    DesktopManager.showErrorMessage("Can't load " +
                                                    fileName);
                    errorCount++;	
                }
            }
            
            p.increment();
        }
        
        ProgressDialogManager.closeProgressDialog(p);

        // Nuke the hash if it is empty
        if(dateToFile.isEmpty())
            dateToFile = null;
    }


    /**
     * Returns the company name associated with the given symbol. Not
     * implemented for the file quote source.
     *
     * @param	symbol	the stock symbol.
     * @return	always an empty string.
     */
    public String getSymbolName(String symbol) {
	return new String("");
    }

    /**
     * Returns the symbol associated with the given company. Not
     * implemented for the file quote source.
     *
     * @param	symbol	a partial company name.
     * @return	always an empty string.
     */
    public String getSymbol(String partialSymbolName) {
	return new String("");
    }

    /**
     * Returns whether we have any quotes for the given symbol.
     *
     * @param	symbol	the symbol we are searching for.
     * @return	whether the symbol was found or not.
     */
    public boolean symbolExists(String symbol) {

        if(checkFiles()) {
            // Iterate through all files until we find one containing the
            // symbol name we are looking for
            Set dates = dateToFile.keySet();
            Iterator iterator = dates.iterator();
            
            while(iterator.hasNext()) {
                TradingDate date = (TradingDate)iterator.next();
                Vector quotes = getContainedQuotes(getFileForDate(date),
                                                   new QuoteRange(symbol));
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
     * Returns the filename that contains quotes for the given date.
     *
     * @param	date	the given date
     * @return	the file containing quotes for this date
     */
    public String getFileForDate(TradingDate date) {
        if(checkFiles()) 
            return (String)dateToFile.get(date);
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
            String file = getFileForDate(date);
            return file != null;
        }
        else
            return false;
    }

    /**
     * Return all the dates which we have quotes for (SLOW0.
     *
     * @return a vector of dates
     */
    public Vector getDates() {
        if(checkFiles()) 
            return new Vector(dateToFile.keySet());
        else
            return new Vector();
    }

    /**
     * Load the given quote range into the quote cache.
     *
     * @param	quoteRange	the range of quotes to load
     * @return  <code>TRUE</code> if the operation suceeded
     * @see Quote
     * @see QuoteCache
     */
    public boolean loadQuoteRange(QuoteRange quoteRange) {

        if(checkFiles()) {
            // This needs to be before the progress dialog otherwise
            // we might end up (during an import) trying to open 3
            // progress dialogs within one thread which is illegal.
            QuoteCache quoteCache = QuoteCache.getInstance();

            // This query might take a while...
            Thread thread = Thread.currentThread();
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            progress.setNote("Loading Quotes...");
            progress.setIndeterminate(true);
            
            // Work out date range in quote range
            TradingDate firstDate = quoteRange.getFirstDate();
            TradingDate lastDate = quoteRange.getLastDate();
            
            // ... all dates?
            if(firstDate == null) {
                firstDate = this.firstDate;
                lastDate = this.lastDate;
            }
            
            Vector dates = Converter.dateRangeToTradingDateVector(firstDate,
                                                                  lastDate);
            
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
                Quote quote;
                
                // Load all quotes from the file
                String fileName = getFileForDate(date);
                
                if(fileName != null) {
                    Vector quotes = getContainedQuotes(fileName, quoteRange);
                                        
                    // Load quotes into cache
                    for(Iterator quoteIterator = quotes.iterator(); quoteIterator.hasNext();) {
                        quoteCache.load((Quote)quoteIterator.next());
                    }
                }
                
                if(thread.isInterrupted())
                    break;
                
                if(dates.size() > 1)
                    progress.increment();
            }
            
            ProgressDialogManager.closeProgressDialog(progress);
            
            return true;
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
    public boolean isMarketIndex(String symbol) {
        // HACK. It needs to keep a file which maintains a flag
        // for whether a symbol is an index or not.
	assert symbol != null;

	if(symbol.length() == 3 && symbol.toUpperCase().charAt(0) == 'X')
	    return true;
	else
	    return false;
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

            String fileName = getFileForDate(date);
            
            if(fileName == null)
                throw new MissingQuoteException();
            
            // Get all ordinaries for that date
            QuoteRange quoteRange = new QuoteRange(QuoteRange.ALL_ORDINARIES, date);
            Vector quotes = getContainedQuotes(fileName, quoteRange);
            
            int advanceDecline = 0;
            
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                Quote quote = (Quote)iterator.next();
                
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
}
