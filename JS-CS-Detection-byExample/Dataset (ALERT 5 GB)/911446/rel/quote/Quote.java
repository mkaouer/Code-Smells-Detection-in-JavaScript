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

import nz.org.venice.util.TradingDate;

/**
 * Representation of either an end-of-day or an intra-day stock quote. This
 * interface allows code to manipulate both quote kinds without writing
 * specific code to handle each.
 *
 * @author Andrew Leppard
 */
public interface Quote {

    /**
     * Represents day close quote for end-of-day quotes or
     * last quote for intra-day quotes.
     */
    public static final int DAY_CLOSE = 0;

    /** Represents day open quote */
    public static final int DAY_OPEN = 1;

    /** 
     * Represents day low quote for end-of-day quotes or
     * current day low for intra-day quotes.
     */
    public static final int DAY_LOW = 2;

    /**
     * Represents day high quote for end-of-day quotes or
     * current day high for intra-day quotes.
     */
    public static final int DAY_HIGH = 3;

    /** 
     * Represents day volume quote for end-of-day quotes or
     * current volume for intra-day quotes.
     */
    public static final int DAY_VOLUME = 4;

    /** Represents current bid. */
    public static final int BID = 5;

    /** Represents current ask. */
    public static final int ASK = 6;

    /**
     * Return the stock's symbol.
     *
     * @return	the symbol
     */
    public Symbol getSymbol();

    /**
     * Return the quote date.
     *
     * @return	the date
     */
    public TradingDate getDate();

    /**
     * Return the day volume quote for end-of-day quotes or the
     * current volume for intra-day quotes.
     *
     * @return	the volume
     */
    public long getDayVolume();

    /**
     * Return the day low for end-of-day quotes or the
     * current day low for intra-day quotes.
     *
     * @return	the day low
     */
    public double getDayLow();

    /**
     * Return the day high for end-of-day quotes or the
     * current day high for intra-day quotes.
     *
     * @return	the day high
     */
    public double getDayHigh();

    /**
     * Return the day open.
     *
     * @return	the day open
     */
    public double getDayOpen();

    /**
     * Return the day close for end-of-day quotes or the last
     * quote for intra-day quotes.
     *
     * @return	the day close
     */
    public double getDayClose();

    /**
     * Get a single quote.
     *
     * @param	quote	the quote type: 
     *                  {@link #DAY_OPEN},
     *                  {@link #DAY_CLOSE},
     *                  {@link #DAY_HIGH},
     *                  {@link #DAY_LOW},
     *                  {@link #DAY_VOLUME},
     *                  {@link #BID}, or
     *                  {@link #ASK}
     * @exception UnsupportedOperationException if the quote type is not
     *            supported by the quote. For example, end of day quotes
     *            do not contain bid or ask prices.
     */
    public double getQuote(int quote)
        throws UnsupportedOperationException;
}