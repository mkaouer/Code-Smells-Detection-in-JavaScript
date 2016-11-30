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

import nz.org.venice.util.Currency;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;

import nz.org.venice.quote.EODQuoteBundle;

/**
 * Representation of a cash account in a portfolio.
 *
 * @author Andrew Leppard
 */
public class CashAccount extends AbstractAccount implements Cloneable {

    // Amount of cash available
    private Money capital;

    // Currency of the cash account.
    private Currency currency;

    // Name of cash account
    private String name;

    /**
     * Create a new cash account with the given currency.
     *
     * @param	name	 the name of the new cash account
     * @param   currency the currency of the cash account
     */
    public CashAccount(String name, Currency currency) {
	this.name = name;
        this.currency = currency;
	this.capital = new Money(currency, 0.0D);
    }

    /**
     * Create a new cash account with the default currency.
     *
     * @param	name	 the name of the new cash account
     */
    public CashAccount(String name) {
        this(name, Currency.getDefaultCurrency());
    }

    public void transaction(Transaction transaction) {
	int type = transaction.getType();

	// Update value of account
	if(type == Transaction.WITHDRAWAL ||
	   type == Transaction.FEE) {
            assert transaction.getAmount().getCurrency().equals(currency);
	    capital = capital.subtract(transaction.getAmount());
	}
	else if(type == Transaction.DEPOSIT ||
		type == Transaction.INTEREST ||
		type == Transaction.DIVIDEND) {
            assert transaction.getAmount().getCurrency().equals(currency);
	    capital = capital.add(transaction.getAmount());
	}
	else if(type == Transaction.ACCUMULATE) {
            assert transaction.getAmount().getCurrency().equals(currency);
            assert transaction.getTradeCost().getCurrency().equals(currency);
	    capital = capital.subtract(transaction.getAmount());
            capital = capital.subtract(transaction.getTradeCost());
	}
	else if(type == Transaction.REDUCE) {
            assert transaction.getAmount().getCurrency().equals(currency);
            assert transaction.getTradeCost().getCurrency().equals(currency);
	    capital = capital.add(transaction.getAmount());
            capital = capital.subtract(transaction.getTradeCost());
	}
	else if(type == Transaction.TRANSFER) {
            assert transaction.getAmount().getCurrency().equals(currency);

	    // Are we transfering to or from this account?
	    if(transaction.getCashAccount() == this) {
		capital = capital.subtract(transaction.getAmount()); // from
	    }
	    else {
		capital = capital.add(transaction.getAmount()); // to
	    }
	}
    }

    /**
     * Create a clone of this account.
     *
     * @return the clone
     */
    public Object clone() {
	CashAccount clonedCashAccount = new CashAccount(getName(), getCurrency());

	return clonedCashAccount;
    }

    /**
     * Compares this cash account to another.
     *
     * @param object another cash account
     * @return <code>true</code> if the cash accounts are equal; <code>false</code> otherwise
     */
    public boolean equals(Object object) {
        if(object instanceof CashAccount) {
            CashAccount account = (CashAccount)object;

            return(account.getName().equals(getName()) &&
                   account.getCurrency().equals(getCurrency()) &&
                   account.getValue().equals(getValue()));
        }
        else
            return false;
    }

    public String getName() {
	return name;
    }

    public Money getValue(EODQuoteBundle quoteBundle, int dateOffset) {
	return capital;
    }

    public Money getValue(EODQuoteBundle quoteBundle, TradingDate date) {
	return capital;
    }

    /**
     * Return the value of this account. Since the value does not
     * depend on any stock price, the cache and date can be
     * omitted.
     */
    public Money getValue() {
	return capital;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void removeAllTransactions() {
	capital = new Money(currency, 0.0D);
    }

    public int getType() {
	return Account.CASH_ACCOUNT;
    }
}
