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

import nz.org.venice.parser.*;
import nz.org.venice.quote.*;

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
	Expression simplified = super.simplify();

        // If the child argument is a number expression we can precompute
        if(simplified.getChild(0) instanceof NumberExpression) {
            try {
		Expression retExp = new NumberExpression(simplified.evaluate(null, null, null, 0), BOOLEAN_TYPE);

		return retExp;
            }
            catch(EvaluationException e) {
                // Shouldn't happen
                assert false;
                return this;
            }
        }
        // If the child argument is a logic expression then we can reverse it.

        // not(x == y) -> x != y
        else if(simplified.getChild(0) instanceof EqualThanExpression) {

            Expression retExp = new NotEqualExpression(simplified.getChild(0).getChild(0), simplified.getChild(0).getChild(1));

	    return retExp;
	}
        // not(x != y) -> x == y
        else if(simplified.getChild(0) instanceof NotEqualExpression) 
            return new EqualThanExpression(simplified.getChild(0).getChild(0), simplified.getChild(0).getChild(1));

        // not(x < y) -> x >= y
        else if(simplified.getChild(0) instanceof LessThanExpression) 
            return new GreaterThanEqualExpression(simplified.getChild(0).getChild(0), simplified.getChild(0).getChild(1));

        // not(x > y) -> x <= y
        else if(getChild(0) instanceof GreaterThanExpression) {
	    Expression retExp = new LessThanEqualExpression(simplified.getChild(0).getChild(0), simplified.getChild(0).getChild(1));
	    return retExp;
	}
        // not(x <= y) -> x > y
        else if(simplified.getChild(0) instanceof LessThanEqualExpression)  {
            return new GreaterThanExpression(simplified.getChild(0).getChild(0), simplified.getChild(0).getChild(1));
	}
        // not(x >= y) -> x < y
        else if(simplified.getChild(0) instanceof GreaterThanEqualExpression) 
            return new LessThanExpression(simplified.getChild(0).getChild(0), simplified.getChild(0).getChild(1));

        // not(not(x)) -> x
        else if(simplified.getChild(0) instanceof NotExpression)
            return simplified.getChild(0).getChild(0);
        else {
            return simplified;
	}
    }

    public String toString() {
	String c1 = (getChild(0) != null) ? getChild(0).toString() : "(null)";
	return new String("not(" + c1 + ")");
    }

    public int checkType() throws TypeMismatchException {
	// sub type must be boolean
	if(getChild(0).checkType() == BOOLEAN_TYPE)
	    return BOOLEAN_TYPE;
	else {
	    int type = getChild(0).checkType();
	    int expectedType = BOOLEAN_TYPE;
	    throw new TypeMismatchException(this, type, expectedType);
	}
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
