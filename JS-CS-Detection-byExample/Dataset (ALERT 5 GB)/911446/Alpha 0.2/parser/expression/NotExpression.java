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
 * An expression which performs boolean <code>not</code> on the 
 * sub-expressions.
 */
public class NotExpression extends UnaryExpression {

    public NotExpression(Expression arg) {
	super(arg);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	if(get().evaluate(variables, quoteBundle, symbol, day) >= Expression.TRUE_LEVEL)
	    return FALSE;
	else
	    return TRUE;
    }

    public Expression simplify() {
        // First simplify all the child arguments
        super.simplify();

        // If the child argument is a number expression we can precompute
        if(get() instanceof NumberExpression) {
            try {
                return new NumberExpression(evaluate(null, null, null, 0), BOOLEAN_TYPE);
            }
            catch(EvaluationException e) {
                // Shouldn't happen
                assert false;
                return this;
            }
        }
        // If the child argument is a logic expression then we can reverse it.

        // not(x == y) -> x != y
        else if(get() instanceof EqualThanExpression) 
            return new NotEqualExpression(get().get(0), get().get(1));

        // not(x != y) -> x == y
        else if(get() instanceof NotEqualExpression) 
            return new EqualThanExpression(get().get(0), get().get(1));

        // not(x < y) -> x >= y
        else if(get() instanceof LessThanExpression) 
            return new GreaterThanEqualExpression(get().get(0), get().get(1));

        // not(x > y) -> x <= y
        else if(get() instanceof GreaterThanExpression) 
            return new LessThanEqualExpression(get().get(0), get().get(1));

        // not(x <= y) -> x > y
        else if(get() instanceof LessThanEqualExpression) 
            return new GreaterThanExpression(get().get(0), get().get(1));

        // not(x >= y) -> x < y
        else if(get() instanceof GreaterThanEqualExpression) 
            return new LessThanExpression(get().get(0), get().get(1));

        // not(not(x)) -> x
        else if(get() instanceof NotExpression)
            return get().get(0);
        else
            return this;
    }

    public String toString() {
	return new String("not(" + get().toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	// sub type must be boolean
	if(get().checkType() == BOOLEAN_TYPE)
	    return BOOLEAN_TYPE;
	else
	    throw new TypeMismatchException();
    }

    /**
     * Get the type of the expression.
     *
     * @return {@link #BOOLEAN_TYPE}.
     */
    public int getType() {
        return BOOLEAN_TYPE;
    }

    public Object clone() {
        return new NotExpression((Expression)get().clone());
    }
}
