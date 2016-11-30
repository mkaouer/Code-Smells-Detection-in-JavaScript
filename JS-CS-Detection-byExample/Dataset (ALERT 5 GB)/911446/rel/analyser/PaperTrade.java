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

package nz.org.venice.analyser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.org.venice.util.Locale;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.ExpressionFactory;
import nz.org.venice.parser.Variable;
import nz.org.venice.parser.Variables;
import nz.org.venice.parser.ImplicitVariables;
import nz.org.venice.portfolio.CashAccount;
import nz.org.venice.portfolio.Portfolio;
import nz.org.venice.portfolio.ShareAccount;
import nz.org.venice.portfolio.StockHolding;
import nz.org.venice.portfolio.Transaction;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.EODQuoteCache;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.WeekendDateException;

/**
 * Paper trades stocks using historical quote data and buy and sell indicators.
 * Paper or back trading is a good way of testing the effectiveness of
 * indicators without risking money. This class allows the user to
 * supply historical quote data and buy and sell indicators.
 * The class will then trade using the given indicators and return the
 * final portfolio.
 *
 * <p>The final portfolio will contain a single cash and a single share account.
 *
 * @author Andrew Leppard
 */
public class PaperTrade {

    // Generic name to call all the cash accounts in all generated portfolios
    protected final static String CASH_ACCOUNT_NAME = Locale.getString("CASH_ACCOUNT");

    // Generic name to call all the share accounts in all generated portfolios
    protected final static String SHARE_ACCOUNT_NAME = Locale.getString("SHARE_ACCOUNT");
    
    // Stocks per line for the tip()
    public final static int STOCKS_PER_LINES = 1;
    
    // Information to get the next day trading prices
    protected static String[] symbolStock;
    protected static boolean[] buyRule;
    protected static boolean[] sellRule;
    protected static double[] buyValue;
    protected static double[] sellValue;

    // Since this process uses so many temporary variables, it makes sense
    // grouping them all together.
    protected class Environment {

        // Direct access to quote cache to avoid calling getInstance() method
        public EODQuoteCache quoteCache;

        // Historical quote data
        public EODQuoteBundle quoteBundle;

        // Current portfolio
        public Portfolio portfolio;

        // Direct reference to portfolio's only cash account
        public CashAccount cashAccount;

        // Direct reference to portfolio's only share account
        public ShareAccount shareAccount;

        // Start date of paper trading
        public int startDateOffset;

        // Last date of paper trading
        public int endDateOffset;
        
        // The rule getting the buy price
        protected String tradeValueBuy;
        
        // The rule getting the sell price
        protected String tradeValueSell;

