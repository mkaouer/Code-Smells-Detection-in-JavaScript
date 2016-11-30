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

/**
 * Abstract base class for the arithmetic expressions:
 * <code>+, /, *, -</code>
 */
abstract public class ArithmeticExpression extends BinaryExpression {

    /**
     * Create a new arithmetic expression with the given left and
     * right arguments.
     */
    public ArithmeticExpression(Expression left, Expression right) {
	super(left, right);
    }

    /**
     * Check the input arguments to the expression. They can only be
     * {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}.
     *     * There are 4 possible cases:
     * {@link #INTEGER_TYPE} operator {@link #INTEGER_TYPE} returns a {@link #INTEGER_TYPE}
     * {@link #INTEGER_TYPE} operator {@link #FLOAT_TYPE} returns a {@link #INTEGER_TYPE}
     * {@link #FLOAT_TYPE} operator {@link #INTEGER_TYPE} returns a {@link #FLOAT_TYPE}
     * {@link #FLOAT_TYPE} operator {@link #FLOAT_TYPE} returns a {@link #FLOAT_TYPE}
     *
     * @return	the type of the expression
     */
    public int checkType() throws TypeMismatchException {
	// Types must be integer or float and not boolean or quote.
	int leftType = getChild(0).checkType();
	int rightType = getChild(1).checkType();

	if((leftType == FLOAT_TYPE || leftType == INTEGER_TYPE) && 
           (rightType == FLOAT_TYPE || rightType == INTEGER_TYPE))
            return getType();
	else {
	    String types = leftType + " , " + rightType;
	    String expectedTypes = FLOAT_TYPE + " , " + FLOAT_TYPE;
	    throw new TypeMismatchException(this, types, expectedTypes);
	}
    }

    public Expression simplify() {
        // First simplify all the child arguments
        Expression simplified = super.simplify();

        // If both the child arguments are constant we can precompute.
        if(simplified.getChild(0) instanceof NumberExpression &&
           simplified.getChild(1) instanceof NumberExpression) {
            try {
                return new NumberExpression(simplified.evaluate(null, null, null, 0), simplified.getType());
            }
            catch(EvaluationException e) {
                // Can happen if we hit 1/0. In which case don't bother to simplify.
                return simplified;
            }
        }
        else
            return simplified;
    }

    /**
     * Get the type of the expression.
     *
     * @return either {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
	int childCount = getChildCount();
	int type = -1;
	
	for (int i = 0; i < childCount; i++) {
	    if (getChild(i) != null) {
		type = getChild(i).getType();
	    }
	    if (type == Expression.FLOAT_TYPE) {
		return type;
	    }
	}
        return type;
    }

}
