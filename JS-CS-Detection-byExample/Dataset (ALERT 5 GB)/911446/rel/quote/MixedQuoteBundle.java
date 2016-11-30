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

package nz.org.venice.quote;

import java.util.Iterator;
import java.util.List;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingTime;

/**
 * When a task requires a mix of end-of-day and intra-day stock quotes, it should
 * create an instance of this class which represents all the task's required
 * quotes. This quote bundle is useful for displaying the latest value of stock quotes. If
 * intra-day quotes are not available, then user of this package will transparently see
 * just the end-of-day quotes. Even when intra-day quotes are available, the end-of-day
 * quotes can be used for calculating quote change.
 * <p>
 * Example:
 * <pre>
 *      MixedQuoteBundle quoteBundle = new MixedQuoteBundle(symbols, firstDate, lastDate);
 *      try {
 *	    double = quoteBundle.getQuote("CBA", Quote.DAY_OPEN, 0);
 *      }
 *      catch(QuoteNotLoadedException e) {
 *          //...
 *      }
 * </pre>
 *
 * @author Andrew Leppard
 * @see EODQuote
 * @see IDQuote
 * @see Quote
 * @see Symbol
 */
public class MixedQuoteBundle implements QuoteBundle {

    // Contains the end of day quotes
    private EODQuoteBundle eodQuoteBundle;

    // Contains the intra-day quotes
    private IDQuoteBundle idQuoteBundle;
    
    /**
     * Create a new mixed quote bundle that contains the end of day quotes from
     * between the two dates given and the current day's intra-day quotes.
     * The quote bundle should be given at least two days of end of day quotes to
     * properly calculate quote change, in case the intra-day quotes are not
     * available.
     *
     * @param symbols the symbols to load
     * @param firstDate the first end of day quotes to load
     * @param lastDate the last end of day quotes to load
     */
    public MixedQuoteBundle(List symbols, TradingDate firstDate, TradingDate lastDate) {
        eodQuoteBundle = new EODQuoteBundle(new EODQuoteRange(symbols, firstDate, lastDate));
        idQuoteBundle = new IDQuoteBundle(symbols);
    }

    public double getQuote(Symbol symbol, int quoteType, int today, int dateOffset)
	throws EvaluationException, MissingQuoteException {

        return getQuote(symbol, quoteType, today + dateOffset);
    }

    public double getQuote(Symbol symbol, int quoteType, int dateOffset)
	throws MissingQuoteException {

        if(dateOffset > eodQuoteBundle.getLastOffset())
            // Retrieve most recent intra-day quote
            return idQuoteBundle.getQuote(symbol, quoteType, idQuoteBundle.getLastOffset());
        else
            return eodQuoteBundle.getQuote(symbol, quoteType, dateOffset);
    }

    public Quote getQuote(Symbol symbol, int offset)
        throws MissingQuoteException {

        Quote quote = null;

        if(useIDQuotes()) {
	    //lastOffset == -1 when there are no intraday quotes available
	    if (idQuoteBundle.getLastOffset() >= 0) {		
		// Retrieve most recent intra-day quotes
		quote = idQuoteBundle.getQuote(symbol, idQuoteBundle.getLastOffset()); 
	    } else {
		quote = eodQuoteBundle.getQuote(symbol, eodQuoteBundle.getLastOffset()); 
	    }
	} else {
	    quote = eodQuoteBundle.getQuote(symbol, offset);
	}
	
	return quote;
    }

    public TradingDate offsetToDate(int dateOffset) {
        if(dateOffset > eodQuoteBundle.getLastOffset())
            return idQuoteBundle.offsetToDate(0);
        else
            return eodQuoteBundle.offsetToDate(dateOffset);
    }

    public double getNearestQuote(Symbol symbol, int quoteType, int dateOffset)
	throws MissingQuoteException {

	//TESME

	double quote = 0.0;
	boolean foundQuote = false;

	try {
	    //First try and get the actual quote first, because
	    //it will be closest.
	    quote = getQuote(symbol, quoteType, dateOffset);
	    foundQuote = true;
	} catch (MissingQuoteException e) {
	    if(dateOffset > eodQuoteBundle.getLastOffset()) {
		// Retrieve most recent intra-day quote
		quote = idQuoteBundle.getNearestQuote(symbol, quoteType, idQuoteBundle.getLastOffset());		
	    } else {
		quote = eodQuoteBundle.getQuote(symbol, quoteType, dateOffset);
	    }
	    foundQuote = true;
	}
	
	if (!foundQuote) {
	    throw MissingQuoteException.getInstance();
	}
	return quote;
    }

    /**
     * Return the fast access offset for the earliest quote in the bundle.
     *
     * @return fast access offset
     */
    public int getFirstOffset() {
        return eodQuoteBundle.getFirstOffset();
    }

    /**
     * Return the fast access offset for the latest quote in the bundle.
     *
     * @return fast access offset
     */
    public int getLastOffset() {
        if(useIDQuotes()) 
	    return eodQuoteBundle.getLastOffset() + 1;
	else
            return eodQuoteBundle.getLastOffset();
    }

    /**
     * Return whether we should display intra-day quotes or end of day quotes.
     * This decision will be made solely on the basis of whether the intra-day quote
     * sync is running. If we are not automatically downloading intra-day quotes,
     * then display end of day quotes.
     *
     * @return <code>true</code> if we should display intra-day quotes.
     */
    private boolean useIDQuotes() {
        return IDQuoteSync.getInstance().isRunning();
    }

    /**
     * Retrieve the fast access offset from the given quote.
     *
     * @param quote quote
     * @return fast access offset
     * @exception WeekendDateException if the date falls on a weekend.
     */
    public int getOffset(Quote quote) throws WeekendDateException {
        assert quote != null;

        if(quote.getDate().after(eodQuoteBundle.getLastDate()))
            return eodQuoteBundle.getLastOffset() + 1;
        else
            return eodQuoteBundle.getOffset(quote);
    }
}
