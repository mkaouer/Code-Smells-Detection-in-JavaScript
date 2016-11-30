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
import org.mov.quote.Quote;
import org.mov.quote.QuoteBundleFunctionSource;
import org.mov.quote.QuoteFunctions;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

/**
 * An expression which finds the RSI over a given trading period.
 *
 * @author Andrew Leppard
 */
public class RSIExpression extends BinaryExpression {

    public RSIExpression(Expression days, Expression lag) {
        super(days, lag);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

        // Extract arguments
	int period = (int)getChild(0).evaluate(variables, quoteBundle, symbol, day);
        if(period <= 0)
            throw EvaluationException.RSI_RANGE_EXCEPTION;
        int offset = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if (offset > 0)
           throw EvaluationException.RSI_OFFSET_EXCEPTION;

        // Calculate and return the RSI. We start the offset one day before the actual offset
        // and increase the period by one day, as the RSI calculation needs an extra day
        // over the period.
	QuoteBundleFunctionSource source = 
	    new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_CLOSE, day, offset - 1,
					  period - 1);
	return QuoteFunctions.rsi(source, period - 1);
    }

    public String toString() {
        Expression periodExpression = getChild(0);
        Expression lagExpression = getChild(1);

        return new String("rsi(" +
                          periodExpression.toString() + ", " +
                          lagExpression.toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	if(getChild(0).checkType() == INTEGER_TYPE &&
	   getChild(1).checkType() == INTEGER_TYPE)
	    return FLOAT_TYPE;
	else
	    throw new TypeMismatchException();
    }

    /**
     * Get the type of the expression.
     *
     * @return {@link #FLOAT_TYPE}.
     */
    public int getType() {
        return FLOAT_TYPE;
    }

    public Object clone() {
        return new RSIExpression((Expression)getChild(0).clone(),
                                 (Expression)getChild(1).clone());
    }
}

