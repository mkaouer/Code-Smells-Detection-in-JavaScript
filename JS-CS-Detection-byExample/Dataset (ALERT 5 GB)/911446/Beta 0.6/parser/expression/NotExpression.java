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

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	if(getChild(0).evaluate(variables, quoteBundle, symbol, day) >= Expression.TRUE_LEVEL)
	    return FALSE;
	else
	    return TRUE;
    }

    public Expression simplify() {
        // First simplify all the child arguments
        super.simplify();

        // If the child argument is a number expression we can precompute
        if(getChild(0) instanceof NumberExpression) {
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
        else if(getChild(0) instanceof EqualThanExpression) 
            return new NotEqualExpression(getChild(0).getChild(0), getChild(0).getChild(1));

        // not(x != y) -> x == y
        else if(getChild(0) instanceof NotEqualExpression) 
            return new EqualThanExpression(getChild(0).getChild(0), getChild(0).getChild(1));

        // not(x < y) -> x >= y
        else if(getChild(0) instanceof LessThanExpression) 
            return new GreaterThanEqualExpression(getChild(0).getChild(0), getChild(0).getChild(1));

        // not(x > y) -> x <= y
        else if(getChild(0) instanceof GreaterThanExpression) 
            return new LessThanEqualExpression(getChild(0).getChild(0), getChild(0).getChild(1));

        // not(x <= y) -> x > y
        else if(getChild(0) instanceof LessThanEqualExpression) 
            return new GreaterThanExpression(getChild(0).getChild(0), getChild(0).getChild(1));

        // not(x >= y) -> x < y
        else if(getChild(0) instanceof GreaterThanEqualExpression) 
            return new LessThanExpression(getChild(0).getChild(0), getChild(0).getChild(1));

        // not(not(x)) -> x
        else if(getChild(0) instanceof NotExpression)
            return getChild(0).getChild(0);
        else
            return this;
    }

    public String toString() {
	return new String("not(" + getChild(0).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	// sub type must be boolean
	if(getChild(0).checkType() == BOOLEAN_TYPE)
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
        return new NotExpression((Expression)getChild(0).clone());
    }
}
