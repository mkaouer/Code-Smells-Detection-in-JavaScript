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
     * {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}. Both must be the same!
     *
     * @return	the type of the expression
     */
    public int checkType() throws TypeMismatchException {
	// Types must be the same and not boolean or quote
	int leftType = getLeft().checkType();
	int rightType = getRight().checkType();

	if(leftType == rightType && 
           (leftType == FLOAT_TYPE || leftType == INTEGER_TYPE))
            return getType();
	else
	    throw new TypeMismatchException();
    }

    public Expression simplify() {
        // First simplify all the child arguments
        super.simplify();

        // If both the child arguments are constant we can precompute.
        if(getLeft() instanceof NumberExpression &&
           getRight() instanceof NumberExpression) {
            try {
                return new NumberExpression(evaluate(null, null, null, 0), getType());
            }
            catch(EvaluationException e) {
                // Can happen if we hit 1/0. In which case don't bother to simplify.
                return this;
            }
        }
        else
            return this;
    }

    /**
     * Get the type of the expression.
     *
     * @return either {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
        assert getLeft().getType() == getRight().getType();

        return getLeft().getType();
    }
}
