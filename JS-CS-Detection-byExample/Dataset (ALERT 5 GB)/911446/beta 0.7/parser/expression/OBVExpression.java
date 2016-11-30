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
import org.mov.quote.QuoteBundle;
import org.mov.quote.QuoteBundleFunctionSource;
import org.mov.quote.QuoteFunctions;
import org.mov.quote.Symbol;

/**
 * An expression which finds the OBV (On Balance Volume) over a given trading period.
 *
 * @author Alberto Nacher
 */
public class OBVExpression extends TernaryExpression {

    /**
     * Create a new On Balance Volume (OBV) expression for the given <code>quote</code> kind,
     * for the given number of <code>days</code>, starting with <code>lag</code> days away.
     *
     * @param	initialValue	the initialValue the OBV start with
     * @param	days	the number of days to count over
     * @param	lag	the offset from the current day
     */
    public OBVExpression(Expression days, Expression lag, Expression initialValue) {
        super(days, lag, initialValue);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

        // Extract arguments
	int period = (int)getChild(0).evaluate(variables, quoteBundle, symbol, day);
        if(period <= 0)
            throw EvaluationException.OBV_RANGE_EXCEPTION;
        int offset = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if (offset > 0)
           throw EvaluationException.OBV_OFFSET_EXCEPTION;
        int initialValue = (int)getChild(2).evaluate(variables, quoteBundle, symbol, day);

        // Calculate and return the OBV.
        QuoteBundleFunctionSource sourceOpen =
            new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_OPEN, day, offset, period);
        QuoteBundleFunctionSource sourceClose =
            new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_CLOSE, day, offset, period);
        QuoteBundleFunctionSource sourceVolume =
            new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_VOLUME, day, offset, period);

        return QuoteFunctions.obv(sourceOpen, sourceClose, sourceVolume, period, initialValue);
    }

    public String toString() {
	return new String("obv(" + 
			  getChild(0).toString() + ", " +
			  getChild(1).toString() + ", " +
			  getChild(2).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	// All inputs must be integer values.
	if(getChild(0).checkType() == INTEGER_TYPE &&
	   getChild(1).checkType() == INTEGER_TYPE &&
	   getChild(2).checkType() == INTEGER_TYPE)
	    return getType();
	else
	    throw new TypeMismatchException();
    }

    public int getType() {
        return INTEGER_TYPE;
    }

    public Object clone() {
        return new OBVExpression((Expression)getChild(0).clone(), 
                                 (Expression)getChild(1).clone(),
                                 (Expression)getChild(2).clone());
    }
}
