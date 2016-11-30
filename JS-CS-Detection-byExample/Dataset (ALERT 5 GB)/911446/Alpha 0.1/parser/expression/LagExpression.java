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

	add(quote);
	add(lag);
    }

    public float evaluate(QuoteBundle quoteBundle, String symbol, int day) 
	throws EvaluationException {

	try {
	    return quoteBundle.getQuote(symbol, getQuoteKind(), day +
					(int)getArg(1).evaluate(quoteBundle, symbol, day));
	}
	catch(MissingQuoteException e) {
	    // TO BE UPDATED. This means if we can't find a quote we assume its
	    // 0. This works if the stock has been taken off the list otherwise
	    // it might produce weird results if a single day for a single stock is
	    // missing.
	    return 0.0F;
	}
    }

    public String toString() {
	return new String("lag(" + getArg(0).toString() + ", " +
			  getArg(1).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {

	// Left type must be quote and right type must be number type
	if(getArg(0).checkType() == QUOTE_TYPE &&
	   getArg(1).checkType() == VALUE_TYPE)
	    return getQuoteType();
	else
	    throw new TypeMismatchException();
    }

    public int getNeededChildren() {
	return 2;
    }
}
