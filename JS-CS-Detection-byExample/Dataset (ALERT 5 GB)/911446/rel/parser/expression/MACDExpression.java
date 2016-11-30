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
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.Symbol;

/**
 * An expression which finds the MACD (Moving Average Convergence Divergence) over a default trading period.
 *
 * @author Alberto Nacher
 */
public class MACDExpression extends BinaryExpression {
    
    final public static int PERIOD_SLOW = 26;
    final public static int PERIOD_FAST = 12;

    /**
     * Create a new Moving Average Convergence Divergence expression for the given <code>quote</code> kind,
     * starting with <code>lag</code> days away.
     * The periods and smoothing constants are set to default values.
     *
     * @param	quote	the quote kind
     * @param	lag	the offset from the current day
     */
    public MACDExpression(Expression quote, Expression lag) {
        super(quote, lag);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

	QuoteSymbol quoteChild = (QuoteSymbol)getChild(0);

        // Extract arguments
        int quoteKind = quoteChild.getQuoteKind();
	Symbol explicitSymbol = (quoteChild.getSymbol() != null) 
	    ? quoteChild.getSymbol() : symbol;
        int offset = (int)getChild(1).evaluate(variables, 
					       quoteBundle, 
					       explicitSymbol, 
					       day);
        if (offset > 0) {
           EvaluationException e = EvaluationException.MACD_OFFSET_EXCEPTION;
	   e.setMessage(this, "", offset);
	    throw e;
	}

        // Calculate and return the MACD.
        QuoteBundleFunctionSource sourceSlow =
            new QuoteBundleFunctionSource(quoteBundle, 
					  explicitSymbol, 
					  quoteKind, 
					  day, 
					  offset, 
					  PERIOD_SLOW);
        QuoteBundleFunctionSource sourceFast =
            new QuoteBundleFunctionSource(quoteBundle, 
					  explicitSymbol, 
					  quoteKind, 
					  day, 
					  offset, 
					  PERIOD_FAST);

        return QuoteFunctions.macd(sourceSlow, sourceFast);
    }

    public String toString() {
	String c1 = (getChild(0) != null) ? getChild(0).toString() : "(null)";
	String c2 = (getChild(1) != null) ? getChild(1).toString() : "(null)";
	
	return new String("macd(" + 
			  c1 + ", " +
			  c2 + ")");
    }

    public int checkType() throws TypeMismatchException {
	// First type must be quote, second type must be integer value
	if((getChild(0).checkType() == FLOAT_QUOTE_TYPE ||
            getChild(0).checkType() == INTEGER_QUOTE_TYPE) &&
	   getChild(1).checkType() == INTEGER_TYPE)
	    return getType();
	else {
	    String types = 
		getChild(0).getType() + " , " + 
		getChild(1).getType();

	    String expectedTypes =
		FLOAT_QUOTE_TYPE + " , " + 
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
        return new MACDExpression((Expression)getChild(0).clone(), 
                                 (Expression)getChild(1).clone());
    }
}
