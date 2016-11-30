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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.io.InputStream;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.HashMap;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;


import nz.org.venice.ui.DesktopManager;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.Converter;



/**
 * Provides functionality to manage database connections and ensures the 
 * relevant tables exist. Classes manage their own queries separately.
 * 
 * @author Mark Hummel
 * @see DatabaseQuoteSource
 * @see nz.org.venice.alert.DatabaseAlertReader
 * @see nz.org.venice.alert.DatabaseAlertWriter
 */
public class DatabaseManager 
{
    private Connection connection = null;
    private boolean checkedTables = false;

    // Database Software

    /** MySQL Database. */
    public final static int MYSQL       = 0;

    /** PostgreSQL Database. */
    public final static int POSTGRESQL  = 1;

    /** Hypersonic SQL Database. */
    public final static int HSQLDB      = 2;

    /** Any generic SQL Database. */
    public final static int OTHER       = 3;

    // Mode

    /** Internal database. */
    public final static int INTERNAL = 0;

    /** External database. */
    public final static int EXTERNAL = 1;

    // MySQL driver info
    public final static String MYSQL_SOFTWARE = "mysql";

    // PostgreSQL driver info
    public final static String POSTGRESQL_SOFTWARE = "postgresql";

    // Hypersonic SQL driver info
    public final static String HSQLDB_SOFTWARE    = "hsql";

    // Shares table
    public final static String SHARE_TABLE_NAME  = "shares";

    // Column names for Share Table
    public final static String DATE_FIELD        = "date";
    public final static String SYMBOL_FIELD      = "symbol";
    public final static String DAY_OPEN_FIELD    = "open";
    public final static String DAY_CLOSE_FIELD   = "close";
    public final static String DAY_HIGH_FIELD    = "high";
    public final static String DAY_LOW_FIELD     = "low";
    public final static String DAY_VOLUME_FIELD  = "volume";

    // Column numbers for Share Table
    public final static int DATE_COLUMN       = 1;
    public final static int SYMBOL_COLUMN     = 2;
    public final static int DAY_OPEN_COLUMN   = 3;
    public final static int DAY_CLOSE_COLUMN  = 4;
    public final static int DAY_HIGH_COLUMN   = 5;
    public final static int DAY_LOW_COLUMN    = 6;
    public final static int DAY_VOLUME_COLUMN = 7;

    // Shares indices
    private final static String DATE_INDEX_NAME   = "date_index";
    private final static String SYMBOL_INDEX_NAME = "symbol_index";

    // Info table
    public final static String LOOKUP_TABLE_NAME = "lookup";
    public final static String NAME_FIELD        = "name";

    // Exchange rate table
    public final static String EXCHANGE_TABLE_NAME = "exchange";

    // Column names for Exchange Table
    // DATE_FIELD
    public final static String SOURCE_CURRENCY_FIELD      = "source_currency";
    public final static String DESTINATION_CURRENCY_FIELD = "destination_currency";
    public final static String EXCHANGE_RATE_FIELD        = "exchange_rate";

    // Alert Tables
    public final static String ALERT_TABLE_NAME = "venice_alerts";
    public final static String OHLCV_ALERT_TABLE_NAME = "alert_OHLCV_targets";
    public final static String GONDOLA_ALERT_TABLE_NAME = "alert_Gondola_targets";
    public final static String START_DATE_ALERT_TABLE_NAME = "alert_start_dates";
    public final static String END_DATE_ALERT_TABLE_NAME = "alert_end_dates";

