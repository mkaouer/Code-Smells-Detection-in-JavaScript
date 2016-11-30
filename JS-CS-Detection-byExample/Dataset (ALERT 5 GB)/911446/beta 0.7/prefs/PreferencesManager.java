/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
   This portion of code Copyright (C) 2004 Dan Makovec (venice@makovec.net)
  
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

package org.mov.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import org.mov.main.Main;
import org.mov.macro.StoredMacro;
import org.mov.portfolio.Account;
import org.mov.portfolio.CashAccount;
import org.mov.portfolio.ShareAccount;
import org.mov.portfolio.Portfolio;
import org.mov.portfolio.PortfolioParserException;
import org.mov.portfolio.PortfolioReader;
import org.mov.portfolio.PortfolioWriter;
import org.mov.portfolio.Transaction;
import org.mov.quote.Symbol;
import org.mov.quote.SymbolFormatException;
import org.mov.table.WatchScreen;
import org.mov.util.Currency;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;
import org.mov.util.TradingDateFormatException;
import org.mov.util.TradingTime;
import org.mov.util.TradingTimeFormatException;

/**
 * The Preferences Manager contains a set of routines for loading and saving all
 * preferences data for the application. Consolidating these routines in a single
 * place allows us to maintain preferences namespace convention and also
 * allows us to easily change the method of storage at a later date if desired.
 * <p>
 * If a save method first clears all preferences data from a node, it is imperative
 * that both the save and the equivalent load methods are <code>synchronized</code>.
 * Otherwise there is the possibility the load call is called just after the
 * delete call which would nuke all the values. Perhaps all the methods
 * should be synchronized.
 *
 * @author Daniel Makovec
 */
public class PreferencesManager {
    // The base in the prefs tree where all Venice settings are stored
    private final static String base = "org.mov";

    // The user root from Venice's point of view
    private static Preferences userRoot = Preferences.userRoot().node(base);

    // This class cannot be instantiated
    private PreferencesManager() {
	// nothing to do
    }

    /** Indicates the quote source is using the inbuilt sample quotes. */
    public static int SAMPLES = 0;

    /** @deprecated Indicates the quote source is accessing quotes in files. */
    public static int FILES = 1;

    /** Indicates the quote source is accessing quotes in a database */
    public static int DATABASE = 2;

    /** Indicates the quote source is accessing the internal database */
    public static int INTERNAL = 3;

    /** Web proxy preferences fields. */
    public class ProxyPreferences {

        /** Web proxy host address. */
	public String host;

        /** Web proxy port. */
	public String port;

        /** Whether we are using the web proxy. */
	public boolean isEnabled;
	
        /** Is authentication enabled? */
        public boolean authEnabled;
        
        /** Authentication user name. */
        public String user;

        /** Authentication password. */
        public String password;
    }

    /** Database preferences fields. */
    public class DatabasePreferences {

	/** Database software (e.g. "mysql"). */
	public String software;

	/** Database driver class. */
	public String driver;

	/** Database host. */
	public String host;

	/** Database port. */
	public String port;

	/** Database name (e.g. "shares") */
	public String database;

	/** Database user name. */
	public String username;

	/** Database password. */
	public String password;
    }

    /** Display preferences fields. */
    public class DisplayPreferences {
	/** X location of main window. */
	public int x;

	/** Y location of main window. */
	public int y;

	/** Width of main window. */
	public int width;

	/** Height of main window. */
	public int height;
    }

    /** Intra-day Quote Sync preferences fields. */
    public class IDQuoteSyncPreferences {
        
        /** Is syncing enabled? */
        public boolean isEnabled;

        /** Symbols to automatically sync. */
        public String symbols;

        /** Time exchange opens. */
        public TradingTime openTime;

        /** Time exchange closes. */
        public TradingTime closeTime;

        /** Polling period in seconds. */
        public int period;
    }

    /**
     * Forces the preferences data to be saved to the backend store (e.g. disk).
     */
    public static void flush() {
	try {
	    userRoot.flush();
	} catch(BackingStoreException e) {
	    // ignore
	}
    }

    /**
     * Fetches the desired user node, based at the <code>base</code> branch
     * @param node the path to the node to be fetched
     */
    public static Preferences getUserNode(String node) {
        if (node.charAt(0) == '/') node = node.substring(1);
        return userRoot.node(node);
    }

