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
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;

/**
 * Provides a filter to parse the Google end-of-day stock quote format.
 * This format uses a date with the month name, prices are in dollars.
 * The first column is the date, then open, high, low, close &
 * volume.
 *
 * This filter is not hooked up to the QuoteFilterList because we
 * cannot currently ask the user to enter the missing symbol.
 *
 * Example:
 * <pre>
 * 6-Jun-08,1.94,1.97,1.87,1.89,964300
 * </pre>
 *
 * @author Andrew Leppard
 */

public class GoogleEODQuoteFilter implements EODQuoteFilter {

    private Symbol symbol;

    /**
     * Creates an instance of the filter.
     */
    public GoogleEODQuoteFilter(Symbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Return the name of the filter.
     *
     * @return	the name of the filter.
     */
    public String getName() {
	return "Google";
    }

    /**
     * Parse the given text string and returns the stock quote or null
     * if it did not contain a valid quote.
     *
     * @param	quoteLine	a single line of text containing a quote
     * @exception QuoteFormatException if the quote could not be parsed
     * @return	the stock quote
     */
    public EODQuote toEODQuote(String quoteLine) throws QuoteFormatException {
	EODQuote quote = null;

        // Ignore blank lines and the header line ("Date,Open,High,Low,Close,Volume").
	if(quoteLine != null && !quoteLine.startsWith("Date,Open")) {
	    String[] quoteParts = quoteLine.split(",");
	    int i = 0;

	    if(quoteParts.length == 6) {
		TradingDate date = null;

                try {
                    date = new TradingDate(quoteParts[i++], TradingDate.US);
                }
                catch(TradingDateFormatException e) {
                    throw new QuoteFormatException(e.getMessage());
                }

                try {
                    double day_open = Double.parseDouble(quoteParts[i++]);
                    double day_high = Double.parseDouble(quoteParts[i++]);
                    double day_low = Double.parseDouble(quoteParts[i++]);
                    double day_close = Double.parseDouble(quoteParts[i++]);
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
        throw new UnsupportedOperationException();
    }
}