    //Column numbers for get all Alerts query
    public final static int ALERT_UUID_COLUMN         = 1;
    public final static int ALERT_HOST_COLUMN         = 2;
    public final static int ALERT_USER_COLUMN         = 3;
    public final static int ALERT_SYMBOL_COLUMN       = 4;
    public final static int ALERT_START_DATE_COLUMN   = 5;
    public final static int ALERT_END_DATE_COLUMN     = 6;
    public final static int ALERT_TARGET_COLUMN       = 7;
    public final static int ALERT_BOUND_TYPE_COLUMN   = 8;
    public final static int ALERT_TARGET_TYPE_COLUMN  = 9;
    public final static int ALERT_ENABLED_COLUMN      = 10;
    public final static int ALERT_DATESET_COLUMN      = 11;
    

    
    // column names for Share Table
    public final static String ALERT_HOST_FIELD        = "host";
    public final static String ALERT_USER_FIELD        = "username";    
    public final static String ALERT_SYMBOL_FIELD      = "symbol";
    public final static String ALERT_START_DATE_FIELD  = "start_date";
    public final static String ALERT_END_DATE_FIELD    = "end_date";
    public final static String ALERT_TARGET_FIELD      = "target";
    public final static String ALERT_BOUND_TYPE_FIELD  = "bound_type";
    public final static String ALERT_TARGET_TYPE_FIELD = "target_type";
    public final static String ALERT_ENABLED_FIELD     = "enabled";

    //Maximum size of Gondola expression in alert
    //On default mysql, max key len = 1000 bytes
    //After symbol, dates, and types we have 960 bytes left.
    //Unix username max is 255, IIRC.
    
    public final static int ALERT_MAX_TARGET_EXP_LEN  = 450;
    public final static int ALERT_MAX_HOST_LEN        = 255;
    public final static int ALERT_MAX_USER_LEN       = 255;

    // Database details
    private int mode;
    
    private String software;
    private String driver;
    
    // Fields for external mode

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    // Fields for internal mode
    private String fileName;

    // Fields for samples mode
    private EODQuoteFilter filter;
    private List fileURLs;

    //HashMap containing queries read from sql library
    private HashMap transactionMap;

    /**
     * Creates a new database connection.
     *
     * @param   software  the database software
     * @param   driver    the class name for the driver to connect to the database
     * @param	host	  the host location of the database
     * @param	port	  the port of the database
     * @param	database  the name of the database
     * @param	username  the user login
     * @param	password  the password for the login
     */
    public DatabaseManager(String software, String driver, String host, String port, 
			       String database, String username, String password) {

        this.mode = EXTERNAL;
        this.software = software;
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

	readQueriesFromLibrary();
    }

    /**
     * Create a new quote source to connect to an internal HSQL 
     * database stored in the given file.
     *
     * @param fileName name of database file
     */
    public DatabaseManager(String fileName) {
        mode = INTERNAL;
        software = HSQLDB_SOFTWARE;
        this.driver = "org.hsqldb.jdbcDriver";
        this.fileName = fileName;

	readQueriesFromLibrary();
    }

    /**
     * Return True if a connection to the database was established
     *        and the database exists with the right tables. 
     */

    // Get the driver and connect to the database. Return FALSE if failed.

    public boolean getConnection() {
        boolean success = true;
       
	success = connect();

        // If we are connected, check the tables exist, if not, create them.
        if(connection != null && !checkedTables) {
	    success = checkedTables = checkDatabase() && createTables();
	}
        return success;
    }

    public String getHost() {
	return host;
    }

    public String getUserName() {
	return username;
    }

    // Connect to the database
    private boolean connect() {
        try {
            // Resolve the classname
            Class.forName(driver);
            
            // We can operate the HSQLDB mode in one of three different wayys.
            // Construct connection string depending on mode
            String connectionURL = null;
	    
            // Set up the conection
            if (mode == INTERNAL && software.equals(HSQLDB_SOFTWARE)) 
                connectionURL = new String("jdbc:hsqldb:file:/" + fileName);
            else {
                connectionURL = new String("jdbc:" + software +"://"+ host +
                                           ":" + port +
                                           "/"+ database);
                if (username != null)
                    connectionURL += new String("?user=" + username +
                                                "&password=" + password);
            }

            connection = DriverManager.getConnection(connectionURL);
	    
        } 
        catch (ClassNotFoundException e) {	    
            // Couldn't find the driver!
            DesktopManager.showErrorMessage(Locale.getString("UNABLE_TO_LOAD_DATABASE_DRIVER", 
                                                             driver, software));
            return false;
        }
        catch (SQLException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_CONNECTING_TO_DATABASE",
                                   
							     e.getMessage()));
	    