    /**
     * Return whether we require the user to explicitly accept the GPL
     * license. Currently the license must be explicilty accepted by
     * the user for each version.
     *
     * @return <code>true</code> if the user needs to explicitly accept the GPL
     */
    public static boolean getHasGPLAcceptance() {
        Preferences node = getUserNode("/license");
        String acceptedVersion = node.get("accepted_version", "not_accepted");
        return !acceptedVersion.equals(Main.SHORT_VERSION);
    }

    /**
     * Set that the user has been shown the GPL and has accepted it. The user
     * will not be bothered again until the next version.
     */
    public static void putHasGPLAcceptance() {
        Preferences node = getUserNode("/license");
        node.put("accepted_version", Main.SHORT_VERSION);
    }

    /**
     * Load the last directory used when importing quote files.
     *
     * @param  dirtype the directory type (e.g. macros, importer, etc)
     * @return the directory.
     */
    public static String getDirectoryLocation(String dirtype) {
        Preferences prefs = getUserNode("/"+dirtype);
        String directory = prefs.get("directory", "");

        if(directory.length() != 0)
            return directory;
        else
            return null;
    }

    /**
     * Save the directory used to import quote files.
     *
     * @param dirtype the directory type (e.g. macros, importer, etc)
     * @param directory the directory.
     */
    public static void putDirectoryLocation(String dirtype, String directory) {
        Preferences prefs = getUserNode("/"+dirtype);
        prefs.put("directory", directory);
    }

    /**
     * Load the list of all stored expressions.
     *
     * @return the list of stored expressions.
     * @see StoredExpression
     */
    public static synchronized List getStoredExpressions() {
	List storedExpressions = new ArrayList();
	Preferences prefs = getUserNode("/equations");

	try {
	    String[] keys = prefs.keys();
	    for(int i = 0; i < keys.length; i++)
		storedExpressions.add(new StoredExpression(keys[i], prefs.get(keys[i], "")));
	}
	catch(BackingStoreException e) {
	    // ignore
	}

	return storedExpressions;
    }

    /**
     * Save the list of all stored expressions.
     *
     * @param storedExpressions the stored expressions.
     * @see StoredExpression
     */
    public static synchronized void putStoredExpressions(List storedExpressions) {
	try {
	    // Remove old expressions
	    Preferences prefs = getUserNode("/equations");
	    prefs.removeNode();
	    prefs = getUserNode("/equations");

	    for(Iterator iterator = storedExpressions.iterator(); iterator.hasNext();) {
		StoredExpression storedExpression = (StoredExpression)iterator.next();
		prefs.put(storedExpression.name, storedExpression.expression);
	    }
	}
	catch(BackingStoreException e) {
	    // ignore
	}
    }

    /**
     * Load the list of all registered macros.
     * 
     * @return the list of registered macros
     * @see StoredMacro
     */
    public static synchronized List getStoredMacros() {
        List stored_macros = new ArrayList();
        Preferences prefs = getUserNode("/macros/info");

	    String dirname = PreferencesManager.getDirectoryLocation("macros");
	    if (dirname == null) return stored_macros;
	    File directory = new File(dirname);
	    if (!directory.isDirectory())
	        return null;

	    String[] list = directory.list(new FilenameFilter() {
	        public boolean accept(File dir, String filename) {
	            return (dir.getAbsolutePath().equals(PreferencesManager.getDirectoryLocation("macros")) &&
	                    filename.indexOf(".py") == filename.length()-3);
	        }
	    });
	    
        for(int i = 0; i < list.length; i++) {
            String name = list[i].substring(0,list[i].length()-3);
            Preferences macro_node = getUserNode("/macros/info/"+list[i]);
            stored_macros.add(new StoredMacro(macro_node.get("name", name), 
                    		  list[i],
                 			  macro_node.getBoolean("on_startup",false),
                       		  macro_node.getInt("start_sequence",0),
                       		  macro_node.getBoolean("in_menu", false)));
        }
        return stored_macros;
    }

    /**
     * Save the list of all registered macros.
     *
     * @param stored_macros the registered macros.
     * @see StoredMacro
     */
    public static synchronized void putStoredMacros(List stored_macros) {
        try {
            // Remove old macro definitions
            Preferences prefs = getUserNode("/macros_info");
            prefs.removeNode();
            prefs = getUserNode("/macros_info");
            
            for(Iterator iterator = stored_macros.iterator(); iterator.hasNext();) {
                StoredMacro stored_macro = (StoredMacro)iterator.next();
                Preferences macro_node = getUserNode("/macros/info/"+stored_macro.getFilename());
                macro_node.put("name", stored_macro.getName());
                macro_node.putBoolean("on_startup", stored_macro.isOn_startup());
                macro_node.putInt("start_sequence", stored_macro.getStart_sequence());
                macro_node.putBoolean("in_menu", stored_macro.isIn_menu());
            }
        }
        catch(BackingStoreException e) {
            // ignore
        }
    }
    
