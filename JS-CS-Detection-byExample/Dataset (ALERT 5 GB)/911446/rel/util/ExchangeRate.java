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

package nz.org.venice.util;

/**
 * An immutable representation of an Exchange Rate. An exchange rate is the conversion rate
 * when converting one currency into another.
 *
 * @author Andrew Leppard
 */
public class ExchangeRate {

    // The date of the exchange rate
    private TradingDate date;

    // The currency we are converting from
    private Currency source;

    // The currency we are converting to
    private Currency destination;

    // The exchange rate
    private double rate;

    /**
     * Create a new exchange rate that converts the source currency into the
     * destination currency.
     *
     * @param date the date of the exchange rate
     * @param source the currency we are converting from
     * @param destination the currency we are converting to
     * @param rate the exchange rate
     */
    public ExchangeRate(TradingDate date, Currency source, Currency destination, double rate) {
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.rate = rate;
    }

    /**
     * Return the date of the exchange rate.
     *
     * @return	the date
     */
    public TradingDate getDate() {
	return date;
    }

    /**
     * Return the currency that this exchange rate is converting from.
     *
     * @return the source currency
     */
    public Currency getSourceCurrency() {
        return source;
    }

    /**
     * Return the currency that this exchange rate is converting into.
     *
     * @return the destination currency
     */
    public Currency getDestinationCurrency() {
        return destination;
    }

    /**
     * Return the exchange rate when converting between the two currencies.
     * If you were converting from USD to AUD, and the exchange rate was
     * 1.33, you would get AUD $1.33 for every USD $1.00.
     *
     * @return the exchange rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * Return a string representation of this exchange rate.
     *
     * @return string representation
     */
    public String toString() {
        return (getSourceCurrency().toString() + ", " +
                getDestinationCurrency().toString() + ", " +
                getRate());
    }
}