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

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.ProgressDialog;
import nz.org.venice.ui.ProgressDialogManager;
import nz.org.venice.util.Currency;
import nz.org.venice.util.ExchangeRate;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;

/**
 * Provides functionality to obtain stock quotes from a database. This class
 * implements the QuoteSource interface to allow users to obtain stock
 * quotes in the fastest possible manner.
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
 * @author Andrew Leppard
 * @see Quote
 * @see EODQuote
 * @see EODQuoteRange
 * @see EODQuoteBundle
 */
public class DatabaseQuoteSource implements QuoteSource
{
    private DatabaseManager manager = null;
    private boolean checkedTables = false;

    // Buffer first and last trading date in database
    private TradingDate firstDate = null;
    private TradingDate lastDate = null;

    // Fields for samples mode
    private EODQuoteFilter filter;
    private List fileURLs;

    // Column numbers
    // DATE_COLUMN
    // SOURCE_CURRENCY_COLUMN
    // DESTINATION_CURRENCY_COLUMN
    private final static int EXCHANGE_RATE_COLUMN = 4;
    

    
    /**
     * Creates a new quote source to connect to an external database.
     *
     * @param   manager   The DatabaseManager object which manages software,
     *                    username/host/port etc.
     */
    public DatabaseQuoteSource(DatabaseManager manager) {
        this.manager = manager;

    }

    /**
     * Returns the company name associated with the given symbol.
     *
     * @param	symbol	the stock symbol.
     * @return	the company name.
     */
    public String getSymbolName(Symbol symbol) {

	String name = null;

	if(manager.getConnection()) {        
	    try {
		Statement statement = manager.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT " + DatabaseManager.NAME_FIELD + " FROM " + 
		     DatabaseManager.LOOKUP_TABLE_NAME +
		     " WHERE " + DatabaseManager.SYMBOL_FIELD + " = '"
		     + symbol + "'");

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the name
		name = RS.getString(1);

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException E) {
		// not a big deal if this fails
	    }
	}

