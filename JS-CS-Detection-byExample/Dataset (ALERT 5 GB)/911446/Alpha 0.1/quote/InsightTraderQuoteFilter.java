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

package org.mov.quote;

import org.mov.util.TradingDate;

/**
 * Provides a filter to parse the Insight Trader quote format. This
 * format uses 2 digit years and prices in cents, volume is divided by 100.
 * The first column is the symbol, then the date, open, high, low, close & 
 * volume.
 * Exampe:
 * <pre>
 * XXX 07/15/99 173 182 171 181 36489
 * </pre>
 */
public class InsightTraderQuoteFilter implements QuoteFilter {

    /**
     * Creates an instance of the filter.
     */
    public InsightTraderQuoteFilter() {
	// nothing to do
    }
   
    /**
     * Return the name of the filter.
     *
     * @return	the name of the filter.
     */
    public String getName() {
	return "Insight Trader";
    }
    
    /**
     * Parse the given text string and returns the stock quote or null
     * if it did not contain a valid quote.
     *
     * @param	quoteList	a single line of text containing a quote
     * @return	the stock quote
     */
    public Quote toQuote(String quoteLine) {
	Quote quote = null;

	if(quoteLine != null) {
	    String[] quoteParts = quoteLine.split(" ");
	    int i = 0;
	    
	    if(quoteParts.length == 7) {
		String symbol = quoteParts[i++];
		TradingDate date = new TradingDate(quoteParts[i++],
						   TradingDate.US);

		// Convert all prices from cents to dollars
		float day_open = Float.parseFloat(quoteParts[i++]) / 100;
		float day_high = Float.parseFloat(quoteParts[i++]) / 100;
		float day_low = Float.parseFloat(quoteParts[i++]) / 100;
		float day_close = Float.parseFloat(quoteParts[i++]) / 100;

		// Convert volume from 1/100th volume to real volume
		int volume = Integer.parseInt(quoteParts[i++]) * 100;
		quote = new Quote(symbol, date, volume, day_low, day_high,
				  day_open, day_close);
	    }	    
	}
	return quote;
    }

    /**
     * Convert the given stock quote to a string line.
     *
     * @param	quote	a stock quote
     * @return	string version of the quote
     */
    public String toString(Quote quote) {
	return new String(quote.getSymbol() + " " + 
			  quote.getDate().toString("mm/dd/yy") + " " +
			  Math.round(quote.getDayOpen()*100) + " " +
			  Math.round(quote.getDayHigh()*100) + " " +
			  Math.round(quote.getDayLow()*100) + " " +
			  Math.round(quote.getDayClose()*100) + " " +
			  quote.getVolume() / 100);
    }
}
