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

import java.lang.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

import org.mov.util.*;
import org.mov.ui.DesktopManager;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;

/**
 * Provides functionality to obtain stock quotes from a database. This class
 * implements the QuoteSource interface to allow users to obtain stock
 * quotes in the fastest possible manner.
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
public class DatabaseQuoteSource implements QuoteSource
{
    private Connection connection;

    // Buffer first and last trading date in database
    private TradingDate firstDate;
    private TradingDate lastDate;

    // When we are importing, first check to make sure the database is OK
    private boolean readyForImport = false;

    // MySQL driver info
    private final static String DRIVER_NAME       = "mysql";
    private final static String MM_MYSQL_DRIVER   = "org.gjt.mm.mysql.Driver";
    private final static String MYSQL_DRIVER      = "com.mysql.jdbc.Driver";

    // Shares table
    private final static String SHARE_TABLE_NAME  = "shares";
    private final static String SYMBOL_FIELD      = "symbol";
    private final static String DATE_FIELD        = "date";
    private final static String DAY_OPEN_FIELD    = "open";
    private final static String DAY_CLOSE_FIELD   = "close";
    private final static String DAY_HIGH_FIELD    = "high";
    private final static String DAY_LOW_FIELD     = "low";
    private final static String DAY_VOLUME_FIELD  = "volume";

    // Shares indices
    private final static String DATE_INDEX_NAME   = "date_index";
    private final static String SYMBOL_INDEX_NAME = "symbol_index";

    // Info table
    private final static String LOOKUP_TABLE_NAME = "lookup";
    private final static String NAME_FIELD        = "name";

    // Connection details
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    /**
     * Creates a new quote source using the database information specified
     * in the user preferences.
     *
     * @param	host	the host location of the database
     * @param	port	the port of the database
     * @param	database	the name of the database
     * @param	username	the user login
     * @param	password	the password for the login
     */
    public DatabaseQuoteSource(String host, String port, String database,
			       String username, String password) {

        connection = null;
        firstDate = null;
	lastDate = null;

        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    // Get the driver and connect to the database. Return FALSE if failed.
    private boolean checkConnection() {

        // Are we already connected?
        if(connection != null)
            return true;

	// Get driver
	try {
            // Try MM MySql driver first as if anything it seems a little
            // faster and it's the one I used first
	    Class.forName(MM_MYSQL_DRIVER).newInstance();
            return connect();
	}
	catch (Exception e) {
            // If the MM mysql driver doesn't work, we can try the
            // official driver from MySql
            try {
                Class.forName(MYSQL_DRIVER).newInstance();
                return connect();
            }
            catch(Exception e2) {
                // Neither worked!
                DesktopManager.showErrorMessage("Unable to load MySQL driver.");
                return false;
            }
	}
    }

    // Connect to the database
    private boolean connect() {
	try {
	    connection =
		DriverManager.
		getConnection("jdbc:" + DRIVER_NAME +"://"+ host +
			      ":" + port +
			      "/"+ database +
			      "?user=" + username +
			      "&password=" + password);
            return true;
	}
	catch (SQLException e) {
	    DesktopManager.showErrorMessage("Error connecting to database:\n" +
                                            e.getMessage());
            return false;
	}
    }

    /**
     * Returns the company name associated with the given symbol.
     *
     * @param	symbol	the stock symbol.
     * @return	the company name.
     */
    public String getSymbolName(Symbol symbol) {

	String name = null;

	if(checkConnection()) {        
	    try {
		Statement statement = connection.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT " + NAME_FIELD + " FROM " + LOOKUP_TABLE_NAME +
		     " WHERE " + SYMBOL_FIELD + " = '"
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

	if(checkConnection()) {
	    try {
		Statement statement = connection.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT " + SYMBOL_FIELD + 
		     " FROM " + LOOKUP_TABLE_NAME + " WHERE LOCATE(" +
		     "UPPER('" + partialCompanyName + "'), " +
		     NAME_FIELD + ") != 0");

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the name
                try {
                    symbol = new Symbol(RS.getString(1));
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

	if(checkConnection()) {
	    try {
		Statement statement = connection.createStatement();
		
		// Return the first date found matching the given symbol.
		// If no dates are found - the symbol is unknown to us.
		// This should take << 1s
                String query =
                    new String("SELECT " + DATE_FIELD + " FROM " +
                               SHARE_TABLE_NAME + " WHERE " + SYMBOL_FIELD + " = '"
                               + symbol + "' " +
                               "LIMIT 1");
		ResultSet RS = statement.executeQuery(query);

                // Find out if it has any rows
                RS.last();
                symbolExists = RS.getRow() > 0;

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                e.getMessage());
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

	if(checkConnection()) {
	    try {
		Statement statement = connection.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT MIN(" + DATE_FIELD + ") FROM " +
		     SHARE_TABLE_NAME);

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the date
		date = RS.getDate(1);

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                e.getMessage());
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
     * Return the last date in the database that has any quotes.
     *
     * @return	newest date with quotes
     */
    public TradingDate getLastDate() {

	// Do we have it buffered?
	if(lastDate != null)
	    return lastDate;

	java.util.Date date = null;

	if(checkConnection()) {
	    try {
		Statement statement = connection.createStatement();
		
		ResultSet RS = statement.executeQuery
		    ("SELECT MAX(" + DATE_FIELD + ") FROM " +
		     SHARE_TABLE_NAME);

		// Import SQL data into vector
		RS.next();

		// Get only entry which is the date
		date = RS.getDate(1);

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                e.getMessage());
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
        // HACK. It needs to keep a table which maintains a flag
        // for whether a symbol is an index or not.
	assert symbol != null;

	if(symbol.length() == 3 && symbol.charAt(0) == 'X')
	    return true;
	else
	    return false;
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
        
	String queryString = buildSQLString(quoteRange);
        boolean success;

	// This query might take a while...
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
        progress.setNote("Loading Quotes...");
        progress.setIndeterminate(true);
	success = executeSQLString(progress, queryString);
        ProgressDialogManager.closeProgressDialog(progress);

        return success;
    }

    // Takes a string containing an SQL statement and then executes it. Returns
    // a vector of quotes.
    private boolean executeSQLString(ProgressDialog progress, String SQLString) {

	if(checkConnection()) {
	    try {
		Statement statement = connection.createStatement();	
                Thread monitor = cancelOnInterrupt(statement);
                Thread thread = Thread.currentThread();
		ResultSet RS = statement.executeQuery(SQLString);

		// All this to find out how many rows in the result set
		RS.last();
		progress.setMaximum(RS.getRow());
		progress.setProgress(0);
		progress.setIndeterminate(false);
		RS.beforeFirst();

                // Monitor thread is no longer needed
                monitor.interrupt();

                if(!thread.isInterrupted()) {
                    QuoteCache quoteCache = QuoteCache.getInstance();

                    while (RS.next()) {
                        quoteCache.load(SymbolRegistry.find(RS.getString(SYMBOL_FIELD)),
                                        new TradingDate(RS.getDate(DATE_FIELD)),
                                        RS.getInt(DAY_VOLUME_FIELD),
                                        RS.getFloat(DAY_LOW_FIELD),
                                        RS.getFloat(DAY_HIGH_FIELD),
                                        RS.getFloat(DAY_OPEN_FIELD),
                                        RS.getFloat(DAY_CLOSE_FIELD));
                        
                        // Update the progress bar per row
                        progress.increment();
                    }
                }                    

		// Clean up after ourselves
		RS.close();
		statement.close();
                return !thread.isInterrupted();
	    }
	    catch(SQLException e) {
                DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                e.getMessage());
	    }
            catch(SymbolFormatException e2) {
                DesktopManager.showErrorMessage("Database contains badly formatted quote symbol." +
                                                e2.getReason());
            }
	}

        return false;
    }

    // This function creates a new thread that monitors the current thread
    // for the interrupt call. If the current thread is interrupted it
    // will cancel the given SQL statement. If cancelOnInterrupt() is called,
    // once the SQL statement has finisehd, you should make sure the
    // thread is terminated by calling "interrupt" on the returned thread.
    private Thread cancelOnInterrupt(final Statement statement) {
        final Thread sqlThread = Thread.currentThread();

        Thread thread = new Thread(new Runnable() {
                public void run() {
                    Thread currentThread = Thread.currentThread();

                    while(true) {

                        try {
                            currentThread.sleep(1000); // 1s
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
    private String buildSQLString(QuoteRange quoteRange) {

	//
	// 1. Create select line
	//

	String queryString = "SELECT * FROM " + SHARE_TABLE_NAME + " WHERE ";

	//
	// 2. Filter select by symbols we are looking for
	//

	String filterString = new String("");

	if(quoteRange.getType() == QuoteRange.GIVEN_SYMBOLS) {
	    List symbols = quoteRange.getAllSymbols();

	    if(symbols.size() == 1) {
		Symbol symbol = (Symbol)symbols.get(0);

		filterString =
		    filterString.concat(SYMBOL_FIELD + " = '" + symbol + "' ");
	    }
	    else {
		assert symbols.size() > 1;

		filterString = filterString.concat(SYMBOL_FIELD + " IN (");
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
	else if(quoteRange.getType() == QuoteRange.ALL_SYMBOLS) {
	    // nothing to do
	}
	else if(quoteRange.getType() == QuoteRange.ALL_ORDINARIES) {
	    filterString = filterString.concat("LENGTH(" + SYMBOL_FIELD + ") = 3 AND " +
					     "LEFT(" + SYMBOL_FIELD + ",1) != 'X' ");
	}
	else {
	    assert quoteRange.getType() == QuoteRange.MARKET_INDICES;

	    filterString = filterString.concat("LENGTH(" + SYMBOL_FIELD + ") = 3 AND " +
					     "LEFT(" + SYMBOL_FIELD + ", 1) = 'X' ");
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

	    filterString =
		filterString.concat(DATE_FIELD + " = '" + quoteRange.getFirstDate() + "' ");
	}

	// Otherwise check within a range of dates
	else {
	    if(filterString.length() > 0)
		filterString = filterString.concat("AND ");

	    filterString =
		filterString.concat(DATE_FIELD + " >= '" + quoteRange.getFirstDate() + "' AND " +
				    DATE_FIELD + " <= '" + quoteRange.getLastDate() + "' ");
	}

	return queryString.concat(filterString);
    }

    // Creates database tables
    private boolean createTable(String databaseName) {

	boolean success = false;

	try {
	    // 1. Create the shares table
	    Statement statement = connection.createStatement();
	    ResultSet RS = statement.executeQuery
		("CREATE TABLE " + SHARE_TABLE_NAME + " (" +
		 DATE_FIELD +		" DATE NOT NULL, " +
		 SYMBOL_FIELD +		" CHAR(6) NOT NULL, " +
		 DAY_OPEN_FIELD +	" FLOAT DEFAULT 0.0, " +
		 DAY_CLOSE_FIELD +	" FLOAT DEFAULT 0.0, " +
		 DAY_HIGH_FIELD +	" FLOAT DEFAULT 0.0, " +
		 DAY_LOW_FIELD +	" FLOAT DEFAULT 0.0, " +
		 DAY_VOLUME_FIELD +	" INT DEFAULT 0, " +
		 "PRIMARY KEY(" + DATE_FIELD + ", " + SYMBOL_FIELD + "))");

	    // 2. Create a couple of indices to speed things up
	    RS = statement.executeQuery
		("CREATE INDEX " + DATE_INDEX_NAME + " ON " +
		 SHARE_TABLE_NAME + " (" + DATE_FIELD + ")");
	    RS = statement.executeQuery
		("CREATE INDEX " + SYMBOL_INDEX_NAME + " ON " +
		 SHARE_TABLE_NAME + " (" + SYMBOL_FIELD + ")");

	    // 3. Create the lookup table
	    RS = statement.executeQuery
		("CREATE TABLE " + LOOKUP_TABLE_NAME + " (" +
		 SYMBOL_FIELD +		" CHAR(6) NOT NULL, " +
		 NAME_FIELD +		" VARCHAR(100), " +
		 "PRIMARY KEY(" + SYMBOL_FIELD + "))");

	    success = true;
	}
	catch (SQLException e) {
            DesktopManager.showErrorMessage("Error talking to database:\n" +
                                            e.getMessage());
	}

	return success;	
    }

    // Make sure database and tables exist before doing import, if
    // the database or tables do not exist then create them
    private boolean prepareForImport(String databaseName) {

	boolean success = true;

	try {
	    DatabaseMetaData meta = connection.getMetaData();

	    // 1. Check database exists
	    {
		ResultSet RS = meta.getCatalogs();
		String traverseDatabaseName;
		boolean foundDatabase = false;
		
		while(RS.next()) {
		    traverseDatabaseName = RS.getString(1);
		
		    if(traverseDatabaseName.equals(databaseName)) {
			foundDatabase = true;
			break;
		    }
		}
		
		if(!foundDatabase) {
		    DesktopManager.showErrorMessage("Can't find " +
						    databaseName +
						    " database.");
		    return false;
		}
	    }

	    // 2. Check table exists - if not create it
	    {
		ResultSet RS =
		    meta.getTables(databaseName, null, "%", null);
		String traverseTables;
		boolean foundTable = false;

		while(RS.next()) {
		    traverseTables = RS.getString(3);
			
		    if(traverseTables.equals(SHARE_TABLE_NAME)) {
			foundTable = true;
                        break;
                    }
		}
		
		// No table? Well have to go create it
		if(!foundTable)
		    success = createTable(databaseName);
	    }

	}
	catch (SQLException e) {
            DesktopManager.showErrorMessage("Error talking to database:\n" +
                                            e.getMessage());
	    return false;
	}

	// If we got here its all ready for importing
	return success;
    }

    /**
     * Import quotes into the database.
     *
     * @param	databaseName	the name of the database
     * @param	quoteBundle	bundle of quotes to import
     * @param	date		the date for the day quotes
     */
    public void importQuotes(String databaseName, QuoteBundle quoteBundle,
			     TradingDate date) {

	if(!checkConnection()) 
            return;

	if(!readyForImport)
	    readyForImport = prepareForImport(databaseName);

	// Dont import a date thats already there
	if(!containsDate(date) && readyForImport) {
	    StringBuffer insertString = new StringBuffer();
	    boolean firstQuote = true;
	    String dateString = date.toString();

	    // Build single query to insert stocks for a whole day into
	    // the table
            Iterator iterator = quoteBundle.iterator();

            if(iterator.hasNext()) {
                while(iterator.hasNext()) {
                    Quote quote = (Quote)iterator.next();

                    if(firstQuote) {
                        insertString.append("INSERT INTO " + SHARE_TABLE_NAME +
                                            " VALUES (");
                        firstQuote = false;
                    }
                    else
                        insertString.append(", (");

                    // Add new quote
                    insertString.append("'" + dateString          + "', " +
                                        "'" + quote.getSymbol()   + "', " +
                                        "'" + quote.getDayOpen()  + "', " +
                                        "'" + quote.getDayClose() + "', " +
                                        "'" + quote.getDayHigh()  + "', " +
                                        "'" + quote.getDayLow()   + "', " +
                                        "'" + quote.getDayVolume()   + "')");
                }

                // Now insert day quote into database
                try {
                    Statement statement = connection.createStatement();
                    ResultSet RS = statement.executeQuery(insertString.toString());
                }
                catch (SQLException e) {
                    DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                    e.getMessage());
                }
            }
	}
    }

    /**
     * Returns whether the source contains any quotes for the given date.
     *
     * @param date the date
     * @return wehther the source contains the given date
     */
    public boolean containsDate(TradingDate date) {
        boolean containsDate = false;

	if(checkConnection()) {
	    try {
		Statement statement = connection.createStatement();
		
		// Return the first date found matching the given date.
		// If no dates are found - the date is not in the source.
		// This should take << 1s.
                String query =
                    new String("SELECT " + DATE_FIELD + " FROM " +
                               SHARE_TABLE_NAME + " WHERE " + DATE_FIELD + " = '"
                               + date + "' " + "LIMIT 1");
		ResultSet RS = statement.executeQuery(query);

                // Find out if it has any rows
                RS.last();
                containsDate = RS.getRow() > 0;

		// Clean up after ourselves
		RS.close();
		statement.close();
	    }
	    catch (SQLException e) {
                DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                e.getMessage());
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

	if(checkConnection()) {

            // This might take a while
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            progress.setIndeterminate(true);
            progress.show("Getting dates...");
            progress.setNote("Getting dates...");
            
            try {
                // 1. Create the table
                Statement statement = connection.createStatement();
                ResultSet RS = statement.executeQuery
                    ("SELECT DISTINCT(" + DATE_FIELD + ") FROM " +
                     SHARE_TABLE_NAME);
                
                while(RS.next()) {
                    dates.add(new TradingDate(RS.getDate(1)));
                    progress.increment();
                }
                
            }
            catch (SQLException e) {
                DesktopManager.showErrorMessage("Error talking to database:\n" +
                                                e.getMessage());
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
       
	if(!checkConnection())
            return 0;

        try {
            //
            // First get number of stocks where close > open
            //

            Statement statement = connection.createStatement();

            String query =
                new String("SELECT COUNT(*) FROM " + SHARE_TABLE_NAME +
                           " WHERE " + DATE_FIELD + " = '" + date + "' AND " +
                           DAY_CLOSE_FIELD + " > " + DAY_OPEN_FIELD + " AND " +
                           "LENGTH(" + SYMBOL_FIELD + ") = 3 AND " +
                           "LEFT(" + SYMBOL_FIELD + ",1) != 'X' ");

            ResultSet RS = statement.executeQuery(query);

            // Find out if it has any rows
            RS.last();
            int advanceDecline = 0;

            if(RS.getRow() > 0) {
                advanceDecline = RS.getInt(1);

                // Clean up after ourselves
                RS.close();
                statement.close();
            }
            else {
                // Clean up after ourselves
                RS.close();
                statement.close();

                throw new MissingQuoteException();
            }

            //
            // Now get number of stocks where close < open
            //

            statement = connection.createStatement();

            query =
                new String("SELECT COUNT(*) FROM " + SHARE_TABLE_NAME +
                           " WHERE " + DATE_FIELD + " = '" + date + "' AND " +
                           DAY_CLOSE_FIELD + " < " + DAY_OPEN_FIELD + " AND " +
                           "LENGTH(" + SYMBOL_FIELD + ") = 3 AND " +
                           "LEFT(" + SYMBOL_FIELD + ",1) != 'X' ");

            RS = statement.executeQuery(query);

            // Find out if it has any rows
            RS.last();

            if(RS.getRow() > 0) {
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
            DesktopManager.showErrorMessage("Error talking to database:\n" +
                                            e.getMessage());
            return 0;
        }
    }

    // This function shows an error message if there are no quotes in the
    // database. We generally only care about this when trying to get the
    // the current date or the lowest or highest. This method will also
    // interrupt the current thread. This way calling code only needs to
    // check for cancellation, rather than each individual fault.
    private void showEmptyDatabaseError() {
        DesktopManager.showErrorMessage("Venice couldn't find any quotes.\n" +
                                        "You can import quotes using the import\n" +
                                        "quote tool under the File menu.");
    }
}