	    DatabaseAccessManager.getInstance().reset();

            return false;
        }

	
        return true;
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
	
        String queryString = "SELECT * FROM " + SHARE_TABLE_NAME + " WHERE ";
	
        //
        // 2. Filter select by symbols we are looking for
        //
	
        String filterString = new String("");
	
        if(quoteRange.getType() == EODQuoteRange.GIVEN_SYMBOLS) {
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
        else if(quoteRange.getType() == EODQuoteRange.ALL_SYMBOLS) {
            // nothing to do
        }
        else if(quoteRange.getType() == EODQuoteRange.ALL_ORDINARIES) {
            filterString = filterString.concat("LENGTH(" + SYMBOL_FIELD + ") = 3 AND " +
                                               left(SYMBOL_FIELD, 1) + " != 'X' ");
        }
        else {
            assert quoteRange.getType() == EODQuoteRange.MARKET_INDICES;
            
            filterString = filterString.concat("LENGTH(" + SYMBOL_FIELD + ") = 3 AND " +
                                               left(SYMBOL_FIELD, 1) + " = 'X' ");
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
            
            filterString = filterString.concat(DATE_FIELD + " = '" +
                                               toSQLDateString(quoteRange.getFirstDate()) + "' ");
        }
	
        // Otherwise check within a range of dates
        else {
            if(filterString.length() > 0)
                filterString = filterString.concat("AND ");
            
            filterString = filterString.concat(DATE_FIELD + " >= '" +
                                               toSQLDateString(quoteRange.getFirstDate()) +
                                               "' AND " +
                                               DATE_FIELD + " <= '" +
                                               toSQLDateString(quoteRange.getLastDate()) +
                                               "' ");
        }
	
        return queryString.concat(filterString);
    }
    
    /**
     * Create the share table.
     *
     * @return <code>true</code> iff this function was successful.
     */
    private boolean createShareTable() {
        boolean success = false;
	
        try {
            // Create the shares table.
	    //Changed symbol to VARCHAR because of issues with 
	    //space handling in PostgreSQL. 
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE " + getTableType() + " TABLE " + SHARE_TABLE_NAME + " (" +
                                    DATE_FIELD +	" DATE NOT NULL, " +
                                    SYMBOL_FIELD +	" VARCHAR(" + Symbol.MAXIMUM_SYMBOL_LENGTH + 
                                    ") NOT NULL, " +
                                    DAY_OPEN_FIELD +	" FLOAT DEFAULT 0.0, " +
                                    DAY_CLOSE_FIELD +	" FLOAT DEFAULT 0.0, " +
                                    DAY_HIGH_FIELD +	" FLOAT DEFAULT 0.0, " +
                                    DAY_LOW_FIELD +	" FLOAT DEFAULT 0.0, " +
                                    DAY_VOLUME_FIELD +	" BIGINT DEFAULT 0, "  +
                                    "PRIMARY KEY(" + DATE_FIELD + ", " + SYMBOL_FIELD + "))");
            
            // CreatsgeTye a couple of indices to speed things up.
            statement.executeUpdate("CREATE INDEX " + DATE_INDEX_NAME + " ON " +
                                    SHARE_TABLE_NAME + " (" + DATE_FIELD + ")");
            statement.executeUpdate("CREATE INDEX " + SYMBOL_INDEX_NAME + " ON " +
                                    SHARE_TABLE_NAME + " (" + SYMBOL_FIELD + ")");
            
            // Create the lookup table.
            //statement.executeUpdate("CREATE " + getTableType() + " TABLE " + LOOKUP_TABLE_NAME + " (" +
            //                        SYMBOL_FIELD +	" VARCHAR(" + Symbol.MAXIMUM_SYMBOL_LENGTH + 
            //                         ") NOT NULL, " +
            //                        NAME_FIELD +	" VARCHAR(100), " +
            //                        "PRIMARY KEY(" + SYMBOL_FIELD + "))");
            success = true;
        }
        catch (SQLException e) {
            // Since hypersonic won't let us check if the table is already created,
            // we need to ignore the inevitable error about the table already being present.
            if(software != HSQLDB_SOFTWARE)
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                                 e.getMessage()));
            else
                success = true;
        }
	
        return success;	
    }
    
    /**
     * Create the exchange table.
     *
     * @return <code>true</code> iff this function was successful.
     */
    private boolean createExchangeTable() {
        boolean success = false;
	
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE " + getTableType() + " TABLE " +
                                    EXCHANGE_TABLE_NAME         + " (" +
                                    DATE_FIELD                  + " DATE NOT NULL, " +

                                    // ISO 4217 currency code is 3 characters.
                                    SOURCE_CURRENCY_FIELD       + " CHAR(3) NOT NULL, " +
                                    DESTINATION_CURRENCY_FIELD  + " CHAR(3) NOT NULL, " +
                                    EXCHANGE_RATE_FIELD         + " FLOAT DEFAULT 1.0, " +
                                    "PRIMARY KEY(" + DATE_FIELD + ", " +
                                    SOURCE_CURRENCY_FIELD + ", " +
                                    DESTINATION_CURRENCY_FIELD + "))");
            success = true;
        }
        catch (SQLException e) {
            // Since hypersonic won't let us check if the table is already created,
            // we need to ignore the inevitable error about the table already being present.
            if(software != HSQLDB_SOFTWARE)
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                                 e.getMessage()));
            else
                success = true;
            
        }
	
        return success;	
    }

    private boolean checkDatabase() {
        boolean success = true;
	
        // Skip this check for hypersonic - it doesn't support it
        if(software != HSQLDB_SOFTWARE) {
            try {
                DatabaseMetaData meta = connection.getMetaData();
	        
                // Check database exists
                {
                    ResultSet RS = meta.getCatalogs();
                    String traverseDatabaseName;
                    boolean foundDatabase = false;
	            
                    while(RS.next()) {
                        traverseDatabaseName = RS.getString(1);
	                
                        if(traverseDatabaseName.equals(database)) {
                            foundDatabase = true;
                            break;
                        }
                    }
	            
                    if(!foundDatabase) {
                        DesktopManager.showErrorMessage(Locale.getString("CANT_FIND_DATABASE",
                                                                         database));
                        return false;
                    }
                }
            }
            catch (SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                                 e.getMessage()));
                return false;
            }
        }
	
        // If we got here the database is available
        return success;
    }

    /**
     * Create the share table.
     *
     * @return <code>true</code> iff this function was successful.
     */

    /*
      alert([username, host], symbol, daterange, target, field, enabled, dateSet)

      Target is the price or expression which will trigger the alert.
      e.g. (close > 5.0) 

      Bound type is one of [upper, lower, exact] and applies for non 
      expression alerts. 

      Target type is one of [open, close, high, low, volume, expression]
      The field to trigger on for value alerts.

      The actual bare minimum fields for an alert are:  
      * symbol
      * effective date range
      * Gondola expression

      since normal bounds like (open > 10.00) map easily to Gondola expressions.
      However, if we want to allow alerts to be editable, then we need to 
      record the fact that the user set a price alert, not an expression alert.
            
     */

    private boolean createAlertTables() {
        boolean success = false;
	
        try {
            // Create the shares table.
	    success = connect();

	    if (success) {
		List queries = (List)transactionMap.get("createAlerts");
		executeUpdateTransaction(queries);            
		success = true;
	    }
	} catch (SQLException e) {
	    // Since hypersonic won't let us check if the table is already created,
	    // we need to ignore the inevitable error about the table already being present.
	    if(software != HSQLDB_SOFTWARE)
		DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
								 e.getMessage()));
	    else {
		success = true;
	    }
	}
	return success;
    }    

    public String addField(String field, String type, boolean last) {
	String rv = (last) ? field + " " + type : field + " " + type + ",";
	return rv;
    }

    public String addField(String field, String type) {
	return addField(field, type, false);
    }

    public String addField(String field, boolean last) {
	String rv = (last) ? field + " " : field + ",";
	return rv;
    }

    public String addField(String field) {
	return addField(field, false);
    }


    public String addQuotedField(String field, boolean last) {
	String rv = (last) ? "'" + field + "' " : "'" + field + "',";
	return rv;
    }

    public String addQuotedField(String field) {
	return addQuotedField(field, false);
    }

    //Return true if the tables were created successfully
    //or if they already exist.    
    private boolean createTables() {
        boolean success = true;
        
        try {
            boolean foundShareTable = false;
            boolean foundExchangeTable = false;
	    boolean foundAlertTables = false;

	    //Using a HashMap instead of adding four extra booleans
	    //to track all these tables. As they are found, they are removed
	    //from the map.
	    //Names are converted to lowercase here because the 
	    //existence check below is converted to lower case.	    
	    HashMap alertTableMap = new HashMap();
	    alertTableMap.put(ALERT_TABLE_NAME.toLowerCase(), "");
	    alertTableMap.put(OHLCV_ALERT_TABLE_NAME.toLowerCase(), "");
	    alertTableMap.put(GONDOLA_ALERT_TABLE_NAME.toLowerCase(), "");
	    alertTableMap.put(START_DATE_ALERT_TABLE_NAME.toLowerCase(), "");
	    alertTableMap.put(END_DATE_ALERT_TABLE_NAME.toLowerCase(), "");	    

            // Skip this check for hypersonic - it doesn't support it
            if(software != HSQLDB_SOFTWARE) {
                DatabaseMetaData meta = connection.getMetaData();
                ResultSet RS = meta.getTables(database, null, "%", null);
                String traverseTables;
                
                while(RS.next()) {
		    //MySQL/PostgreSQL is not case sensitive
                    traverseTables = RS.getString(3).toLowerCase();
                    
                    if(traverseTables.equalsIgnoreCase(SHARE_TABLE_NAME))
                        foundShareTable = true;

                    if(traverseTables.equalsIgnoreCase(EXCHANGE_TABLE_NAME))
                        foundExchangeTable = true;

		    //Remove the table from the list of alert tables to 
		    //find. 
		    if (alertTableMap.get(traverseTables) != null) {
			alertTableMap.remove(traverseTables);
		    }
                }
		//If empty, it means all the required alert tables exist.
		if (alertTableMap.isEmpty()) {
		    foundAlertTables = true;
		}
            }

            // No table? Let's try and create them.
            if(!foundShareTable)
                success = createShareTable();
            if(!foundExchangeTable && success)
                success = createExchangeTable();
	    if (!foundAlertTables && success) 
		success = createAlertTables();
        }
        catch (SQLException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                             e.getMessage()));
            success = false;
        }

        return success;
    }



    /**
     * Shutdown the database. Only used for the internal database.
     */
    public void shutdown() {
        // We only need to shutdown the internal HYSQLDB database
        if(software == HSQLDB_SOFTWARE && mode == INTERNAL && getConnection()) {
            try {
                Statement statement = connection.createStatement();
                ResultSet RS = statement.executeQuery("SHUTDOWN");
                RS.close();
                statement.close();
            } 
            catch(SQLException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
                                                                 e.getMessage()));
            }
        } 	
    }


    /**
     * Return the SQL clause for returning the left most characters in
     * a string. This function is needed because there seems no portable
     * way of doing this.
     *
     * @param field the field to extract
     * @param length the number of left most characters to extract
     * @return the SQL clause for performing <code>LEFT(string, letters)</code>
     */
    public  String left(String field, int length) {
        if(software.equals(MYSQL_SOFTWARE))
            return new String("LEFT(" + field + ", " + length + ")");
        else {
            // This is probably more portable than the above
            return new String("SUBSTR(" + field + ", 1, " + length + ")");
        }        
    }

    /**
     * Return SQL modify that comes after <code>CREATE</code> and before <code>TABLE</code>.
     * Currently this is only used for HSQLDB.
     *
     * @return the SQL modify for <code>CREATE</code> calls.
     */
    private String getTableType() {
        // We need to supply the table type "CACHED" when creating a HSQLDB
        // table. This tells the database to store the table on disk and cache
        // part of it in memory. If we do not specify this, it will load and
        // work with the entire table in memory.
        if(software.equals(HSQLDB_SOFTWARE))
            return new String("CACHED");
        else
            return "";
    }


    public Statement createStatement() {
	assert connection != null;

	Statement rv = null;

	try {
	    rv = connection.createStatement();
	} catch (SQLException e) {
	    DesktopManager.showErrorMessage(Locale.getString("ERROR_TALKING_TO_DATABASE",
							     e.getMessage()));	
	} finally {
	    return rv;
	}
    }

    /**
     * Return a date string that can be used as part of an SQL query.
     * E.g. 2000-12-03.
     *
     * @param date Date.
     * @return Date string ready for SQL query.
     */
    public String toSQLDateString(TradingDate date) {
	Integer args[] = { new Integer(date.getYear()), new Integer(date.getMonth()), new Integer(date.getDay()) };
     
	int lengths[] = { 4, 2, 2};

	String mesg = Converter.dateFormat(args, lengths, "-");

	return mesg;
	
      //This is much better but not available in 1.4.2
      //return String.format("%04d-%02d-%02d", args);

    }

        /**
     * Return the SQL clause for detecting whether the given symbol appears
     * in the table.
     *
     * @param symbol the symbol
     * @return the SQL clause
     */
    protected String buildSymbolPresentQuery(Symbol symbol) {
        if(software == HSQLDB_SOFTWARE)
            return new String("SELECT TOP 1 " + DatabaseManager.SYMBOL_FIELD + " FROM " +
                              DatabaseManager.SHARE_TABLE_NAME + " WHERE " + DatabaseManager.SYMBOL_FIELD + " = '"
                              + symbol + "' ");
        else
            return new String("SELECT " + DatabaseManager.SYMBOL_FIELD + " FROM " +
                              DatabaseManager.SHARE_TABLE_NAME + " WHERE " + DatabaseManager.SYMBOL_FIELD + " = '"
                              + symbol + "' LIMIT 1");
    }

    /**
     * Return the SQL clause for detecting whether the given date appears
     * in the table.
     *
     * @param date the date
     * @return the SQL clause
     */
    protected String buildDatePresentQuery(TradingDate date) {
        if(software == HSQLDB_SOFTWARE)
            return new String("SELECT TOP 1 " + DatabaseManager.DATE_FIELD + " FROM " +
                              DatabaseManager.SHARE_TABLE_NAME + " WHERE " + DatabaseManager.DATE_FIELD + " = '"
                              + toSQLDateString(date) + "' ");
        else
            return new String("SELECT " + DatabaseManager.DATE_FIELD + " FROM " +
                              DatabaseManager.SHARE_TABLE_NAME + " WHERE " + DatabaseManager.DATE_FIELD + " = '"
                              + toSQLDateString(date) + "' LIMIT 1");
    }

    /**
     * @return false if the database does not allow multiple row inserts
     * in a single statement.
     *
     * i.e. to insert or update two rows requires two SQL statements.
     */

    public boolean supportForSingleRowUpdatesOnly() {
	return (software == HSQLDB_SOFTWARE) ? true : false;
    }
    
    /**
     * @return true if the database supports transactions .
     *
     */

    //Don't know yet if HSQLDB supports transactions
    public boolean supportForTransactions() {
	return true;
    }

    private String buildTransactionString(List queries) {
	String queryString;
	if (supportForTransactions()) {
	    queryString = "BEGIN;";
	} else {
	    queryString = "";
	}
	
	Iterator iterator = queries.iterator();
	while (iterator.hasNext()) {
	    String query = (String)iterator.next();
	    queryString += query;
	}
	queryString += "COMMIT;";
	return queryString;
    }

    public void executeUpdateTransaction(List queries) throws SQLException {
	assert connection != null;
	boolean autoCommit = connection.getAutoCommit();
	connection.setAutoCommit(false);

	try {
	    Iterator iterator = queries.iterator();
	    while (iterator.hasNext()) {
		String query = (String)iterator.next();
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	    }
	    connection.commit();
	    connection.setAutoCommit(autoCommit);
	} catch (SQLException e) {
	    connection.rollback();
	    throw new SQLException(e.getMessage());
	}
    }

    private List executeQueryTransaction(List queries) throws SQLException {
	Vector results = new Vector();
	Iterator iterator = queries.iterator();
	while (iterator.hasNext()) {
	    String query = (String)iterator.next();
	    Statement statement = connection.createStatement();
	    ResultSet rs = statement.executeQuery(query);
	    results.add(rs);
	}
	return results;	
    }
   
    private void readQueriesFromLibrary() {
	transactionMap = new HashMap();
	
	String queryLib = "nz/org/venice/util/sql/venice-queries.sql.xml";
	InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(queryLib);
	
	if (inputStream == null) {
	    DesktopManager.
		showErrorMessage(Locale.getString("VENICE_PROBLEM_TITLE"),
				 Locale.getString("ERROR_TALKING_TO_DATABASE", "Resource " + queryLib + " not found"));
	    return;
	}
	
	try {
	
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser parser = factory.newSAXParser();
	    
	    
	    parser.parse(inputStream, new DefaultHandler() {
		    private boolean newQuery = false;
		    private boolean newTransaction = false;
		    private String newTransactionName;
		    private String newQueryString = "";
		    private Vector queryStack = new Vector();

		    public void startElement(String uri, String local,
					     String qname, 
					     Attributes attributes) {
	
			if (qname.equals("transaction")) {
			    if (newTransaction) {
				//Parse error
			    }
			    newTransactionName = attributes.getValue("name");
			    newTransaction = true;
			}

			if (qname.equals("query")) {
			    if (newQuery) {
				//Parse error
			    }			    
			    newQuery = true;		
			}
			
			if (qname.equals("parameter")) {
			    if (!newQuery) {
				//Parse error
			    }
			    String parm = attributes.getValue("name");
			    if (parm.equals("tableType")) {
				newQueryString += getTableType();
			    } else if (parm.equals("maxSymbolLength")) {
				newQueryString += Symbol.MAXIMUM_SYMBOL_LENGTH;
			    } else {
				//User supplied parameter
				newQueryString += "%" + parm;
			    }
			}
		    }

		    public void endElement(String uri, String local, 
					   String qname) {
			
			if (qname.equals("transaction") && newTransaction) {
			    transactionMap.put(newTransactionName, 
					       queryStack);
			    queryStack = new Vector();
			}
			
			if (qname.equals("query") && newQuery) {
			    queryStack.add(newQueryString);
			    newQuery = false;
			    newQueryString = new String();
			}
		    }

		    public void characters(char[] text, int start, int length) {
			String str = new String(text, start, length);

			if (newQuery) {
			    newQueryString += str;
			}
		    }
		});	    
	} catch (SAXException e) {

	} catch (ParserConfigurationException e) {

	} catch (java.io.IOException e) {

	} finally {
	    
	}	
    }

    public List getQueries(String transactionName) {
	return (List)transactionMap.get(transactionName); 
    }

    public String replaceParameter(String query, String parameterName, 
				 String parameterValue) {
	
	Pattern p = Pattern.compile("%" + parameterName);
	Matcher m = p.matcher(query);

	StringBuffer sb = new StringBuffer();
	while (m.find()) {
	    m.appendReplacement(sb, parameterValue);
	}
	m.appendTail(sb);
	
	return sb.toString();

    }
    
    public String getUUID() {
	UUID id = UUIDGenerator.getInstance().generateRandomBasedUUID();
	return id.toString();
    }
}


