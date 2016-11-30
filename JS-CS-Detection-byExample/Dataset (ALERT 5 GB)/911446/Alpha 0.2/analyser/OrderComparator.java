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
import org.mov.parser.Parser;
import org.mov.parser.Variables;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.Quote;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

public class OrderComparator implements Comparator {

    public final static int NO_REAL_ORDER         = 0;
    public final static int STOCK_SYMBOL          = 1;
    public final static int DAY_VOLUME_DECREASING = 2;
    public final static int DAY_VOLUME_INCREASING = 3;
    public final static int DAY_LOW_DECREASING    = 4;
    public final static int DAY_LOW_INCREASING    = 5;
    public final static int DAY_HIGH_DECREASING   = 6;
    public final static int DAY_HIGH_INCREASING   = 7;
    public final static int DAY_OPEN_DECREASING   = 8;
    public final static int DAY_OPEN_INCREASING   = 9;
    public final static int DAY_CLOSE_DECREASING  = 10;
    public final static int DAY_CLOSE_INCREASING  = 11;
    public final static int CHANGE_DECREASING     = 12;
    public final static int CHANGE_INCREASING     = 13;
    public final static int EQUATION              = 14;

    private QuoteBundle quoteBundle;
    private Expression orderByEquation;
    private int orderByKey;
    private int dateOffset = 0;
    private boolean isDateSet;

    public OrderComparator(QuoteBundle quoteBundle, int orderByKey) {
        this.quoteBundle = quoteBundle;
        this.orderByKey = orderByKey;

        assert orderByKey < EQUATION;

        isDateSet = false;
    }

    public OrderComparator(QuoteBundle quoteBundle, Expression orderByEquation) {
        this.quoteBundle = quoteBundle;
        this.orderByEquation = orderByEquation;
        this.orderByKey = EQUATION;

        isDateSet = false;
    }

    public int getOrderByKey() {
        return orderByKey;
    }

    public void setDateOffset(int dateOffset) {
        this.dateOffset = dateOffset;

        isDateSet = true;
    }

    public int compare(Object object1, Object object2) {

        Symbol symbol1 = (Symbol)object1;
        Symbol symbol2 = (Symbol)object2;

        assert isDateSet;

        try {
            switch(orderByKey) {
            case(NO_REAL_ORDER):
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
            float valueOne = orderByEquation.evaluate(new Variables(), quoteBundle, symbol1, 
                                                      dateOffset);
            float valueTwo = orderByEquation.evaluate(new Variables(), quoteBundle, symbol2, 
                                                      dateOffset);

            return compare(valueTwo, valueOne);
        }
        catch(EvaluationException e) {
            // I don't know how to easily notify the user of this...
            return 0;        
        }
    }

    private int compare(float one, float two) {
        if(one < two)
            return -1;
        else if(one > two)
            return 1;
        else
            return 0;
    }
}
