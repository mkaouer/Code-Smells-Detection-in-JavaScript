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

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.QuoteBundleFunctionSource;
import org.mov.quote.QuoteFunctions;
import org.mov.quote.Symbol;

/**
 * An expression which finds the average quote over a given trading period.
 *
 * @author Andrew Leppard
 */
public class AvgExpression extends TernaryExpression {
   
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
	super(quote, days, lag);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

        // Extract arguments
	int period = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if(period <= 0)
            throw EvaluationException.AVG_RANGE_EXCEPTION;
        int quoteKind = ((QuoteExpression)getChild(0)).getQuoteKind();
        int offset = (int)getChild(2).evaluate(variables, quoteBundle, symbol, day);
        if (offset > 0)
           throw EvaluationException.AVG_OFFSET_EXCEPTION;

        // Calculate and return the average.
        QuoteBundleFunctionSource source =
            new QuoteBundleFunctionSource(quoteBundle, symbol, quoteKind, day, offset, period);

        return QuoteFunctions.avg(source, period);
    }

    public String toString() {
	return new String("avg(" + 
			  getChild(0).toString() + ", " +
			  getChild(1).toString() + ", " +
			  getChild(2).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	// First type must be quote, second and third types must be value
	if((getChild(0).checkType() == FLOAT_QUOTE_TYPE ||
            getChild(0).checkType() == INTEGER_QUOTE_TYPE) &&
	   getChild(1).checkType() == INTEGER_TYPE &&
	   getChild(2).checkType() == INTEGER_TYPE)
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
        return new AvgExpression((Expression)getChild(0).clone(), 
                                 (Expression)getChild(1).clone(),
                                 (Expression)getChild(2).clone());
    }
}