        /**
         * Create a new environment for paper trading.
         *
         * @param quoteBundle the historical quote data
         * @param portfolioName the name of the portfolio
         * @param startDate start date of trading
         * @param endDate last date of trading
         * @param capital initial capital for trading
         */
        public Environment(EODQuoteBundle quoteBundle,
                           String portfolioName,
                           TradingDate startDate,
                           TradingDate endDate,
                           Money capital,
                           String tradeValueBuy,
                           String tradeValueSell) {

            this.quoteBundle = quoteBundle;
            this.quoteCache = EODQuoteCache.getInstance();
            
            this.tradeValueBuy = tradeValueBuy;
            this.tradeValueSell = tradeValueSell;
            
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

    // Users shouldn't instantiate this class
    protected PaperTrade() {
        // nothing to do
    }

    /**
     * Attempt to sell the given stock holding. If we could not meet the trade
     * cost, the stock will not be sold.
     *
     * @param environment the paper trade environment
     * @param stockHolding the stock holding to sell
     * @param tradeCost the cost of a trade
     * @param day date of trade
     */
    protected static void sell(Environment environment,
                             Variables variables,
                             StockHolding stockHolding,
                             Money tradeCost,
                             double sellPrice,
                             int day)
	throws EvaluationException, MissingQuoteException {

	// Make sure we have enough money for the trade
	if(environment.cashAccount.getValue().isGreaterThanEqual(tradeCost)) {

	    // Get the number of shares we own - we will sell all of them
	    int shares = stockHolding.getShares();
            Symbol symbol = stockHolding.getSymbol();

            // If the sellPrice is zero, buy at open price.
            if (sellPrice==0) {
                sellPrice = environment.quoteBundle.getQuote(symbol, Quote.DAY_OPEN, day);
            }
            // If the wished price is lower than the maximum of the day,
            // your stocks will be sold.
            // It simulates an order of selling at fixed price (sellPrice).
            if (sellPrice<=environment.quoteBundle.getQuote(symbol, Quote.DAY_HIGH, day)) {
                Money amount =
                    new Money(shares * sellPrice);
                TradingDate date = environment.quoteBundle.offsetToDate(day);
                Transaction sell = Transaction.newReduce(date,
                                                         amount,
                                                         symbol,
                                                         shares,
                                                         tradeCost,
                                                         environment.cashAccount,
                                                         environment.shareAccount);
                environment.portfolio.addTransaction(sell);
            }
	}
    }

    /**
     * Attempt to buy the given symbol.
     *
     * @param environment the paper trade environment
     * @param symbol the stock to buy
     * @param amount the amount to spend on the stock
     * @param tradeCost the cost of a trade (not including the stock price)
     * @param day date of trade
     * @return <code>true</code> if we had enough money to acquire the stock.
     */
    protected static boolean buy(Environment environment,
                               Variables variables,
                               Symbol symbol,
                               Money amount,				
                               Money tradeCost,
                               double buyPrice,
                               int day)
	throws EvaluationException, MissingQuoteException {

        // If the buyPrice is zero, buy at open price.
        if (buyPrice==0) {
            buyPrice = environment.quoteBundle.getQuote(symbol, Quote.DAY_OPEN, day);
        }
        // If the wished price is greater than the minimum of the day,
        // your stocks will be bought.
        // It simulates an order of buying at fixed price (buyPrice).
        if (buyPrice>=environment.quoteBundle.getQuote(symbol, Quote.DAY_LOW, day)) {
            // Calculate maximum number of shares we can buy with the given amount
            double sharePrice = buyPrice;
            int shares = (int)Math.floor(amount.doubleValue() / sharePrice);

            if(shares > 0) {
                // Now calculate the actual amount the shares will cost
                amount = new Money(sharePrice * shares);

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
        }

        return false;
    }

    /**
     * Iterate through our stock holdings on the given date and decide
     * whether to sell any stock.
     *
     * @param environment the paper trade environment
     * @param quoteBundle the historical quote data
     * @param variables any Gondola variables set
     * @param sell the sell indicator
     * @param buy the buy indicator
     * @param dateOffset date to examine
     * @param tradeCost the cost of a trade
     * @param symbols ordered list of symbols on that date
     * @param orderCache cache of ordered symbols
     */
    private static void sellTrades(Environment environment,
                                   EODQuoteBundle quoteBundle,
                                   Variables variables,
                                   Expression sell,
                                   Expression buy,
                                   int dateOffset,
                                   Money tradeCost,
                                   List symbols,
                                   OrderCache orderCache)
        throws EvaluationException {

        // Iterate through our stock holdings and see if we should sell any
        List stockHoldings = new ArrayList(environment.shareAccount.getStockHoldings().values());

        for(Iterator iterator = stockHoldings.iterator(); iterator.hasNext();) {
            StockHolding stockHolding = (StockHolding)iterator.next();
            Symbol symbol = stockHolding.getSymbol();

            // If we care about the order, make sure the "order" variable is set.
            if(orderCache.isOrdered()) {
                int order = symbols.indexOf(symbol);

                // It's possible that we don't have a quote for the symbol today.
                // So skip it.
                if(order == -1)
                    continue;
                variables.setValue("order", order);
            }

            variables.setValue("held", getHoldingTime(environment, stockHolding, dateOffset));

            variables.setValue("stockcapital", getStockCapital(environment, stockHolding, symbol, dateOffset));

            try {
                // If you want to buy the stock, do not sell it.
                if(!(buy.evaluate(variables, quoteBundle, symbol, dateOffset) >= Expression.TRUE)) {
                    if(sell.evaluate(variables, quoteBundle, symbol, dateOffset) >= Expression.TRUE) {
                        // calculate the price wanted by user trade value expression
                        // to sell the stock (tradeValueWanted).
                        // If trade value expression is 'open', then
                        // set tradeValueWanted = 0 and sell at open price.
                        double tradeValueWanted = 0;
                        if(!environment.tradeValueSell.equals("open")) {
                            Expression tradeValueSellExpression = ExpressionFactory.newExpression(environment.tradeValueSell);
                            tradeValueWanted = tradeValueSellExpression.evaluate(variables, environment.quoteBundle, symbol, dateOffset);
                        }

                        // Did we have enough money to buy at least one share?
                        // Will the stock reach the price wanted (tradeValueWanted)?
                        sell(environment, variables, stockHolding, tradeCost, tradeValueWanted, dateOffset + 1);
                    }
                }
            }
            catch(MissingQuoteException e) {
                // ignore and move on
            }
            //catch(EvaluationException e) {
                // Ignore and move on
            //}
        }
    }

    /**
     * Iterate through all the stocks on the market on the given date and
     * decide whether to buy any stock.
     *
     * @param environment the paper trade environment
     * @param quoteBundle the historical quote data
     * @param variables any Gondola variables set
     * @param buy the buy indicator
     * @param dateOffset date to examine
     * @param tradeCost the cost of a trade
     * @param symbols ordered list of symbols on that date
     * @param orderCache cache of ordered symbols
     * @param stockValue amount of money to spend on stock
     */
    private static void buyTrades(Environment environment,
                                  EODQuoteBundle quoteBundle,
                                  Variables variables,
                                  Expression buy,
                                  int dateOffset,
                                  Money tradeCost,
                                  List symbols,
                                  OrderCache orderCache,
                                  Money stockValue)
        throws EvaluationException {

        variables.setValue("held", 0);
        variables.setValue("stockcapital", 0);

        // If we have enough money, iterate through stocks available today -
        // should we buy any of it?
        Money cash = environment.cashAccount.getValue();

        if(stockValue.add(tradeCost.multiply(2)).isLessThanEqual(cash)) {
            int order = 0;

            // Iterate through stocks available today - should we buy or sell any of it?
            for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
                Symbol symbol = (Symbol)iterator.next();

                // Skip if we already own it
                if(!environment.shareAccount.isHolding(symbol)) {

                    // If we care about the order, make sure the "order" variable is set
                    if(orderCache.isOrdered())
                        variables.setValue("order", order);

                    try {
                        if(buy.evaluate(variables, quoteBundle, symbol,
                                        dateOffset) >= Expression.TRUE) {

                            // calculate the price wanted by user trade value expression
                            // to buy the stock (tradeValueWanted).
                            // If trade value expression is 'open', then
                            // set tradeValueWanted = 0 and buy at open price.
                            double tradeValueWanted = 0;
                            if(!environment.tradeValueBuy.equals("open")) {
                                Expression tradeValueBuyExpression = ExpressionFactory.newExpression(environment.tradeValueBuy);
                                tradeValueWanted = tradeValueBuyExpression.evaluate(variables, environment.quoteBundle, symbol, dateOffset);
                            }
                            
                            // Did we have enough money to buy at least one share?
                            // Will the stock reach the price wanted (tradeValueWanted)?
                            if(buy(environment, variables, symbol, stockValue,  tradeCost,
                                   tradeValueWanted, dateOffset + 1)) {

                                // If there is no more money left, don't even look at the
                                // other stocks
                                cash = environment.cashAccount.getValue();

                                if(stockValue.add(tradeCost.multiply(2)).isGreaterThan(cash))
                                    break;
                            }
                        }
                    }
                    catch(MissingQuoteException e) {
                        // Ignore and move on
                    }
                    //catch(EvaluationException e) {
                        // Ignore and move on
                    //}
                }

                order++;
            }
        }
    }

    /**
     * Return the number of days we have held the given stock.
     *
     * @param environment the paper trade environment
     * @param stockHolding to query
     * @param dateOffset current date
     * @return the holding time
     */
    protected static int getHoldingTime(Environment environment, StockHolding stockHolding,
                                      int dateOffset) {
        try {
            return (1 - (EODQuoteCache.getInstance().dateToOffset(stockHolding.getDate()) -
                         dateOffset));

        }
        catch(WeekendDateException e) {
            assert false;
            return 0;
        }
    }


    /**
     * Return the actual capital of the given stock.
     *
     * @param environment the paper trade environment
     * @param stockHolding to query
     * @param dateOffset current date
     * @param symbol the stock of whom you want to know the capital owned
     * @return the actual capital of the given stock
     */
    protected static double getStockCapital(Environment environment, StockHolding stockHolding,
                                      Symbol symbol, int dateOffset) {
        double retValue = 0.0D;
        try {
                // Set the actual value of capital for the stock
                int shares = stockHolding.getShares();
                double price = environment.quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, dateOffset);
                retValue = (double)price*shares;
        }
        catch(MissingQuoteException e) {	    
            // do nothing	    
            // Can't do nothing unless num shares == 0, 
	    // otherwise stockcapital will = 0

	    int shares = stockHolding.getShares();
	    
	    try {
		if (shares > 0) {
		    double price = environment.quoteBundle.getNearestQuote(symbol, Quote.DAY_CLOSE, dateOffset);
		    retValue = (double)price*shares;
		} 
	    } catch (MissingQuoteException mqe) {
		//No idea what to do here
	    }
        }
        finally {
        }
	return retValue;
    }