    //Store the users text made for this symbol     
    public static void putUserNotes(String symbol, String text) {
        String xpath = "/userNotes/" + symbol;
        Preferences prefs = getUserNode("/userNotes");
	
        prefs = getUserNode("/userNotes");
        prefs.put(symbol, text);
    }

    // Retrieve the notes made for this symbol.
    public static String getUserNotes(String symbol) {
	String text, xpath;
	Preferences prefs;

	text = "";
	xpath = "/userNotes" + symbol;

	prefs = getUserNode("/userNotes");
	if (prefs != null) {
	    text = prefs.get(symbol, "");
	}
	    
	return text;

    }

    /**
     * Load all saved user input in an Analyser Page.
     *
     * @param key a key which identifies which page settings to load.
     * @return mapping of settings.
     * @see org.mov.analyser.AnalyserPage
     */
    public static HashMap getAnalyserPageSettings(String key) {

	HashMap settings = new HashMap();
	Preferences p = getUserNode("/analyser/" + key);
	String[] settingList = null;

	// Get all the settings that we've saved
	try {
	    settingList = p.keys();
	}
	catch(BackingStoreException e) {
	    // ignore
	}

	// Now populate settings into a hash
	for(int i = 0; i < settingList.length; i++) {
	    String value = p.get(settingList[i], "");
	    settings.put((Object)settingList[i], (Object)value);
	}

	return settings;
    }

