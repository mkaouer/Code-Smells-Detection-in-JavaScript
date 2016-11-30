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

package org.mov.parser.expression;

import org.mov.quote.*;
import org.mov.parser.*;

/**
 * An expression which finds the average quote over a given trading period.
 */
public class AvgExpression extends QuoteExpression {
   
    /**
     * Create a new average expression for the given <code>quote</code> kind,
     * for the given number of <code>days</code> starting with 
     * <code>lag</code> days away.
     *
     * @param	quote	the quote kind to average
     * @param	days	the number of days to average over
     * @param	lag	the offset from the current day
     */
    public AvgExpression(Expression quote, Expression days,
			 Expression lag) {
	super(quote);

	add(quote);
	add(days);
	add(lag);
    }

    public float evaluate(QuoteBundle quoteBundle, String symbol, int day) 
	throws EvaluationException {
	
	int days = (int)getArg(1).evaluate(quoteBundle, symbol, day);
	int lastDay = day + (int)getArg(2).evaluate(quoteBundle, symbol, day);

	return avg(quoteBundle, symbol, getQuoteKind(), days, lastDay);
    }

    public String toString() {
	return new String("avg(" + 
			  getArg(0).toString() + ", " +
			  getArg(1).toString() + ", " +
			  getArg(2).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {

	// First type must be quote, second and third types must be value
	if(getArg(0).checkType() == QUOTE_TYPE &&
	   getArg(1).checkType() == VALUE_TYPE &&
	   getArg(2).checkType() == VALUE_TYPE)
	    return getQuoteType();
	else
	    throw new TypeMismatchException();
    }

    public int getNeededChildren() {
	return 3;
    }

    /** 
     * Average the stock quotes for a given symbol in a given range. 
     *
     * @param	quoteBundle	the quote bundle to read the quotes from.
     * @param	symbol	the symbol to use.
     * @param	quote	the quote type we are interested in, e.g. DAY_OPEN.
     * @param	lastDay	fast access date offset in cache.
     * @return	average stock quote.
     */
    static public float avg(QuoteBundle quoteBundle, String symbol, 
			    int quote, int days, int lastDay) {

	float avg = 0.0F;
        int daysAveraged = 0;

	// Sum quotes
	for(int i = lastDay - days + 1; i <= lastDay; i++) {
            try {
                avg += quoteBundle.getQuote(symbol, quote, i);
                daysAveraged++;
            }
            catch(MissingQuoteException e) {
                // nothing to do
            }
        }       

	// Average
        if(daysAveraged > 1)
            avg /= daysAveraged;

	return avg;
    }
}

