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

import nz.org.venice.util.Locale;
import nz.org.venice.util.Report;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;

/**
 * Provides a filter to parse the Ezy Chart quote format. This
 * format uses 2 digit years and prices in cents. The first
 * column is the symbol, then the date, open, high, low, close & volume.
 * Exampe:
 * <pre>
 * XXX,990715,173,182,171,181,3648921
 * </pre>
 *
 * @author Andrew Leppard
 */
public class EzyChartQuoteFilter implements IFileEODQuoteFilter {

    /**
     * Creates an instance of the filter.
     */
    public EzyChartQuoteFilter() {
	// nothing to do
    }
   
    /**
     * Return the name of the filter.
     *
     * @return	the name of the filter.
     */
    public String getName() {
	return "Ezy Chart";
    }
    
    /**
     * Parse the given text string and returns the stock quote or null
     * if it did not contain a valid quote.
     *
     * @param	quoteLine	a single line of text containing a quote
     * @return	the stock quote
     * @exception QuoteFormatException if the quote could not be parsed
     */
    public EODQuote toEODQuote(String quoteLine) throws QuoteFormatException {
	EODQuote quote = null;

	if(quoteLine != null) {
	    String[] quoteParts = quoteLine.split(",");
	    int i = 0;
	    
	    if(quoteParts.length == 7) {
                Symbol symbol = null;

                try {
                    symbol = Symbol.find(quoteParts[i++]);
                }
                catch(SymbolFormatException e) {
                    throw new QuoteFormatException(e.getMessage());
                }

		TradingDate date = null;

                try {
                    date = new TradingDate(quoteParts[i++],
                                           TradingDate.US);
                }
                catch(TradingDateFormatException e) {
                    throw new QuoteFormatException(e.getMessage());
                }

		// Convert all prices from cents to dollars
                try {
                    double day_open = Double.parseDouble(quoteParts[i++]) / 100.0;
                    double day_high = Double.parseDouble(quoteParts[i++]) / 100.0;
                    double day_low = Double.parseDouble(quoteParts[i++]) / 100.0;
                    double day_close = Double.parseDouble(quoteParts[i++]) / 100.0;
                    long day_volume = Long.parseLong(quoteParts[i++]);
                    quote = new EODQuote(symbol, date, day_volume, day_low, day_high,
                                         day_open, day_close);
                } 
                catch(NumberFormatException e) {
                    throw new QuoteFormatException(Locale.getString("ERROR_PARSING_NUMBER",
                                                                    quoteParts[i - 1]));
                }
	    }	    
            else
                throw new QuoteFormatException(Locale.getString("WRONG_FIELD_COUNT"));
	}
	return quote;
    }

    /**
     * Convert the given stock quote to a string line.
     *
     * @param	quote	a stock quote
     * @return	string version of the quote
     */
    public String toString(EODQuote quote) {
	return new String(quote.getSymbol() + "," + 
			  quote.getDate().toString("yymmdd") + "," +
			  Math.round(quote.getDayOpen()*100.0) + "," +
			  Math.round(quote.getDayHigh()*100.0) + "," +
			  Math.round(quote.getDayLow()*100.0) + "," +
			  Math.round(quote.getDayClose()*100.0) + "," +
			  quote.getDayVolume());
    }

    public IFileEODQuoteImport getImporter(Report report) {
	return new FileEODQuoteImport(report, this);
    }
}