    /**
     * Save all user input in an Analyser Page.
     *
     * @param key a key which identifies which page settings to save.
     * @param settings the settings to save.
     * @see org.mov.analyser.AnalyserPage
     */
    public static void putAnalyserPageSettings(String key, HashMap settings) {
	Preferences p = getUserNode("/analyser/" + key);
	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

	    p.put(setting, value);
	}
    }

    /**
     * Load the last preferences page visited.
     *
     * @return index of the last preferences page visited.
     */
    public static int getLastPreferencesPage() {
	Preferences prefs = getUserNode("/prefs");
	return prefs.getInt("page", 0);
    }

    /**
     * Save last preferences page visited.
     *
     * @param page index of the last preferences page visited.
     */
    public static void putLastPreferencesPage(int page) {
	Preferences prefs = getUserNode("/prefs");
	prefs.putInt("page", page);
    }

    /**
     * Load the cache's maximum number of quotes.
     *
     * @return the maximum number of quotes.
     */
    public static int getMaximumCachedQuotes() {
	Preferences prefs = getUserNode("/cache");
        return prefs.getInt("maximumQuotes", 100000);
    }

    /**
     * Save the cache's maximum number of quotes.
     *
     * @param maximumCachedQuotes the maximum number of quotes.
     */
    public static void putMaximumCachedQuotes(int maximumCachedQuotes) {
        Preferences prefs = getUserNode("/cache");
        prefs.putInt("maximumQuotes", maximumCachedQuotes);
    }
    
    /**
     * Return a list of the names of all the watch screens.
     *
     * @return the list of watch screen names.
     */
    public static List getWatchScreenNames() {
	Preferences p = getUserNode("/watchscreens");
        List watchScreenNames = new ArrayList();

	try {
	    String preferenceWatchScreenNames[] = p.childrenNames();

            for(int i = 0; i < preferenceWatchScreenNames.length; i++) {
                String watchScreenName = preferenceWatchScreenNames[i];
                watchScreenNames.add(watchScreenName);
            }
	}
	catch(BackingStoreException e) {
	    // don't care
	}

        // Make sure list is in alphabetical order
        Collections.sort(watchScreenNames);

	return watchScreenNames;
    }

    /**
     * Load the watch screen with the given name.
     *
     * @param name the name of the watch screen to load.
     * @return the watch screen.
     */
    public static synchronized WatchScreen getWatchScreen(String name) {
        WatchScreen watchScreen = new WatchScreen(name);

        Preferences p = getUserNode("/watchscreens/" + name);

	try {
            // Load symbols
            String[] symbols = p.node("symbols").childrenNames();

            for(int i = 0; i < symbols.length; i++)
                try {
                    watchScreen.addSymbol(Symbol.find(symbols[i]));
                } catch(SymbolFormatException e) {
                    assert false;
                }
        }
	catch(BackingStoreException e) {
	    // don't care
	}

        return watchScreen;
    }

    /**
     * Save the watch screen.
     *
     * @param watchScreen the watch screen.
     */
    public static synchronized void putWatchScreen(WatchScreen watchScreen) {
        Preferences p = getUserNode("/watchscreens/" + watchScreen.getName());
	p.put("name", watchScreen.getName());

        // Clear old symbols
        try {
            p.node("symbols").removeNode();
        }
        catch(BackingStoreException e) {
            // don't care
        }

        // Save watched symbols
        List symbols = watchScreen.getSymbols();

        for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();

            // Later on we will associate things like alerts and stops
            // for each symbol. But at the moment we only keep the list
            // of symbols
            Preferences symbolPrefs = p.node("symbols").node(symbol.toString());
            symbolPrefs.put("present", "1");
        }
    }

    /**
     * Delete the watch screen.
     *
     * @param name the watch screen name.
     */
    public static synchronized void deleteWatchScreen(String name) {
	Preferences p = getUserNode("/watchscreens/" + name);

	try {
	    p.removeNode();
	}
	catch(BackingStoreException e) {
	    // don't care
	}
    }

    /**
     * Return a  list of the names of all the portfolios.
     *
     * @return the list of portfolio names.
     */
    public static synchronized List getPortfolioNames() {
        List portfolioNames = new ArrayList();

        // First retrieve all the portfolios stored in ~/Venice/Portfolios/ (0.7b and up)
        // Portfolios are now stored as files, as opposed to Java prefences, because this
        // improves read and write times (especially on Mac OS X) and makes portfolio
        // management easier for the user.
        String[] portfolioFileNames = getPortfolioHome().list();
        String suffix = ".xml";

        for(int i = 0; i < portfolioFileNames.length; i++) {
            String portfolioFileName = portfolioFileNames[i];

            // Ignore files without trailing suffix
            if(portfolioFileName.endsWith(suffix)) {
                // Remove trailing suffix
                String portfolioName =
                    portfolioFileName.substring(0, portfolioFileName.length() - suffix.length()); 
                portfolioNames.add(portfolioName);
            }
        }

        // Now retrieve all the portfolios stored in Java preferences (up to 0.6b)
	try {
            Preferences p = getUserNode("/portfolio");
	    String[] preferencePortfolioNames = p.childrenNames();

            for(int i = 0; i < preferencePortfolioNames.length; i++) {
                String portfolioName = preferencePortfolioNames[i];
                portfolioNames.add(portfolioName);
            }
	}
	catch(BackingStoreException e) {
	    // don't care
	}

        // Make sure list is in alphabetical order
        Collections.sort(portfolioNames);

	return portfolioNames;
    }

    /**
     * Delete the portfolio.
     *
     * @param name the portfolio name.
     */
    public static synchronized void deletePortfolio(String name) {
        // Delete the portfolio from ~/Venice/Portfolios/ (0.7b and up)
        File portfolioFile = new File(getPortfolioHome(), name.concat(".xml"));
        portfolioFile.delete();

        // Delete the portfolio from Java preferences (up to 0.6b)
	try {
            Preferences p = getUserNode("/portfolio/" + name);
	    p.removeNode();
	}
	catch(BackingStoreException e) {
	    // don't care
	}
    }

    /**
     * Read the portfolio contained the given file. Venice stores portfolios
     * in files from 0.7b and up.
     *
     * @param portfolioFile the file containing the portfolio.
     * @return the Portfolio contained in the file.
     * @exception PreferencesException if there was an error loading the portfolio.
     */
    private static Portfolio getPortfolioFromFile(File portfolioFile)
        throws PreferencesException {
        try {
            FileInputStream inputStream = new FileInputStream(portfolioFile);
            Portfolio portfolio = PortfolioReader.read(inputStream);
            inputStream.close();
            return portfolio;
        }
        catch(IOException e) {
            throw new PreferencesException(e.getMessage());
        }
        catch(PortfolioParserException e) {
            throw new PreferencesException(e.getMessage());
        }
        catch(SecurityException e) {
            throw new PreferencesException(e.getMessage());
        }
    }

    /**
     * Read the portfolio with the given name from the preferences. Venice stores
     * portfolios in preferences up to 0.6b.
     *
     * @param name the name of the portfolio to retrieve.
     * @return the Portfolio.
     * @exception PreferencesException if there was an error loading the portfolio.
     */
    private static Portfolio getPortfolioFromPreferences(String name)
        throws PreferencesException {
        // Venice 0.6b did not support multiple currencies. So just default
        // to the user's default currency.
	Portfolio portfolio = new Portfolio(name, Currency.getDefaultCurrency());
	
	Preferences p = getUserNode("/portfolio/" + name);

	try {
	    // Load accounts
	    String[] accountNames = p.node("accounts").childrenNames();

	    for(int i = 0; i < accountNames.length; i++) {
		Preferences accountPrefs =
		    p.node("accounts").node(accountNames[i]);
		Account account;

		String accountType = accountPrefs.get("type", "share");
		if(accountType.equals("share")) {
		    account = new ShareAccount(accountNames[i], Currency.getDefaultCurrency());
		}
		else {
		    account = new CashAccount(accountNames[i], Currency.getDefaultCurrency());
		}

		portfolio.addAccount(account);
	    }

	    // Load transactions
	    List transactions = new ArrayList();

	    String[] transactionNumbers =
		p.node("transactions").childrenNames();
	
	    for(int i = 0; i < transactionNumbers.length; i++) {
		Preferences transactionPrefs =
		    p.node("transactions").node(transactionNumbers[i]);

		int type = getTransactionType(transactionPrefs.get("type", ""));

		TradingDate date = null;

                try {
                    date =
                        new TradingDate(transactionPrefs.get("date",
                                                             "01/01/2000"),
                                        TradingDate.BRITISH);
                }
                catch(TradingDateFormatException e) {
                    throw new PreferencesException(e.getMessage());
                }

		Money amount = new Money(Currency.getDefaultCurrency(),
                                         transactionPrefs.getDouble("amount", 0.0D));
		Symbol symbol = null;
		int shares = transactionPrefs.getInt("shares", 0);
		Money tradeCost = new Money(Currency.getDefaultCurrency(),
                                            transactionPrefs.getDouble("trade_cost", 0.0D));

                try {
                    String symbolString = transactionPrefs.get("symbol", "");

                    if(symbolString.length() > 0)
                        symbol = Symbol.find(transactionPrefs.get("symbol", ""));
                }
                catch(SymbolFormatException e) {
                    throw new PreferencesException(e.getMessage());
                }

                String cashAccountName = transactionPrefs.get("cash_account", "");
                String cashAccountName2 = transactionPrefs.get("cash_account2", "");
                String shareAccountName = transactionPrefs.get("share_account", "");
                    
                CashAccount cashAccount = null;
                CashAccount cashAccount2 = null;
                ShareAccount shareAccount = null;

		try {
		    cashAccount =
			(CashAccount)portfolio.findAccountByName(cashAccountName);
                }
                catch(ClassCastException e) {
                    throw new PreferencesException(Locale.getString("EXPECTING_CASH_ACCOUNT",
                                                                    cashAccountName));
                }

		try {
                    cashAccount2 =
			(CashAccount)portfolio.findAccountByName(cashAccountName2);
                }
                catch(ClassCastException e) {
                    throw new PreferencesException(Locale.getString("EXPECTING_CASH_ACCOUNT",
                                                                    cashAccountName2));
                }
                
                try {
		    shareAccount =
			(ShareAccount)portfolio.findAccountByName(shareAccountName);
                }
                catch(ClassCastException e) {
                    throw new PreferencesException(Locale.getString("EXPECTING_SHARE_ACCOUNT",
                                                                    shareAccountName));
                }

                // Skip transactions which have an account. There seems to have been
                // an old bug which created duplication transactions with no account.
                if(cashAccount != null || cashAccount2 != null || shareAccount != null) {
                    // Build transaction and add it to the portfolio
                    Transaction transaction =
                        new Transaction(type, date, amount, symbol, shares,
                                        tradeCost, cashAccount, cashAccount2,
                                        shareAccount);

                    transactions.add(transaction);
                }
            }

	    portfolio.addTransactions(transactions);
	
	}
	catch(BackingStoreException e) {
            throw new PreferencesException(e.getMessage());
	}

	return portfolio;
    }

    /**
     * Load the portfolio with the given name.
     *
     * @param portoflioName the name of the portfolio to load.
     * @return the portfolio.
     * @exception PreferencesException if there was an error loading the portfolio.
     */
    public static synchronized Portfolio getPortfolio(String portfolioName)
        throws PreferencesException {
        File portfolioFile = new File(getPortfolioHome(), portfolioName.concat(".xml"));

        // Load the portfolio from ~/Venice/Portfolios/ (0.7b and up)
        if(portfolioFile.exists())
            return getPortfolioFromFile(portfolioFile);

        // Load the portfolio from Java preferences (up to 0.6b)
        else
            return getPortfolioFromPreferences(portfolioName);
    }

    // Venice 0.1 & 0.2 did not have i8ln support so they saved the
    // transactions by name. But this does not work if the transaction
    // names can change! But I also want 0.3 to be backward compatible
    // with 0.2. So this routine will understand both transaction name
    // and transaction number.
    private static int getTransactionType(String transactionType) {
	// Venice 0.3+ saves transactions by numbers.
	try {
	    return Integer.parseInt(transactionType);
	}
	catch(NumberFormatException e) {
	    // not a number
	}

	// Otherwise compare with all the old transaction names
	if(transactionType.equals("Accumulate"))
	    return Transaction.ACCUMULATE;
	else if(transactionType.equals("Reduce"))
	    return Transaction.REDUCE;
	else if(transactionType.equals("Deposit"))
	    return Transaction.DEPOSIT;
	else if(transactionType.equals("Fee"))
	    return Transaction.FEE;
	else if(transactionType.equals("Interest"))
	    return Transaction.INTEREST;
	else if(transactionType.equals("Withdrawal"))
	    return Transaction.WITHDRAWAL;
	else if(transactionType.equals("Dividend"))
	    return Transaction.DIVIDEND;
	else if(transactionType.equals("Dividend DRP"))
	    return Transaction.DIVIDEND_DRP;
	else
	    return Transaction.TRANSFER;
    }

    /**
     * Return the directroy which contains Venice's HSQLDB database.
     *
     * @return Database directroy.
     */
    private static File getDatabaseHome() {
        File veniceHome = getVeniceHome();
        File databaseHome = new File(veniceHome, "Database");
        if (!databaseHome.exists())
            databaseHome.mkdir();
        return databaseHome;
    }

    /**
     * Return the directory which contains Venice's portfolios.
     *
     * @return Portfolio directory.
     */
    private static File getPortfolioHome() {
        File veniceHome = getVeniceHome();
        File portfolioHome = new File(veniceHome, "Portfolio");
        if (!portfolioHome.exists())
            portfolioHome.mkdir();
        return portfolioHome;
    }

    /**
     * Return Venice's home directory. Venice uses this directory to store important
     * files such as portfolios. If this directory does not exist it will be
     * created.
     *
     * @return Home directory
     */
    private static File getVeniceHome() {
        File veniceHome = new File(System.getProperty("user.home"), "Venice");
        if (!veniceHome.exists())
            veniceHome.mkdir();
        return veniceHome;
    }

    /**
     * Save the portfolio.
     *
     * @param portfolio the portfolio.
     * @exception PreferencesException if there was an error saving the portfolio.
     */
    public static synchronized void putPortfolio(Portfolio portfolio)
        throws PreferencesException {
        try {
            File portfolioFile = new File(getPortfolioHome(), portfolio.getName() + ".xml");
            FileOutputStream outputStream = new FileOutputStream(portfolioFile);
            PortfolioWriter.write(portfolio, outputStream);
            outputStream.close();
        }
        catch(IOException e) {
            throw new PreferencesException(e.getMessage());
        }
        catch(SecurityException e) {
            throw new PreferencesException(e.getMessage());
        }
        
        // Clear old portfolio from preferences if present (up to 0.6b).
	try {
            Preferences p = getUserNode("/portfolio/" + portfolio.getName());
	    p.removeNode();
	}
	catch(BackingStoreException e) {
	    throw new PreferencesException(e.getMessage());
	}
    }

    /**
     * Load proxy settings.
     *
     * @return proxy preferences.
     */
    public static ProxyPreferences getProxySettings() {
        Preferences prefs = getUserNode("/proxy");
        PreferencesManager preferencesManager = new PreferencesManager();
        ProxyPreferences proxyPreferences = preferencesManager.new ProxyPreferences();
        proxyPreferences.host = prefs.get("host", "proxy");
        proxyPreferences.port = prefs.get("port", "8080");
        proxyPreferences.isEnabled = prefs.getBoolean("enabled", false);
        
    	proxyPreferences.user= prefs.get("user", "");
    	proxyPreferences.password = prefs.get("password", "");
    	proxyPreferences.authEnabled = prefs.getBoolean("authEnabled", false);

        return proxyPreferences;
    }

    /**
     * Save proxy settings.
     *
     * @param proxyPreferences the new proxy preferences.
     */
    public static void putProxySettings(ProxyPreferences proxyPreferences) {
	Preferences prefs = getUserNode("/proxy");
	prefs.put("host", proxyPreferences.host);
	prefs.put("port", proxyPreferences.port);
	prefs.putBoolean("enabled", proxyPreferences.isEnabled);
	
	prefs.put("user", proxyPreferences.user);
	prefs.put("password", proxyPreferences.password);
	prefs.putBoolean("authEnabled", proxyPreferences.authEnabled);
    }

    /**
     * Load language setting. Returns <code>null</code> if there is no language
     * setting saved in preferences.
     *
     * @return ISO Language Code
     */
    public static String getLanguageCode() {
        Preferences prefs = getUserNode("/language");
        return prefs.get("locale", null);
    }

    /**
     * Save language setting.
     *
     * @param languageCode ISO Language Code
     */
    public static void putLanguageCode(String languageCode) {
        Preferences prefs = getUserNode("/language");
	prefs.put("locale", languageCode);
    }

    /**
     * Load user interface setting.
     *
     * @return the minimum decimal digits to be displayed
     */
    public static int getMinDecimalDigits() {
        // 3 is the default, if anything goes wrong
        int retValue = 3;
        Preferences prefs = getUserNode("/min_user_interface");
        String str = prefs.get("min_decimal_digits", "3");
        try {
            retValue = Integer.parseInt(str);
        } catch(Exception ex) {
            retValue = 3;
        }
        return retValue;
    }

    /**
     * Save user interface setting.
     *
     * @param minDecimalDigits the minimum decimal digits to be displayed
     */
    public static void putMinDecimalDigits(String minDecimalDigits) {
        Preferences prefs = getUserNode("/min_user_interface");
	prefs.put("min_decimal_digits", minDecimalDigits);
    }

    /**
     * Load user interface setting.
     *
     * @return the maximum decimal digits to be displayed
     */
    public static int getMaxDecimalDigits() {
        // 3 is the default, if anything goes wrong
        int retValue = 3;
        Preferences prefs = getUserNode("/max_user_interface");
        String str = prefs.get("max_decimal_digits", "3");
        try {
            retValue = Integer.parseInt(str);
        } catch(Exception ex) {
            retValue = 3;
        }
        return retValue;
    }

    /**
     * Save user interface setting.
     *
     * @param maxDecimalDigits the maximum decimal digits to be displayed
     */
    public static void putMaxDecimalDigits(String maxDecimalDigits) {
        Preferences prefs = getUserNode("/max_user_interface");
	prefs.put("max_decimal_digits", maxDecimalDigits);
    }

    /**
     * Load default chart setting.
     *
     * @return the default chart to be displayed
     */
    public static String getDefaultChart() {        
        Preferences prefs = getUserNode("/default_chart_defaults");
        String str = prefs.get("default_chart", "Line Graph");        
        return str;
    }

    /**
     * Save default chart setting.
     *
     * @param defaultChart the chart to be displayed
     */
    public static void putDefaultChart(String defaultChart) {

        Preferences prefs = getUserNode("/default_chart_defaults");
	prefs.put("default_chart", defaultChart);
    }

    /**
     * Get quote source setting.
     *
     * @return quote source, one of {@link #DATABASE}, {@link #FILES} or {@link #SAMPLES}.
     */
    public static int getQuoteSource() {
	Preferences prefs = getUserNode("/quote_source");
	String quoteSource = prefs.get("source", "samples");
	
	if(quoteSource.equals("samples"))
	    return SAMPLES;
	else if(quoteSource.equals("files"))
            // File quote source is deprecated. Switch to internal quote source.
	    return INTERNAL;
	else if(quoteSource.equals("database"))
	    return DATABASE;
	else 
        return INTERNAL;
    }

    /**
     * Set quote source setting.
     *
     * @param quoteSource the quote source, one of {@link #DATABASE}, {@link #INTERNAL} or 
     *                    {@link #SAMPLES}.
     */
    public static void putQuoteSource(int quoteSource) {
        assert(quoteSource == DATABASE || quoteSource == SAMPLES || quoteSource == INTERNAL);

	    Preferences prefs = getUserNode("/quote_source");
            String source;

	    if(quoteSource == SAMPLES)
	        source = "samples";
	    else if(quoteSource == DATABASE)
	        source = "database";
	    else
            source = "internal";

        prefs.put("source", source);
    }

    /**
     * Load database settings.
     *
     * @return database preferences.
     */
    public static DatabasePreferences getDatabaseSettings() {
        Preferences prefs = getUserNode("/quote_source/database");
        PreferencesManager preferencesManager = new PreferencesManager();
        DatabasePreferences databasePreferences =
            preferencesManager.new DatabasePreferences();
        databasePreferences.software = prefs.get("software", "mysql");
        databasePreferences.driver   = prefs.get("driver", "com.mysql.jdbc.Driver");
        databasePreferences.host     = prefs.get("host", "db");
        databasePreferences.port     = prefs.get("port", "3306");
        databasePreferences.database = prefs.get("dbname", "shares");
        databasePreferences.username = prefs.get("username", "");
        databasePreferences.password = prefs.get("password", "3306");
        return databasePreferences;
    }

    /**
     * Save database settings.
     *
     * @param databasePreferences the new database preferences.
     */
    public static void putDatabaseSettings(DatabasePreferences databasePreferences) {
	Preferences prefs = getUserNode("/quote_source/database");
	prefs.put("software", databasePreferences.software);
	prefs.put("driver", databasePreferences.driver);
	prefs.put("host", databasePreferences.host);
	prefs.put("port", databasePreferences.port);
	prefs.put("dbname", databasePreferences.database);
	prefs.put("username", databasePreferences.username);
	prefs.put("password", databasePreferences.password);
    }

    /**
     * Load the file name to store the internal database.
     *
     * @return internal database file name
     */
    public static String getInternalFileName() {
        File databaseFile = new File(getDatabaseHome(), "Database");
        String databaseFileName = "Database";

        try {
            databaseFileName = databaseFile.getCanonicalPath();
        }
        catch(IOException e) {
            // don't care
        }
        catch(SecurityException e) {
            // don't care
        }        

        return databaseFileName;
    }

    /**
     * Load display settings.
     *
     * @return display preferences.
     */
    public static DisplayPreferences getDisplaySettings() {
        Preferences prefs = getUserNode("/display");
        PreferencesManager preferencesManager = new PreferencesManager();
        DisplayPreferences displayPreferences =
            preferencesManager.new DisplayPreferences();
        displayPreferences.x = prefs.getInt("default_x", 0);
        displayPreferences.y = prefs.getInt("default_y", 0);
        displayPreferences.width = prefs.getInt("default_width", 400);
        displayPreferences.height = prefs.getInt("default_height", 400);
        return displayPreferences;
    }

    /**
     * Save display settings.
     *
     * @param displayPreferences the new display preferences.
     */
    public static void putDisplaySettings(DisplayPreferences displayPreferences) {
	Preferences prefs = getUserNode("/display");
	prefs.putInt("default_x", displayPreferences.x);
	prefs.putInt("default_y", displayPreferences.y);
	prefs.putInt("default_width", displayPreferences.width);
	prefs.putInt("default_height", displayPreferences.height);
    }

    /**
     * Load intra-day quote sync module preferences.
     *
     * @return the preferences.
     * @see org.mov.quote.IDQuoteSyncModule
     */
    public static IDQuoteSyncPreferences getIDQuoteSyncPreferences() {
        Preferences prefs = getUserNode("/id_quote_sync");
        PreferencesManager preferencesManager = new PreferencesManager();
        IDQuoteSyncPreferences idQuoteSyncPreferences =
            preferencesManager.new IDQuoteSyncPreferences();

        idQuoteSyncPreferences.isEnabled = prefs.getBoolean("isEnabled", false);
        idQuoteSyncPreferences.symbols = prefs.get("symbols", "");

        try {
            idQuoteSyncPreferences.openTime = new TradingTime(prefs.get("openTime", "9:00:00"));
            idQuoteSyncPreferences.closeTime = new TradingTime(prefs.get("closeTime", "16:00:00"));
        }
        catch(TradingTimeFormatException e) {
            // This should never happen - but deal with the possibility gracefully.
            idQuoteSyncPreferences.openTime = new TradingTime(9, 0, 0);
            idQuoteSyncPreferences.closeTime = new TradingTime(16, 0, 0);
        }

        idQuoteSyncPreferences.period = prefs.getInt("period", 60);
        return idQuoteSyncPreferences;
    }

    /**
     * Save intra-day quote sync module preferences.
     *
     * @param idQuoteSyncPreferences the preferences.
     * @see org.mov.quote.IDQuoteSyncModule
     */
    public static void putIDQuoteSyncPreferences(IDQuoteSyncPreferences idQuoteSyncPreferences) {
        Preferences prefs = getUserNode("/id_quote_sync");
        prefs.putBoolean("isEnabled", idQuoteSyncPreferences.isEnabled);
        prefs.put("symbols", idQuoteSyncPreferences.symbols);
        prefs.put("openTime", idQuoteSyncPreferences.openTime.toString());
        prefs.put("closeTime", idQuoteSyncPreferences.closeTime.toString());
        prefs.putInt("period", idQuoteSyncPreferences.period);

    }
}
