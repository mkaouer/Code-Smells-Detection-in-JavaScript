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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.mov.util.Locale;
import org.mov.util.TradingDate;

/**
 * This class represents a way of describing a range or a set of end-of-day quotes. A range of
 * quotes will consist of a set of symbols that we are interested in. This can be
 * described by either explicitly giving a set of symbols, e.g. ANZ, CBA, NBA, WBC. Or by
 * specifying the type of symbols we are intersted in, e.g. all ordinaries, all symbols,
 * market indices etc.
 * <p>
 * The quote range is also limited by a date range.
 * <p>
 * For example this class could represent all CBA quotes from 1/12/2000 to 12/12/2000, or
 * all market indices.
 * <p>
 * A quote range represents a range or set of quotes. It does not represent the actual
 * quotes themselves. The class that represents the quotes in a quote range is
 * {@link QuoteBundle}.
 */
public class EODQuoteRange implements Cloneable {

    /** Represents all 3 letter symbols, not included market indices */
    public final static int ALL_ORDINARIES = 1;

    /** Represents all symbols */
    public final static int ALL_SYMBOLS = 2;

    /** Represents market indices */
    public final static int MARKET_INDICES = 3;

    /** Represents an explicit list of symbols */
    public final static int GIVEN_SYMBOLS = 4;

    // Given two quotes ranges - do their date ranges overlap?

    // There is no overlap
    private final static int NO_OVERLAP = 1;

    // There is partial overlap
    private final static int PARTIAL_OVERLAP = 2;

    // The first range is totally contained by the other
    private final static int CONTAINED = 3;

    // The first range contains the other range
    private final static int CONTAINS = 4;

    // The first and last dates in the quote range. If the first date is null
    // it indicates that we represent all dates available to us
    private TradingDate firstDate;
    private TradingDate lastDate;	

    // Only one of these two should be set
    private List symbols = null;

    // ALL_ORDINARIES, ALL_SYMBOLS, MARKET_INDICES or GIVEN_SYMBOLS
    private int type = 0;

    /**
     * Create a quote range that represents all the given symbols for all the
     * dates we have quotes.
     *
     * @param symbols   list of symbols
     */
    public EODQuoteRange(List symbols) {
	this.symbols = new ArrayList(symbols);
	this.type = GIVEN_SYMBOLS;
	this.firstDate = null;
	this.lastDate = null;
    }

    /**
     * Create a quote range that represents all the given symbols for all the
     * dates we have quotes.
     *
     * @param symbols   list of symbols
     */
    public EODQuoteRange(SortedSet symbols) {
	this.symbols = new ArrayList(symbols);
	this.type = GIVEN_SYMBOLS;
	this.firstDate = null;
	this.lastDate = null;
    }

    /**
     * Create a quote range that represents all the given symbols between
     * the first and the last dates given (inclusive).
     *
     * @param symbols   list of symbols
     * @param firstDate earliest date
     * @param lastDate  latest date
     */
    public EODQuoteRange(List symbols, TradingDate firstDate,
                         TradingDate lastDate) {
	this.symbols = new ArrayList(symbols);
	this.type = GIVEN_SYMBOLS;
	this.firstDate = firstDate;
	this.lastDate = lastDate;
    }

    /**
     * Create a quote range that represents all the given symbols on the given date.
     *
     * @param symbols   list of symbols
     * @param date      the date
     */
    public EODQuoteRange(List symbols, TradingDate date) {
	this.symbols = new ArrayList(symbols);
	this.type = GIVEN_SYMBOLS;
	this.firstDate = date;
	this.lastDate = date;
    }

    /**
     * Create a quote range that represents the given symbol for all the
     * dates we have quotes.
     *
     * @param   symbol  the symbol
     */
    public EODQuoteRange(Symbol symbol) {
	this.symbols = new ArrayList();
	symbols.add(symbol);

	this.type = GIVEN_SYMBOLS;
	this.firstDate = null;
	this.lastDate = null;
    }

    /**
     * Create a quote range that represents the given symbol between
     * the first and the last dates given (inclusive).
     *
     * @param symbol  the symbol
     * @param firstDate earliest date
     * @param lastDate  latest date
     */
    public EODQuoteRange(Symbol symbol, TradingDate firstDate,
                         TradingDate lastDate) {
	this.symbols = new ArrayList();
	symbols.add(symbol);

	this.type = GIVEN_SYMBOLS;
	this.firstDate = firstDate;
	this.lastDate = lastDate;
    }

