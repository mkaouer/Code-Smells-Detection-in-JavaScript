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

package org.mov.portfolio;

import org.mov.util.*;
import org.mov.quote.*;

import java.util.*;

/** 
 * Representation of a portfolio. A portfolio object contains several
 * accounts, accounts can be either {@link CashAccount} or
 * {@link ShareAccount}. 
 */
public class Portfolio implements Cloneable {

    // Name of portfolio
    private String name;

    // List of accounts
    Vector accounts = new Vector();

    // Transaction history
    Vector transactions = new Vector();

    /**
     * Create a new empty portfolio.
     *
     * @param	name	The name of the portfolio
     */
    public Portfolio(String name) {
	this.name = name;
    }

    /**
     * Return the portfolio name.
     *
     * @return	the name of the portfolio
     */
    public String getName() {
	return name;
    }

    /**
     * Add an account to the portfolio.
     *
     * @param	account	the new account to add
     */
    public void addAccount(Account account) {
	accounts.add(account);
    }

    /**
     * Record multiple transactions on the portfolio. 
     *
     * @param	transactions	a vector of transactions
     * @see	Transaction
     */
    public void addTransactions(Vector transactions) {

	// Sort transactions by date
	ArrayList list = new ArrayList((Collection)transactions);
	Collections.sort(list);

	// Add them in one by one
	Iterator iterator = list.iterator();

	while(iterator.hasNext()) {
	    Transaction transaction = (Transaction)iterator.next();

	    addTransaction(transaction);
	}
    }

    /**
     * Record a single transaction on the portfolio. 
     *
     * @param	transactions	a new transaction
     */
    public void addTransaction(Transaction transaction) {

	// If the transaction is older than an existing transaction then remove all
	// transactions. Put the new transaction with them, sort them all and then
	// add all of the transactions. I.e. we must add the transactions in chronological
	// order to prevent things like selling stock before we have bought it.

	if(countTransactions() > 0 && 
	   ((Transaction)transactions.lastElement()).compareTo(transaction) > 0) {

	    Vector allTransactions = (Vector)transactions.clone();
	    allTransactions.add(transaction);

	    removeAllTransactions();
	    addTransactions(allTransactions);
	}

	// Otherwise we can just append
	else {
	    // Record history of transactions
	    transactions.add(transaction);

	    // Now update accounts
	    Iterator iterator = accounts.iterator();
	    
	    while(iterator.hasNext()) {
		Account account = (Account)iterator.next();
		
		// Is this account involved in the transaction? If it
		// it is we'll need to update it
		if(account == transaction.getCashAccount() ||
		   account == transaction.getCashAccount2() ||
		   account == transaction.getShareAccount()) {
		    account.transaction(transaction);
		}
	    }
	}
    }

    public Object clone() {

	// First clone portfolio object
	Portfolio clonedPortfolio = new Portfolio(getName());

	// Now clone accounts and insert the cloned accounts into
	// the cloned portfolio
	Iterator accountIterator = accounts.iterator();
	while(accountIterator.hasNext()) {
	    Account account = (Account)accountIterator.next();
	    Object clonedAccount;

	    if(account.getType() == Account.SHARE_ACCOUNT) {
		clonedAccount = ((ShareAccount)account).clone();
	    }
	    else {
		assert account.getType() == Account.CASH_ACCOUNT;
		clonedAccount = ((CashAccount)account).clone();
	    }

	    clonedPortfolio.addAccount((Account)clonedAccount);
	}

	// Now clone the transactions
	Iterator transactionIterator = transactions.iterator();
	while(transactionIterator.hasNext()) {
	    Transaction transaction = 
		(Transaction)transactionIterator.next();
	    Transaction clonedTransaction = (Transaction)transaction.clone();

	    // Adjust share/cash account so it referes to the cloned
	    // portfolio accounts - not the old ones.
	    if(clonedTransaction.getShareAccount() != null) {

		String accountName = 
		    clonedTransaction.getShareAccount().getName();
		ShareAccount shareAccount = (ShareAccount) 
		    clonedPortfolio.findAccountByName(accountName);

		clonedTransaction.setShareAccount(shareAccount);
	    }
	    if(clonedTransaction.getCashAccount() != null) {

		String accountName = 
		    clonedTransaction.getCashAccount().getName();
		CashAccount cashAccount = (CashAccount)
		    clonedPortfolio.findAccountByName(accountName);

		clonedTransaction.setCashAccount(cashAccount);
	    }
	    if(clonedTransaction.getCashAccount2() != null) {

		String accountName = 
		    clonedTransaction.getCashAccount2().getName();
		CashAccount cashAccount2 = (CashAccount)
		    clonedPortfolio.findAccountByName(accountName);

		clonedTransaction.setCashAccount2(cashAccount2);
	    }
	    
	    clonedPortfolio.addTransaction(clonedTransaction);
	}

	return clonedPortfolio;
    }

