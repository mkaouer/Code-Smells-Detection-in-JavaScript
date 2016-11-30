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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mov.util.*;
import org.mov.parser.*;
import org.mov.portfolio.*;
import org.mov.quote.*;

public class PaperTrade {

    private final static String CASH_ACCOUNT_NAME = "Cash Account";
    private final static String SHARE_ACCOUNT_NAME = "Share Account";

    private class Environment {
        public QuoteBundle quoteBundle;
        public Portfolio portfolio;
        public CashAccount cashAccount;
        public ShareAccount shareAccount;
        public int startDateOffset;
        public int endDateOffset;

        public Environment(QuoteBundle quoteBundle,
                           String portfolioName,
                           TradingDate startDate,
                           TradingDate endDate,
                           float capital) {

            this.quoteBundle = quoteBundle;

            // First set up a new (transient) portfolio
            portfolio = new Portfolio(portfolioName, true);

            // Add a cash account and a share account
            cashAccount = new CashAccount(CASH_ACCOUNT_NAME);
            shareAccount = new ShareAccount(SHARE_ACCOUNT_NAME);

            portfolio.addAccount(cashAccount);
            portfolio.addAccount(shareAccount);

            // Deposit starting capital into portfolio
            Transaction transaction = 
                Transaction.newDeposit(startDate, capital, cashAccount);

            portfolio.addTransaction(transaction);

            // Now find the fast date offsets
            try {
                startDateOffset = quoteBundle.dateToOffset(startDate);
                endDateOffset = quoteBundle.dateToOffset(endDate);
            }
            catch(WeekendDateException e) {
                assert(false);
                
                startDateOffset = endDateOffset = 0;
            }
        }
    }

    private PaperTrade() {
        // users shouldn't instantiate this class
    }

    private static boolean sell(Environment environment,
				Symbol symbol,
				float tradeCost,
				int day) 
	throws MissingQuoteException {

	// Make sure we have enough money for the trade
	if(environment.cashAccount.getValue() >= tradeCost) {

	    // Get the number of shares we own - we will sell all of them
	    StockHolding stockHolding = environment.shareAccount.get(symbol);
	    int shares = 0;
	    if(stockHolding != null) 
		shares = stockHolding.getShares();

	    // Only sell if we have any!
	    if(shares > 0) {

		// How much are they worth? We sell at the day open price.
		float amount = 
                    shares * environment.quoteBundle.getQuote(symbol, Quote.DAY_OPEN, day);
		TradingDate date = environment.quoteBundle.offsetToDate(day);
		Transaction sell = Transaction.newReduce(date, 
                                                         amount,
							 symbol, 
                                                         shares,
							 tradeCost,
							 environment.cashAccount,
							 environment.shareAccount);
		environment.portfolio.addTransaction(sell);

		return true;
	    }
	}

	return false;
    }

    private static boolean buy(Environment environment,
			       Symbol symbol,
			       float amount,				 
			       float tradeCost,
			       int day) 
	throws MissingQuoteException {


	// Calculate maximum number of shares we can buy with
	// the given amount
	float sharePrice = environment.quoteBundle.getQuote(symbol, Quote.DAY_OPEN, day);
	int shares = 
	    (new Double(Math.floor(amount / sharePrice))).intValue();
	
	// Now calculate the actual amount the shares will cost
	amount = sharePrice * shares;
	
	// Make sure we have enough money for the trade
	if(environment.cashAccount.getValue() >= (tradeCost + amount)) {

	    TradingDate date = environment.quoteBundle.offsetToDate(day);
	    Transaction buy = Transaction.newAccumulate(date, 
                                                        amount,
							symbol, 
                                                        shares,
							tradeCost,
							environment.cashAccount,
							environment.shareAccount);

	    environment.portfolio.addTransaction(buy);
	    return true;
	}
	
	return false;
    }

    private static void sellTrades(Environment environment, 
                                   QuoteBundle quoteBundle,
                                   Variables variables, 
                                   Expression sell,
                                   int dateOffset,
                                   float tradeCost,
                                   List symbols,
                                   OrderComparator orderComparator) 
        throws EvaluationException {

        // Iterate through the stocks we own. Should we sell any of it? 
        List heldSymbols = 
            new ArrayList(environment.shareAccount.getStockHoldings().keySet());
        
        for(Iterator iterator = heldSymbols.iterator(); iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();
            
            // If we care about the order, make sure the "order" variable is set
            if(orderComparator != null) {
                int order = symbols.indexOf(symbol);

                // Symbol not in list? Then ignore
                if (order == -1)
                    continue;

                variables.setValue("order", order);
            }

            variables.setValue("held", getHoldingTime(environment, symbol, dateOffset));

            try {
                if(sell.evaluate(variables, quoteBundle, symbol, 
                                 dateOffset) >= Expression.TRUE)
                    sell(environment, symbol, tradeCost, dateOffset + 1);
            }
            catch(MissingQuoteException e) {
                // ignore and move on
            }
        }
    }