    /**
     * Create a quote range that represents all the symbols of the given
     * type on the given date.
     *
     * @param type      the type, one of {@link #ALL_ORDINARIES}, {@link #ALL_SYMBOLS},
     *                  {@link #MARKET_INDICES}
     * @param date      the date
     */
    public EODQuoteRange(int type, TradingDate date) {

        assert type != GIVEN_SYMBOLS;

	this.symbols = null;
	this.type = type;
	this.firstDate = date;
	this.lastDate = date;
    }

    /**
     * Create a quote range that represents all the symbols of the given
     * type between the first and the last dates given (inclusive).
     *
     * @param type      the type, one of {@link #ALL_ORDINARIES}, {@link #ALL_SYMBOLS},
     *                  {@link #MARKET_INDICES}
     * @param firstDate earliest date
     * @param lastDate  latest date
     */
    public EODQuoteRange(int type, TradingDate firstDate, TradingDate lastDate) {

        assert type != GIVEN_SYMBOLS;

	this.symbols = null;
	this.type = type;
	this.firstDate = firstDate;
	this.lastDate = lastDate;
    }

    /**
     * Create a quote range that represents all the symbols of the given
     * type for all the dates we have quotes.
     * This might use up a lot of memory!
     *
     * @param type      the type, one of {@link #ALL_ORDINARIES}, {@link #ALL_SYMBOLS},
     *                  {@link #MARKET_INDICES}
     */
    public EODQuoteRange(int type) {

        assert type != GIVEN_SYMBOLS;

	this.symbols = null;
	this.type = type;
	this.firstDate = null;
	this.lastDate = null;
    }

    /**
     * Create a clone of this quote range.
     *
     * @return a clone of this quote range
     */
    public Object clone() {
	EODQuoteRange cloned;

	if(type == GIVEN_SYMBOLS)
	    cloned = new EODQuoteRange(symbols, firstDate, lastDate);
        else
	    cloned = new EODQuoteRange(type, firstDate, lastDate);

	return (Object)cloned;
    }

    /**
     * Return all the symbols in the quote range. This function is only valid for
     * quote ranges that have explicitly given a set of symbols.
     *
     * @return a list of symbols
     */
    public List getAllSymbols() {

	assert type == GIVEN_SYMBOLS && symbols != null;
	return symbols;
    }

    /**
     * Set the first date of the quote range.
     *
     * @param firstDate earliest date
     */
    public void setFirstDate(TradingDate firstDate) {
	this.firstDate = firstDate;
    }

    /**
     * Set the last date of the quote range.
     *
     * @param lastDate latest date
     */
    public void setLastDate(TradingDate lastDate) {
	this.lastDate = lastDate;
    }

    /**
     * Get the first date of the quote range.
     *
     * @return  the earliest date of the quote range, <code>null</code> indicates
     *          that the quote range encompasses all available dates
     */
    public TradingDate getFirstDate() {
	return firstDate;
    }

    /**
     * Get the last date of the quote range.
     *
     * @return  the latest date of the quote range, <code>null</code> indicates
     *          that the quote range encompasses all available dates
     */
    public TradingDate getLastDate() {
	return lastDate;
    }

    /**
     * Return whether this quote range includes any quotes. This function checks
     * for a null quote range, not an empty quote cache.
     *
     * @return <code>true</code> if this quote range covers an empty range,
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return (type == GIVEN_SYMBOLS && symbols.size() == 0);
    }

    /**
     * Get the type of the quote range.
     *
     * @return one of {@link #ALL_ORDINARIES}, {@link #ALL_SYMBOLS},
     *                {@link #MARKET_INDICES}, {@link #GIVEN_SYMBOLS}
     */
    public int getType() {
	return type;
    }

