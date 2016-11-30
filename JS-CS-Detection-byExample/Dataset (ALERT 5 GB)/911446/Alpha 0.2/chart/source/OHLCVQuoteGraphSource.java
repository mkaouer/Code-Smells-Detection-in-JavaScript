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
import org.mov.quote.*;

import java.util.*;

/**
 * Provides a <code>QuoteBundle</code> graph source. This class
 * allows graph sources for day Open, High, Low, Close and
 * Volume (OHLCV).
 */
public class OHLCVQuoteGraphSource implements GraphSource {

    private QuoteBundle quoteBundle;
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
    public OHLCVQuoteGraphSource(QuoteBundle quoteBundle, int quote) {
	this.quote = quote;
	this.quoteBundle = quoteBundle;

	// Should only be a single symbol in the quote bundle anyway
	symbol = quoteBundle.getFirstSymbol();

	// Build graphable so this source can be directly graphed
	graphable = new Graphable();
	Float value;

	for(TradingDate date = quoteBundle.getFirstDate();
	    date.compareTo(quoteBundle.getLastDate()) <= 0;
	    date = date.next(1)) {

	    try {
		value = new Float(quoteBundle.getQuote(symbol, quote, date));
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
		return
		    new String("<html>" +
			       symbol + 
			       ", " +
			       date.toLongString() +
			       "<p>" +
			       "<font color=red>" + 
			       quoteBundle.getQuote(symbol, 
					      Quote.DAY_LOW, date) +
			       " </font>" +
			       "<font color=green>" + 
			       quoteBundle.getQuote(symbol, 
					      Quote.DAY_HIGH, date) + 
			       " </font>" +
			       quoteBundle.getQuote(symbol, 
					      Quote.DAY_OPEN, date) +
			       " " + 
			       quoteBundle.getQuote(symbol, 
					      Quote.DAY_CLOSE, date) +
			       "</html>");
	    }
	}
	catch(MissingQuoteException e) {
	    return null;
	}
    }

    public String getYLabel(float value) {
	if(quote == Quote.DAY_VOLUME) {
	    final float BILLION = 1000000000F;
	    final float MILLION = 1000000F;
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
	    return PriceFormat.priceToString(value);
	}
    }

    public float[] getAcceptableMajorDeltas() {

	if(quote == Quote.DAY_VOLUME) {
	    float[] major = {10F,
			     100F,
			     1000F, // 1T
			     10000F,
			     100000F,
			     1000000F, // 1M
			     10000000F,
			     100000000F,
			     1000000000F, // 1B
			     10000000000F}; 
	    return major;
	}
	else {
	    float[] major = {0.001F, // 0.1c
			     0.01F, // 1c
			     0.1F, // 10c
			     1.0F, // $1
			     10.0F, // $10
			     100.0F, // $100
			     1000.0F}; // $1000
	    return major;	    
	}
    }

    public float[] getAcceptableMinorDeltas() {
	if(quote == Quote.DAY_VOLUME) {
	    float[] minor = {1F, 1.5F, 2F, 2.5F, 3F, 4F, 5F, 6F, 8F};
	    return minor;
	}
	else {
	    float[] minor = {1F, 1.1F, 1.25F, 1.3333F, 1.5F, 2F, 2.25F, 
			     2.5F, 3F, 3.3333F, 4F, 5F, 6F, 6.5F, 7F, 7.5F, 
			     8F, 9F};
	    return minor;
	}
    }
}
