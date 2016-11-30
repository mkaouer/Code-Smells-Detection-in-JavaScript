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

import org.mov.util.*;
import org.mov.parser.*;
import org.mov.quote.*;

/**
 * An expression which returns a quote.
 */
public class LagExpression extends QuoteExpression {
   
    /**
     * Create a new average expression for the given <code>quote</code> kind,
     * for <code>lag</code> days away.
     *
     * @param	quote	the quote kind to return
     * @param	lag	the offset from the current day
     */
    public LagExpression(Expression quote, Expression lag) {
	super(quote);

        assert quote != null && lag != null;

	add(quote);
	add(lag);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

        int lag = (int)get(1).evaluate(variables, quoteBundle, symbol, day);

        try {
            return quoteBundle.getQuote(symbol, getQuoteKind(), day, lag);
        }
        catch(MissingQuoteException e) {
            // What should I do in this case?
            return 0.0F;
        }
    }

    public String toString() {
	return new String("lag(" + get(0).toString() + ", " +
			  get(1).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {

	// Left type must be quote and right type must be number type
	if((get(0).checkType() == FLOAT_QUOTE_TYPE ||
            get(0).checkType() == INTEGER_QUOTE_TYPE) &&
	   get(1).checkType() == INTEGER_TYPE)
	    return getType();
	else
	    throw new TypeMismatchException();
    }

    public int getNeededChildren() {
	return 2;
    }

    public Object clone() {
        return new LagExpression((Expression)get(0).clone(), 
                                 (Expression)get(1).clone());
    }

}