    /**
     * Returns whether the quote range contains the given symbol.
     *
     * @param   symbol  the symbol
     * @return  <code>true</code> if the symbol is in the quote range, <code>false</code>
     *          otherwise
     */
    public boolean containsSymbol(Symbol symbol) {

	// containsSymbols() and containsAllSymbols() could be factored together
	// but are not since containsSymbol() can be called a lot by QuoteBundle so its best
	// to keep it as simple as possible
	if(type == ALL_SYMBOLS)
	    return true;

	else if(type == GIVEN_SYMBOLS)
	    return symbols.contains(symbol);

	else if(type == ALL_ORDINARIES)
            if(!QuoteSourceManager.getSource().isMarketIndex(symbol) &&
               symbol.length() <= 3)
                return true;
            else
                return false;

	else {
	    assert type == MARKET_INDICES;
	
	    return QuoteSourceManager.getSource().isMarketIndex(symbol);
	}
    }

    /**
     * Expand the quote range to include a new symbol. This function is only valid
     * for quote ranges that have explicitly given a set of symbols.
     *
     * @param symbol new symbol
     */
    public void addSymbol(Symbol symbol) {
        assert type == GIVEN_SYMBOLS;

        if(!symbols.contains(symbol))
            symbols.add(symbol);
    }

    /**
     * Returns whether the quote range contains all the given symbols.
     *
     * @param   containedSymbols        all the symbols to check for
     * @return  <code>true</code> if all the symbols are in the quote range, <code>false</code>
     *          otherwise
     */
    public boolean containsAllSymbols(List containedSymbols) {

	assert containedSymbols != null && containedSymbols.size() > 0;

	if(type == ALL_SYMBOLS)
	    return true;

	else if(type == GIVEN_SYMBOLS)
	    return symbols.containsAll(containedSymbols);

	else if(type == ALL_ORDINARIES) {
	    Iterator iterator = containedSymbols.iterator();
	
	    while(iterator.hasNext()) {
		Symbol symbol = (Symbol)iterator.next();

		if(QuoteSourceManager.getSource().isMarketIndex(symbol) ||
                   symbol.length() > 3)
		    return false;
	    }

	    return true;
	}

	else {
	    assert type == MARKET_INDICES;

	    Iterator iterator = containedSymbols.iterator();
	
	    while(iterator.hasNext()) {
		Symbol symbol = (Symbol)iterator.next();

		if(!QuoteSourceManager.getSource().isMarketIndex(symbol))
		    return false;
	    }

	    return true;
	}
    }

    /**
     * Creates a string representation of the quote range without referring
     * to the dates.
     *
     * @return string representation, e.g. "All Ordinaries"
     */
    public String getDescription() {

        // If the type is a list of symbols, we can do better than just
        // saying "Given Symbols".
        if(getType() == GIVEN_SYMBOLS) {
            String string = "";

            Iterator iterator = getAllSymbols().iterator();

            while(iterator.hasNext()) {
                Symbol symbol = (Symbol)iterator.next();

                string = string.concat(symbol.toString());

                if(iterator.hasNext())
                    string = string.concat(", ");
            }

            return string;
        }
        else
            return getDescription(getType());
    }

    /**
     * Creates a string representation of the given quote range type.
     *
     * @param type the type, one of {@link #ALL_ORDINARIES}, {@link #ALL_SYMBOLS},
     *             {@link #MARKET_INDICES} or {@link #GIVEN_SYMBOLS}
     * @return string representation, e.g. "All Ordinaries"
     */
    public static String getDescription(int type) {
        if(type == ALL_SYMBOLS)
            return Locale.getString("ALL_SYMBOLS");
        else if(type == GIVEN_SYMBOLS)
            return Locale.getString("GIVEN_SYMBOLS");
        else if(type == ALL_ORDINARIES)
            return Locale.getString("ALL_ORDINARIES");
        else {
            assert type == MARKET_INDICES;
            return Locale.getString("MARKET_INDICES");
        }
    }

    /**
     * Create a string representation of the quote range. This is for debugging
     * purposes.
     *
     * @return  string representation
     */
    public String toString() {

        // Get basic description
        String string = getDescription();

        // Between what dates? (This locale code might need some work).
        if(getFirstDate() == null)
            string = Locale.getString("FOR_ALL_DATES", string);
        else if(getFirstDate().equals(getLastDate()))
            string = Locale.getString("ON_DATE", string, getFirstDate().toString());
        else
            string = Locale.getString("BETWEEN_DATES", string, getFirstDate().toString(),
                                      getLastDate().toString());

        return string;
    }

