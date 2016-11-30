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

import nz.org.venice.util.Currency;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;
import nz.org.venice.util.ExchangeRate;
import nz.org.venice.util.Locale;
import nz.org.venice.util.UnknownCurrencyCodeException;

/**
 * Provides a filter to parse currency exchange rates supplied from Yahoo.
 * The currency exchange rate format contains the two currencies being exchanged,
 * the current price, the date of the quote, the time of the quote, and
 * the bid and ask rates.
 *
 * Example:
 * <pre>
 * "USDJPY=X", 115.845,	1/25/2006, 5:10pm, 115.83, 115.86
 * </pre>
 *
 * @author Andrew Leppard
 */
public class YahooExchangeRateFilter {

    /**
     * Create an instance of this filter.
     */
    public YahooExchangeRateFilter() {
        // Nothing to do
    }

    /**
     * Parse the given text string and return the exchange rate.
     *
     * @param line a single line of text containing the exchange rate
     * @exception ExchangeRateFormatException if the line could not be parsed
     * @return the exchange rate
     */
    public ExchangeRate toExchangeRate(String line)
        throws ExchangeRateFormatException {
 
        assert line != null;

        String[] parts = line.split(",");
        int i = 0;
        
        // Parse to/from currencies.
        
        // It's actually 6 but we ignore the other entries, as it makes sense to
        // try to be robust and not blow up if Yahoo adds or removes an entry.
        if(parts.length < 3)
            throw new ExchangeRateFormatException(Locale.getString("WRONG_FIELD_COUNT"));
        
        if(parts[i].length() != 10)
            throw new ExchangeRateFormatException(Locale.getString("UNKNOWN_CURRENCY_CODE",
                                                                   parts[i]));
        
        Currency sourceCurrency = null;
        Currency destinationCurrency = null;
        
        String sourceCurrencyCode = parts[i].substring(1, 4);      // "USDJPY=X" -> USD
        String destinationCurrencyCode = parts[i].substring(4, 7); // "USDJPY=X" -> JPY
        
        try {
            sourceCurrency = new Currency(sourceCurrencyCode);
            destinationCurrency = new Currency(destinationCurrencyCode);
        }
        catch(UnknownCurrencyCodeException e) {
            throw new
                ExchangeRateFormatException(Locale.getString("UNKNOWN_CURRENCY_CODE",
                                                             e.getReason()));
        }
        
        i++;
        
        // Parse exchange rate
        double value = 0.0D;
        
        try {
            value = Double.parseDouble(parts[i]);
        }
        catch(NumberFormatException e) {
            throw new ExchangeRateFormatException(Locale.getString("ERROR_PARSING_NUMBER",
                                                                   parts[i]));
        }
        
        i++;

        // Parse date
        TradingDate date = null;
        String dateString = parts[i].replaceAll("\"", "");

        try {
            date = new TradingDate(dateString, TradingDate.US);
        }
        catch(TradingDateFormatException e) {
            throw new ExchangeRateFormatException(e.getMessage());
        }

        return new ExchangeRate(date, sourceCurrency, destinationCurrency, value);
    }
}
