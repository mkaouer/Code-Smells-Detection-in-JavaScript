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

package nz.org.venice.parser.expression;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;

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
        if(period <= 0) {
            EvaluationException e = EvaluationException.RSI_RANGE_EXCEPTION;
	    e.setMessage(this, "", period);
	    throw e;
	}
        int offset = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if (offset > 0) {
	    EvaluationException e = EvaluationException.RSI_OFFSET_EXCEPTION;
	    e.setMessage(this, "", offset);
	    throw e;
	}

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
	
	String periodExpressionString = (periodExpression != null) 
	    ? periodExpression.toString() 
	    : "(null)";

	String lagExpressionString = (lagExpression != null) 
	    ? lagExpression.toString() 
	    : "(null)";
	

        return new String("rsi(" +
                          periodExpressionString + ", " +
                          lagExpressionString + ")");
    }

    public int checkType() throws TypeMismatchException {
	if(getChild(0).checkType() == INTEGER_TYPE &&
	   getChild(1).checkType() == INTEGER_TYPE)
	    return FLOAT_TYPE;
	else {
	    String types = 
		getChild(0).getType() + " , " + 
		getChild(1).getType();

	    String expectedTypes = 
		INTEGER_TYPE + " , " +
		INTEGER_TYPE;
		
	    throw new TypeMismatchException(this, types, expectedTypes);
	}
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

