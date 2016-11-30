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
import nz.org.venice.util.Currency;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;

/**
 * Generic interface for all financial account objects. This interface
 * defines some generic properties that all accounts need to have such
 * as name, type and value.
 *
 * @author Andrew Leppard
 */
public interface Account {

    /** Account is a cash account (bank account, cash management account etc)
     */
    public static final int CASH_ACCOUNT = 0;

    /** Account is a share trading account which contains a list of shares */
    public static final int SHARE_ACCOUNT = 1;

    /**
     * Return the name of this account.
     *
     * @return	name of the account
     */
    public String getName();

    /**
     * Return the type of this account.
     *
     * @return	type of the account, either {@link #CASH_ACCOUNT} or
     *		{@link #SHARE_ACCOUNT}
     */
    public int getType();

    /**
     * Return the value of this account on the given day.
     *
     * @param	quoteBundle	the quote bundle
     * @param	dateOffset fast date offset
     */
    public Money getValue(EODQuoteBundle quoteBundle, int dateOffset)
	throws MissingQuoteException;

    /**
     * Return the value of this account on the given day.
     *
     * @param quoteBundle the quote bundle
     * @param date        the date
     */
    public Money getValue(EODQuoteBundle quoteBundle, TradingDate date)
	throws MissingQuoteException;

    /**
     * Return the currency of the account.
     *
     * @return default currency.
     */
    public Currency getCurrency();

    /**
     * Perform a transaction on this account.
     *
     * @param	transaction	transaction to occur
     */
    public void transaction(Transaction transaction);

    /**
     * Remove all transactions from account.
     */
    public void removeAllTransactions();
}

