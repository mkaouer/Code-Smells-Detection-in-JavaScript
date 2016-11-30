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


/*
 * Preferences.java
 *
 * Created on 29 January 2002, 20:04
 *
 * This class provides a convenient way for all parts of the Venice system to
 * obtain preference information without violating Preferences namespace convention
 */

package org.mov.prefs;

import org.mov.util.*;
import org.mov.portfolio.*;
import java.util.*;
import java.util.prefs.*;

/**
 *
 * @author  Dan
 * @version 1.0
 */
public class PreferencesManager {

    /** The base in the prefs tree where all Venice settings are stored */
    private final static String base = "org.mov";
    
    /** The user root from Venice's point of view */
    private static Preferences user_root = Preferences.userRoot().node(base);
  
    /** Fetches the root user node that parts of Venice may access */
    public static java.util.prefs.Preferences userRoot() {
        return user_root;
    }

    /** Value given to an integer if it is undefined and we need to 
	know if it is defined or not */
    public static final int UNDEFINED_INT = -1000000;

    /** Fetches the desired user node, based at the <code>base</code> branch
     * @param node the path to the node to be fetched
     */
    public static java.util.prefs.Preferences getUserNode(String node) {
        if (node.charAt(0) == '/') node = node.substring(1);
        return user_root.node(node);
    }

    public static int loadCachedAdvance(TradingDate date) {
	Preferences p = getUserNode("/advance");	

	return p.getInt(date.toString(), UNDEFINED_INT);
    }


    public static void saveCachedAdvance(TradingDate date, int value) {
	Preferences p = getUserNode("/advance");	

	p.putInt(date.toString(), value);
    }

    public static int loadCachedDecline(TradingDate date) {
	Preferences p = getUserNode("/decline");	

	return p.getInt(date.toString(), UNDEFINED_INT);
    }


    public static void saveCachedDecline(TradingDate date, int value) {
	Preferences p = getUserNode("/decline");	

	p.putInt(date.toString(), value);
    }

    public static HashMap loadEquations() {
	
	HashMap equations = new HashMap();
	Preferences p = getUserNode("/filters/functions");

	try {
	    String[] keys = p.keys();
	    for(int i = 0; i < keys.length; i++) {
		equations.put(keys[i], p.get(keys[i], ""));
	    }
	}
	catch(BackingStoreException e) {
	    // dont care
	}

	return equations;
    }

    public static HashMap loadLastPaperTradeSettings() {

	HashMap settings = new HashMap();
	Preferences p = getUserNode("/papertrade");
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

    public static void saveLastPaperTradeSettings(HashMap settings) {
	Preferences p = getUserNode("/papertrade");

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

	    p.put(setting, value);
	}
    }

    public static String[] getPortfolioNames() {
	Preferences p = getUserNode("/portfolio");
	String[] portfolioNames = null;

	try {
	    portfolioNames = p.childrenNames();
	}
	catch(BackingStoreException e) {
	    // don't care
	}

	return portfolioNames;
    }

    public static void deletePortfolio(String name) {
	Preferences p = getUserNode("/portfolio/" + name);

	try {
	    p.removeNode();
	}
	catch(BackingStoreException e) {
	    // don't care
	}
    }

