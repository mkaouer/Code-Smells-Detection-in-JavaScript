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

import java.util.*;

import org.mov.util.*;

/**
 * Representation of a stock quote for a given stock on a given date.
 */
public class Quote {
    private String symbol;
    private TradingDate date;
    private int volume;
    private float day_low;
    private float day_high;
    private float day_open;
    private float day_close;

    /** Represents day close quote */
    public static final int DAY_CLOSE = 0;

    /** Represents day open quote */
    public static final int DAY_OPEN = 1;

    /** Represents day low quote */
    public static final int DAY_LOW = 2;

    /** Represents day high quote */
    public static final int DAY_HIGH = 3;

    /** Represents day volume quote */
    public static final int DAY_VOLUME = 4;

    /**
     * Create a new stock quote for the given date.
     *
     * @param	symbol	the stock symbol
     * @param	date	the date for this stock quote
     * @param	volume	the number of shares traded on this date
     * @param	day_low	the lowest quote on this date
     * @param	day_high	the highest quote on this date
     * @param	day_open	the opening quote on this date
     * @param	day_close	the closing quote on this date
     */
    public Quote(String symbol, TradingDate date,
		 int volume, float day_low, float day_high,
		 float day_open, float day_close) {

	setSymbol(symbol);
	setDate(date);

	this.volume = volume;
	this.day_low = day_low;
	this.day_high = day_high;
	this.day_open = day_open;
	this.day_close = day_close;
    }

    /**
     * Return the stock's symbol.
     *
     * @return	the symbol
     */
    public String getSymbol() {
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
    public int getVolume() {
	return volume;
    }

    /**
     * Return the day low.
     *
     * @return	the day low
     */
    public float getDayLow() {
	return day_low;
    }

    /**
     * Return the day high.
     *
     * @return	the day high
     */
    public float getDayHigh() {
	return day_high;
    }

    /**
     * Return the day open.
     *
     * @return	the day open
     */
    public float getDayOpen() {
	return day_open;
    }

    /**
     * Return the day close.
     *
     * @return	the day close
     */
    public float getDayClose() {
	return day_close;
    }

    /**
     * Set the symbol for this quote.
     *
     * @param	symbol	the stock symbol
     */
    public void setSymbol(String symbol) {	
	if(symbol != null)
	    this.symbol = symbol.toLowerCase();
	else
	    this.symbol = null;
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
    public boolean equals(Quote quote) {
	if(getSymbol().equals(quote.getSymbol()) &&
	   getDate().equals(quote.getDate()) &&
	   getDayLow() == quote.getDayLow() &&
	   getDayHigh() == quote.getDayHigh() &&
	   getDayOpen() == quote.getDayOpen() &&
	   getDayClose() == quote.getDayClose() &&
	   getVolume() == quote.getVolume())
	    return true;
	else
	    return false;
    }

    /**
     * Get a single quote.
     *
     * @param	quote	the quote type <code>DAY_OPEN, DAY_CLOSE, DAY_HIGH,
     *			DAY_VOLUME</code> or <code>DAY_LOW</code>
     */
    public float getQuote(int quote) {

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
	    return getVolume();
	default:
	    assert false;
	    return 0.0F;
	}
    }

    /**
     * Return a string representation of the stock quote.
     *
     * @return	a string representation of the stock quote.
     */
    public String toString() {
	return new String(getSymbol() + ", " + getDate() + ", " +
			  getDayOpen() + ", " + getDayHigh() + ", " + 
			  getDayLow() + ", " + getDayClose() + ", " + 
			  getVolume());
			   
    }
}
