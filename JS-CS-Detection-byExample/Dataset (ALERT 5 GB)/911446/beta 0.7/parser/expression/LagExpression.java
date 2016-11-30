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

import org.mov.parser.EvaluationException;
import org.mov.parser.Expression;
import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

/**
 * An expression which returns a quote.
 *
 * @author Andrew Leppard
 */
public class LagExpression extends BinaryExpression {

    /**
     * Create a new average expression for the given <code>quote</code> kind,
     * for <code>lag</code> days away.
     *
     * @param	quote	the quote kind to return
     * @param	lag	the offset from the current day
     */
    public LagExpression(Expression quote, Expression lag) {
        super(quote, lag);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

        int lag = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if (lag > 0)
           throw EvaluationException.LAG_OFFSET_EXCEPTION;
        int quoteKind = ((QuoteExpression)getChild(0)).getQuoteKind();

        try {
            return quoteBundle.getQuote(symbol, quoteKind, day, lag);
        }
        catch(MissingQuoteException e) {
            // What should I do in this case?
            return 0.0D;
        }
    }

    public String toString() {
        Expression quoteExpression = getChild(0);
        Expression lagExpression = getChild(1);

        return new String("lag(" +
                          quoteExpression.toString() + ", " +
                          lagExpression.toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	// Left type must be quote and right type must be number type
	if((getChild(0).checkType() == FLOAT_QUOTE_TYPE ||
            getChild(0).checkType() == INTEGER_QUOTE_TYPE) &&
	   getChild(1).checkType() == INTEGER_TYPE)
	    return getType();
	else
	    throw new TypeMismatchException();
    }

    public int getType() {
        if(getChild(0).getType() == FLOAT_QUOTE_TYPE)
            return FLOAT_TYPE;
        else {
            assert getChild(0).getType() == INTEGER_QUOTE_TYPE;
            return INTEGER_TYPE;
        }
    }

    public Object clone() {
        return new LagExpression((Expression)getChild(0).clone(),
                                 (Expression)getChild(1).clone());
    }
}
