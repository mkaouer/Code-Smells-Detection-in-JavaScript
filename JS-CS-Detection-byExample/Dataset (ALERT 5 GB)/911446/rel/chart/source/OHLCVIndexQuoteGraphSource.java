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

import nz.org.venice.chart.*;
import nz.org.venice.ui.QuoteFormat;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.Locale;
import nz.org.venice.quote.*;

import java.util.*;

/**
 * Provides a <code>EODQuoteBundle</code> index source. This class
 * allows index sources for day Open, High, Low, Close and
 * Volume (OHLCV).
 */
public class OHLCVIndexQuoteGraphSource implements GraphSource {

    private EODQuoteBundle quoteBundle;
    private int quote;
    private Symbol symbol;
    private Graphable graphable;
    private int numSymbols;

    /**
     * Create a new graph source from the quote bundle with the given
     * quote type.
     *
     * @param	quoteBundle the quote bundle containing stock quotes
     * @param	quote	the quote kind, one of: {@link Quote#DAY_OPEN}, 
     * {@link Quote#DAY_CLOSE}, {@link Quote#DAY_HIGH} or 
     * {@link Quote#DAY_LOW}
     */
    public OHLCVIndexQuoteGraphSource(EODQuoteBundle quoteBundle, int quote) {	
	this.quote = quote;
	this.quoteBundle = quoteBundle;
	
	List symbolList = quoteBundle.getAllSymbols();
	numSymbols = symbolList.size();

	assert numSymbols > 0;

	// Build graphable so this source can be directly graphed
	graphable = new Graphable();
	Double value;
	double temp;
	boolean addValue = true;

	for(TradingDate date = quoteBundle.getFirstDate(); 
	    date.compareTo(quoteBundle.getLastDate()) <= 0;
	    date = date.next(1)) {

	    int valueCnt = 0;

	    temp = 0.0;
	    addValue = false;

	    for (int i = 0; i < numSymbols; i++) {
		symbol = (Symbol)symbolList.get(i);
		try {
		    value = new Double(quoteBundle.getQuote(symbol, quote, date));
		    temp += value.doubleValue();
		    valueCnt++;
		    addValue = true;
		}
		catch(MissingQuoteException e) {
		    // ignore
		    //addValue = false;
		}
	    }

	    if (addValue && temp != 0.0) {
		// Simple average for index	   
		temp = temp / valueCnt;
		value = new Double(temp);
    
		graphable.putY((Comparable)date, value);
	    }
	    
	}
        
        // Make sure we contain at least one value!
        assert graphable.getXRange().size() > 0;        
    }

    public Graphable getGraphable() {
	return graphable;
    }

    public String getName() {
	String rv = Locale.getString("INDEX");

	Iterator iterator = quoteBundle.getAllSymbols().iterator();
	while (iterator.hasNext()) {
	    Symbol symbol = (Symbol)iterator.next();
	    rv += (" " + symbol.toString() + " ");
	}
	return rv;
    }

    public int getType() {
	return GraphSource.INDEX;
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

    //I don't think Indeces get corrected for ex/div and splits.
    public void adjust(int type, double adjustValue, Comparable start, boolean direction) {
		
    }

}
