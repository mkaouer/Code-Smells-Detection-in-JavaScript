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
public class RandomWithoutSeedExpression extends TerminalExpression {

    private Random random;

    /**
     * Create a new random expression 
     */

    public RandomWithoutSeedExpression() {
	super();
        random = new Random();
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

	return random.nextDouble();            	
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
        return new RandomWithoutSeedExpression();
    }
}
