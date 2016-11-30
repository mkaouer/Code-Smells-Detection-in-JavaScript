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

package nz.org.venice.portfolio;

import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.EODQuoteCache;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.util.Currency;
import nz.org.venice.util.ExchangeRateCache;
import nz.org.venice.util.Money;
import nz.org.venice.util.MoneyFormatException;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;
import nz.org.venice.util.UnknownCurrencyCodeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Representation of a portfolio. A portfolio object contains several
 * accounts, accounts can be either {@link CashAccount} or
 * {@link ShareAccount}.
 *
 * @author Andrew Leppard
 */
public class Portfolio implements Cloneable {

    // Name of portfolio
    private String name;

    // List of accounts
    private List accounts = new ArrayList();

    // Transaction history
    private List transactions = new ArrayList();

    // If the portfolio is transient it is just used for displaying
    // information to the user and shouldn't be saved
    private boolean isTransient;

    // We keep track of the amount of cash deposited in the Portfolio
    // so we can calculate its profit/loss.
    private Money deposits;

    // The base currency of the portfolio. This currency will be the
    // default when creating new accounts, and will be used to display
    // the total value of the portfolio.
    private Currency currency;

    // Local reference to the exchange rate cache
    private ExchangeRateCache exchangeRateCache;

    /**
     * Create a new empty portfolio using the given currency.
     *
     * @param	name	 The name of the portfolio
     * @param   currency The base currency of the portfolio
     */
    public Portfolio(String name, Currency currency) {
        this(name, currency, false);
    }

    /**
     * Create a new empty portfolio using the default currency. The portfolio can be
     * permanent or transient.
     *
     * @param	name	    The name of the portfolio
     * @param   isTransient Set to <code>true</code> if the portfolio displays
     *                      working information and shouldn't be saved.
     */
    public Portfolio(String name, boolean isTransient) {
        this(name, Currency.getDefaultCurrency(), isTransient);
    }