	return name;
    }

    /**
     * Returns the symbol associated with the given company.
     *
     * @param	partialCompanyName a partial company name.
     * @return	the company symbol.
     */
    public Symbol getSymbol(String partialCompanyName) {

	Symbol symbol = null;

	if(manager.getConnection()) {
	    try {

		String query = "SELECT " + 
		    DatabaseManager.SYMBOL_FIELD + 
		    " FROM " + DatabaseManager.LOOKUP_TABLE_NAME + 
		    " WHERE LOCATE(" +
		    "UPPER('" + partialCompanyName + "'), " +
		    DatabaseManager.NAME_FIELD + ") != 0";

		Statement statement = manager.createStatement();
		ResultSet RS = statement.executeQuery(query);

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the name
                try {
                    symbol = Symbol.find(RS.getString(1));
                }
                catch(SymbolFormatException e) {
                    // Error in data. Ignore.
                }

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException E) {
		// not a big deal if this fails
	    }
	}

	return symbol;
    }

    /**
     * Returns whether we have any quotes for the given symbol.
     *
     * @param	symbol	the symbol we are searching for
     * @return	whether the symbol was found or not
     */
    public boolean symbolExists(Symbol symbol) {
        boolean symbolExists = false;

	if(manager.getConnection()) {
	    try {
		Statement statement = manager.createStatement();
		
		// Return the first date found matching the given symbol.
		// If no dates are found - the symbol is unknown to us.
		// This should take << 1s
		String query = manager.buildSymbolPresentQuery(symbol);

		ResultSet RS = statement.executeQuery(query);

                // Find out if it has any rows
                symbolExists = RS.next();

		// Clean up after ourselves
		RS.close();
		statement.close();

	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
	    }
	}
	
        return symbolExists;
    }

    /**
     * Return the first date in the database that has any quotes.
     *
     * @return	oldest date with quotes
     */
    public TradingDate getFirstDate() {

	// Do we have it buffered?
	if(firstDate != null)
	    return firstDate;

	java.util.Date date = null;

	if(manager.getConnection()) {
	    try {
		Statement statement = manager.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT MIN(" + DatabaseManager.DATE_FIELD + ") FROM " +
		     DatabaseManager.SHARE_TABLE_NAME);

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the date
		date = RS.getDate(1);

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
	    }
	}

	if(date != null) {
	    firstDate = new TradingDate(date);
	    return firstDate;
	}
	else {
            showEmptyDatabaseError();
	    return null;
        }
    }

    /**
     * Force the database to reset the first and last dates so that new data
     * can be displayed.
     */
    public synchronized void cacheExpiry() {
	firstDate = null;
	lastDate = null;
    }

    /**
     * Return the last date in the database that has any quotes.
     *
     * @return	newest date with quotes
     */
    public TradingDate getLastDate() {
	// Do we have it buffered?
	if(lastDate != null)
	    return lastDate;

	java.util.Date date = null;

	if(manager.getConnection()) {
	    try {
		Statement statement = manager.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT MAX(" + DatabaseManager.DATE_FIELD + ") FROM " +
		     DatabaseManager.SHARE_TABLE_NAME);

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the date
		date = RS.getDate(1);

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
            }
	}

	if(date != null) {
	    lastDate = new TradingDate(date);
	    return lastDate;
	}
	else {
            showEmptyDatabaseError();
	    return null;
        }
    }

    /**
     * Is the given symbol a market index?
     *
     * @param	symbol to test
     * @return	yes or no
     */
    public boolean isMarketIndex(Symbol symbol) {
	assert symbol != null;

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
     * Load the given quote range into the quote cache.
     *
     * @param	quoteRange	the range of quotes to load
     * @return  <code>TRUE</code> if the operation suceeded
     * @see EODQuote
     * @see EODQuoteCache
     */
    public boolean loadQuoteRange(EODQuoteRange quoteRange) {

	String queryString = buildSQLString(quoteRange);
        boolean success;
	
	// This query might take a while...
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
        progress.setNote(Locale.getString("LOADING_QUOTES"));
        progress.setIndeterminate(true);
	
	success = executeSQLString(progress, queryString);
        ProgressDialogManager.closeProgressDialog(progress);
	
        return success;
    }

    /**
     * This function takes an SQL query statement that should return a list of
     * quotes. This function executes the statement and stores the quotes into
     * database. 
     *
     * @return <code>true</code> iff this function was successful.
     */
    private boolean executeSQLString(ProgressDialog progress, String SQLString) {

	if(manager.getConnection()) {
	    try {
		Statement statement = manager.createStatement();	
                Thread monitor = cancelOnInterrupt(statement);
                Thread thread = Thread.currentThread();
		ResultSet RS = statement.executeQuery(SQLString);

                // Monitor thread is no longer needed
                monitor.interrupt();

                if(!thread.isInterrupted()) {
                    EODQuoteCache quoteCache = EODQuoteCache.getInstance();

                    while (RS.next()) {
                        quoteCache.load(Symbol.find(RS.getString(DatabaseManager.SYMBOL_COLUMN).trim()),
                                        new TradingDate(RS.getDate(DatabaseManager.DATE_COLUMN)),
                                        RS.getLong(DatabaseManager.DAY_VOLUME_COLUMN),
                                        RS.getFloat(DatabaseManager.DAY_LOW_COLUMN),
                                        RS.getFloat(DatabaseManager.DAY_HIGH_COLUMN),
                                        RS.getFloat(DatabaseManager.DAY_OPEN_COLUMN),
                                        RS.getFloat(DatabaseManager.DAY_CLOSE_COLUMN));
                    }
                }                    

		// Clean up after ourselves
		RS.close();
		statement.close();
                return !thread.isInterrupted();
	    }
	    catch(SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
	    }
            catch(SymbolFormatException e2) {
                DesktopManager.showErrorMessage(Locale.getString("DATABASE_BADLY_FORMATTED_SYMBOL",
								 e2.getMessage()));
            }
	}

        return false;
    }

    // This function creates a new thread that monitors the current thread
    // for the interrupt call. If the current thread is interrupted it
    // will cancel the given SQL statement. If cancelOnInterrupt() is called,
    // once the SQL statement has finished, you should make sure the
    // thread is terminated by calling "interrupt" on the returned thread.
    private Thread cancelOnInterrupt(final Statement statement) {
        final Thread sqlThread = Thread.currentThread();

        Thread thread = new Thread(new Runnable() {
                public void run() {
                    Thread currentThread = Thread.currentThread();

                    while(true) {

                        try {
                            Thread.sleep(1000); // 1s
                        }
                        catch(InterruptedException e) {
                            break;
                        }

                        if(currentThread.isInterrupted())
                            break;
                        
                        if(sqlThread.isInterrupted()) {
                            try {
                                statement.cancel();
                            }
                            catch(SQLException e) {
                                // It's not a big deal if we can't cancel it
                            }
                            break;
                        }
                    }
                }
            });

        thread.start();
        return thread;
    }

    // Creates an SQL statement that will return all the quotes in the given
    // quote range.
    private String buildSQLString(EODQuoteRange quoteRange) {
        //
        // 1. Create select line
        //
	
        String queryString = "SELECT * FROM " + 
	    DatabaseManager.SHARE_TABLE_NAME + " WHERE ";
	
        //
        // 2. Filter select by symbols we are looking for
        //
	
        String filterString = new String("");
	
        if(quoteRange.getType() == EODQuoteRange.GIVEN_SYMBOLS) {
            List symbols = quoteRange.getAllSymbols();
            
            if(symbols.size() == 1) {
                Symbol symbol = (Symbol)symbols.get(0);
                
                filterString =
                    filterString.concat(DatabaseManager.SYMBOL_FIELD + 
					" = '" + symbol + "' ");
            }
            else {
                assert symbols.size() > 1;
                
                filterString = filterString.concat(DatabaseManager.SYMBOL_FIELD + " IN (");
                Iterator iterator = symbols.iterator();
                
                while(iterator.hasNext()) {
                    Symbol symbol = (Symbol)iterator.next();
                    
                    filterString = filterString.concat("'" + symbol + "'");
                    
                    if(iterator.hasNext())
                        filterString = filterString.concat(", ");
                }
		
                filterString = filterString.concat(") ");
            }
        }
        else if(quoteRange.getType() == EODQuoteRange.ALL_SYMBOLS) {
            // nothing to do
        }
        else if(quoteRange.getType() == EODQuoteRange.ALL_ORDINARIES) {
            filterString = filterString.concat("LENGTH(" + 
					       DatabaseManager.SYMBOL_FIELD + 
					       ") = 3 AND " +
                                               manager.left(DatabaseManager.SYMBOL_FIELD, 1) + " != 'X' ");
        }
        else {
            assert quoteRange.getType() == EODQuoteRange.MARKET_INDICES;
            
            filterString = filterString.concat("LENGTH(" + 
					       DatabaseManager.SYMBOL_FIELD + 
					       ") = 3 AND " +
                                               manager.left(DatabaseManager.SYMBOL_FIELD, 1) + " = 'X' ");
        }
	
        //
        // 3. Filter select by date range
        //
	
        // No dates in quote range, mean load quotes for all dates in the database
        if(quoteRange.getFirstDate() == null) {
            // nothing to do
        }
	
        // If they are the same its only one day
        else if(quoteRange.getFirstDate().equals(quoteRange.getLastDate())) {
            if(filterString.length() > 0)
                filterString = filterString.concat("AND ");
            
            filterString = filterString.concat(DatabaseManager.DATE_FIELD + 
					       " = '" +
                                               manager.
					       toSQLDateString(quoteRange.
							       getFirstDate()) 
					       + "' ");
        }
	
        // Otherwise check within a range of dates
        else {
            if(filterString.length() > 0)
                filterString = filterString.concat("AND ");
            
            filterString = filterString.concat(DatabaseManager.DATE_FIELD + 
					       " >= '" +
                                               manager.
					       toSQLDateString(quoteRange.
							       getFirstDate()) +
                                               "' AND " +
                                               DatabaseManager.DATE_FIELD + 
					       " <= '" +
                                               manager.
					       toSQLDateString(quoteRange.
							       getLastDate()) +
                                               "' ");
        }
	
        return queryString.concat(filterString);
    }
    
    
    /**
     * Import quotes into the database.
     *
     * @param quotes list of quotes to import
     * @return the number of quotes imported
     */
    public int importQuotes(List quotes) {
        // TODO: This function should probably update the cached firstDate and lastDate.
        int quotesImported = 0;

        if(quotes.size() > 0 && manager.getConnection()) {

            // Query the database to see which of these quotes is present
            List existingQuotes = findMatchingQuotes(quotes);

            // Remove duplicates
            List newQuotes = new ArrayList();
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                EODQuote quote = (EODQuote)iterator.next();

                if(!containsQuote(existingQuotes, quote))
                    newQuotes.add(quote);
            }

            if(newQuotes.size() > 0) {
                if(manager.supportForSingleRowUpdatesOnly()) {
                    quotesImported = importQuoteMultipleStatements(newQuotes);
		} else {
                    quotesImported = importQuoteSingleStatement(newQuotes);
		}
	    }
        }

        return quotesImported;
    }

    /**
     * Searches the list of quotes for the given quote. A match only
     * requires the symbol and date fields to match.
     *
     * @param quotes the list of quotes to search
     * @param quote the quote to search for
     * @return <code>true</code> if the quote is in the list, <code>false</code> otherwise
     */
    private boolean containsQuote(List quotes, EODQuote quote) {
        for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
            EODQuote containedQuote = (EODQuote)iterator.next();

            if(containedQuote.getSymbol().equals(quote.getSymbol()) &&
               containedQuote.getDate().equals(quote.getDate()))
                return true;
        }

        return false;
    }

    /**
     * Import quotes into the database using a separate insert statement for
     * each row. Use this function when the database does not support
     * multi-row inserts.
     *
     * @param	quotes list of quotes to import
     * @return the number of quotes imported
     */
    private int importQuoteMultipleStatements(List quotes) {
        int quotesImported = 0;

        // Iterate through the quotes and import them one-by-one.
        Iterator iterator = quotes.iterator();
        
        try {
            while(iterator.hasNext()) {
                EODQuote quote = (EODQuote)iterator.next();
                
                String insertQuery = new String("INSERT INTO " + 
						DatabaseManager.SHARE_TABLE_NAME +
                                                " VALUES (" +
                                                "'" + manager.
						toSQLDateString(quote
								.getDate()) 
						+ "', " +
                                                "'" + quote.getSymbol()
						+ "', " +
                                                "'" + quote.getDayOpen()
						+ "', " +
                                                "'" + quote.getDayClose()
						+ "', " +
                                                "'" + quote.getDayHigh()
						+ "', " +
                                                "'" + quote.getDayLow()
						+ "', " +
                                                "'" + quote.getDayVolume()
						+ "')");
                
                // Now insert the quote into database
                Statement statement = manager.createStatement();
                statement.executeUpdate(insertQuery);
                quotesImported++;
            }
        }
        catch (SQLException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                             e.getMessage()));
        }

        return quotesImported;
    }

    /**
     * Import quotes into the database using a single insert statement for
     * all rows. Use this function when the database supports
     * multi-row inserts.
     *
     * @param	quotes list of quotes to import
     * @return the number of quotes imported
     */
    private int importQuoteSingleStatement(List quotes) {
        int quotesImported = 0;
        StringBuffer insertString = new StringBuffer();
        boolean firstQuote = true;
        
        // Build single query to insert stocks for a whole day into
        for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
            EODQuote quote = (EODQuote)iterator.next();
            
            if(firstQuote) {
                insertString.append("INSERT INTO " + 
				    DatabaseManager.SHARE_TABLE_NAME +
                                    " VALUES (");
                firstQuote = false;
            }
            else
                insertString.append(", (");
            
            // Add new quote
            insertString.append("'" + manager.toSQLDateString(quote.getDate()) 
				+ "', " +
                                "'" + quote.getSymbol()                + "', " +
                                "'" + quote.getDayOpen()               + "', " +
                                "'" + quote.getDayClose()              + "', " +
                                "'" + quote.getDayHigh()               + "', " +
                                "'" + quote.getDayLow()                + "', " +
                                "'" + quote.getDayVolume()             + "')");
        }
        
        try {
            Statement statement = manager.createStatement();
            statement.executeUpdate(insertString.toString());
            quotesImported = quotes.size();
        }
        catch (SQLException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                             e.getMessage()));
        }

        return quotesImported;
    }

    /**
     * Returns whether the source contains any quotes for the given date.
     *
     * @param date the date
     * @return wehther the source contains the given date
     */
    public boolean containsDate(TradingDate date) {
        boolean containsDate = false;

	if(manager.getConnection()) {
	    try {
		Statement statement = manager.createStatement();
		
		// Return the first date found matching the given date.
		// If no dates are found - the date is not in the source.
		// This should take << 1s.
                String query = manager.buildDatePresentQuery(date);
		ResultSet RS = statement.executeQuery(query);

                // Find out if it has any rows
                containsDate = RS.next();

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
		DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
	    }
	}

        return containsDate;
    }

    /**
     * Return all the dates which we have quotes for. REALLY SLOW.
     *
     * @return	a list of dates
     */    
    public List getDates() {
	List dates = new ArrayList();

	if(manager.getConnection()) {

            // This might take a while
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            progress.setIndeterminate(true);
            progress.show(Locale.getString("GETTING_DATES"));
            progress.setNote(Locale.getString("GETTING_DATES"));
            
            try {
                // Get dates
                Statement statement = manager.createStatement();
                ResultSet RS = statement.executeQuery
                    ("SELECT DISTINCT(" + DatabaseManager.DATE_FIELD + 
		     ") FROM " +
                     DatabaseManager.SHARE_TABLE_NAME);
                
                while(RS.next()) {
                    dates.add(new TradingDate(RS.getDate(1)));
                    progress.increment();
                }
                
            }
            catch (SQLException e) {
		DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
            }
            
            ProgressDialogManager.closeProgressDialog(progress);
        }

	return dates;
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
       
	if(!manager.getConnection())
            return 0;

        try {
            //
            // First get number of stocks where close > open
            //

            Statement statement = manager.createStatement();

            String query =
                new String("SELECT COUNT(*) FROM " + 
			   DatabaseManager.SHARE_TABLE_NAME +
                           " WHERE " + DatabaseManager.DATE_FIELD + " = '" + 
			   manager.toSQLDateString(date) + "' AND " +
                           DatabaseManager.DAY_CLOSE_FIELD + " > " + 
			   DatabaseManager.DAY_OPEN_FIELD + " AND " +
                           "LENGTH(" + DatabaseManager.SYMBOL_FIELD + 
			   ") = 3 AND " +
                           manager.left(DatabaseManager.SYMBOL_FIELD ,1)  + 
			   " != 'X' ");

            ResultSet RS = statement.executeQuery(query);
            boolean isDatePresent = RS.next();
            int advanceDecline = 0;

            if(isDatePresent) {
                advanceDecline = RS.getInt(1);

                // Clean up after ourselves
                RS.close();
                statement.close();
            }
            else {
                // Clean up after ourselves
                RS.close();
                statement.close();

                throw MissingQuoteException.getInstance();
            }

            //
            // Now get number of stocks where close < open
            //

            statement = manager.createStatement();

            query =
                new String("SELECT COUNT(*) FROM " + 
			   DatabaseManager.SHARE_TABLE_NAME +
                           " WHERE " + DatabaseManager.DATE_FIELD + " = '" + 
			   manager.toSQLDateString(date) + "' AND " +
                           DatabaseManager.DAY_CLOSE_FIELD + " < " + 
			   DatabaseManager.DAY_OPEN_FIELD + " AND " +
                           "LENGTH(" + DatabaseManager.SYMBOL_FIELD + 
			   ") = 3 AND " +
                           manager.left(DatabaseManager.SYMBOL_FIELD, 1) + 
			   " != 'X' ");

            RS = statement.executeQuery(query);
            isDatePresent = RS.next();

            if(isDatePresent) {
                advanceDecline -= RS.getInt(1);

                // Clean up after ourselves
                RS.close();
                statement.close();
            }
            else {
                // Clean up after ourselves
                RS.close();
                statement.close();

                // Shouldn't happen!
                assert false;
            }

            return advanceDecline;
        }
        catch (SQLException e) {
	    DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
							     e.getMessage()));
            return 0;
        }
    }




    /**
     * Return the advance/decline for the given date. This returns the number
     * of all ordinary stocks that rose (day close > day open) - the number of all
     * ordinary stocks that fell.
     *
     * @param firstDate the first date in the range
     * @param lastDate  the last date in the range
     * @exception throw MissingQuoteException if none of the dates are in the source
     */
    public HashMap getAdvanceDecline(TradingDate firstDate, TradingDate lastDate)
        throws MissingQuoteException {
       
	if(!manager.getConnection())
            return null;

        try {
            //
            // First get number of stocks where close > open
            //

            Statement statement = manager.createStatement();

            String query =
                new String("SELECT COUNT(*), date FROM " + 
			   DatabaseManager.SHARE_TABLE_NAME +
                           " WHERE " + DatabaseManager.DATE_FIELD + 
			   " >= '" + manager.toSQLDateString(firstDate) + 
			   "' AND " + 
			   DatabaseManager.DATE_FIELD + " <= '" + 
			   manager.toSQLDateString(lastDate) + "' AND " + 
                           DatabaseManager.DAY_CLOSE_FIELD + " > " + 
			   DatabaseManager.DAY_OPEN_FIELD + " AND " +
                           "LENGTH(" + DatabaseManager.SYMBOL_FIELD + 
			   ") = 3 AND " +
                           manager.left(DatabaseManager.SYMBOL_FIELD ,1)  + 
			   " != 'X' GROUP BY " + DatabaseManager.DATE_FIELD + 
			   " ORDER BY " + DatabaseManager.DATE_FIELD + " ASC ");

            ResultSet RS = statement.executeQuery(query);
            boolean areDatesPresent = false;
	    HashMap advanceDeclineMap = new HashMap();
	    TradingDate firstDateFromQuery = (TradingDate)lastDate.clone();
	    
	    boolean firstRow = true;
	    while (RS.next()) {
		areDatesPresent = true;
		int advanceDecline = 0;
		TradingDate keyDate = new TradingDate(RS.getDate(2));		
                advanceDeclineMap.put(keyDate, new Integer(RS.getInt(1)));

		if (firstRow) {
		    firstRow = false;
		    firstDateFromQuery = keyDate;
		} 
	    } 
	    

	    // Clean up after ourselves
	    RS.close();
	    statement.close();

	    if (!areDatesPresent) {
                throw MissingQuoteException.getInstance();
            }

            //
            // Now get number of stocks where close < open
            //

            statement = manager.createStatement();

            query =
                new String("SELECT COUNT(*), date FROM " + 
			   DatabaseManager.SHARE_TABLE_NAME +
                           " WHERE " + DatabaseManager.DATE_FIELD + " >= '" + 
			   manager.toSQLDateString(firstDate) + "' AND " + 
			   DatabaseManager.DATE_FIELD + " <= '" + 
			   manager.toSQLDateString(lastDate) + "' AND " + 
                           DatabaseManager.DAY_CLOSE_FIELD + " < " + 
			   DatabaseManager.DAY_OPEN_FIELD + " AND " +
                           "LENGTH(" + DatabaseManager.SYMBOL_FIELD + 
			   ") = 3 AND " +
                           manager.left(DatabaseManager.SYMBOL_FIELD, 1) + 
			   " != 'X' GROUP BY " + DatabaseManager.DATE_FIELD + 
			   " ORDER BY " + DatabaseManager.DATE_FIELD + " ASC ");

            RS = statement.executeQuery(query);
            areDatesPresent = false; 
	  
	    firstRow = true;
	    while (RS.next()) {
		areDatesPresent = true;
		TradingDate keyDate = new TradingDate(RS.getDate(2));
		Integer advanceVal = (Integer)(advanceDeclineMap.get(keyDate));
		
		int advanceValue = (advanceVal != null) ? advanceVal.intValue() : 0;
		
		advanceDeclineMap.
		    put( keyDate, new Integer(advanceValue - RS.getInt(1)));		
		if (firstRow && !firstDateFromQuery.before(keyDate)) {
		    firstRow = false;
		    firstDateFromQuery = keyDate;
		}      
	    }
	    
	    //Backfill map with 0 for all dates 
	    //between firstDate and firstDateFromQuery	    
	    while (firstDate.before(firstDateFromQuery)) {
		//Decrement the date first, otherwise the first
		//data value will be zeroed.
		firstDateFromQuery = firstDateFromQuery.previous(1);
		advanceDeclineMap.put(firstDateFromQuery, new Integer(0));
	    }

	    // Clean up after ourselves
	    RS.close();
	    statement.close();
            	    
            if (!areDatesPresent) {
                // Shouldn't happen!
                assert false;
            }

            return advanceDeclineMap;
        }
        catch (SQLException e) {
	    DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
							     e.getMessage()));
            return null;
        }
    }


    
    /**
     * The database is very slow at taking an arbitrary list of symbol and date pairs
     * and finding whether they exist in the database. This is unfortuante because
     * we need this functionality so we don't try to import quotes that are already
     * in the database. If we try to import a quote that is already present, we
     * get a constraint violation error. We can't just ignore this error because
     * we can't tell errors apart and we don't want to ignore all import errors.
     * <p>
     * This function examines the list of quotes and optimises the query for returning
     * matching quotes. This basically works by seeing if all the quotes are on
     * the same date or have the same symbol.
     * <p>
     * CAUTION: This function will return all matches, but it may return some false ones too.
     * The SQL query returned will only return the symbol and date fields.
     * Don't call this function if the quote list is empty.
     *
     * @param quotes the quote list.
     * @return SQL query statement
     */
    private String buildMatchingQuoteQuery(List quotes) {
        boolean sameSymbol = true;
        boolean sameDate = true;
        Symbol symbol = null;
        TradingDate date = null;
        TradingDate startDate = null;
        TradingDate endDate = null;

        // This function should only be called if there are any quotes to match
        assert quotes.size() > 0;

        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT " + DatabaseManager.SYMBOL_FIELD + "," + 
		      DatabaseManager.DATE_FIELD + " FROM " +
                      DatabaseManager.SHARE_TABLE_NAME + " WHERE ");
        
        // Check if all the quotes have the same symbol or fall on the same date.
        for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
            EODQuote quote = (EODQuote)iterator.next();

            if(symbol == null || date == null) {
                symbol = quote.getSymbol();
                startDate = endDate = date = quote.getDate();
            }
            else {
                if(!symbol.equals(quote.getSymbol()))
                    sameSymbol = false;
                if(!date.equals(quote.getDate()))
                    sameDate = false;

                // Keep a track of the date range in case we do a symbol query, as if
                // they are importing a single symbol, we don't want to pull in every date
                // to check!
                if(quote.getDate().before(startDate))
                    startDate = quote.getDate();
                if(quote.getDate().after(endDate))
                    endDate = quote.getDate();
            }
        }

        // 1. All quotes have the same symbol.
        if(sameSymbol)
            buffer.append(DatabaseManager.SYMBOL_FIELD + " = '" + 
			  symbol.toString() + "' AND " +
                          DatabaseManager.DATE_FIELD + " >= '" + 
			  manager.toSQLDateString(startDate) + "' AND " +
                          DatabaseManager.DATE_FIELD + " <= '" + 
			  manager.toSQLDateString(endDate) + "' ");

        // 2. All quotes are on the same date.
        else if(sameDate)
            buffer.append(DatabaseManager.DATE_FIELD + " = '" + 
			  manager.toSQLDateString(date) + "'");

        // 3. The quotes contain a mixture of symbols and dates. Bite the bullet
        // and do a slow SQL query which checks each one individually.
        else {
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                EODQuote quote = (EODQuote)iterator.next();
                buffer.append("(" + DatabaseManager.SYMBOL_FIELD + " = '" + 
			      quote.getSymbol() + "' AND " +
                              DatabaseManager.DATE_FIELD + " = '" + 
			      manager.toSQLDateString(quote.getDate()) + "')");
		
                if(iterator.hasNext())
                    buffer.append(" OR ");
            }
        }

        return buffer.toString();
    }

    /**
     * Return a list of all the quotes in the database that match the input list.
     * This function is used during import to find out which quotes are already
     * in the database.
     * <p>
     * CAUTION: This function will return all matches, but it may return some false ones too.
     * The SQL query returned will only return the symbol and date fields.
     *
     * @param quotes quotes to query
     * @return matching quotes
     */
    private List findMatchingQuotes(List quotes) {
        List matchingQuotes = new ArrayList();
        
	if(manager.getConnection() && quotes.size() > 0) {
            // Since this is part of import, don't bother with progress dialog
            try {
                // Construct query from list
                Statement statement = manager.createStatement();
                String query = buildMatchingQuoteQuery(quotes);
                ResultSet RS = statement.executeQuery(query);

                // Retrieve matching quotes
                while(RS.next()) {
                    try {
                        matchingQuotes.add(new EODQuote(Symbol.find(RS.getString(DatabaseManager.SYMBOL_FIELD)),
                                                        new TradingDate(RS.getDate(DatabaseManager.DATE_FIELD)),
                                                        0, 0.0, 0.0, 0.0, 0.0));
                    }
                    catch(SymbolFormatException e) {
                        // This can't happen because we are only matching already known
                        // valid symbols.
                        assert false;
                    }
                }

                // Clean up after ourselves
                RS.close();
                statement.close();
            }
            catch(SQLException e2) {
		DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e2.getMessage()));
            }
        }

        return matchingQuotes;
    }

    /**
     * This function shows an error message if there are no quotes in the
     * database. We generally only care about this when trying to get the
     * the current date or the lowest or highest. This method will also
     * interrupt the current thread. This way calling code only needs to
     * check for cancellation, rather than each individual fault.
     */
    private void showEmptyDatabaseError() {
        DesktopManager.showErrorMessage(Locale.getString("NO_QUOTES_FOUND"));
    }





    /**
     * Import currency exchange rates into the database.
     *
     * @param exchangeRates a list of exchange rates to import.
     */
    public void importExchangeRates(List exchangeRates) {
        if (exchangeRates.size() > 0 && manager.getConnection()) {
            // Iterate through the exchange rates and import them one-by-one.
            Iterator iterator = exchangeRates.iterator();

            try {
                while(iterator.hasNext()) {
                    ExchangeRate exchangeRate = (ExchangeRate)iterator.next();
                    String sourceCurrencyCode = exchangeRate.getSourceCurrency().getCurrencyCode();
                    String destinationCurrencyCode =
                        exchangeRate.getDestinationCurrency().getCurrencyCode();

                    String insertQuery =
                        new String("INSERT INTO " + 
				   DatabaseManager.EXCHANGE_TABLE_NAME + 
				   " VALUES (" +
                                   "'" + manager.
				   toSQLDateString(exchangeRate.getDate()) 
				   + "', " +
                                   "'" + sourceCurrencyCode
				   + "', " +
                                   "'" + destinationCurrencyCode
				   + "', " +
                                   "'" + exchangeRate.getRate()
				   + "')");

                    // Now insert the exchange rate into the dataqbase
                    Statement statement = manager.createStatement();
                    statement.executeUpdate(insertQuery);
                }
            }
            catch (SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                                 e.getMessage()));
            }
        }
    }

    public List getExchangeRates(Currency sourceCurrency, Currency destinationCurrency) {
        List list = new ArrayList();
        
	if(!manager.getConnection())
            return list;

        try {
            Statement statement = manager.createStatement();
            String query = new String("SELECT * FROM " + 
				      DatabaseManager.EXCHANGE_TABLE_NAME + 
				      " WHERE " +
                                      DatabaseManager.SOURCE_CURRENCY_FIELD + " = '" +
                                      sourceCurrency.getCurrencyCode() +
                                      "' AND " +
                                      DatabaseManager.DESTINATION_CURRENCY_FIELD + " ='" +
                                      destinationCurrency.getCurrencyCode() + "'");

            ResultSet RS = statement.executeQuery(query);

            while (RS.next())
                list.add(new ExchangeRate(new TradingDate(RS.getDate(DatabaseManager.DATE_COLUMN)),
                                          sourceCurrency,
                                          destinationCurrency,
                                          RS.getDouble(EXCHANGE_RATE_COLUMN)));
        }
        catch(SQLException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                             e.getMessage()));
        }

        return list;
    }



    public void shutdown() {
	manager.shutdown();
    }
}


 