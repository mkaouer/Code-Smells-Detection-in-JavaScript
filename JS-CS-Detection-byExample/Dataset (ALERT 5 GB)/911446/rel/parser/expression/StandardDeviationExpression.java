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

import nz.org.venice.parser.Expression;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.Symbol;

/**
 * An expression which finds the standard deviation of a quote over a given trading period.
 *
 * @author Andrew Leppard
 */
public class StandardDeviationExpression extends TernaryExpression {
   
    /**
     * Create a new standard deviation expression for the given <code>quote</code> kind,
     * for the given number of <code>days</code> starting with 
     * <code>lag</code> days away.
     *
     * @param	quote	the quote kind to analyse
     * @param	days	the number of days to analyse
     * @param	lag	the offset from the current day
     */
    public StandardDeviationExpression(Expression quote, Expression days,
                                       Expression lag) {
	super(quote, days, lag);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	QuoteSymbol quoteChild = (QuoteSymbol)getChild(0);
	Symbol explicitSymbol = (quoteChild.getSymbol() != null) 
	    ? quoteChild.getSymbol() : symbol;

        // Extract arguments
	int period = (int)getChild(1).evaluate(variables, 
					       quoteBundle, 
					       explicitSymbol, 
					       day);
        if(period <= 0) {
             EvaluationException e = EvaluationException.SD_RANGE_EXCEPTION;
	     e.setMessage(this, "", period);
	     throw e;

	}
        int quoteKind = quoteChild.getQuoteKind();
        int offset = (int)getChild(2).evaluate(variables, 
					       quoteBundle, 
					       explicitSymbol, 
					       day);
        if (offset > 0) {
            EvaluationException e = EvaluationException.SD_OFFSET_EXCEPTION;
	    e.setMessage(this, "", offset);
	    throw e;
	}

        // Calculate and return the standard deviation.
        QuoteBundleFunctionSource source =
            new QuoteBundleFunctionSource(quoteBundle, 
					  explicitSymbol, 
					  quoteKind, 
					  day, 
					  offset, 
					  period);

        return QuoteFunctions.sd(source, period);
    }

    public String toString() {

	String c1 = (getChild(0) != null) ? getChild(0).toString() : "(null)";
	String c2 = (getChild(1) != null) ? getChild(1).toString() : "(null)";
	String c3 = (getChild(2) != null) ? getChild(2).toString() : "(null)";
	

	return new String("sd(" + 
			  c1 + ", " +
			  c2 + ", " +
			  c3 + ")");
    }

    public int checkType() throws TypeMismatchException {
	// First type must be quote, second and third types must be value
	if((getChild(0).checkType() == FLOAT_QUOTE_TYPE ||
            getChild(0).checkType() == INTEGER_QUOTE_TYPE) &&
	   getChild(1).checkType() == INTEGER_TYPE &&
	   getChild(2).checkType() == INTEGER_TYPE)
	    return getType();
	else {
	    String types = 
		getChild(0).getType() + " , " + 
		getChild(1).getType() + " , " + 
		getChild(2).getType();

	    String expectedTypes =
		FLOAT_QUOTE_TYPE + " , " + 
		INTEGER_TYPE     + " , " + 
		INTEGER_TYPE;

	    throw new TypeMismatchException(this, types, expectedTypes);
	}
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
        return new StandardDeviationExpression((Expression)getChild(0).clone(), 
                                               (Expression)getChild(1).clone(),
                                               (Expression)getChild(2).clone());
    }
}
