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

import java.util.*;

import org.mov.util.*;
import org.mov.quote.*;

/** Representation of a cash account in a portfolio.
 */
public class CashAccount implements Account, Cloneable {

    // Amount of cash available
    private float capital;

    private String name;

    /**
     * Create a new cash account.
     *
     * @param	name	the name of the new cash account
     */
    public CashAccount(String name) {
	this.name = name;
	this.capital = 0.0F;
    }

    public void transaction(Transaction transaction) {

	int type = transaction.getType();

	// Update value of account
	if(type == Transaction.WITHDRAWAL ||
	   type == Transaction.FEE) {
	    capital -= transaction.getAmount();
	}
	else if(type == Transaction.DEPOSIT ||
		type == Transaction.INTEREST ||
		type == Transaction.DIVIDEND) {
	    capital += transaction.getAmount();
	}
	else if(type == Transaction.ACCUMULATE) {
	    capital -= (transaction.getAmount() + transaction.getTradeCost());
	}
	else if(type == Transaction.REDUCE) {
	    capital += (transaction.getAmount() - transaction.getTradeCost());
	}
	else if(type == Transaction.TRANSFER) {
	    // Are we transfering to or from this account?
	    if(transaction.getCashAccount() == this) { 
		capital -= transaction.getAmount(); // from
	    }
	    else { 
		capital += transaction.getAmount(); // to
	    }
	}
    }

    public Object clone() {
	CashAccount clonedCashAccount = new CashAccount(getName());

	return clonedCashAccount;
    }

    public String getName() {
	return name;
    }

    public float getValue(QuoteBundle quoteBundle, TradingDate date) {
	return capital;
    }

    /**
     * Return the value of this account. Since the value does not
     * depend on any stock price, the cache and date can be
     * omitted.
     */
    public float getValue() {
	return capital;
    }

    public void removeAllTransactions() {
	capital = 0.0F;
    }

    public int getType() {
	return Account.CASH_ACCOUNT;
    }
}