    /**
     * Create a new empty portfolio using the given currency. The portfolio can be
     * permanent or transient.
     *
     * @param	name	    The name of the portfolio
     * @param   currency    The base currency of the portfolio
     * @param   isTransient Set to <code>true</code> if the portfolio displays
     *                      working information and shouldn't be saved.
     */
    public Portfolio(String name, Currency currency, boolean isTransient) {
	this.name = name;
        this.currency = currency;
        this.isTransient = isTransient;
        this.deposits = new Money(currency, 0.0D);
        this.exchangeRateCache = ExchangeRateCache.getInstance();
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
     * Set the portfolio name.
     *
     * @param name the new portfolio name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the portfolio's default currency.
     *
     * @return the default currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Return whether the portfolio is transient or permanent.
     *
     * @return <code>true</code> if the portfolio is transient and shouldn't
     *         be saved
     */
    public boolean isTransient() {
        return isTransient;
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
     * @param	transactions	a list of transactions
     * @see	Transaction
     */
    public void addTransactions(List transactions) {

	// Sort transactions by date
	List list = new ArrayList(transactions);
	Collections.sort(list);

	// Add them in one by one
	for(Iterator iterator = list.iterator(); iterator.hasNext();) {
	    Transaction transaction = (Transaction)iterator.next();

	    addTransaction(transaction);
	}
    }

    /**
     * Record a single transaction on the portfolio.
     *
     * @param	transaction	a new transaction
     */
    public void addTransaction(Transaction transaction) {


	// If the transaction is older than an existing transaction then remove all
	// transactions. Put the new transaction with them, sort them all and then
	// add all of the transactions. I.e. we must add the transactions in chronological
	// order to prevent things like selling stock before we have bought it.
	if(countTransactions() > 0 &&
	   ((Transaction)transactions.get(transactions.size() - 1)).compareTo(transaction) > 0) {

	    List allTransactions = new ArrayList(transactions);
	    allTransactions.add(transaction);

	    removeAllTransactions();
	    addTransactions(allTransactions);
	}

	// Otherwise we can just append
	else {
	    // Record history of transactions
	    transactions.add(transaction);

	    // Now update accounts
	    for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
		Account account = (Account)iterator.next();
		
		// Is this account involved in the transaction? If it
		// it is we'll need to update it
		if(account == transaction.getCashAccount() ||
		   account == transaction.getCashAccount2() ||
		   account == transaction.getShareAccount()) {
		    account.transaction(transaction);
		}
            }

            // Update our deposit figure for profit/loss calculation. Convert the
            // currency into the portfolo's default currency if necessary.
            if(transaction.getType() == Transaction.WITHDRAWAL)
                deposits = exchangeRateCache.subtract(transaction.getDate(),
                                                      deposits,
                                                      transaction.getAmount());
            else if(transaction.getType() == Transaction.DEPOSIT)
                deposits = exchangeRateCache.add(transaction.getDate(),
                                                 deposits,
                                                 transaction.getAmount());
	}
    }

    /**
     * Create a clone of this portfolio.
     *
     * @return clone
     */
    public Object clone() {

	// First clone portfolio object
	Portfolio clonedPortfolio = new Portfolio(getName(), getCurrency());

	// Now clone accounts and insert the cloned accounts into
	// the cloned portfolio
        for(Iterator accountIterator = accounts.iterator();
            accountIterator.hasNext();) {

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
	for (Iterator transactionIterator = transactions.iterator();
             transactionIterator.hasNext();) {

	    Transaction transaction =
		(Transaction)transactionIterator.next();
	    Transaction clonedTransaction = (Transaction)transaction.clone();

	    // Adjust share/cash account so it refers to the cloned
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
    public List getAccounts() {
	return accounts;
    }

    /**
     * Return the number of accounts of the given type the portfolio has
     *
     * @param	type	account type, e.g. {@link Account#CASH_ACCOUNT}
     * @return	number of accounts of the given type
     */
    public int countAccounts(int type) {
	int count = 0;

        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
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
     * @param	name the name of the account to search for
     * @return	the account with the same name as given or <code>null</code>
     *		if it could not be found
     */
    public Account findAccountByName(String name) {
        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
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
	    Transaction transaction = (Transaction)transactions.get(0);

	    return transaction.getDate();
	}
	else {
	    return null;
	}
    }

    /**
     * Return the date of the last transaction in this portfolio.
     *
     * @return	date of the last transaction
     */
    public TradingDate getLastDate() {
	if(transactions.size() > 0) {
	    Transaction transaction = (Transaction)transactions.get(transactions.size() - 1);

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
    public List getSymbolsTraded() {
	Set symbolsTraded = new HashSet();

	for (Iterator iterator = transactions.iterator(); iterator.hasNext();) {
	    Transaction transaction = (Transaction)iterator.next();
	    if(transaction.getType() == Transaction.ACCUMULATE)
		symbolsTraded.add(transaction.getSymbol());
	}

	return new ArrayList(symbolsTraded);
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

	for (Iterator iterator = transactions.iterator(); iterator.hasNext();) {
	    Transaction transaction = (Transaction)iterator.next();
	    if(transaction.getType() == type)
		count++;
	}
	
	return count;
    }

    /**
     * Return the transaction history.
     *
     * @return	transaction history
     * @see	Transaction
     */
    public List getTransactions() {
	return transactions;
    }

    /**
     * Remove all transactions from portfolio.
     */
    public void removeAllTransactions() {
	transactions.clear();
        deposits = new Money(currency, 0.0D);

	// A portfolio with no transactions has no value or stock so
	// remove them from accounts
        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
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
    public Money getValue(EODQuoteBundle quoteBundle, TradingDate date)
 	throws MissingQuoteException {
        
        Money value = new Money(currency, 0.0D);
        
        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
 	    Account account = (Account)iterator.next();
            
            // Convert the value into the portoflio's default currency if necessary
 	    value = exchangeRateCache.add(date, value, account.getValue(quoteBundle, date));
        }

        return value;
    }
    
    /**
     * Get the value of the portfolio on the given day. Currently
     * this function should only be called for dates after the last
     * transaction. When it calculates the value it will assume all
     * transactions have taken place.
     *
     * @param	quoteBundle	the quote bundle
     * @param	dateOffset fast date offset
     * @return	the value
     */
     public Money getValue(EODQuoteBundle quoteBundle, int dateOffset)
 	throws MissingQuoteException {

         TradingDate date = EODQuoteCache.getInstance().offsetToDate(dateOffset);
         Money value = new Money(currency, 0.0D);

         for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
 	    Account account = (Account)iterator.next();

            // Convert the value into the default portoflio's currency if necessary
 	    value = exchangeRateCache.add(date, value, account.getValue(quoteBundle, dateOffset));
         }
	
         return value;
     }

    /**
     * Return a list of all the stocks currently held in the portfolio.
     *
     * @return the stock list
     */
    public List getStocksHeld() {
	Set stocksHeld = new HashSet();

        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
	    Account account = (Account)iterator.next();

            if(account.getType() == Account.SHARE_ACCOUNT) {
                ShareAccount shareAccount = (ShareAccount)account;

                stocksHeld.addAll(shareAccount.getStockHoldings().keySet());
            }
        }

        return new ArrayList(stocksHeld);
    }

    /**
     * Get the cash value of the Portfolio on the current day.
     *
     * @param   date the date.
     * @return	the value
     */
    public Money getCashValue(TradingDate date) {
        Money value = new Money(currency, 0.0D);

        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
	    Account account = (Account)iterator.next();

            if(account.getType() == Account.CASH_ACCOUNT) {
                CashAccount cashAccount = (CashAccount)account;

                // Convert the cash value into the default portoflio's currency if necessary
                value = exchangeRateCache.add(date, value, cashAccount.getValue());
            }
	}
	
        return value;
    }

    /**
     * Get the share value of the Portfolio on the current day.
     *
     * @param	quoteBundle	the quote bundle
     * @param	date            the date
     * @return	the value
     */
    public Money getShareValue(EODQuoteBundle quoteBundle, TradingDate date)
	throws MissingQuoteException {
        Money value = new Money(currency, 0.0D);

        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
            Account account = (Account)iterator.next();

            if(account.getType() == Account.SHARE_ACCOUNT)
                // Convert the share value into the default portoflio's currency if necessary
                value = exchangeRateCache.add(date, value, account.getValue(quoteBundle, date));
        }

	return value;
    }

    /**
     * Get the return of the Portfolio on the current day.
     *
     * @param	quoteBundle	the quote bundle
     * @param	date            the date
     * @return	the value
     */
    public Money getReturnValue(EODQuoteBundle quoteBundle, TradingDate date)
	throws MissingQuoteException {

        // The profit loss is calculated as the value of the Portfolio minus
        // the amount of cash deposited in it.
        Money value = getValue(quoteBundle, date);
        value = value.subtract(deposits);
	return value;
    }

    /**
     * Return an iterator that iterates over every day from the date of
     * the first transaction to whenever the user chooses to stop
     * iterating. Each iteration will return the state of the Portfolio
     * on that date. This allows the user to efficiently query a range of
     * historical states of the Portfolio.
     *
     * @return the portfolio iterator
     * @see #getPortfolio(TradingDate)
     */
    public Iterator iterator() {
        return new PortfolioIterator(this);
    }

    /**
     * Returns the state of the Portfolio on the given date. If the
     * date is before the last transaction in the Portfolio, then
     * this function will-reconstruct the Portfolio to how it would
     * have been on that date. This can be slow, so if you want to
     * query a range of dates, the {@link #iterator} method is
     * more efficient.
     *
     * @param date the date to query
     * @return the Portfolio on the given date
     */
    public Portfolio getPortfolio(TradingDate date) {
        // If the date falls after the date of the last transaction
        // then the current portoflio object is correct.
        if(getLastDate() == null || date.compareTo(getLastDate()) >= 0)
            return this;

        // Otherwise we will need to rebuild the portfolio up to the
        // given date. Clone the portfolio, remove all the transactions
        // and then add them back again.
        else {
            Portfolio portfolio = (Portfolio)clone();
            List transactions = new ArrayList(portfolio.getTransactions());
            portfolio.removeAllTransactions();

            for(Iterator iterator = transactions.iterator(); iterator.hasNext();) {
                Transaction transaction = (Transaction)iterator.next();

                // Should we include this transaction?
                if(transaction.getDate().compareTo(date) <= 0)
                    portfolio.addTransaction(transaction);

                // Otherwise we've added all the transactions and can return.
                else
                    break;
            }

            return portfolio;
        }
    }

    /**
     * Compares this portfolio to another.
     *
     * @param object another portfolio
     * @return <code>true</code> if the portfolios are equal; <code>false</code> otherwise
     */
    public boolean equals(Object object) {
        Portfolio portfolio = (Portfolio)object;

        // The accounts, unlike the transactions, are not stored in any order.
        // So to do an array comparision we will need to sort them by name.
        List accounts = getAccounts();
        Collections.sort(accounts);
        List portfolioAccounts = portfolio.getAccounts();
        Collections.sort(portfolioAccounts);

        return(portfolio.getName().equals(getName()) &&
               portfolioAccounts.equals(accounts) &&
               portfolio.getTransactions().equals(getTransactions()) &&
               portfolio.isTransient() == isTransient());
    }

    /**
     * An iterator which iterates through the states of the Portfolio
     * from the date of the first transaction to whenever the caller
     * stops iterating.
     */
    private class PortfolioIterator implements Iterator {

        // Current state of the Portfolio
        private Portfolio iteratorPortfolio;

        // List of transactions in the final Portfolio
        private ListIterator transactionIterator;

        // Current date
        private TradingDate currentDate;

        /**
         * Create a new Portfolio Iterator that iterates over the given
         * portfolio.
         *
         * @param referencePortfolio Portfolio to iterate over
         */
        public PortfolioIterator(Portfolio referencePortfolio) {
            // Create a copy of the portfolio and extract the list of
            // transactions.
            iteratorPortfolio =
                (Portfolio)referencePortfolio.clone();

            // Extract the transactions and get the iterator pointing to the
            // first transaction. The transaction list will be in date order.
            List transactions =
                new ArrayList(iteratorPortfolio.getTransactions());
            iteratorPortfolio.removeAllTransactions();
            transactionIterator = transactions.listIterator();

            // Work out the point we iterate from
            currentDate = referencePortfolio.getStartDate();
        }

        /**
         * Return if we have any more dates to iterator over.
         *
         * @return always returns <code>TRUE</code>
         */
        public boolean hasNext() {
            return true;
        }

        /**
         * This operation makes no sense in the current context.
         *
         * @raises UnsupportedOperationException
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Traverse to the state of the Portfolio on the next trading date.
         * The state of the Portfolio may be identical to the previous state.
         */
        public Object next() {
            while(transactionIterator.hasNext()) {
                Transaction transaction = (Transaction)transactionIterator.next();

                // Has this transaction happened on our given date?
                if(transaction.getDate().compareTo(currentDate) <= 0)
                    iteratorPortfolio.addTransaction(transaction);

                // If it's happened after, we've gone too far! Put it back.
                else {
                    transactionIterator.previous();
                    break;
                }
            }

            currentDate = currentDate.next(1);

            return (Object)iteratorPortfolio;
        }
    }

}
