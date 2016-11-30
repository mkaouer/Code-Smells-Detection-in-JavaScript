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

import nz.org.venice.quote.Symbol;
import nz.org.venice.util.Locale;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;

/**
 * Representation of a single transaction on the portfolio. A transaction
 * is ANY kind of financial action from trading a stock to receiving
 * credit interest.
 * Transactions are used to build up a portfolio, the user does not
 * enter how many shares they own but rather their share transactions and
 * their current total is calculated from that.
 *
 * @author Andrew Leppard
 */
public class Transaction implements Cloneable, Comparable {

    // Transaction types on any account. Do not change the
    // value mapping! Otherwise the new version won't be
    // backwards compatible.

    /** Withdrawl cash */
    public static final int WITHDRAWAL = 0;

    /** Deposit cash */
    public static final int DEPOSIT = 1;

    /** Credit/Debit interest */
    public static final int INTEREST = 2;

    /** Fee (FID, TAX etc) */
    public static final int FEE = 3;

    /** Accumulate (buy) shares */
    public static final int ACCUMULATE = 4;

    /** Reduce (sell) shares */
    public static final int REDUCE = 5;

    /** Dividend */
    public static final int DIVIDEND = 6;

    /** Dividend DRP (Dividend Reinvestment Programme) */
    public static final int DIVIDEND_DRP = 7;

    /** Transfer money between two cash accounts */
    public static final int TRANSFER = 8;

    // All possible fields for all possible transactions
    private TradingDate date;
    private int type;
    private Money amount;
    private Symbol symbol;
    private int shares;
    private Money tradeCost;
    private CashAccount cashAccount;
    private CashAccount cashAccount2;
    private ShareAccount shareAccount;

    /**
     * Create a new generic transaction.
     *
     * @param	type	type of the transaction, e.g. {@link #INTEREST}
     * @param	date	date the transaction took place
     * @param	amount	the amount/value/cost of the transaction
     * @param	symbol	for share trades and dividends, the symbol traded
     * @param	shares	for share trades and dividend DRPs, the number of
     *			symbols bought/solded/re-invested
     * @param	tradeCost	for shares trades, the cost of the trade
     *			including any GST, Stamp Duty, Taxes etc
     * @param	cashAccount	related cash account (if any)
     * @param	cashAccount2	second related cash account (if any)
     * @param	shareAccount	related share account (if any)
     */
    public Transaction(int type, TradingDate date, Money amount,
		       Symbol symbol, int shares, Money tradeCost,
		       CashAccount cashAccount, CashAccount cashAccount2,
		       ShareAccount shareAccount) {
	this.type = type;
	this.date = date;
	this.amount = amount;
	this.symbol = symbol;
	this.shares = shares;
	this.tradeCost = tradeCost;
	this.cashAccount = cashAccount;
	this.cashAccount2 = cashAccount2;
	this.shareAccount = shareAccount;
    }

    /**
     * Create a new withdrawal transaction. Money has been withdrawn from
     * a {@link CashAccount}.
     *
     * @param	date	date the transaction took place
     * @param	amount	the amount withdrawn
     * @param	account	cash account
     */
    public static Transaction newWithdrawal(TradingDate date,
					    Money amount,
					    CashAccount account) {
	return new Transaction(WITHDRAWAL, date, amount, null, 0,
			       null, account, null, null);
    }

    /**
     * Create a new deposit transaction. Money has been deposited into
     * a {@link CashAccount}.
     *
     * @param	date	date the transaction took place
     * @param	amount	the amount deposited
     * @param	account	cash account
     */
    public static Transaction newDeposit(TradingDate date,
					 Money amount,
					 CashAccount account) {
	return new Transaction(DEPOSIT, date, amount, null, 0,
			       null, account, null, null);
    }

    /**
     * Create a new interest transaction. {@link CashAccount} has
     * received credit interest.
     *
     * @param	date	date the transaction took place
     * @param	amount	the amount deposited
     * @param	account	cash account
     */
    public static Transaction newInterest(TradingDate date,
					  Money amount,
					  CashAccount account) {
	return new Transaction(INTEREST, date, amount, null, 0,
			       null, account, null, null);
    }