    /**
     * Return the actual capital of the portfolio.
     *
     * @param portfolio the paper trade portfolio
     * @param quoteBundle historical quote data
     * @param dateOffset current date
     * @return the actual capital of the portfolio
     */
    protected static double getCapital(Portfolio portfolio, EODQuoteBundle quoteBundle, int dateOffset) {
        double retValue = 0.0D;
        try {
            retValue = portfolio.getValue(quoteBundle, dateOffset).doubleValue();
        }
        catch(MissingQuoteException e) {
            // Ignore and move on
        }
        finally {
        }
	return retValue;
    }


    /**
     * Perform paper trading using a fixed stock value. This method will try to keep
     * the value of each stock holding equal to <code>stockValue</code>.
     *
     * @param portfolioName name to call portfolio
     * @param quoteBundle historical quote data
     * @param variables any Gondola variables set
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param buy the buy indicator
     * @param sell the sell indicator
     * @param capital initial capital in the portfolio
     * @param stockValue the rough value of each stock holding
     * @param tradeCost the cost of a trade
     * @return the portfolio at the close of the last day's trade
     */
    public static Portfolio paperTrade(String portfolioName,
                                       EODQuoteBundle quoteBundle,
                                       Variables variables,
                                       OrderCache orderCache,
                                       TradingDate startDate,
				       TradingDate endDate,
				       Expression buy,
				       Expression sell,
				       Money capital,
                                       Money stockValue,
				       Money tradeCost,
                                       String tradeValueBuy,
                                       String tradeValueSell)
        throws EvaluationException {

        // Set up environment for paper trading
        PaperTrade paperTrade = new PaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital,
                                                             tradeValueBuy,
                                                             tradeValueSell);
        int dateOffset = environment.startDateOffset;
	
