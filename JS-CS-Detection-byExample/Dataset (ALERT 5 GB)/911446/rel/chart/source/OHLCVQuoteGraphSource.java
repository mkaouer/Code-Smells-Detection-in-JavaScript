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

package nz.org.venice.chart.source;

import java.util.Iterator;

import nz.org.venice.chart.*;
import nz.org.venice.ui.QuoteFormat;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;
import nz.org.venice.quote.*;

/**
 * Provides a <code>EODQuoteBundle</code> graph source. This class
 * allows graph sources for day Open, High, Low, Close and
 * Volume (OHLCV).
 *
 * @author Andrew Leppard
 */

// rename to EODQuoteGraphSource?
public class OHLCVQuoteGraphSource implements GraphSource {

    private EODQuoteBundle quoteBundle;
    private int quote;
    private Symbol symbol;
    private Graphable graphable;

    /**
     * Create a new graph source from the quote bundle with the given
     * quote type.
     *
     * @param	quoteBundle the quote bundle containing stock quotes
     * @param	quote	the quote kind, one of: {@link Quote#DAY_OPEN},
     * {@link Quote#DAY_CLOSE}, {@link Quote#DAY_HIGH} or
     * {@link Quote#DAY_LOW}
     */
    public OHLCVQuoteGraphSource(EODQuoteBundle quoteBundle, int quote) {
	this.quote = quote;
	this.quoteBundle = quoteBundle;

	// Should only be a single symbol in the quote bundle anyway
	symbol = quoteBundle.getFirstSymbol();

	// Build graphable so this source can be directly graphed
	graphable = new Graphable();
	Double value;

	for(TradingDate date = quoteBundle.getFirstDate();
	    date.compareTo(quoteBundle.getLastDate()) <= 0;
	    date = date.next(1)) {

	    try {
		value = new Double(quoteBundle.getQuote(symbol, quote, date));
		graphable.putY((Comparable)date, value);
	    }
	    catch(MissingQuoteException e) {
                // ignore
	    }
	}	

        // Make sure we contain at least one value!
        assert graphable.getXRange().size() > 0;
    }

    public Graphable getGraphable() {
	return graphable;
    }

    public String getName() {
	return symbol.toString();
    }

    public int getType() {
	return GraphSource.SYMBOL;
    }

    //FIXME For some reason, adjustments aren't reversible, 
    //ie split 2 for 1 from some date. Reverse Split 1 for 2 from same date
    //doesn't work.

    public void adjust(int type, double value, Comparable startPoint, boolean forward) {
	double newValue = 0.0;

	if (forward) {
	    Comparable last = graphable.getEndX();	    
	    Iterator iterator = graphable.iterator();
	    //Advance the iterator until we're at the offset point

	    while (iterator.hasNext()) {
		Comparable X = (Comparable)iterator.next();
		
		if (X.compareTo(startPoint) < 0) {
		    continue;
		}
		
		Double prevValue = graphable.getY(X);
		newValue = getNewValue(type, prevValue.doubleValue(), value);

		graphable.putY(X, new Double(newValue));
	    }	    
	} else {
	    Comparable last = graphable.getStartX();	    
	    Iterator iterator = graphable.iterator();

	    while (iterator.hasNext()) {
		Comparable X = (Comparable)iterator.next();
		
		//Past the date point of adjustment - leave rest of data alone.
		if (X.compareTo(startPoint) > 0) {
		    break;
		}		

		Double prevValue = graphable.getY(X);
		newValue = getNewValue(type, prevValue.doubleValue(), value);

		graphable.putY(X, new Double(newValue));
	    }
	}
    }
    
    private double getNewValue(int type, double oldValue, double operand) {
	double rv = 0.0;

	switch (type) {
	case Adjustment.ADJUST_SPLIT:
	    assert operand != 0;
	    rv = oldValue / operand;
	    break;
	case Adjustment.ADJUST_DIVIDEND:	     
	    rv = oldValue + operand;
	    //Don't adjust into negative territory
	    if (rv <  0.0) {
		rv = 0.0;
	    }
	    break;
	default:
	    assert false;
	}
	return rv;
    }
    

    public String getToolTipText(Comparable x) {
	// In OHLCV graphs the x axis is in dates
	TradingDate date = (TradingDate)x;

	try {	
	    if(quote == Quote.DAY_VOLUME) {
		return
		    new String("<html>" +
			       symbol +
			       ", " +
			       date.toLongString() +
			       "<p>" +
			       Math.round(quoteBundle.
					  getQuote(symbol,
						   Quote.DAY_VOLUME,
						   date)) +
			       "</html>");
	    }
	    else {
                double dayLow = quoteBundle.getQuote(symbol, Quote.DAY_LOW, date);
                double dayHigh = quoteBundle.getQuote(symbol, Quote.DAY_HIGH, date);
                double dayOpen = quoteBundle.getQuote(symbol, Quote.DAY_OPEN, date);
                double dayClose = quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, date);

		return
		    new String("<html>" +
			       symbol +
			       ", " +
			       date.toLongString() +
			       "<p>" +
			       "<font color=red>" +
			       QuoteFormat.quoteToString(dayLow) +
			       " </font>" +
			       "<font color=green>" +
			       QuoteFormat.quoteToString(dayHigh) +
			       " </font>" +
			       QuoteFormat.quoteToString(dayOpen) +
			       " " +
			       QuoteFormat.quoteToString(dayClose) +
			       "</html>");
	    }
	}
	catch(MissingQuoteException e) {
	    return null;
	}
    }

    public String getYLabel(double value) {
	if(quote == Quote.DAY_VOLUME) {
	    final double BILLION = 1000000000D;
	    final double MILLION = 1000000D;
	    String extension = "";
	
	    if(Math.abs(value) >= BILLION) {
		value /= BILLION;
		extension = "B";
	    }
	    else if(Math.abs(value) >= MILLION) {
		value /= MILLION;
		extension = "M";
	    }
	
	    return Integer.toString((int)value) + extension;
	}
	else {
	    return Money.toString(value);
	}
    }

    public double[] getAcceptableMajorDeltas() {

	if(quote == Quote.DAY_VOLUME) {
	    double[] major = {10D,
			     100D,
			     1000D, // 1T
			     10000D,
			     100000D,
			     1000000D, // 1M
			     10000000D,
			     100000000D,
			     1000000000D, // 1B
			     10000000000D};
	    return major;
	}
	else {
	    double[] major = {0.001D, // 0.1c
			     0.01D, // 1c
			     0.1D, // 10c
			     1.0D, // $1
			     10.0D, // $10
			     100.0D, // $100
			     1000.0D}; // $1000
	    return major;	
	}
    }

    public double[] getAcceptableMinorDeltas() {
	if(quote == Quote.DAY_VOLUME) {
	    double[] minor = {1D, 1.5D, 2D, 2.5D, 3D, 4D, 5D, 6D, 8D};
	    return minor;
	}
	else {
	    double[] minor = {1D, 1.1D, 1.25D, 1.3333D, 1.5D, 2D, 2.25D,
			     2.5D, 3D, 3.3333D, 4D, 5D, 6D, 6.5D, 7D, 7.5D,
			     8D, 9D};
	    return minor;
	}
    }

}