    /**
     * Return a new <i>clipped</i> quote range that, if possible, does not overlap with
     * this quote range. The idea behind this function is that when we need to load in
     * quotes specified from a quote range, we want to check with all the currently loaded
     * quote ranges to see if some of the quotes have already been loaded.
     * <p>
     * e.g.
     * <pre>
     * EODQuoteRange quoteRange = new EODQuoteRange("CBA", new TradingDate(2000, 1, 1),
     *                                              new TradingDate(2000, 12, 1));
     * EODQuoteRange quoteRange2 = new EODQuoteRange("CBA", new TradingDate(1999, 1, 1),
     *                                               new TradingDate(2000, 6, 6));
     * EODQuoteRange clippedQuoteRange = quoteRange.clipRange(quoteRange2);
     *
     * System.out.println(clippedQuoteRange);
     *
     * >> cba between 1-1-1999 and 31-12-1999
     *
     * </pre>
     *
     * @param   quoteRange      quote range to clip
     * @return  the clipped quote range
     */
    public EODQuoteRange clip(EODQuoteRange quoteRange) {
	int overlapType = getOverlapType(quoteRange);

	if(overlapType == CONTAINS || overlapType == PARTIAL_OVERLAP) {

	    if((getType() == ALL_ORDINARIES && quoteRange.getType() == ALL_ORDINARIES) ||
	       (getType() == MARKET_INDICES && quoteRange.getType() == MARKET_INDICES) ||
	       getType() == ALL_SYMBOLS ||
	
	       (getType() == ALL_ORDINARIES && quoteRange.getType() == GIVEN_SYMBOLS &&
		containsAllSymbols(quoteRange.getAllSymbols())) ||

	       (getType() == GIVEN_SYMBOLS && quoteRange.getType() == GIVEN_SYMBOLS &&
		containsAllSymbols(quoteRange.getAllSymbols()))) {

		// They partially overlap - perform clip
		if(overlapType == PARTIAL_OVERLAP) {
		    EODQuoteRange clipped = (EODQuoteRange)quoteRange.clone();

		    // this:        [----------]    -> [----------]
		    // quote range:    [----------]                [-]
 		    if(getFirstDate().compareTo(quoteRange.getFirstDate()) <= 0 &&
                       getLastDate().compareTo(quoteRange.getLastDate()) <= 0)
                        clipped.setFirstDate(getLastDate().next(1));

		    // this:           [----------] ->    [----------]
		    // quote range: [----------]       [-]
		    else {
			assert
			    getFirstDate().compareTo(quoteRange.getFirstDate()) >= 0 &&
			    getLastDate().compareTo(quoteRange.getLastDate()) >= 0;
                        clipped.setLastDate(getFirstDate().previous(1));
		    }
	
                    return clipped;

		}
		else {
		    assert overlapType == CONTAINS;
		    return new EODQuoteRange(new ArrayList(),
                                             quoteRange.getFirstDate(),
                                             quoteRange.getLastDate());
		}
	    }
	    else
		return quoteRange;
	}
	else
	    return quoteRange;
    }

    // Given two quote ranges. Return if they overlap.
    // CONTAINS - this quote range contains the given quote range
    // CONTAINED - this quote range is contained by the given quote range
    // NO_OVERLAP - the two quote ranges don't overlap
    // PARTIAL_OVERLAP - the two quotes overlap slightly
    private int getOverlapType(EODQuoteRange quoteRange) {
	if(getFirstDate() == null)
	    return CONTAINS;

	else if(quoteRange.getFirstDate() == null)
	    return CONTAINED;

	else if(getFirstDate().compareTo(quoteRange.getLastDate()) > 0 ||
		getLastDate().compareTo(quoteRange.getFirstDate()) < 0)
	    return NO_OVERLAP;
	
	else if(getFirstDate().compareTo(quoteRange.getFirstDate()) <= 0 &&
		getLastDate().compareTo(quoteRange.getLastDate()) >= 0)
	    return CONTAINS;

	else if(getFirstDate().compareTo(quoteRange.getFirstDate()) > 0 &&
		getLastDate().compareTo(quoteRange.getLastDate()) < 0)
	    return CONTAINED;
	
	return PARTIAL_OVERLAP;
    }
}