    /**
     * Return all the accounts in the portfolio
     *
     * @return	accounts
     */
    public Vector getAccounts() {
	return accounts;
    }

    /**
     * Return the number of accounts of the given type the portfolio has
     *
     * @param	type	account type, e.g. {@link Account#CASH_ACCOUNT}
     * @return	number of accounts of the given type
     */
    public int countAccounts(int type) {
	Iterator iterator = accounts.iterator();
	int count = 0;

	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();
	    if(account.getType() == type)
		count++;
	}

	return count;
    }

    /**
     * Find and return the account with the given name in the
     * portfolio.
     *
     * @param	the name of the account to search for
     * @return	the account with the same name as given or <code>null</code>
     *		if it could not be found
     */
    public Account findAccountByName(String name) {
	Iterator iterator = accounts.iterator();

	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();

	    if(account.getName().equals(name)) 
		return account;
	}

	return null;
    }
    
    /**
     * Return the start date of this portfolio. The start date is
     * defined as the date of the first transaction.
     *
     * @return	date of the first transaction
     */
    public TradingDate getStartDate() {
	if(transactions.size() > 0) {
	    Transaction transaction = (Transaction)transactions.firstElement();

	    return transaction.getDate();
	}
	else {
	    return null;
	}
    }

    /**
     * Returns all the symbols traded in this portfolio.
     *
     * @return	symbols traded
     */
    public Vector getSymbolsTraded() {
	Set symbolsTraded = new HashSet();
	Iterator iterator = transactions.iterator();

	while(iterator.hasNext()) {
	    Transaction transaction = (Transaction)iterator.next();
	    if(transaction.getType() == Transaction.ACCUMULATE) 
		symbolsTraded.add(transaction.getSymbol().toLowerCase());
	}

	return new Vector(symbolsTraded);
    }

    /**
     * Count the number of transactions.
     *
     * @return	the number of transactions
     */
    public int countTransactions() {
	return transactions.size();
    }

    /**
     * Count the number of transactions of the given type.
     *
     * @return	the number of transactions
     */
    public int countTransactions(int type) {
	int count = 0;
	Iterator iterator = transactions.iterator();

	while(iterator.hasNext()) {
	    Transaction transaction = (Transaction)iterator.next();
	    if(transaction.getType() == type)
		count++;
	}
	
	return count;
    }

    /**
     * Return the transaction history.
     *
     * @param	transaction history
     * @see	Transaction
     */
    public Vector getTransactions() {
	return transactions;
    }

    /**
     * Remove all transactions from portfolio.
     */
    public void removeAllTransactions() {
	transactions.removeAllElements();

	// A portfolio with no transactions has no value or stock so 
	// remove them from accounts
	Iterator iterator = accounts.iterator();
	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();	   
	    account.removeAllTransactions();
	}
    }

    /**
     * Get the value of the portfolio on the given day. Currently
     * this function should only be called for dates after the last
     * transaction. When it calculates the value it will assume all
     * transactions have taken place.
     *
     * @param	quoteBundle	the quote bundle
     * @param	date	the date to calculate the value
     * @return	the value
     */
    public float getValue(QuoteBundle quoteBundle, TradingDate date) 
	throws MissingQuoteException {

	Iterator iterator = accounts.iterator();
	float value = 0.0F;
	
	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();

	    value += account.getValue(quoteBundle, date);
	}
	
	return value;
    }

}
