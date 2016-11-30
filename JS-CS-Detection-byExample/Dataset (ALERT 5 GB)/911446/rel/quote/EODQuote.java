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

import java.util.ArrayList;
import java.util.List;

import nz.org.venice.ui.QuoteFormat;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;

/**
 * Representation of an end-of-day stock quote for a given stock on a given date.
 *
 * @author Andrew Leppard
 */
public class EODQuote implements Quote {
    private Symbol symbol;
    private TradingDate date;
    private long day_volume;
    private double day_low;
    private double day_high;
    private double day_open;
    private double day_close;

    /** Quotes before this year are considered suspect and raise warnings. */
    private static final int SUSPECT_YEAR = 1900;
    private static final TradingDate SUSPECT_DATE = new TradingDate(SUSPECT_YEAR, 0, 0);

    /** Difference in quote prices (fraction of a cent) before they are assumed to be the same. */
    private static final double EPSILON = 0.000001D;

    /**
     * Create a new end-of-day stock quote for the given date.
     *
     * @param	symbol	        the stock symbol
     * @param	date	        the date of this stock quote
     * @param	day_volume	the number of shares traded on this date
     * @param	day_low	        the lowest quote on this date
     * @param	day_high	the highest quote on this date
     * @param	day_open	the opening quote on this date
     * @param	day_close	the closing quote on this date
     */
    public EODQuote(Symbol symbol, TradingDate date,
                    long day_volume, double day_low, double day_high,
                    double day_open, double day_close) {
	setSymbol(symbol);
	setDate(date);

	this.day_volume = day_volume;
	this.day_low = day_low;
	this.day_high = day_high;
	this.day_open = day_open;
	this.day_close = day_close;
    }

    /**
     * Clean up the quote. This fixes any irregularities that might exist in the quote,
     * such as the day low not being the lowest quote.
     *
     * @exception QuoteFormatException if there were any problems with the quote
     */
    public void verify()
        throws QuoteFormatException {

        List messages = new ArrayList();

        if(date.before(SUSPECT_DATE))
            messages.add(Locale.getString("SUSPECT_YEAR", SUSPECT_YEAR));

        if(date.after(new TradingDate()))
            messages.add(Locale.getString("FUTURE_DATE"));

        if(date.isWeekend())
           messages.add(Locale.getString("WEEKEND_QUOTE"));

        if(day_low > day_open || day_low > day_close || day_low > day_high) {
            messages.add(Locale.getString("DAY_LOW_NOT_LOWEST"));
            day_low = Math.min(Math.min(day_open, day_close), day_high);
        }

        if(day_high < day_open || day_high < day_close || day_high < day_low) {
            messages.add(Locale.getString("DAY_HIGH_NOT_HIGHEST"));
            day_high = Math.max(Math.max(day_open, day_close), day_low);
        }

        if(day_low < 0) {
            messages.add(Locale.getString("DAY_LOW_LESS_THAN_ZERO"));
            day_low = 0;
        }

        if(day_high < 0) {
            messages.add(Locale.getString("DAY_HIGH_LESS_THAN_ZERO"));
            day_high = 0;
        }

        if(day_open < 0) {
            messages.add(Locale.getString("DAY_OPEN_LESS_THAN_ZERO"));
            day_open = 0;
        }

        if(day_close < 0) {
            messages.add(Locale.getString("DAY_CLOSE_LESS_THAN_ZERO"));
            day_close = 0;
        }

        if(day_volume < 0) {
            messages.add(Locale.getString("DAY_VOLUME_LESS_THAN_ZERO"));
            day_volume = 0;
        }

        if(messages.size() > 0)
            throw new QuoteFormatException(messages);
    }

    /**
     * Return the stock's symbol.
     *
     * @return	the symbol
     */
    public Symbol getSymbol() {
	return symbol;
    }

    /**
     * Return the quote date.
     *
     * @return	the date
     */
    public TradingDate getDate() {
	return date;
    }

    /**
     * Return the volume.
     *
     * @return	the volume
     */
    public long getDayVolume() {
	return day_volume;
    }

    /**
     * Return the day low.
     *
     * @return	the day low
     */
    public double getDayLow() {
	return day_low;
    }

    /**
     * Return the day high.
     *
     * @return	the day high
     */
    public double getDayHigh() {
	return day_high;
    }

    /**
     * Return the day open.
     *
     * @return	the day open
     */
    public double getDayOpen() {
	return day_open;
    }

    /**
     * Return the day close.
     *
     * @return	the day close
     */
    public double getDayClose() {
	return day_close;
    }

    /**
     * Set the symbol for this quote.
     *
     * @param	symbol	the stock symbol
     */
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Set the quote date.
     *
     * @param	date	the date
     */

    public void setDate(TradingDate date) {
	this.date = date;
    }

    /**
     * Compare the two stock quotes for equality.
     *
     * @param	quote	the quote to compare against
     * @return	<code>1</code> if they are equal; <code>0</code> otherwise
     */
    public boolean equals(Object quote) {
        return equals((EODQuote)quote);
    }

    /**
     * Compare the two stock quotes for equality.
     *
     * @param	quote	the quote to compare against
     * @return	<code>1</code> if they are equal; <code>0</code> otherwise
     */
    public boolean equals(EODQuote quote) {
        return (getSymbol().equals(quote.getSymbol()) &&
                getDate().equals(quote.getDate()) &&
                Math.abs(getDayLow() - quote.getDayLow()) < EPSILON &&
                Math.abs(getDayHigh() - quote.getDayHigh()) < EPSILON &&
                Math.abs(getDayOpen() - quote.getDayOpen()) < EPSILON &&
                Math.abs(getDayClose() - quote.getDayClose()) < EPSILON &&
                getDayVolume() == quote.getDayVolume());
    }

    public double getQuote(int quote)
        throws UnsupportedOperationException {

	switch(quote) {
	case(DAY_OPEN):
	    return getDayOpen();
	case(DAY_CLOSE):
	    return getDayClose();
	case(DAY_LOW):
	    return getDayLow();
	case(DAY_HIGH):
	    return getDayHigh();
	case(DAY_VOLUME):
	    return getDayVolume();
	default:
            throw new UnsupportedOperationException();
	}
    }

    /**
     * Return a string representation of the stock quote.
     *
     * @return	a string representation of the stock quote.
     */
    public String toString() {
	return new String(getSymbol() + ", " +
                          getDate() + ", " +
			  QuoteFormat.quoteToString(getDayOpen()) + ", " +
                          QuoteFormat.quoteToString(getDayHigh()) + ", " +
			  QuoteFormat.quoteToString(getDayLow()) + ", " +
                          QuoteFormat.quoteToString(getDayClose()) + ", " +
			  getDayVolume());

    }
}