	// Paper Trading variables
	ImplicitVariables.getInstance().setup(variables, orderCache.isOrdered());
        
        // daysfromstart
        int daysRest = (int)(-1) * dateOffset;

        // Now iterate through each trading date and decide whether
	// to buy/sell. The last date is used for placing the previous
	// date's buy/sell orders.
	while(dateOffset < environment.endDateOffset) {
            
            // Set the value of days elapsed from the begin of the Paper Trade process
	    int daysFromStart = daysRest + dateOffset;
            variables.setValue("daysfromstart", daysFromStart);
	    
	    // days from last transaction, if no such transaction, 
	    // the daysfromstart value should be safe to use.
	    TradingDate dateOfLastTransaction = environment.portfolio.getLastDate();
	    
	    int daysLastTrn = (dateOfLastTransaction != null) ? daysFromStart - dateOfLastTransaction.getDifference(startDate) : daysFromStart;

	    variables.setValue("daysfromlastransaction", daysLastTrn);
            
            // Set the value of the number of transactions done until now
            variables.setValue("transactions", environment.portfolio.countTransactions());
            
            // Set the value of actual capital
            variables.setValue("capital", getCapital(environment.portfolio,
                    environment.quoteBundle, dateOffset));
            
            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffset);

	    int origTransactionCount = environment.portfolio.countTransactions();