    /**
     * Create a new fee transaction. {@link CashAccount} has been
     * debited with some sort of fee.
     *
     * @param	date	date the transaction took place
     * @param	amount	the amount withdrawn
     * @param	account	cash account
     */
    public static Transaction newFee(TradingDate date,
				     Money amount,
				     CashAccount account) {
	return new Transaction(FEE, date, amount, null, 0,
			       null, account, null, null);
    }

    /**
     * Create a new accumulate transaction. Shares have been bought.
     *
     * @param	date	date the transaction took place
     * @param	amount	the cost of the shares (not including trade cost)
     * @param	symbol	the symbol traded
     * @param	shares	number of shares traded
     * @param	tradeCost	for shares trades, the cost of the trade
     *			including any GST, Stamp Duty, Taxes etc
     * @param	shareAccount	share account
     * @param	cashAccount	cash account
     */
    public static Transaction newAccumulate(TradingDate date,
					    Money amount,
					    Symbol symbol,
					    int shares,
					    Money tradeCost,
					    CashAccount cashAccount,
					    ShareAccount shareAccount) {
	return new Transaction(ACCUMULATE, date, amount, symbol, shares,
			       tradeCost, cashAccount, null, shareAccount);
    }

    /**
     * Create a new reduce transaction. Shares have been sold.
     *
     * @param	date	date the transaction took place
     * @param	amount	the value of the shares (not including trade cost)
     * @param	symbol	the symbol traded
     * @param	shares	number of shares traded
     * @param	tradeCost	for shares trades, the cost of the trade
     *			including any GST, Stamp Duty, Taxes etc
     * @param	shareAccount	share account
     * @param	cashAccount	cash account
     */
    public static Transaction newReduce(TradingDate date,
					Money amount,
					Symbol symbol,
					int shares,
					Money tradeCost,
					CashAccount cashAccount,
					ShareAccount shareAccount) {
	return new Transaction(REDUCE, date, amount, symbol, shares,
			       tradeCost, cashAccount, null, shareAccount);
    }

    /**
     * Create a new dividend transaction. A share has paid a dividend.
     *
     * @param	date	date the transaction took place
     * @param	amount	the value of the dividend
     * @param	symbol	the stock paying the dividend
     * @param	shareAccount	share account
     * @param	cashAccount	cash account
     */
    public static Transaction newDividend(TradingDate date,
					  Money amount,
					  Symbol symbol,
					  CashAccount cashAccount,
					  ShareAccount shareAccount) {
	return new Transaction(DIVIDEND, date, amount, symbol, 0,
			       null, cashAccount, null, shareAccount);
    }

    /**
     * Create a new dividend transaction. A share has paid a dividend and
     * the dividend has been re-invested back into the holding, increasing
     * the holding by several shares.
     *
     * @param	date	date the transaction took place
     * @param	symbol	the stock paying the dividend
     * @param	shares	the number of shares gained
     * @param	shareAccount	share account
     */
    public static Transaction newDividendDRP(TradingDate date,
					     Symbol symbol,
					     int shares,
					     ShareAccount shareAccount) {
	return new Transaction(DIVIDEND_DRP, date, null, symbol, shares,
			       null, null, null, shareAccount);
    }

    /**
     * Create a new transfer transaction. A transfer transaction is
     * when cash has been transferred between two cash accounts.
     *
     * @param	date	date the transaction took place
     * @param	amount	the value of the transfer
     * @param	cashAccount	source cash account
     * @param	cashAccount2	destination cash account
     */
    public static Transaction newTransfer(TradingDate date,
					  Money amount,
					  CashAccount cashAccount,
					  CashAccount cashAccount2) {
	return new Transaction(TRANSFER, date, amount, null, 0,
			       null, cashAccount, cashAccount2, null);
    }

