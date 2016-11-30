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

package org.mov.chart.source;

import org.mov.chart.*;
import org.mov.ui.*;
import org.mov.util.*;
import org.mov.portfolio.*;
import org.mov.quote.*;

import java.util.*;

/**
 * Provides a Portfolio graph source. This class allows portfolios
 * to be graphed.
 */
public class PortfolioGraphSource implements GraphSource {

    /** Graph the market value (day close) of the portfolio */
    public static final int MARKET_VALUE = 0;

    /** Graph the portfolio showing the profit/loss value */
    public static final int PROFIT_LOSS = 1;

    private QuoteBundle quoteBundle;
    private int mode;
    private Graphable graphable;
    private Portfolio portfolio;

    /**
     * Provides a graph source for graphing portfolios. This
     * class allows a portfolio to be graphed in a variety of ways.
     *
     * @param	portfolio	the portfolio to graph
     * @param	quoteBundle	quote bundle containing all the necessary
     *				quotes to calculate the portfolio value
     *				for every day
     * @param	mode		<code>MARKET_VALUE</code> for the market
     *				value of the portfolio; <code>PROFIT_LOSS
     *				</code> for showing the profit loss
     *				made
     */
    public PortfolioGraphSource(Portfolio portfolio, QuoteBundle quoteBundle,
				int mode) {
	this.portfolio = portfolio;
	this.quoteBundle = quoteBundle;
	this.mode = mode;

	// If theres no start date - theres no transactions and therefore
	// nothign to graph
	if(portfolio.getStartDate() == null)
	    return;

	// Create a copy of the portfolio without the transactions
	Portfolio temporaryPortfolio = (Portfolio)portfolio.clone();
	List transactions = new ArrayList(temporaryPortfolio.getTransactions());
	Iterator transactionIterator = transactions.iterator();
	Transaction transaction = (Transaction)transactionIterator.next();

	// Remove all transactions now and add them back as we reach
	// the appropriate dates
	temporaryPortfolio.removeAllTransactions();
	
	// Get start date from first transaction, get end date from
	// latest date in the bundle
	TradingDate startDate = transaction.getDate();
	TradingDate endDate = quoteBundle.getLastDate();

	// Iterate through each day between start and end date, recreating
	// portfolio value on that day
	List dateRange = TradingDate.dateRangeToList(startDate, endDate);
	Iterator dateIterator = dateRange.iterator();	

	if(mode == MARKET_VALUE) {
	    createMarketValueGraphable(dateIterator, transactionIterator,
				       transaction, temporaryPortfolio);
	}
	else {
	    createProfitLossGraphable(dateIterator, transactionIterator,
				      transaction, temporaryPortfolio);
	}

    }

    // Create a graphable for the market value of the portfolio over time
    private void createMarketValueGraphable(Iterator dateIterator,
					    Iterator transactionIterator,
					    Transaction transaction,
					    Portfolio temporaryPortfolio) {
	// Build graphable so this source can be directly graphed
	graphable = new Graphable();

	while(dateIterator.hasNext()) {
	    TradingDate date = (TradingDate)dateIterator.next();

	    while(transaction != null &&
		  transaction.getDate().compareTo(date) <= 0) {
		temporaryPortfolio.addTransaction(transaction);

		if(transactionIterator.hasNext())
		    transaction = (Transaction)transactionIterator.next();
		else
		    transaction = null; // no more transactions
	    }

	    try {
		Float value =
		    new Float(temporaryPortfolio.getValue(quoteBundle, date));
		graphable.putY((Comparable)date, value);
	    }
	    catch(MissingQuoteException e) {
		// This gets thrown if we couldnt calculate a share value
		// for a given date. This occurs on public holidays etc.
		// Its OK. Just don't give a quote for that day!
	    }
	}
    }

    // Create a graphable which shows the profit gains + losses of the
    // portfolio over time, without respect to the value
    private void createProfitLossGraphable(Iterator dateIterator,
					   Iterator transactionIterator,
					   Transaction transaction,
					   Portfolio temporaryPortfolio) {

	// Build graphable so this source can be directly graphed
	graphable = new Graphable();

	// Keep track of cash deposited into portfolio.
	float depositedCash = 0.0F;

	while(dateIterator.hasNext()) {
	    TradingDate date = (TradingDate)dateIterator.next();

	    while(transaction != null &&
		  transaction.getDate().compareTo(date) <= 0) {

		// If its a cash deposit/withdrawal we need to update
		// our cash value
		if(transaction.getType() == Transaction.WITHDRAWAL) {
		    depositedCash -= transaction.getAmount();
		}
		else if(transaction.getType() == Transaction.DEPOSIT) {
		    depositedCash += transaction.getAmount();
		}

		temporaryPortfolio.addTransaction(transaction);

		if(transactionIterator.hasNext())
		    transaction = (Transaction)transactionIterator.next();
		else
		    transaction = null; // no more transactions
	    }

	    try {
		Float value =
		    new Float(temporaryPortfolio.getValue(quoteBundle, date) -
			      depositedCash);
		graphable.putY((Comparable)date, value);
	    }
	    catch(MissingQuoteException e) {
		// This gets thrown if we couldnt calculate a share value
		// for a given date. This occurs on public holidays etc.
		// Its OK. Just don't give a quote for that day!
	    }
	}

    }

    public Graphable getGraphable() {
	return graphable;
    }

    public String getName() {
	return portfolio.getName();
    }

    public String getToolTipText(Comparable x) {

	// In portfolio graphs the x axis is in dates
	TradingDate date = (TradingDate)x;
	// Get value for this date
	Float value = graphable.getY(x);
	
	if(value != null) {
	    String name = portfolio.getName();

	    if(mode == PROFIT_LOSS)
		name = name.concat(" Profit");

	    return new String("<html>" +
			      name +
			      ", " +
			      date.toLongString() +
			      "<p>" +
			      getYLabel(value.floatValue()));
	}
	else {
	    return null;
	}
    }

    public String getYLabel(float value) {
	return PriceFormat.priceToString(value);
    }

    public float[] getAcceptableMajorDeltas() {
	float[] major = {0.001F, // 0.1c
			 0.01F, // 1c
			 0.1F, // 10c
			 1.0F, // $1
			 10.0F, // $10
			 100.0F, // $100
			 1000.0F, // $1k
			 10000.0F, // $10k (secure)
			 100000.0F, // $100k (well off)
			 1000000.0F, // $1M (rich)
			 10000000.0F, // $10M (very rich)
			 100000000.0F, // $100M (super rich)
			 1000000000.0F, // $1B (wow)
			 1000000000.0F}; // $10B (Bill Gates)

	return major;	
    }

    public float[] getAcceptableMinorDeltas() {
	float[] minor = {1F, 1.1F, 1.25F, 1.3333F, 1.5F, 2F, 2.25F,
			 2.5F, 3F, 3.3333F, 4F, 5F, 6F, 6.5F, 7F, 7.5F,
			 8F, 9F};
	return minor;
    }
}