    public static Portfolio loadPortfolio(String portfolioName) {
	Portfolio portfolio = new Portfolio(portfolioName);
	
	Preferences p = getUserNode("/portfolio/" + portfolioName);

	try {
	    // Load accounts
	    String[] accountNames = p.node("accounts").childrenNames();

	    for(int i = 0; i < accountNames.length; i++) {
		Preferences accountPrefs = 
		    p.node("accounts").node(accountNames[i]);
		Account account;

		String accountType = accountPrefs.get("type", "share");
		if(accountType.equals("share")) {
		    account = new ShareAccount(accountNames[i]);
		}
		else {
		    account = new CashAccount(accountNames[i]);
		}

		portfolio.addAccount(account);
	    }

	    // Load transactions
	    Vector transactions = new Vector();

	    String[] transactionNumbers = 
		p.node("transactions").childrenNames();
	    
	    for(int i = 0; i < transactionNumbers.length; i++) {
		Preferences transactionPrefs = 
		    p.node("transactions").node(transactionNumbers[i]);

		int type = 
		    Transaction.stringToType(transactionPrefs.get("type",
								  "withdrawal"));
		TradingDate date = 
		    new TradingDate(transactionPrefs.get("date", 
							 "01/01/2000"),
				    TradingDate.BRITISH);
		float amount = transactionPrefs.getFloat("amount", 0.0F);
		String symbol = transactionPrefs.get("symbol", "");
		int shares = transactionPrefs.getInt("shares", 0);
		float tradeCost = transactionPrefs.getFloat("trade_cost",
							    0.0F);

		try {
		    String cashAccountName = transactionPrefs.get("cash_account", "");
		    CashAccount cashAccount = 
			(CashAccount)portfolio.findAccountByName(cashAccountName);

		    String cashAccountName2 = transactionPrefs.get("cash_account2", "");
		    CashAccount cashAccount2 = 
			(CashAccount)portfolio.findAccountByName(cashAccountName2);

		    String shareAccountName = transactionPrefs.get("share_account", "");
		    ShareAccount shareAccount = 
			(ShareAccount)portfolio.findAccountByName(shareAccountName);

		    // Build transaction and add it to the portfolio
		    Transaction transaction = 
			new Transaction(type, date, amount, symbol, shares,
					tradeCost, cashAccount, cashAccount2, shareAccount);
						
		    transactions.add(transaction);
		}
		catch(ClassCastException e) {
		    // Shouldnt happen unless portfolio gets corrupted
		    assert false;
		}
	    }

	    portfolio.addTransactions(transactions);
	    
	}
	catch(BackingStoreException e) {
	    // don't care
	}

	return portfolio;
    }

    public static void savePortfolio(Portfolio portfolio) {
	Preferences p = getUserNode("/portfolio/" + portfolio.getName());
	p.put("name", portfolio.getName());

	// Clear old accounts and transactions
	try {
	    p.node("accounts").removeNode();
	    p.node("transactions").removeNode();
	}
	catch(BackingStoreException e) {
	    // don't care
	}

	// Save accounts
	Vector accounts = portfolio.getAccounts();
	Iterator iterator = accounts.iterator();

	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();
	    Preferences accountPrefs = 
		p.node("accounts").node(account.getName());
	    
	    if(account.getType() == Account.SHARE_ACCOUNT) {
		accountPrefs.put("type", "share");
	    }
	    else {
		accountPrefs.put("type", "cash");
	    }
	}       

	// Save transactions
	Vector transactions = portfolio.getTransactions();
	iterator = transactions.iterator();
	int i = 0; // Store transactions as node 0, 1, 2 etc

	while(iterator.hasNext()) {
	    Transaction transaction = (Transaction)iterator.next();
	    Preferences transactionPrefs = p.node("transactions/" +
						  Integer.toString(i++));
	    
	    transactionPrefs.put("type", 
			     Transaction.typeToString(transaction.getType()));
	    transactionPrefs.put("date", 
			     transaction.getDate().toString("dd/mm/yyyy"));
	    transactionPrefs.putFloat("amount", transaction.getAmount());

	    if(transaction.getSymbol() != null) 
		transactionPrefs.put("symbol", transaction.getSymbol());

	    transactionPrefs.putInt("shares", transaction.getShares());
	    transactionPrefs.putFloat("trade_cost", 
				      transaction.getTradeCost());

	    CashAccount cashAccount = transaction.getCashAccount();
	    if(cashAccount != null) 
		transactionPrefs.put("cash_account", 
				     cashAccount.getName());

	    CashAccount cashAccount2 = transaction.getCashAccount2();
	    if(cashAccount2 != null) 
		transactionPrefs.put("cash_account2", 
				     cashAccount2.getName());

	    ShareAccount shareAccount = transaction.getShareAccount();
	    if(shareAccount != null) 
		transactionPrefs.put("share_account", 
				     shareAccount.getName());
	}
    }
}