    /**
     * Convert between a given transaction type and a text string.
     *
     * @param	type	the transaction type
     * @return	string representation of transaction
     */
    public static String typeToString(int type) {
	String[] typeNames = {Locale.getString("WITHDRAWAL_TRANSACTION"),
			      Locale.getString("DEPOSIT_TRANSACTION"),
			      Locale.getString("INTEREST_TRANSACTION"),
			      Locale.getString("FEE_TRANSACTION"),
			      Locale.getString("ACCUMULATE_TRANSACTION"),
			      Locale.getString("REDUCE_TRANSACTION"),
			      Locale.getString("DIVIDEND_TRANSACTION"),
			      Locale.getString("DIVIDEND_DRP_TRANSACTION"),
			      Locale.getString("TRANSFER_TRANSACTION")};

	if(type < typeNames.length)
	    return typeNames[type];
	else {
	    assert false;
	    return Locale.getString("TRANSFER");
	}
    }

    /**
     * Convert between a transaction type string and its transaction type.
     *
     * @param	type	string representation of the transaction
     * @return	the transaction type
     */
    public static int stringToType(String type) {
	if(type.equals(Locale.getString("ACCUMULATE_TRANSACTION")))
	    return Transaction.ACCUMULATE;
	else if(type.equals(Locale.getString("REDUCE_TRANSACTION")))
	    return Transaction.REDUCE;
	else if(type.equals(Locale.getString("DEPOSIT_TRANSACTION")))
	    return Transaction.DEPOSIT;
	else if(type.equals(Locale.getString("FEE_TRANSACTION")))
	    return Transaction.FEE;
	else if(type.equals(Locale.getString("INTEREST_TRANSACTION")))
	    return Transaction.INTEREST;
	else if(type.equals(Locale.getString("WITHDRAWAL_TRANSACTION")))
	    return Transaction.WITHDRAWAL;
	else if(type.equals(Locale.getString("DIVIDEND_TRANSACTION")))
	    return Transaction.DIVIDEND;
	else if(type.equals(Locale.getString("DIVIDEND_DRP_TRANSACTION")))
	    return Transaction.DIVIDEND_DRP;
	else {
	    assert type.equals(Locale.getString("TRANSFER_TRANSACTION"));
	    return Transaction.TRANSFER;
	}
    }

    /**
     * Compare this transaction to another one. Transactions are compared
     * by date only.
     *
     * @param	object	other transaction object
     * @return	<code>-1</code> if this transaction is before;
     *		<code>0</code> if they fall on the same date;
     *		<code>1</code> if this transaction is after
     */
    public int compareTo(Object object) {
	Transaction transaction = (Transaction)object;

	// Sort transactions based on date
	return(getDate().compareTo(transaction.getDate()));
    }

    /**
     * Compares this transaction to another.
     *
     * @param object another transaction
     * @return <code>true</code> if the transactions are equal; <code>false</code> otherwise
     */
    public boolean equals(Object object) {
        Transaction transaction = (Transaction)object;

        // Test the fields that are always present
        if(!transaction.getDate().equals(getDate()) ||
           transaction.getType() != getType() &&
           transaction.getShares() != getShares())
            return false;

        // The following fields might be null, so check they are either both null or not
        if((transaction.getSymbol() == null) != (getSymbol() == null) ||
           (transaction.getCashAccount() == null) != (getCashAccount() == null) ||
           (transaction.getCashAccount2() == null) != (getCashAccount2() == null) ||
           (transaction.getShareAccount() == null) != (getShareAccount() == null))
            return false;

        // Now check if the fields contain the same value
        if(transaction.getSymbol() != null &&
           !transaction.getSymbol().equals(getSymbol()))
            return false;

        if(transaction.getAmount() != null &&
           !transaction.getAmount().equals(getAmount()))
            return false;

        if(transaction.getTradeCost() != null &&
           !transaction.getTradeCost().equals(getTradeCost()))
            return false;

        if(transaction.getCashAccount() != null &&
           !transaction.getCashAccount().getName().equals(getCashAccount().getName()))
            return false;

        if(transaction.getCashAccount2() != null &&
           !transaction.getCashAccount2().getName().equals(getCashAccount2().getName()))
            return false;

        if(transaction.getShareAccount() != null &&
           !transaction.getShareAccount().getName().equals(getShareAccount().getName()))
            return false;

        // If we got here they are the same
        return true;
    }

