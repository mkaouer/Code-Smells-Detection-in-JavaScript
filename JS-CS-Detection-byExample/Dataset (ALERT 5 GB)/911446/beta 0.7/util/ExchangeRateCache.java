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

package org.mov.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

import javax.swing.JDesktopPane;

import org.mov.prefs.PreferencesManager;
import org.mov.quote.DatabaseQuoteSource;
import org.mov.quote.ImportExportException;
import org.mov.quote.QuoteSourceManager;
import org.mov.quote.YahooExchangeRateImport;
import org.mov.ui.DesktopManager;
import org.mov.ui.NumberDialog;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;

/**
 * Cache of exchange rates. This class caches exchange rates in memory from the
 * database. It is also responsible for importing the exchange rates, saving them
 * into the database, loading them from database, and querying the user for
 * exchange rates. It also provides helper functions to make performing cross-currency
 * calculations easier.
 *
 * @author Andrew Leppard
 */
public class ExchangeRateCache {

    // Singleton instance of this class
    private static ExchangeRateCache instance = null;

    // Map between the pair (source currency, destination currency) and a map
    // which maps trading dates to exchange rates. See getKey().
    private Map currencyMap;
    
    // Reference to the desktop so that the cache can display dialogs
    private JDesktopPane desktopPane;

    // Class should only be constructed once by this class
    private ExchangeRateCache() {
        currencyMap = new HashMap();
        desktopPane = null;
    }

    /**
     * Create or return the singleton instance of the exchange rate cache.
     *
     * @return singleton instance of this class
     */
    public static synchronized ExchangeRateCache getInstance() {
	if(instance == null)
	    instance = new ExchangeRateCache();

        return instance;
    }

    /**
     * Inform the exchange rate cache of the desktop which will be used
     * to display dialogs.
     *
     * @param desktopPane the desktop pane to use
     */
    public void setDesktopPane(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }

    /**
     * Return the exchange rate between the two currencies on the given date.
     *
     * @param date                the date for the exchange
     * @param sourceCurrency      the source currency to convert from
     * @param destinationCurrency the destination currency to convert to
     * @param the rate to change the source currency into the destination currency.
     */
    public synchronized double getRate(TradingDate date, Currency sourceCurrency,
                                       Currency destinationCurrency) {
        Double rate = null;

        // If the source and destination currencies are the same then return immediately.
        if(sourceCurrency.equals(destinationCurrency))
            return 1.0D;

        // Have we loaded the exchange rates from the database between these two currencies?
        TreeMap exchangeMap = (TreeMap)currencyMap.get(getKey(sourceCurrency,
                                                              destinationCurrency));

        // If we have no values, load exchange rates from the database.
        if(exchangeMap == null) {
            loadExchangeRates(sourceCurrency, destinationCurrency);
            exchangeMap = (TreeMap)currencyMap.get(getKey(sourceCurrency, destinationCurrency));
        }

        // Is the rate in the cache?
        if(exchangeMap != null) {
            rate = (Double)exchangeMap.get(date);
            if(rate != null)
                return rate.doubleValue();
        }

        // If the date is not newer than any in the cache, find the first date
        // after that date. Return the exchange rate on that date.
        if(exchangeMap != null && exchangeMap.size() > 0) {
            TradingDate latestDate = (TradingDate)exchangeMap.lastKey();
            if(latestDate.after(date)) {
                Set dateSet = exchangeMap.keySet();
                for(Iterator iterator = dateSet.iterator(); iterator.hasNext();) {
                    TradingDate iteratorDate = (TradingDate)iterator.next();
                    if(iteratorDate.after(date) || !iterator.hasNext()) {
                        rate = (Double)exchangeMap.get(iteratorDate);
                        assert rate != null;
                        return rate.doubleValue();
                    }
                }
            }
        }

        // If the date is newer than any in the cache, then import a new rate from the internet.
        rate = importExchangeRate(sourceCurrency, destinationCurrency);
        if (rate != null)
            return rate.doubleValue();

        // Otherwise ask the user for the exchange rate.
        rate = queryExchangeRate(date, sourceCurrency, destinationCurrency);

        return rate.doubleValue();
    }

    /**
     * Add the two given monies. This function will perform currency conversion
     * if necessary.
     *
     * @param date             the date for the exchange
     * @param destinationMoney the "destination" money.
     * @param sourceMoney      the "source" money
     * @return the sum of the two monies in the same currency as the "destination" money.
     */
    public Money add(TradingDate date, Money destinationMoney, Money sourceMoney) {
        if(!destinationMoney.getCurrency().equals(sourceMoney.getCurrency()))
            sourceMoney = sourceMoney.exchange(destinationMoney.getCurrency(),
                                               getRate(date,
                                                       sourceMoney.getCurrency(),
                                                       destinationMoney.getCurrency()));
        return destinationMoney.add(sourceMoney);        
    }

    /**
     * Subtract the two given monies. The "source" money will be subtracted from
     * the "destination" money. This function will perform currency conversion
     * if necessary.
     *
     * @param date             the date for the exchange
     * @param destinationMoney the "destination" money.
     * @param sourceMoney      the "source" money
     * @return the subtraction of "source" money from "destination" money in the same
     *         currency as the "destination" money.
     */
    public Money subtract(TradingDate date, Money destinationMoney, Money sourceMoney) {
        if(!destinationMoney.getCurrency().equals(sourceMoney.getCurrency()))
            sourceMoney = sourceMoney.exchange(destinationMoney.getCurrency(),
                                               getRate(date,
                                                       sourceMoney.getCurrency(),
                                                       destinationMoney.getCurrency()));
        return destinationMoney.subtract(sourceMoney);        
    }

