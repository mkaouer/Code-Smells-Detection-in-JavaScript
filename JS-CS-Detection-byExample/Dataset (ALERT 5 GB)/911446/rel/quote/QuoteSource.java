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

import java.util.List;
import java.util.HashMap;

import nz.org.venice.util.Currency;
import nz.org.venice.util.TradingDate;

/**
 * Provides a generic interface in which we can query stock quotes from
 * multiple sources. The source could either be directly from files,
 * a database, a unique internal format or from the internet.
 *
 * @author Andrew Leppard
 */
public interface QuoteSource {

    /**
     * Returns the company name associated with the given symbol.
     *
     * @param	symbol	the stock symbol
     * @return	the company name
     */
    public String getSymbolName(Symbol symbol);

    /**
     * Returns the symbol associated with the given company.
     *
     * @param	partialCompanyName	a partial company name
     * @return	the company symbol
     */
    public Symbol getSymbol(String partialCompanyName);

    /**
     * Returns whether we have any quotes for the given symbol.
     *
     * @param	symbol	the symbol we are searching for
     * @return	whether the symbol was found or not
     */
    public boolean symbolExists(Symbol symbol);

    /**
     * Return the latest date we have any stock quotes for.
     *
     * @return	the most recent quote date
     */
    public TradingDate getLastDate();

    /**
     * Return the earliest date we have any stock quotes for.
     *
     * @return	the oldest quote date
     */
    public TradingDate getFirstDate();

    /**
     * Load the given quote range into the quote cache.
     *
     * @param	quoteRange	the range of quotes to load
     * @return  <code>TRUE</code> if the operation suceeded
     * @see EODQuoteCache
     */
    public boolean loadQuoteRange(EODQuoteRange quoteRange);

    /**
     * Returns whether the source contains any quotes for the given date.
     *
     * @param date the date
     * @return wehther the source contains the given date
     */
    public boolean containsDate(TradingDate date);

    /**
     * Return all the dates which we have quotes for.
     *
     * @return	a vector of dates
     */
    public List getDates();

    /**
     * Is the given symbol a market index?
     *
     * @param	symbol to test
     * @return	yes or no
     */
    public boolean isMarketIndex(Symbol symbol);

    /**
     * Return the advance/decline for the given date. This returns the number
     * of all ordinary stocks that rose (day close > day open) - the number of all
     * ordinary stocks that fell.
     *
     * @param date the date
     * @return the difference between the number of advances and declines for 
     *         date
     * @exception throw MissingQuoteException if the date isn't in the source
     */
    public int getAdvanceDecline(TradingDate date)
        throws MissingQuoteException;


    /**
     * Return the advance/decline for the given date range. This returns the number
     * of all ordinary stocks that rose (day close > day open) - the number of all
     * ordinary stocks that fell.
     *
     * @param startDate the start of the inclusive date range
     * @param endDate the end of the inclusive date range
     * @return A map of dates and advance/decline differences for each date in 
     *         the range 
     * @exception throw MissingQuoteException if the date range isn't in the source
     */
    public HashMap getAdvanceDecline(TradingDate startDate, TradingDate endDate)
        throws MissingQuoteException;


    /**
     * Return all the stored exchange rates between the two currencies.
     *
     * @param sourceCurrency the currency to convert from
     * @param destinationCurrency the currency to convert to
     * @return the exchange rate being the number of destinationCurrency that you can buy per
     *         sourceCurrency
     */
    public List getExchangeRates(Currency sourceCurrency, Currency destinationCurrency);

    /**
     * Shutdown the quote source.
     */
    public void shutdown();

    /**
     * Force the quote source to reload and not use cached values. 
     */
    public void cacheExpiry();
}
