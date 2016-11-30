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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import nz.org.venice.analyser.ann.ArtificialNeuralNetwork;
import nz.org.venice.util.Locale;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.ExpressionFactory;
import nz.org.venice.parser.Variable;
import nz.org.venice.parser.Variables;
import nz.org.venice.parser.ImplicitVariables;
import nz.org.venice.portfolio.Portfolio;
import nz.org.venice.portfolio.StockHolding;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.ui.ProgressDialog;


/**
 * This class perform the paper trade analysis for the artificial neural network.
 * A specific class has been developed, extended from PaperTrade {@link PaperTrade},
 * because ANNs have a complete different behaviour compared to other analysis based
 * on Gondola language.
 * ANNs need to be trained, and the training session needs to know how the things would
 * be happened, if different choices have been taken day by day.
 * For further information about the techniques used, you should find out the Cross Target
 * technique (@author Prof. Pietro Terna).
 * That is the technique used here to get the buy/sell signals.
 * See http://web.econ.unito.it/terna/ct-era/ct-era.html.
 *
 * <p>The final portfolio will contain a single cash and a single share account.
 *
 * Cross Target method to get buy and sell signal through an ANN.
 * The cross target method works in the following way:
 * we make some guesses about buy and sell signals (actions) and
 * about capital (effect of actions),
 * the guesses are done by artificial neural network (ANN);
 * then we train the ANN comparing what the ANN has guessed with the following values:
 * the buy and sell signals are compared with the buy and sell signals which would be
 * to get a capital equal to the capital guessed plus the percental increment wished;
 * the capital signal is compared with the capital got trading
 * with the guessed buy and sell signals.
 *
 * For the sake of simplicity in Merchant of Venice we've used a simplified version
 * of CT technique.
 * We do not use the capital as output of ANN, but we use only two outputs (the buy
 * and sell signals).
 * We pilot the buy and sell signals according to what happens in the future:
 * we calculate if we gain enough in one of the next days
 * (one from the next day trading until the window forecast day trading).
 * We gain enough if and only if the earning percentage is higher than the user defined one,
 * in one of the window forecast days.
 *
 * The core of the CT method has done in the setANNTrainingParameters method in this class.
 *
 * @author Alberto Nacher
 */
public class ANNPaperTrade extends PaperTrade {
    
    /*
     * input and output arrays used to train the ANN
    */
    private static double[][] ANNInputArray;
    private static double[][] ANNOutputDesiredArray;

    // Users shouldn't instantiate this class
    private ANNPaperTrade() {
        // nothing to do
    }


    /**
     * Iterate through our stock holdings on the given date and decide
     * whether to sell any stock.
     *
     * @param environment the paper trade environment
     * @param quoteBundle the historical quote data
     * @param variables any Gondola variables set
     * @param dateOffset date to examine
     * @param tradeCost the cost of a trade
     * @param symbols ordered list of symbols on that date
     * @param orderCache cache of ordered symbols
     * @param inputExpressions the input expressions of ANN
     * @param artificialNeuralNetwork the ANN object
     */
    private static void sellTrades(Environment environment,
                                    EODQuoteBundle quoteBundle,
                                    Variables variables,
                                    int dateOffset,
                                    Money tradeCost,
                                    List symbols,
                                    OrderCache orderCache,
                                    Expression[] inputExpressions,
                                    ArtificialNeuralNetwork artificialNeuralNetwork)
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
            variables.setValue("stockcapital",
                    getStockCapital(environment, stockHolding, symbol, dateOffset));

