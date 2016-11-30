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

package org.mov.analyser;

import java.util.Comparator;

import org.mov.parser.EvaluationException;
import org.mov.parser.Expression;
import org.mov.parser.Variables;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.Quote;
import org.mov.quote.EODQuoteBundle;
import org.mov.quote.Symbol;

/**
 * This comparator orders the stock quotes on a given date. The GP
 * allows the user or order the evaluation of stock quotes, by several
 * criteria such as unordered, alphabetically by symbol, volume decreasing etc.
 * This comparator performs the ordering.
 *
 * @author Andrew Leppard
 * @see OrderCache
 * @see QuoteRangePage
 */
public class OrderComparator implements Comparator {

    /** Don't order the stock symbols. */
    public final static int NO_ORDER              = 0;

    /** Order the stock symbols alphabetically. */
    public final static int STOCK_SYMBOL          = 1;

    /** Order the stock symbols by the highest volume on the date. */
    public final static int DAY_VOLUME_DECREASING = 2;

    /** Order the stock symbols by the lowest volume on the date. */
    public final static int DAY_VOLUME_INCREASING = 3;

    /** Order the stock symbols by the highest day low on the date. */
    public final static int DAY_LOW_DECREASING    = 4;

    /** Order the stock symbols by the lowest day low on the date. */
    public final static int DAY_LOW_INCREASING    = 5;

    /** Order the stock symbols by the highest day high on the date. */
    public final static int DAY_HIGH_DECREASING   = 6;

    /** Order the stock symbols by the lowest day high on the date. */
    public final static int DAY_HIGH_INCREASING   = 7;

    /** Order the stock symbols by the highest day open on the date. */
    public final static int DAY_OPEN_DECREASING   = 8;

    /** Order the stock symbols by the lowest day open on the date. */
    public final static int DAY_OPEN_INCREASING   = 9;

    /** Order the stock symbols by the highest day close on the date. */
    public final static int DAY_CLOSE_DECREASING  = 10;

    /** Order the stock symbols by the lowest day close on the date. */
    public final static int DAY_CLOSE_INCREASING  = 11;

    /** Order the stock symbols by the highest change on the date. */
    public final static int CHANGE_DECREASING     = 12;

    /** Order the stock symbols by the lowest change on the date. */
    public final static int CHANGE_INCREASING     = 13;

    /** Order the stock symbols by the given equation. */
    public final static int EQUATION              = 14;

    // Quote data to used for ordering
    private EODQuoteBundle quoteBundle;

    // Equation used for order (only if orderByKey == EQUATION).
    private Expression orderByEquation;

    // Method of ordering: NO_ORDER, STOCK_SYMBOL etc
    private int orderByKey;

    // Date to order.
    private int dateOffset = 0;

    // Has dateOffset been set yet?
    private boolean isDateSet;

    /**
     * Create a new order comparator that will order the quotes in the given
     * quote bundle using the given order.
     *
     * @param quoteBundle quote data used for ordering
     * @param orderByKey method of ordering
     */
    public OrderComparator(EODQuoteBundle quoteBundle, int orderByKey) {
        this.quoteBundle = quoteBundle;
        this.orderByKey = orderByKey;

        assert orderByKey < EQUATION;

        isDateSet = false;
    }

    /**
     * Create a new order comparator that will order the quotes in the given
     * quote bundle using the given equation.
     *
     * @param quoteBundle quote data used for ordering
     * @param orderByEquation equation used for ordering
     */
    public OrderComparator(EODQuoteBundle quoteBundle, Expression orderByEquation) {
        this.quoteBundle = quoteBundle;
        this.orderByEquation = orderByEquation;
        this.orderByKey = EQUATION;

        isDateSet = false;
    }

    /**
     * Return the method of ordering. If the quotes will be ordered by equation
     * then this function will return {@link #EQUATION}.
     *
     * @return order key
     */
    public int getOrderByKey() {
        return orderByKey;
    }

    /**
     * Return whether the stock quotes are ordered.
     *
     * @return <code>true</code> if the order is not {@link #NO_ORDER}.
     */
    public boolean isOrdered() {
        return orderByKey == NO_ORDER;
    }

    /**
     * Set the date to order the stock quotes.
     *
     * @param dateOffset fast access date offset
     */
    public void setDateOffset(int dateOffset) {
        this.dateOffset = dateOffset;

        isDateSet = true;
    }