    /**
     * Exchange the given money for the given currency. Use the exchange rate that
     * is valid on the given date.
     *
     * @param date     the date for the exchange
     * @param money    the money to convert
     * @param currency the currency to convert to
     * @return the converted currency.
     */
    public Money exchange(TradingDate date, Money money, Currency currency) {
        if(!money.getCurrency().equals(currency))
            money = money.exchange(currency, getRate(date, money.getCurrency(), currency));

        return money;
    }

    /**
     * Load all the exchange rates that convert between the given currencies from the
     * database. The exchange rates will be stored in the cache.
     *
     * @param sourceCurrency      the source currency to convert from
     * @param destinationCurrency the destination currency to convert to
     */
    private void loadExchangeRates(Currency sourceCurrency, Currency destinationCurrency) {
        List exchangeRates = QuoteSourceManager.getSource().getExchangeRates(sourceCurrency,
                                                                             destinationCurrency);

        for(Iterator iterator = exchangeRates.iterator(); iterator.hasNext();) {
            ExchangeRate exchangeRate = (ExchangeRate)iterator.next();
            addToCache(exchangeRate);
        }
    }

    /**
     * Import the latest exchange rate to convert between the two given currencies.
     * The exchange rate will be imported from Yahoo Finance.
     *
     * @param sourceCurrency      the source currency to convert from
     * @param destinationCurrency the destination currency to convert to
     * @return The exchange rate imported or <code>null</code> if there was an error.
     */
    private Double importExchangeRate(Currency sourceCurrency, Currency destinationCurrency) {
        Double rate = null;

        try {
            ExchangeRate exchangeRate =
                YahooExchangeRateImport.importExchangeRate(sourceCurrency, destinationCurrency);

            addToDatabase(exchangeRate);
            addToCache(exchangeRate);

            rate = new Double(exchangeRate.getRate());
        } catch(ImportExportException e) {
            // TODO: Work out how to display errors to the user without returning
            // control to the calling function.
        }

        return rate;
    }

    /**
     * Query the user to entire an exchange rate to convert from the given
     * source currency to the given destination currency.
     *
     * @param date                the date for the exchange rate
     * @param sourceCurrency      the source currency to convert from
     * @param destinationCurrency the destination currency to convert to
     * @return The exchange rate given by the user.
     */
    private Double queryExchangeRate(TradingDate date, Currency sourceCurrency,
                                     Currency destinationCurrency) {
        double defaultValue = 1.0D;

        // Find the latest exchange rate for the currencies. Use this as the default
        // exchange rate to display in the dialog. If none are found, simply use 1.0D.
        TreeMap exchangeMap = (TreeMap)currencyMap.get(getKey(sourceCurrency,
                                                              destinationCurrency));

        if(exchangeMap != null && exchangeMap.size() > 0) {
            TradingDate latestDate = (TradingDate)exchangeMap.lastKey();
            Double latestRate = (Double)exchangeMap.get(latestDate);
            if(latestRate != null)
                defaultValue = latestRate.doubleValue();
        }

        // Now display a dialog querying the user to enter the exchange rate.
        Double value = NumberDialog.getDouble(desktopPane,
                                              Locale.getString("EXCHANGE_RATE_TITLE"),
                                              Locale.getString("EXCHANGE_RATE_PROMPT",
                                                               sourceCurrency.toString(),
                                                               destinationCurrency.toString(),
                                                               date.toString()),
                                              defaultValue);

        // If the user supplies a value, cache it, and use that.
        if (value != null) {
            // Cache value if given
            ExchangeRate rate = new ExchangeRate(date, sourceCurrency, destinationCurrency,
                                                 value.doubleValue());
            addToDatabase(rate);
            addToCache(rate);
        }

        // Otherwise just use the default rate we calculated above.
        else
            value = new Double(defaultValue);

        return value;
    }

    /**
     * Return the key to use in the cache's <code>currencyMap<code> to extract another
     * map that maps trading dates to exchange rates for converting from the given
     * source currency to the given destination currency.
     *
     * @param sourceCurrency the source currency to convert from
     * @param destinationCurrency the destination currency to convert to
     * @return key for <code>currencyMap</code>
     */
    private Object getKey(Currency sourceCurrency, Currency destinationCurrency) {
        return sourceCurrency.getCurrencyCode() + destinationCurrency.getCurrencyCode();
    }

    /**
     * Store the given exchange rate in the database.
     *
     * @param rate the exchange rate to store in the database.
     */
    private void addToDatabase(ExchangeRate rate) {
        // If we aren't using a database, then no import is necessary.
        int quoteSource = PreferencesManager.getQuoteSource();
        if(quoteSource == PreferencesManager.DATABASE ||
           quoteSource == PreferencesManager.INTERNAL) {
            DatabaseQuoteSource databaseQuoteSource =
                (DatabaseQuoteSource)QuoteSourceManager.getSource();
            List list = new ArrayList();

            list.add(rate);
            databaseQuoteSource.importExchangeRates(list);
        }
    }

    /**
     * Add the given exchange rate to the cache.
     *
     * @param rate the exchange rate to cache
     */
    private void addToCache(ExchangeRate rate) {
        TreeMap exchangeMap = (TreeMap)currencyMap.get(getKey(rate.getSourceCurrency(),
                                                              rate.getDestinationCurrency()));
        if(exchangeMap == null) {
            exchangeMap = new TreeMap();
            currencyMap.put(getKey(rate.getSourceCurrency(),
                                   rate.getDestinationCurrency()),
                            exchangeMap);
        }

        exchangeMap.put(rate.getDate(), new Double(rate.getRate()));
    }
}