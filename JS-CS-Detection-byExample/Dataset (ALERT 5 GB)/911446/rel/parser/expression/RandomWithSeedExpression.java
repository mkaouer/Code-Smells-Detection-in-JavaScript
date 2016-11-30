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

import java.util.Random;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.Symbol;

/**
 * An expression which returns a random number.
 *
 * @author Mark Hummel
 */
public class RandomWithSeedExpression extends UnaryExpression {

    private Random random;

    /**
     * Create a new random expression for the given <code>seed</code>
     */    

    public RandomWithSeedExpression(Expression arg) {
	super(arg);
        random = new Random();
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

	double seed = getChild(0).evaluate(variables, 
					   quoteBundle, 
					   symbol,
					   day);
	    
	random.setSeed( (long)seed);	 
	return random.nextDouble();            	
    }

    /**
     * Check the optional input argument to the expression. It can only be
     * {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}
     *
     * @return the type of the expression
     */
    public int checkType() throws TypeMismatchException {
	if (getChild(0) != null) {
	    int type = getChild(0).checkType();
	    if (type == FLOAT_TYPE || type == INTEGER_TYPE) {
		return getType();
	    } else {
		throw new TypeMismatchException(this, type, FLOAT_TYPE);
	    }
	}
	return getType();
    }

    /**
     * Return the name of the expression. *
     * @return random()
     */

    public String toString() {
	return new String("random()");
    }
    

    /**
     * Get the type of the expression
     * 
     * @return {@link #FLOAT_TYPE}
     */
    public int getType() {
	return FLOAT_TYPE;
    }

    public Object clone() {	
        return new RandomWithSeedExpression(getChild(0));
    }
}
