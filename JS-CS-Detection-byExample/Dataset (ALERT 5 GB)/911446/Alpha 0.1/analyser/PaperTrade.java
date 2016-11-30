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

package org.mov.analyser;

import org.mov.util.*;
import org.mov.parser.*;
import org.mov.portfolio.*;
import org.mov.quote.*;

public class PaperTrade {

    private final static String CASH_ACCOUNT_NAME = "Cash Account";
    private final static String SHARE_ACCOUNT_NAME = "Share Account";

    private static Portfolio createPortfolio(String portfolioName,
					    TradingDate startDate,
					    float capital) {
	Portfolio portfolio = new Portfolio(portfolioName);

	// Add a cash account and a share account
	CashAccount cashAccount = new CashAccount(CASH_ACCOUNT_NAME);
	ShareAccount shareAccount = new ShareAccount(SHARE_ACCOUNT_NAME);

	portfolio.addAccount(cashAccount);
	portfolio.addAccount(shareAccount);

	// Deposit starting capital into portfolio
	Transaction transaction = 
	    Transaction.newDeposit(startDate, capital, cashAccount);

	portfolio.addTransaction(transaction);

	return portfolio;
    }


    private static boolean sell(ScriptQuoteBundle quoteBundle,
				Portfolio portfolio,
				CashAccount cashAccount,
				ShareAccount shareAccount,
				String symbol,
				float tradeCost,
				int day) 
	throws MissingQuoteException {

	// Make sure we have enough money for the trade
	if(cashAccount.getValue() >= tradeCost) {

	    // Get the number of shares we own - we will sell all of them
	    StockHolding stockHolding = shareAccount.get(symbol);
	    int shares = 0;
	    if(stockHolding != null) 
		shares = stockHolding.getShares();

	    // Only sell if we have any!
	    if(shares > 0) {

		// How much are they worth? We sell at the day open price
		float amount = shares * quoteBundle.getQuote(symbol, Quote.DAY_OPEN,
							     day);
		
		TradingDate date = quoteBundle.offsetToDate(day);
		Transaction sell = Transaction.newReduce(date, amount,
							 symbol, shares,
							 tradeCost,
							 cashAccount,
							 shareAccount);
		portfolio.addTransaction(sell);

		return true;
	    }
	}

	return false;
    }

    private static boolean buy(ScriptQuoteBundle quoteBundle,
			       Portfolio portfolio,
			       CashAccount cashAccount,
			       ShareAccount shareAccount,
			       String symbol,
			       float amount,				 
			       float tradeCost,
			       int day) 
	throws MissingQuoteException {


	// Calculate maximum number of shares we can buy with
	// the given amount
	float sharePrice = quoteBundle.getQuote(symbol, Quote.DAY_OPEN, day);
	int shares = 
	    (new Double(Math.floor(amount / sharePrice))).intValue();
	
	// Now calculate the actual amount the shares will cost
	amount = sharePrice * shares;
	
	// Make sure we have enough money for the trade
	if(cashAccount.getValue() >= (tradeCost + amount)) {

	    TradingDate date = quoteBundle.offsetToDate(day);
	    Transaction buy = Transaction.newAccumulate(date, amount,
							symbol, shares,
							tradeCost,
							cashAccount,
							shareAccount);

	    portfolio.addTransaction(buy);

	    return true;
	}
	
	return false;
    }

    public static Portfolio paperTrade(String portfolioName, 
				       ScriptQuoteBundle quoteBundle, String symbol,
				       TradingDate startDate, 
				       TradingDate endDate,
				       Expression buy,
				       Expression sell,
				       float capital,
				       float tradeCost) {

	// First create a portfolio suitable for paper trading
	Portfolio portfolio = createPortfolio(portfolioName,
					      startDate,
					      capital);
	ShareAccount shareAccount = 
	    (ShareAccount)
	    portfolio.findAccountByName(SHARE_ACCOUNT_NAME);
	CashAccount cashAccount = 
	    (CashAccount)
	    portfolio.findAccountByName(CASH_ACCOUNT_NAME);

	int dateOffset; 
	int endDateOffset; 

	try {
	    dateOffset = quoteBundle.dateToOffset(startDate);
	    endDateOffset = quoteBundle.dateToOffset(endDate);
	}
	catch(WeekendDateException e) {
	    assert(false);

	    dateOffset = endDateOffset = 0;
	}

	// This is set when we own the stock
	boolean ownStock = false;

	// Now iterate through each trading date and decide whether
	// to buy/sell. The last date is used for placing the previous
	// date's buy/sell orders.
	while(dateOffset < endDateOffset) {

	    try {		

		// If we own the stock should we sell?
		if(ownStock) {
		    if(sell.evaluate(quoteBundle, symbol, dateOffset) >= Expression.TRUE) {
			if(sell(quoteBundle, portfolio, cashAccount,
				shareAccount, symbol, 
				tradeCost, dateOffset + 1))
			    ownStock = false;
		    }
		}
		
		// If we don't own the stock should we buy?
		else {
		    if(buy.evaluate(quoteBundle, symbol, dateOffset) >= Expression.TRUE) {
			// Spend all our money except for enough to do
			// a buy and a later sell trade
			float amount = cashAccount.getValue() - 2 * tradeCost;

			if(buy(quoteBundle, portfolio, cashAccount, 
			       shareAccount, symbol, amount, 
			       tradeCost, dateOffset + 1))
			    ownStock = true;

		    }
		}
	    }
	    catch(MissingQuoteException e) {
		// quote was missing
	    }

	    catch(EvaluationException e) {
		// Expression didn't evaluate 
	    }

	    // Go to the next trading date
	    dateOffset++;
	}

	return portfolio;
    }


}
