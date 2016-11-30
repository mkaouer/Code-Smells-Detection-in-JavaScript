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
 * Provides a filter to parse the Meta Stock quote format. This
 * format uses 4 digit years and prices in dollars. The first
 * column is the symbol, then the date, open, high, low, close & volume.
 * Exampe:
 * <pre>
 * XXX,19990715,1.73,1.82,1.71,1.81,3648921
 * </pre>
 */
public class MetaStockQuoteFilter implements QuoteFilter {

    /**
     * Creates an instance of the filter.
     */
    public MetaStockQuoteFilter() {
	// nothing to do
    }
   
    /**
     * Return the name of the filter.
     *
     * @return	the name of the filter.
     */
    public String getName() {
	return "MetaStock";
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
	    String[] quoteParts = quoteLine.split(",");
	    int i = 0;
	    
	    if(quoteParts.length == 7) {
		String symbol = quoteParts[i++];
		TradingDate date = new TradingDate(quoteParts[i++],
						   TradingDate.BRITISH);
		float day_open = Float.parseFloat(quoteParts[i++]);
		float day_high = Float.parseFloat(quoteParts[i++]);
		float day_low = Float.parseFloat(quoteParts[i++]);
		float day_close = Float.parseFloat(quoteParts[i++]);
		int volume = Integer.parseInt(quoteParts[i++]);
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
	return new String(quote.getSymbol() + "," + 
			  quote.getDate().toString("yyyymmdd") + "," +
			  quote.getDayOpen() + "," +
			  quote.getDayHigh() + "," +
			  quote.getDayLow() + "," +
			  quote.getDayClose() + "," +
			  quote.getVolume());
    }
}