    public Object clone() {
	Transaction clonedTransaction =
	    new Transaction(getType(),
			    getDate(),
			    getAmount(),
			    getSymbol(),
			    getShares(),
			    getTradeCost(),
			    getCashAccount(),
			    getCashAccount2(),
			    getShareAccount());
	
	return clonedTransaction;
    }

    /**
     * Convert this transaction to a tab-separated value string.
     *
     * @return	text version
     */
    public String toString() {
	String cashAccountName = "";
	String cashAccountName2 = "";
	String shareAccountName = "";
        String symbol = "-";

	if(getCashAccount() != null)
	    cashAccountName = getCashAccount().getName();

	if(getCashAccount2() != null)
	    cashAccountName2 = getCashAccount2().getName();

	if(getShareAccount() != null)
	    shareAccountName = getShareAccount().getName();
	
        if(getSymbol() != null)
            symbol = getSymbol().toString();

	// Write in CSV format
	// date, type, amount, symbol, shares, tradeCost,
	// cashAccount, shareAccount
	return new String(getDate().toString("dd/mm/yyyy") + "\t" +
			  Transaction.typeToString(getType()) + "\t" +
			  getAmount() + "\t" +
			  symbol + "\t" +
			  getShares() + "\t" +
			  getTradeCost() + "\t" +
			  cashAccountName + "\t" +
			  cashAccountName2 + "\t" +
			  shareAccountName);
    }

    /**
     * Return the type of this transaction.
     *
     * @return	type
     */
    public int getType() {
	return type;
    }

    /**
     * Return the date this transaction occured on.
     *
     * @return	date
     */
    public TradingDate getDate() {
	return date;
    }

    /**
     * Return the amount of this transaction if applicable.
     *
     * @return	amount
     */
    public Money getAmount() {
	return amount;
    }

    /**
     * Return the symbol traded if this transaction was a share transaction.
     *
     * @return	symbol or <code>null</code> if this transaction was not a
     *		share transaction
     */
    public Symbol getSymbol() {
	return symbol;
    }

    /**
     * Return the shares traded if this transaction was a share transaction.
     *
     * @return	number of shares or <code>0</code> if this transaction was
     *		not a share transaction
     */
    public int getShares() {
	return shares;
    }

    /**
     * Return the cost of the trade if this transaction was a share
     * transaction.
     *
     * @return cost of trade or <code>0</code> if this transaction was
     *		not a share transaction
     */
    public Money getTradeCost() {
	return tradeCost;
    }

    /**
     * Return the associated cash account if any.
     *
     * @return	cash account or <code>null</code> if no cash account
     *		associated with this transaction
     * @see	CashAccount
     */
    public CashAccount getCashAccount() {
	return cashAccount;
    }

    /**
     * Return the second associated cash account if any.
     *
     * @return	second cash account or <code>null</code> if only zero or
     *		one cash accounts are associated with this transaction
     * @see	CashAccount
     */
    public CashAccount getCashAccount2() {
	return cashAccount2;
    }

    /**
     * Return the associated share account if any.
     *
     * @return	share account or <code>null</code> if no cash account
     *		associated with this transaction
     * @see	ShareAccount
     */
    public ShareAccount getShareAccount() {
	return shareAccount;
    }

    /**
     * Set the associated cash account.
     *
     * @param	cashAccount the new cash account
     * @see	CashAccount
     */
    public void setCashAccount(CashAccount cashAccount) {
	this.cashAccount = cashAccount;
    }

    /**
     * Set the secondary associated cash account.
     *
     * @param	cashAccount2 the new secondary cash account
     * @see	CashAccount
     */
    public void setCashAccount2(CashAccount cashAccount2) {
	this.cashAccount2 = cashAccount2;
    }

    /**
     * Set the associated share account.
     *
     * @param	shareAccount the new share account
     * @see	ShareAccount
     */
    public void setShareAccount(ShareAccount shareAccount) {
	this.shareAccount = shareAccount;
    }
}