    /**
     * Compare two stock symbols on the date set by {@link #setDateOffset}.
     *
     * @param object1 first symbol
     * @param object2 second symbol
     * @return <code>-1</code> if the first symbol comes first,
     *         <code>0</code> if the symbols are equal OR
     *         <code>1</code> if the first symbol comes last.
     */
    public int compare(Object object1, Object object2) {

        Symbol symbol1 = (Symbol)object1;
        Symbol symbol2 = (Symbol)object2;

        assert isDateSet;

        try {
            switch(orderByKey) {
            case(NO_ORDER):
                return 0;
            case(STOCK_SYMBOL):
                return symbol1.compareTo(symbol2);
            case(DAY_VOLUME_INCREASING):
                return compare(quoteBundle.getQuote(symbol1, Quote.DAY_VOLUME, dateOffset),
                               quoteBundle.getQuote(symbol2, Quote.DAY_VOLUME, dateOffset));
            case(DAY_VOLUME_DECREASING):
                return compare(quoteBundle.getQuote(symbol2, Quote.DAY_VOLUME, dateOffset),
                               quoteBundle.getQuote(symbol1, Quote.DAY_VOLUME, dateOffset));
            case(DAY_LOW_INCREASING):
                return compare(quoteBundle.getQuote(symbol1, Quote.DAY_LOW, dateOffset),
                               quoteBundle.getQuote(symbol2, Quote.DAY_LOW, dateOffset));
            case(DAY_LOW_DECREASING):
                return compare(quoteBundle.getQuote(symbol2, Quote.DAY_LOW, dateOffset),
                               quoteBundle.getQuote(symbol1, Quote.DAY_LOW, dateOffset));
            case(DAY_HIGH_INCREASING):
                return compare(quoteBundle.getQuote(symbol1, Quote.DAY_HIGH, dateOffset),
                               quoteBundle.getQuote(symbol2, Quote.DAY_HIGH, dateOffset));
            case(DAY_HIGH_DECREASING):
                return compare(quoteBundle.getQuote(symbol2, Quote.DAY_HIGH, dateOffset),
                               quoteBundle.getQuote(symbol1, Quote.DAY_HIGH, dateOffset));
            case(DAY_OPEN_INCREASING):
                return compare(quoteBundle.getQuote(symbol1, Quote.DAY_OPEN, dateOffset),
                               quoteBundle.getQuote(symbol2, Quote.DAY_OPEN, dateOffset));
            case(DAY_OPEN_DECREASING):
                return compare(quoteBundle.getQuote(symbol2, Quote.DAY_OPEN, dateOffset),
                               quoteBundle.getQuote(symbol1, Quote.DAY_OPEN, dateOffset));
            case(DAY_CLOSE_INCREASING):
                return compare(quoteBundle.getQuote(symbol1, Quote.DAY_CLOSE, dateOffset),
                               quoteBundle.getQuote(symbol2, Quote.DAY_CLOSE, dateOffset));
            case(DAY_CLOSE_DECREASING):
                return compare(quoteBundle.getQuote(symbol2, Quote.DAY_CLOSE, dateOffset),
                               quoteBundle.getQuote(symbol1, Quote.DAY_CLOSE, dateOffset));
            case(CHANGE_INCREASING):
                return compare(quoteBundle.getQuote(symbol1, Quote.DAY_CLOSE, dateOffset) /
                               quoteBundle.getQuote(symbol1, Quote.DAY_OPEN,  dateOffset),
                               quoteBundle.getQuote(symbol2, Quote.DAY_CLOSE, dateOffset) /
                               quoteBundle.getQuote(symbol2, Quote.DAY_OPEN,  dateOffset));
            case(CHANGE_DECREASING):
                return compare(quoteBundle.getQuote(symbol2, Quote.DAY_CLOSE, dateOffset) /
                               quoteBundle.getQuote(symbol2, Quote.DAY_OPEN,  dateOffset),
                               quoteBundle.getQuote(symbol1, Quote.DAY_CLOSE, dateOffset) /
                               quoteBundle.getQuote(symbol1, Quote.DAY_OPEN,  dateOffset));
            case(EQUATION):
                return compareByEquation(symbol1, symbol2);
            default:
                assert false;
                return 0;
            }
        }
        catch(MissingQuoteException e) {
            assert false;
            return 0;
        }
    }

    private int compareByEquation(Symbol symbol1, Symbol symbol2) {
        assert orderByKey == EQUATION;

        try {
            double valueOne = orderByEquation.evaluate(new Variables(), quoteBundle, symbol1,
                                                       dateOffset);
            double valueTwo = orderByEquation.evaluate(new Variables(), quoteBundle, symbol2,
                                                       dateOffset);

            return compare(valueTwo, valueOne);
        }
        catch(EvaluationException e) {
            // I don't know how to easily notify the user of this...
            return 0;
        }
    }

    private int compare(double one, double two) {
        if(one < two)
            return -1;
        else if(one > two)
            return 1;
        else
            return 0;
    }
}