            try {
                // Generate the input array of doubles according to the input expressions
                double[] inputDoubles = new double[inputExpressions.length];
                for (int ii=0; ii<inputDoubles.length; ii++) {
                    inputDoubles[ii] = inputExpressions[ii].evaluate(variables,
                    quoteBundle, symbol, dateOffset);
                }
                
                // calculate the price wanted by user trade value expression
                // to sell the stock (tradeValueWanted).
                // If trade value expression is 'open', then
                // set tradeValueWanted = 0 and sell at open price.
                double tradeValueWanted = 0;
                if(!environment.tradeValueSell.equals("open")) {
                    Expression tradeValueSellExpression =
                            ExpressionFactory.newExpression(environment.tradeValueSell);
                    tradeValueWanted =
                            tradeValueSellExpression.evaluate(variables,
                            environment.quoteBundle, symbol, dateOffset);
                }

                // If you want to buy the stock, do not sell it.
                boolean[] buySell = artificialNeuralNetwork.run(inputDoubles);
                if(!(buySell[artificialNeuralNetwork.OUTPUT_BUY])) {
                    if(buySell[artificialNeuralNetwork.OUTPUT_SELL]) {
                        
                        // Did we have enough money to buy at least one share?
                        // Will the stock reach the price wanted (tradeValueWanted)?
                        sell(environment, variables, stockHolding,
                                tradeCost, tradeValueWanted, dateOffset + 1);
                    }
                }
            }
            catch(MissingQuoteException e) {
                // ignore and move on
            }
            catch(EvaluationException e) {
                // Ignore and move on
            }
        }
    }

    /**
     * Iterate through all the stocks on the market on the given date and
     * decide whether to buy any stock.
     *
     * @param environment the paper trade environment
     * @param quoteBundle the historical quote data
     * @param variables any Gondola variables set
     * @param dateOffset date to examine
     * @param tradeCost the cost of a trade
     * @param symbols ordered list of symbols on that date
     * @param orderCache cache of ordered symbols
     * @param stockValue amount of money to spend on stock
     * @param inputExpressions the input expressions of ANN
     * @param artificialNeuralNetwork the ANN object
     */
    private static void buyTrades(Environment environment,
                                    EODQuoteBundle quoteBundle,
                                    Variables variables,
                                    int dateOffset,
                                    Money tradeCost,
                                    List symbols,
                                    OrderCache orderCache,
                                    Money stockValue,
                                    Expression[] inputExpressions,
                                    ArtificialNeuralNetwork artificialNeuralNetwork)
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

                    // calculate the price wanted by user trade value expression
                    // to buy the stock (tradeValueWanted).
                    // If trade value expression is 'open', then
                    // set tradeValueWanted = 0 and buy at open price.
                    double tradeValueWanted = 0;
                    if(!environment.tradeValueBuy.equals("open")) {
                        Expression tradeValueBuyExpression =
                                ExpressionFactory.newExpression(environment.tradeValueBuy);
                        tradeValueWanted =
                                tradeValueBuyExpression.evaluate(variables,
                                environment.quoteBundle, symbol, dateOffset);
                    }
                            
                    try {
                        // Generate the input array of doubles according to the input expressions
                        double[] inputDoubles = new double[inputExpressions.length];
                        for (int ii=0; ii<inputDoubles.length; ii++) {
                            inputDoubles[ii] = inputExpressions[ii].evaluate(variables,
                            quoteBundle, symbol, dateOffset);
                        }
                
                        boolean[] buy = artificialNeuralNetwork.run(inputDoubles);
                        if(buy[artificialNeuralNetwork.OUTPUT_BUY]) {

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
     * Perform paper trading using a fixed stock value.
     *
     * @param portfolioName name to call portfolio
     * @param quoteBundle historical quote data
     * @param variables any Gondola variables set
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param capital initial capital in the portfolio
     * @param stockValue the rough value of each stock holding
     * @param tradeCost the cost of a trade
     * @param tradeValueBuy the value at which we want to buy
     * @param tradeValueSell the value at which we want to sell
     * @param progress the progress bar shown while ANN is running
     * @param inputExpressions the input expressions of ANN
     * @param artificialNeuralNetwork the ANN object
     *
     * @return the portfolio at the close of the last day's trade
     */
    public static Portfolio paperTrade(String portfolioName,
                EODQuoteBundle quoteBundle,
                Variables variables,
                OrderCache orderCache,
                TradingDate startDate,
                TradingDate endDate,
                Money capital,
                Money stockValue,
                Money tradeCost,
                String tradeValueBuy,
                String tradeValueSell,
                ProgressDialog progress,
                Expression[] inputExpressions,
                ArtificialNeuralNetwork artificialNeuralNetwork)
        throws EvaluationException {

        // Set up environment for paper trading
        ANNPaperTrade paperTrade = new ANNPaperTrade();
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

        int timeLength = Math.abs(dateOffset) - Math.abs(environment.endDateOffset);
        progress.setMaximum(timeLength);
                
        // Now iterate through each trading date and decide whether
        // to buy/sell. The last date is used for placing the previous
        // date's buy/sell orders.
        while(dateOffset < environment.endDateOffset) {
            
            // Running the ANN means we might need to load in
            // more quotes so the note may have changed...
            progress.setNote(Locale.getString("RUNNING"));
            progress.increment();
            
            // Set the value of days elapsed from the begin of the Paper Trade process
            variables.setValue("daysfromstart", daysRest + dateOffset);

            // Set the value of the number of transactions done until now
            variables.setValue("transactions", environment.portfolio.countTransactions());

            // Set the value of actual capital
            variables.setValue("capital", getCapital(environment.portfolio,
                    environment.quoteBundle, dateOffset));

            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffset);
            
            sellTrades(environment, quoteBundle, variables, dateOffset, tradeCost,
                    symbols, orderCache,
                    inputExpressions, artificialNeuralNetwork);
            buyTrades(environment, quoteBundle, variables, dateOffset, tradeCost,
                    symbols, orderCache, stockValue,
                    inputExpressions, artificialNeuralNetwork);
            
            dateOffset++;
        }

        // Set the tip for the next day
        setTip(environment, quoteBundle, variables, dateOffset, tradeCost,
                orderCache.getTodaySymbols(dateOffset), orderCache,
                inputExpressions, artificialNeuralNetwork);
        
        return environment.portfolio;
    }

    /**
     * Perform training using a fixed stock value.
     *
     * @param portfolioName name to call portfolio
     * @param quoteBundle historical quote data
     * @param variables any Gondola variables set
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param capital initial capital in the portfolio
     * @param stockValue the rough value of each stock holding
     * @param tradeCost the cost of a trade
     * @param tradeValueBuy the value at which we want to buy
     * @param tradeValueSell the value at which we want to sell
     * @param progress the progress bar shown while ANN is running
     * @param ANNTrainingPage the pointer to the training page
     * @param inputExpressions the input expressions of ANN
     * @param artificialNeuralNetwork the ANN object
     */
    public static void paperTraining(String portfolioName,
                EODQuoteBundle quoteBundle,
                Variables variables,
                OrderCache orderCache,
                TradingDate startDate,
                TradingDate endDate,
                Money capital,
                Money stockValue,
                Money tradeCost,
                String tradeValueBuy,
                String tradeValueSell,
                ProgressDialog progress,
                ANNTrainingPage ANNTrainingPage,
                Expression[] inputExpressions,
                ArtificialNeuralNetwork artificialNeuralNetwork)
        throws EvaluationException {

        // Total cycles to train ANN
        int totCycles = ANNTrainingPage.getTotCycles();
        progress.setMaximum(totCycles);
        
        // Set up environment for paper trading
        ANNPaperTrade paperTrade = new ANNPaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital,
                                                             tradeValueBuy,
                                                             tradeValueSell);
        int dateOffset = environment.startDateOffset;

        // ANN training arrays
        int dateOffsetToGetANNArrayLength = dateOffset;
        int ANNArrayLength = 0;
        int ANNArrayPointer = 0;
        while(dateOffsetToGetANNArrayLength < environment.endDateOffset) {
            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffsetToGetANNArrayLength);
            for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
                Symbol symbol = (Symbol)iterator.next();
                ANNArrayLength++;
            }
            dateOffsetToGetANNArrayLength++;
        }
        ANNInputArray =
                new double[ANNArrayLength][inputExpressions.length];
        ANNOutputDesiredArray =
                new double[ANNArrayLength][artificialNeuralNetwork.OUTPUT_NEURONS];
	// Paper Trading variables
	ImplicitVariables.getInstance().setup(variables, orderCache.isOrdered());
		    
        // daysfromstart
        int daysRest = (int)(-1) * dateOffset;

        // Now iterate through each trading date and decide whether
        // to buy/sell. The last date is used for placing the previous
        // date's buy/sell orders.
        while(dateOffset < environment.endDateOffset) {

            // Set the value of days elapsed from the begin of the Paper Trade process
            variables.setValue("daysfromstart", daysRest + dateOffset);

            // Set the value of the number of transactions done until now
            variables.setValue("transactions", environment.portfolio.countTransactions());

            // Set the value of actual capital
            variables.setValue("capital", getCapital(environment.portfolio,
                    environment.quoteBundle, dateOffset));

            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffset);

            // Iterate through stocks available today - should we buy or sell any of it?
            for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
                Symbol symbol = (Symbol)iterator.next();
                /* 
                 * Set ANNInputArray and ANNOutputDesiredArray arrays
                 * row by row.
                 * Each call to setANNTrainingParameters method makes
                 * an assignment to a single row.
                 */
                setANNTrainingParameters(ANNInputArray[ANNArrayPointer],
                        ANNOutputDesiredArray[ANNArrayPointer],
                        environment, quoteBundle, variables,
                        dateOffset, symbol,
                        inputExpressions, artificialNeuralNetwork,
                        ANNTrainingPage.getMinEarningPercentage(),
                        ANNTrainingPage.getWindowForecast());

                ANNArrayPointer++;
            }

            dateOffset++;
        }

        // Train the ANN
        artificialNeuralNetwork.runTraining(ANNInputArray, ANNOutputDesiredArray,
                ANNTrainingPage.getLearningRate(), 
                ANNTrainingPage.getMomentum(), ANNTrainingPage.getPreLearning(), 
                ANNTrainingPage.getTotCycles(), ANNArrayPointer);
    }

    /**
     * Perform training using a fix number of stocks.
     *
     * @param portfolioName name to call portfolio
     * @param quoteBundle historical quote data
     * @param variables any Gondola variables set
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param capital initial capital in the portfolio
     * @param numberStocks try to keep this number of stocks in the portfolio
     * @param tradeCost the cost of a trade
     * @param tradeValueBuy the value at which we want to buy
     * @param tradeValueSell the value at which we want to sell
     * @param progress the progress bar shown while ANN is running
     * @param ANNTrainingPage the pointer to the training page
     * @param inputExpressions the input expressions of ANN
     * @param artificialNeuralNetwork the ANN object
     */
    public static void paperTraining(String portfolioName,
                EODQuoteBundle quoteBundle,
                Variables variables,
                OrderCache orderCache,
                TradingDate startDate,
                TradingDate endDate,
                Money capital,
                int numberStocks,
                Money tradeCost,
                String tradeValueBuy,
                String tradeValueSell,
                ProgressDialog progress,
                ANNTrainingPage ANNTrainingPage,
                Expression[] inputExpressions,
                ArtificialNeuralNetwork artificialNeuralNetwork)
        throws EvaluationException {

        // Total cycles to train ANN
        int totCycles = ANNTrainingPage.getTotCycles();
        progress.setMaximum(totCycles);
                    
        // Set up environment for paper trading
        ANNPaperTrade paperTrade = new ANNPaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital,
                                                             tradeValueBuy,
                                                             tradeValueSell);
        int dateOffset = environment.startDateOffset;

        // ANN training arrays
        int dateOffsetToGetANNArrayLength = dateOffset;
        int ANNArrayLength = 0;
        int ANNArrayPointer = 0;
        while(dateOffsetToGetANNArrayLength < environment.endDateOffset) {
            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffsetToGetANNArrayLength);
            for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
                Symbol symbol = (Symbol)iterator.next();
                ANNArrayLength++;
            }
            dateOffsetToGetANNArrayLength++;
        }
        ANNInputArray = new double[ANNArrayLength][inputExpressions.length];
        ANNOutputDesiredArray = new double[ANNArrayLength][artificialNeuralNetwork.OUTPUT_NEURONS];

	// Paper Trading variables
	ImplicitVariables.getInstance().setup(variables, orderCache.isOrdered());
	// daysfromstart
        int daysRest = (int)(-1) * dateOffset;

        // Now iterate through each trading date and decide whether
        // to buy/sell. The last date is used for placing the previous
        // date's buy/sell orders.
        while(dateOffset < environment.endDateOffset) {

            // Set the value of days elapsed from the begin of the Paper Trade process
            variables.setValue("daysfromstart", daysRest + dateOffset);

            // Set the value of the number of transactions done until now
            variables.setValue("transactions", environment.portfolio.countTransactions());

            // Set the value of actual capital
            variables.setValue("capital", getCapital(environment.portfolio,
                    environment.quoteBundle, dateOffset));

            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffset);

            // Calculate the input for ANN training
            Money stockValue = new Money(0);
            try {
                // stockValue = (portfolio / numberStocks) - (2 * tradeCost)
                Money portfolioValue = environment.portfolio.getValue(quoteBundle, dateOffset);
                stockValue =
                    portfolioValue.divide(numberStocks).subtract(tradeCost.multiply(2));
            }
            catch(MissingQuoteException e) {
                // Ignore and move on
            }
            
            // Iterate through stocks available today - should we buy or sell any of it?
            for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
                Symbol symbol = (Symbol)iterator.next();
                /* 
                 * Set ANNInputArray and ANNOutputDesiredArray arrays
                 * row by row.
                 * Each call to setANNTrainingParameters method makes an assignment to
                 * a single row.
                 */
                setANNTrainingParameters(ANNInputArray[ANNArrayPointer],
                        ANNOutputDesiredArray[ANNArrayPointer],
                        environment, quoteBundle, variables,
                        dateOffset, symbol,
                        inputExpressions, artificialNeuralNetwork,
                        ANNTrainingPage.getMinEarningPercentage(),
                        ANNTrainingPage.getWindowForecast());
                
                ANNArrayPointer++;
            }

            dateOffset++;
        }

        // Train the ANN
        artificialNeuralNetwork.runTraining(ANNInputArray, ANNOutputDesiredArray,
                ANNTrainingPage.getLearningRate(), 
                ANNTrainingPage.getMomentum(), ANNTrainingPage.getPreLearning(), 
                ANNTrainingPage.getTotCycles(), ANNArrayPointer);
    }

    /**
     * Perform paper trading using a fix number of stocks.
     *
     * @param portfolioName name to call portfolio
     * @param quoteBundle historical quote data
     * @param variables any Gondola variables set
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param capital initial capital in the portfolio
     * @param numberStocks try to keep this number of stocks in the portfolio
     * @param tradeCost the cost of a trade
     * @param tradeValueBuy the value at which we want to buy
     * @param tradeValueSell the value at which we want to sell
     * @param progress the progress bar shown while ANN is running
     * @param inputExpressions the input expressions of ANN
     * @param artificialNeuralNetwork the ANN object
     *
     * @return the portfolio at the close of the last day's trade
     */
    public static Portfolio paperTrade(String portfolioName,
                EODQuoteBundle quoteBundle,
                Variables variables,
                OrderCache orderCache,
                TradingDate startDate,
                TradingDate endDate,
                Money capital,
                int numberStocks,
                Money tradeCost,
                String tradeValueBuy,
                String tradeValueSell,
                ProgressDialog progress,
                Expression[] inputExpressions,
                ArtificialNeuralNetwork artificialNeuralNetwork)
        throws EvaluationException {

        // Set up environment for paper trading
        ANNPaperTrade paperTrade = new ANNPaperTrade();
        Environment environment = paperTrade.new Environment(quoteBundle,
                                                             portfolioName,
                                                             startDate,
                                                             endDate,
                                                             capital,
                                                             tradeValueBuy,
                                                             tradeValueSell);
        int dateOffset = environment.startDateOffset;

	ImplicitVariables.getInstance().setup(variables, orderCache.isOrdered());
			        
        // daysfromstart
        int daysRest = (int)(-1) * dateOffset;
        
        int timeLength = Math.abs(dateOffset) - Math.abs(environment.endDateOffset);
        progress.setMaximum(timeLength);
                
        // Now iterate through each trading date and decide whether
        // to buy/sell. The last date is used for placing the previous
        // date's buy/sell orders.
        while(dateOffset < environment.endDateOffset) {
            
            // Running the ANN means we might need to load in
            // more quotes so the note may have changed...
            progress.setNote(Locale.getString("RUNNING"));
            progress.increment();

            // Set the value of days elapsed from the begin of the Paper Trade process
            variables.setValue("daysfromstart", daysRest + dateOffset);
            
            // Set the value of the number of transactions done until now
            variables.setValue("transactions", environment.portfolio.countTransactions());
            
            // Set the value of actual capital
            variables.setValue("capital", getCapital(environment.portfolio,
                    environment.quoteBundle, dateOffset));
            
            // Get all the (ordered) symbols that we can trade for today and
            // that we have quotes for.
            List symbols = orderCache.getTodaySymbols(dateOffset);

            sellTrades(environment, quoteBundle, variables, dateOffset, tradeCost,
                        symbols, orderCache,
                        inputExpressions, artificialNeuralNetwork);
            try {
                // stockValue = (portfolio / numberStocks) - (2 * tradeCost)
                Money portfolioValue = environment.portfolio.getValue(quoteBundle, dateOffset);
                Money stockValue =
                    portfolioValue.divide(numberStocks).subtract(tradeCost.multiply(2));
                buyTrades(environment, quoteBundle, variables, dateOffset, tradeCost,
                        symbols, orderCache, stockValue,
                        inputExpressions, artificialNeuralNetwork);
            }
            catch(MissingQuoteException e) {
                // Ignore and move on
            }

            dateOffset++;
        }

        // Set the tip for the next day
        setTip(environment, quoteBundle, variables, dateOffset, tradeCost,
                orderCache.getTodaySymbols(dateOffset), orderCache,
                inputExpressions, artificialNeuralNetwork);
        
        return environment.portfolio;
    }

    /**
     * Set the information for the tip of the next day.
     */
    private static void setTip(Environment environment,
                            EODQuoteBundle quoteBundle,
                            Variables variables,
                            int dateOffset,
                            Money tradeCost,
                            List symbols,
                            OrderCache orderCache,
                            Expression[] inputExpressions,
                            ArtificialNeuralNetwork artificialNeuralNetwork) {
                                      
        symbolStock = new String[symbols.size()];
        buyRule = new boolean[symbols.size()];
        sellRule = new boolean[symbols.size()];
        buyValue = new double[symbols.size()];
        sellValue = new double[symbols.size()];
        
        setSellTip(environment, quoteBundle, variables, dateOffset,
                    tradeCost, symbols, orderCache,
                    inputExpressions, artificialNeuralNetwork);
        
        setBuyTip(environment, quoteBundle, variables, dateOffset,
                    tradeCost, symbols, orderCache,
                    inputExpressions, artificialNeuralNetwork);
        
    }

    private static void setSellTip(Environment environment,
                                    EODQuoteBundle quoteBundle,
                                    Variables variables,
                                    int dateOffset,
                                    Money tradeCost,
                                    List symbols,
                                    OrderCache orderCache,
                                    Expression[] inputExpressions,
                                    ArtificialNeuralNetwork artificialNeuralNetwork) {
        
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
                    variables.setValue("held",
                            getHoldingTime(environment, stockHolding, dateOffset));
                    variables.setValue("stockcapital",
                            getStockCapital(environment, stockHolding, symbol, dateOffset));
                    break;
                } else {
                    variables.setValue("held", 0);
                    variables.setValue("stockcapital", 0.0D);
                }
            }

            try {
                // Generate the input array of doubles according to the input expressions
                double[] inputDoubles = new double[inputExpressions.length];
                for (int ii=0; ii<inputDoubles.length; ii++) {
                    inputDoubles[ii] = inputExpressions[ii].evaluate(variables,
                    quoteBundle, symbol, dateOffset);
                }
                
                // Get if the stock must be sold
                boolean[] sell = artificialNeuralNetwork.run(inputDoubles);
                sellRule[index] = sell[artificialNeuralNetwork.OUTPUT_SELL];
                        
                // calculate the price wanted by user trade value expression
                // to sell the stock (tradeValueWanted).
                // If trade value expression is 'open', then
                // set the price to zero (sell at open price).
                sellValue[index] = 0;
                if(!environment.tradeValueSell.equals("open")) {
                    Expression tradeValueSellExpression =
                            ExpressionFactory.newExpression(environment.tradeValueSell);
                    sellValue[index] =
                            tradeValueSellExpression.evaluate(variables,
                            environment.quoteBundle, symbol, dateOffset);
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
                                int dateOffset,
                                Money tradeCost,
                                List symbols,
                                OrderCache orderCache,
                                Expression[] inputExpressions,
                                ArtificialNeuralNetwork artificialNeuralNetwork) {
                                      
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
                // Generate the input array of doubles according to the input expressions
                double[] inputDoubles = new double[inputExpressions.length];
                for (int ii=0; ii<inputDoubles.length; ii++) {
                    inputDoubles[ii] = inputExpressions[ii].evaluate(variables,
                    quoteBundle, symbol, dateOffset);
                }
                
                // Get if the stock must be bought
                boolean[] buy = artificialNeuralNetwork.run(inputDoubles);
                buyRule[index] = buy[artificialNeuralNetwork.OUTPUT_BUY];
                
                // If you own the stock and both sell and buy rule fire,
                // you wouldn't sell it, neither would you buy it.
                // So it is necessary set the buyRule and sellRule to false.
                //if(environment.shareAccount.isHolding(symbol) && sellRule[index] &&
                //  buyRule[index]) {
                //    sellRule[index] = false;
                //    buyRule[index] = false;
                //}

                // calculate the price wanted by user trade value expression
                // to buy the stock (tradeValueWanted).
                // If trade value expression is 'open', then
                // set this price to zero (buy at open price).
                buyValue[index] = 0;
                if(!environment.tradeValueBuy.equals("open")) {
                    Expression tradeValueBuyExpression =
                            ExpressionFactory.newExpression(environment.tradeValueBuy);
                    buyValue[index] =
                            tradeValueBuyExpression.evaluate(variables,
                            environment.quoteBundle, symbol, dateOffset);
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
    
    /*
     * This is the core method which manages the Cross Target technique.
     * All the CT method is described at the beginning of this class.
     */
    private static void setANNTrainingParameters(double[] ANNInputArrayRow,
            double[] ANNOutputDesiredArrayRow,
            Environment environment,
            EODQuoteBundle quoteBundle,
            Variables variables,
            int dateOffset,
            Symbol symbol,
            Expression[] inputExpressions,
            ArtificialNeuralNetwork artificialNeuralNetwork,
            double minEarningPercentage,
            int windowForecast) {
        
        /* Input parameters for the ANN */
        try {
            for (int ii=0; ii<inputExpressions.length; ii++) {
                ANNInputArrayRow[ii] = inputExpressions[ii].evaluate(variables,
                quoteBundle, symbol, dateOffset);
            }
        } catch (EvaluationException ex) {
            // Do nothing if we cannot get any of the inputs
        }

        
        /* Output parameters for the ANN */
        
        /*
         * open and close prices of the next trading days
         * (the next trading days are equal to window forecast parameter).
         * buySignal is activated if there is at least one day among the window
         * forecast days, when we gain a percentage equal to or higher than earning percentage.
         */
        double openPrice = 0;
        boolean buySignal = false;
        double closePrice = 0;
        try {
            // Get the open price
            openPrice = environment.quoteBundle.getQuote(symbol,
                    Quote.DAY_OPEN, dateOffset + 1);
            // Loop for all the days in the window forecast
            for (int ii = 1; ii < windowForecast + 1; ii++) {
                if ((dateOffset + ii) <= environment.endDateOffset) {
                    // Get the close price, ii days after the trading day.
                    closePrice = environment.quoteBundle.getQuote(symbol,
                            Quote.DAY_CLOSE, dateOffset + ii);
                    double earn = (100.0D * (closePrice - openPrice) / openPrice);
                    // if we earn in the ii trading day,
                    // then set the buy signal to true.
                    if (earn > minEarningPercentage) {
                        buySignal = true;
                    }
                }
            }
        }
        catch(MissingQuoteException e) {
            // Ignore and move on
        }
                
        // Set the wanted buy and sell signals
        if (buySignal) {
            // BUY AND NOT SELL
            ANNOutputDesiredArrayRow[artificialNeuralNetwork.OUTPUT_BUY] =
                    artificialNeuralNetwork.HIGH_BOOL;
            ANNOutputDesiredArrayRow[artificialNeuralNetwork.OUTPUT_SELL] =
                    artificialNeuralNetwork.LOW_BOOL;
        } else {
            // NOT BUY AND SELL
            ANNOutputDesiredArrayRow[artificialNeuralNetwork.OUTPUT_BUY] =
                    artificialNeuralNetwork.LOW_BOOL;
            ANNOutputDesiredArrayRow[artificialNeuralNetwork.OUTPUT_SELL] =
                    artificialNeuralNetwork.HIGH_BOOL;
        }
    }
}