            sellTrades(environment, quoteBundle, variables, sell, buy, dateOffset, tradeCost,
                       symbols, orderCache);
	    
	    resetDaysLastVar(environment, variables, origTransactionCount, daysLastTrn);

            buyTrades(environment, quoteBundle, variables, buy, dateOffset, tradeCost,
                      symbols, orderCache, stockValue);

            dateOffset++;
        }

        // Set the tip for the next day
        setTip(environment, quoteBundle, variables, buy, sell, dateOffset, tradeCost,
                  orderCache.getTodaySymbols(dateOffset), orderCache);
        
        return environment.portfolio;
    }

    /**
     * Perform paper trading keeping the number of stocks in the portfolio roughly constant.
     * This method will try to keep the number of stocks in the portfolio roughly equal
     * to <code>numberStocks</code>, and will try to have all of them at roughly the same value.
     *
     * @param portfolioName name to call portfolio
     * @param quoteBundle historical quote data
     * @param variables any Gondola variables set
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param buy the buy indicator
     * @param sell the sell indicator
     * @param capital initial capital in the portfolio
     * @param numberStocks try to keep this number of stocks in the portfolio
     * @param tradeCost the cost of a trade
     * @return the portfolio at the close of the last day's trade
     */
    public static Portfolio paperTrade(String portfolioName,
                                       EODQuoteBundle quoteBundle,
                                       Variables variables,
                                       OrderCache orderCache,
                                       TradingDate startDate,
				       TradingDate endDate,
				       Expression buy,
				       Expression sell,
				       Money capital,
                                       int numberStocks,
				       Money tradeCost,
                                       String tradeValueBuy,
                                       String tradeValueSell)
        throws EvaluationException {

        // Set up environment for paper trading
        PaperTrade paperTrade = new PaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital,
                                                             tradeValueBuy,
                                                             tradeValueSell);
        int dateOffset = environment.startDateOffset;

        // Paper Trading variables
	ImplicitVariables.getInstance().setup(variables, orderCache.isOrdered());
        
        // daysfromstart
        int daysRest = (int)(-1) * dateOffset;
        
        // Now iterate through each trading date and decide whether
	// to buy/sell. The last date is used for placing the previous
	// date's buy/sell orders.
	while(dateOffset < environment.endDateOffset) {

            // Set the value of days elapsed from the begin of the Paper Trade process
	    int daysFromStart = daysRest + dateOffset;
	    
            variables.setValue("daysfromstart", daysFromStart);
	
	    // days from last transaction, if no such transaction, 
	    // the daysfromstart value should be safe to use.
	    TradingDate dateOfLastTransaction = environment.portfolio.getLastDate();
	    
	    int daysLastTrn = (dateOfLastTransaction != null) ? daysFromStart - dateOfLastTransaction.getDifference(startDate) : daysFromStart;
	    
	    variables.setValue("daysfromlasttransaction", daysLastTrn);
            
            // Set the value of the number of transactions done until now
            variables.setValue("transactions", environment.portfolio.countTransactions());
            
            // Set the value of actual capital
            variables.setValue("capital", getCapital(environment.portfolio,
                    environment.quoteBundle, dateOffset));
            
            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffset);

	    int origTransactionCount = environment.portfolio.countTransactions();

            sellTrades(environment, quoteBundle, variables, sell, buy, dateOffset, tradeCost,
                       symbols, orderCache);
	    
	    resetDaysLastVar(environment, variables, origTransactionCount, daysLastTrn);

	    daysLastTrn = (int)variables.getValue("daysfromlasttransaction");

            try {
                // stockValue = (portfolio / numberStocks) - (2 * tradeCost)
                Money portfolioValue = environment.portfolio.getValue(quoteBundle, dateOffset);
                Money stockValue =
                    portfolioValue.divide(numberStocks).subtract(tradeCost.multiply(2));

		buyTrades(environment, quoteBundle, variables, buy, dateOffset, tradeCost,
			  symbols, orderCache, stockValue);
		
            }
            catch(MissingQuoteException e) {
                // Ignore and move on
            }

            dateOffset++;
        }

        // Set the tip for the next day
        setTip(environment, quoteBundle, variables, buy, sell, dateOffset, tradeCost,
                  orderCache.getTodaySymbols(dateOffset), orderCache);
        
        return environment.portfolio;
    }

    /**
     * Set the information for the tip of the next day.
     */
    private static void setTip(Environment environment,
                                  EODQuoteBundle quoteBundle,
                                  Variables variables,
                                  Expression buy,
                                  Expression sell,
                                  int dateOffset,
                                  Money tradeCost,
                                  List symbols,
                                  OrderCache orderCache) {
                                      
        symbolStock = new String[symbols.size()];
        buyRule = new boolean[symbols.size()];
        sellRule = new boolean[symbols.size()];
        buyValue = new double[symbols.size()];
        sellValue = new double[symbols.size()];
        
        setSellTip(environment, quoteBundle, variables, sell, dateOffset,
                    tradeCost, symbols, orderCache);
        
        setBuyTip(environment, quoteBundle, variables, buy, dateOffset,
                    tradeCost, symbols, orderCache);
        
    }

    private static void setSellTip(Environment environment,
                                   EODQuoteBundle quoteBundle,
                                   Variables variables,
                                   Expression sell,
                                   int dateOffset,
                                   Money tradeCost,
                                   List symbols,
                                   OrderCache orderCache) {
        
        // Count the sell tip for the next day
                                      
        int order = 0;

        int index = 0;
        
        // Iterate through stocks available today - should we sell any of it?
        for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();

            // If we care about the order, make sure the "order" variable is set.
            if(orderCache.isOrdered()) {
                order = symbols.indexOf(symbol);

                // It's possible that we don't have a quote for the symbol today.
                // So skip it.
                if(order == -1)
                    continue;
                variables.setValue("order", order);
            }

            // Check if the stock is hold, so that held variable is set.
            List stockHoldings = new ArrayList(environment.shareAccount.getStockHoldings().values());
            for(Iterator iteratorHolding = stockHoldings.iterator(); iteratorHolding.hasNext();) {
                StockHolding stockHolding = (StockHolding)iteratorHolding.next();
                Symbol symbolHolding = stockHolding.getSymbol();
                if (symbolHolding.toString().equals(symbol.toString())) {
                    variables.setValue("held", getHoldingTime(environment, stockHolding, dateOffset));
                    variables.setValue("stockcapital", getStockCapital(environment, stockHolding, symbol, dateOffset));
                    break;
                } else {
                    variables.setValue("held", 0);
                    variables.setValue("stockcapital", 0.0D);
                }
            }

            try {
                // Get if the stock must be sold
                sellRule[index] = (sell.evaluate(variables, quoteBundle, symbol, dateOffset) >= Expression.TRUE);
                        
                // calculate the price wanted by user trade value expression
                // to sell the stock (tradeValueWanted).
                // If trade value expression is 'open', then
                // set the price to zero (sell at open price).
                sellValue[index] = 0;
                if(!environment.tradeValueSell.equals("open")) {
                    Expression tradeValueSellExpression = ExpressionFactory.newExpression(environment.tradeValueSell);
                    sellValue[index] = tradeValueSellExpression.evaluate(variables, environment.quoteBundle, symbol, dateOffset);
                }
           }
            catch(EvaluationException e) {
                // do nothing
            }
            finally {
                index++;
            }
        }
    }
    
    private static void setBuyTip(Environment environment,
                                  EODQuoteBundle quoteBundle,
                                  Variables variables,
                                  Expression buy,
                                  int dateOffset,
                                  Money tradeCost,
                                  List symbols,
                                  OrderCache orderCache) {
                                      
        // Count the buy tip for the next day
        variables.setValue("held", 0);
        
        variables.setValue("stockcapital", 0.0D);

        int order = 0;
        
        int index = 0;
        
        // Iterate through stocks available today
        for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();

            symbolStock[index] = new String(symbol.get());
            
            // If we care about the order, make sure the "order" variable is set
            if(orderCache.isOrdered())
                variables.setValue("order", order);

            try {
                // Get if the stock must be bought
                buyRule[index] = (buy.evaluate(variables, quoteBundle, symbol,
                            dateOffset) >= Expression.TRUE);
                
                // If you own the stock and both sell and buy rule fire,
                // you wouldn't sell it, neither would you buy it.
                // So it is necessary set the buyRule and sellRule to false.
                //if(environment.shareAccount.isHolding(symbol) && sellRule[index] && buyRule[index]) {
                //    sellRule[index] = false;
                //    buyRule[index] = false;
                //}

                // calculate the price wanted by user trade value expression
                // to buy the stock (tradeValueWanted).
                // If trade value expression is 'open', then
                // set this price to zero (buy at open price).
                buyValue[index] = 0;
                if(!environment.tradeValueBuy.equals("open")) {
                    Expression tradeValueBuyExpression = ExpressionFactory.newExpression(environment.tradeValueBuy);
                    buyValue[index] = tradeValueBuyExpression.evaluate(variables, environment.quoteBundle, symbol, dateOffset);
                }
          }
            catch(EvaluationException e) {
                // do nothing
            }
            finally {
                index++;
            }

            order++;
        }
    }
    
    /**
     * Return a string representing the tip for next day trading.
     * The method can be called after a paperTrade one, so doing
     * it obtains a tip for next day trading, where next is the date
     * folowing the end date of the trading period of paperTrade.
     *
     * @return the string representing the tip.
     */
    public static String getTip() {
        // tip() format for output numbers
        NumberFormat format;
        
        format = NumberFormat.getInstance();
        format.setMinimumIntegerDigits(1);
        int minDecimalDigits = PreferencesManager.getMinDecimalDigits();
        int maxDecimalDigits = PreferencesManager.getMaxDecimalDigits();
        format.setMinimumFractionDigits(minDecimalDigits);
        format.setMaximumFractionDigits(maxDecimalDigits);

        StringBuffer retValue = new StringBuffer();
        int found = 0;
        
        retValue.append(Locale.getString("BUY_STOCKS"));
        
        for (int i=0; i<symbolStock.length; i++) {

            if (buyRule[i]) {
                if (found%STOCKS_PER_LINES==0) {
                    retValue.append("\n");
                } else {
                    retValue.append(", ");
                }
                
                retValue.append(symbolStock[i]);
                
                if (buyValue[i]!=0)
                    retValue.append(" (@ " + format.format(buyValue[i]) + ")");
                
                found++;
            }
            
        }
        
        retValue.append("\n\n");

        found = 0;
        retValue.append(Locale.getString("SELL_STOCKS"));
            
        for (int i=0; i<symbolStock.length; i++) {
            
            if (sellRule[i]) {
                if (found%STOCKS_PER_LINES==0) {
                    retValue.append("\n");
                } else {
                    retValue.append(", ");
                }
                
                retValue.append(symbolStock[i]);
                
                if (sellValue[i]!=0)
                    retValue.append(" (@ " + format.format(sellValue[i]) + ")");
                
                found++;
           }
            
       }
        
        retValue.append("\n");
        
        return retValue.toString();
    }

    private static void resetDaysLastVar(Environment environment, Variables variables, int origTrnCount, int lastTrnOffset) {
	int newTransactionCount = 
	    environment.portfolio.countTransactions() -
	    origTrnCount;

	if (newTransactionCount > 0) {
	    variables.setValue("daysfromlasttransaction", 
			       0);	    
	}
    }
}
