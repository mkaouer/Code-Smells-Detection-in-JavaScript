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

package nz.org.venice.analyser.gp;

import java.util.Iterator;
import java.util.List;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.EODQuoteBundleIterator;
import nz.org.venice.quote.EODQuoteCache;
import nz.org.venice.quote.EODQuoteRange;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.util.TradingDate;

/**
 * This class is a specialised version of the EODQuoteBundle tailored specifically
 * for the GP. It differs from in that it enforces a window of dates.
 * This window prevents the GP expressions from accessing quotes
 * too far in the past which would slow down calculations (e.g. calculating the
 * average value of a stock for the last 3,000 days), and also prevents GP expressions
 * accessing quotes that would be in their future.
 *
 * @author Andrew Leppard
 * @see nz.org.venice.quote.EODQuoteBundle
 */
public class GPQuoteBundle extends EODQuoteBundle {

    private int window;

    /**
     * Create a new quote bundle for the GP.
     *
     * @param quoteBundle wrap the given quote bundle
     * @param window prevent access to quotes this many days before
     *               the first quote in the given quote bundle
     */
    public GPQuoteBundle(EODQuoteBundle quoteBundle, int window) {
        super(quoteBundle.getQuoteRange());

        this.window = window;
    }

    /**
     * Get a stock quote. This function has been primarily created for Gondola
     * scripts. It passes in the current date and the date offset so that
     * specialised QuoteBundle implementations such as {@link GPQuoteBundle} can prevent the GP
     * accessing 'future' dates.
     *
     * @param symbol  the stock symbol
     * @param quoteType the quote type, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param today fast access date offset of current date, see {@link EODQuoteCache}
     * @param offset offset from current date
     * @return the quote
     * @exception EvaluationException if the script isn't allowed access to the quote.
     */
    public double getQuote(Symbol symbol, int quoteType, int today, int offset)
	throws EvaluationException, MissingQuoteException {

        // Trying to access a future quote?
        if(offset > 0)
            throw EvaluationException.FUTURE_DATE_EXCEPTION;

        // Trying to access a date too far into the past?
	// Math.abs(Integer.MIN_VALUE) returns Integer.MIN_VALUE which 
	// is negative.
        else if(Math.abs(offset) > Math.abs(window) ||
		offset == Integer.MIN_VALUE) 
            throw EvaluationException.PAST_DATE_EXCEPTION;

	

        // Date is within range
        else
            return getQuote(symbol, quoteType, today + offset);
    }

    /**
     * Set the qutoe range which specifies this quote bundle.
     *
     * @param quoteRange        the new quote range
     */
    public void setQuoteRange(EODQuoteRange quoteRange) {	
	//assert false;
	//Is this method not meant to be called?
	//It was being called and the assertion 
	//triggering, stopping the GP process
	super.setQuoteRange(quoteRange);
    }
}