    private static void buyTrades(Environment environment,
                                  QuoteBundle quoteBundle,
                                  Variables variables,
                                  Expression buy,
                                  int dateOffset,
                                  float tradeCost,
                                  List symbols,
                                  OrderComparator orderComparator,
                                  float stockValue) 
        throws EvaluationException {

        variables.setValue("held", 0);

        // If we have enough money, iterate through stocks available today -
        // should we buy any of it?
        if((stockValue + 2 * tradeCost) <= environment.cashAccount.getValue()) {
            int order = 0;

            // Iterate through stocks available today - should we buy or sell any of it?
            for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
                
                Symbol symbol = (Symbol)iterator.next();

                // Skip if we already own it
                if(!environment.shareAccount.isHolding(symbol)) {

                    // If we care about the order, make sure the "order" variable is set
                    if(orderComparator != null)
                        variables.setValue("order", order);

                    try {
                        if(buy.evaluate(variables, quoteBundle, symbol, 
                                        dateOffset) >= Expression.TRUE) {
                            buy(environment, symbol, stockValue,  tradeCost, 
                                dateOffset + 1);
                        
                            // If there is no more money left, don't even look at the
                            // other stocks
                            if((stockValue + 2 * tradeCost) > environment.cashAccount.getValue())
                                break;
                        }
                    }
                    catch(MissingQuoteException e) {
                        // Ignore and move on
                    }
                }

                order++;
            }
        }
    }

    private static int getHoldingTime(Environment environment, Symbol symbol, int dateOffset) {
        int holdingTime = 0;
        StockHolding stockHolding = environment.shareAccount.get(symbol);

        try {              
            holdingTime = 1 -(QuoteCache.getInstance().dateToOffset(stockHolding.getDate()) - 
                              dateOffset);
        }
        catch(WeekendDateException e) {
            assert false;
        }

        return holdingTime;
    }

    public static Portfolio paperTrade(String portfolioName,
                                       QuoteBundle quoteBundle, 
                                       Variables variables,
                                       OrderComparator orderComparator,
                                       TradingDate startDate, 
				       TradingDate endDate,
				       Expression buy,
				       Expression sell,
				       float capital,
                                       float stockValue,
				       float tradeCost) 
        throws EvaluationException {

        // Set up environment for paper trading
        PaperTrade paperTrade = new PaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital);
        int dateOffset = environment.startDateOffset;

        if(orderComparator != null && !variables.contains("order"))
            variables.add("order", Expression.INTEGER_TYPE);
        if(!variables.contains("held"))
            variables.add("held", Expression.INTEGER_TYPE, 0);

        // Now iterate through each trading date and decide whether
	// to buy/sell. The last date is used for placing the previous
	// date's buy/sell orders.
	while(dateOffset < environment.endDateOffset) {

            List symbols = quoteBundle.getSymbols(dateOffset);

            // If we've been asked to trade in a specific order, then order them
            if(orderComparator != null) {
                orderComparator.setDateOffset(dateOffset);
                Collections.sort(symbols, orderComparator);
            }

            sellTrades(environment, quoteBundle, variables, sell, dateOffset, tradeCost, 
                       symbols, orderComparator);
            buyTrades(environment, quoteBundle, variables, buy, dateOffset, tradeCost, 
                      symbols, orderComparator, stockValue);

            dateOffset++;
        }

        return environment.portfolio;
    }

    public static Portfolio paperTrade(String portfolioName,
                                       QuoteBundle quoteBundle, 
                                       Variables variables,
                                       OrderComparator orderComparator,
                                       TradingDate startDate, 
				       TradingDate endDate,
				       Expression buy,
				       Expression sell,
				       float capital,
                                       int numberStocks,
				       float tradeCost) 
        throws EvaluationException {

        // Set up environment for paper trading
        PaperTrade paperTrade = new PaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital);
        int dateOffset = environment.startDateOffset;

        if(orderComparator != null && !variables.contains("order"))
            variables.add("order", Expression.INTEGER_TYPE);
        if(!variables.contains("held"))
            variables.add("held", Expression.INTEGER_TYPE, 0);

        // Now iterate through each trading date and decide whether
	// to buy/sell. The last date is used for placing the previous
	// date's buy/sell orders.
	while(dateOffset < environment.endDateOffset) {
            List symbols = quoteBundle.getSymbols(dateOffset);

            // If we've been asked to trade in a specific order, then order them
            if(orderComparator != null) {
                orderComparator.setDateOffset(dateOffset);
                Collections.sort(symbols, orderComparator);
            }

            sellTrades(environment, quoteBundle, variables, sell, dateOffset, tradeCost, 
                       symbols, orderComparator);

            try {
                float stockValue = ((environment.portfolio.getValue(quoteBundle, dateOffset) /
                                     numberStocks) - (2 * tradeCost));

                buyTrades(environment, quoteBundle, variables, buy, dateOffset, tradeCost, 
                          symbols, orderComparator, stockValue);
            }
            catch(MissingQuoteException e) {
                // Ignore and move on
            }

            dateOffset++;
        }

        return environment.portfolio;
    }
}
