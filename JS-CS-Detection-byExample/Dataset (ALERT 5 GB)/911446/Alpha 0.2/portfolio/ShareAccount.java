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

/** Representation of a share account in a portfolio.
 */
public class ShareAccount implements Account, Cloneable {
    
    // Current stock holdings
    private HashMap stockHoldings = new HashMap();

    // Name of share portfolio
    private String name;

    /**
     * Create a new share account.
     *
     * @param	name	the name of the new share account
     */
    public ShareAccount(String name) {
	this.name = name;
    }

    public Object clone() {
	ShareAccount clonedShareAccount = new ShareAccount(getName());

	return clonedShareAccount;
    }

    public String getName() {
	return name;
    }

    public float getValue(QuoteBundle quoteBundle, int dateOffset)
	throws MissingQuoteException {
 
	Set set = stockHoldings.keySet();
	Iterator iterator = set.iterator();
	float value = 0;

	while(iterator.hasNext()) {
	    Symbol symbol = (Symbol)iterator.next();
	    StockHolding holding = (StockHolding)stockHoldings.get(symbol);

	    value += quoteBundle.getQuote(holding.getSymbol(), 
					  Quote.DAY_CLOSE, dateOffset) *
		holding.getShares();
	}
	
	return value;
    }

    public void transaction(Transaction transaction) {
	
	Symbol symbol = transaction.getSymbol();
	int shares = transaction.getShares();
	int type = transaction.getType();

	// Get current holding in this stock
	StockHolding holding = 
	    (StockHolding)stockHoldings.get(symbol);

	if(type == Transaction.ACCUMULATE ||
	   type == Transaction.DIVIDEND_DRP) {
            assert shares > 0;

            float averageCost = (transaction.getAmount() / 
                                 transaction.getShares());

	    // Do we already own the stock? If so accumulate
	    if(holding != null)
		holding.accumulate(shares, averageCost);
	    else // otherwise add new stock to portfolio
		stockHoldings.put(symbol, new StockHolding(symbol, shares, averageCost,
                                                           transaction.getDate()));
	}
	else if(type == Transaction.REDUCE) {
	    // We shouldnt be selling stuff we don't own
	    assert holding != null;
            assert shares > 0;
	    
	    holding.reduce(shares);
	    
	    // do we have any left? if not remove stock holding from
	    // holdings
	    if(holding.getShares() <= 0)
		stockHoldings.remove(symbol);
	}
    }

    /**
     * Return the stock holding for a given symbol.
     *
     * @param	symbol	the stock symbol
     * @return	stock holding for the symbol or <code>null</code> if we
     *		do not own any
     */
    public StockHolding get(Symbol symbol) {
	return (StockHolding)stockHoldings.get(symbol);
    }

    /**
     * Return whether the account is holding the current symbol.
     *
     * @param symbol the stock symbol
     * @return <code>TRUE</code> if we are holding the symbol; <code>FALSE</code> otherwise
     */
    public boolean isHolding(Symbol symbol) {
        return stockHoldings.containsKey(symbol);
    }

    /**
     * Return all the stock holdings for this share account.
     *
     * @return	all stock holdings
     */
    public HashMap getStockHoldings() {
	return stockHoldings;
    }

    public int getType() {
	return Account.SHARE_ACCOUNT;
    }

    public void removeAllTransactions() {
	// Removing all transactions means removing all stocks
	// from our account
	stockHoldings.clear();
    }

    /**
     * Return the number of stock holdings in this share account.
     *
     * @return	number of stocks ownwed
     */
    public int size() {
	return stockHoldings.size();
    }
}
