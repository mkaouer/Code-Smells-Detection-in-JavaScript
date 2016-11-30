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
 * Abstract base class for the comparision expressions:
 * <code>>, <, ==, !=, <=, >=</code>
 */
abstract public class ComparisionExpression extends BinaryExpression {

    /**
     * Create a new comparision expression with the given left and
     * right arguments.
     */
    public ComparisionExpression(Expression left, Expression right) {
	super(left, right);
    }

    /**
     * Check the input arguments to the expression. They can only be
     * {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}. Both must be the same!
     *
     * @return	the type of the expression
     */
    public int checkType() throws TypeMismatchException {
	// left & right types must be the same and not boolean or quote
	int leftType = getChild(0).checkType();
	int rightType = getChild(1).checkType();
		
	if(leftType == rightType && 
           (leftType == FLOAT_TYPE || leftType == INTEGER_TYPE))
            return getType();	
	
	if ( (leftType == FLOAT_TYPE || leftType == INTEGER_TYPE) &&
	     (rightType == FLOAT_TYPE || rightType == INTEGER_TYPE)) 
	    return getType();
	
	else {
	    String types = 
		getChild(0).getType() + " , " +
		getChild(1).getType();
	    String expectedTypes = FLOAT_TYPE + "," + FLOAT_TYPE;

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
                return new NumberExpression(simplified.evaluate(null, null, null, 0), getType());
            }
            catch(EvaluationException e) {
                // Shouldn't happen
                assert false;
                return simplified;
            }
        }
        else
            return simplified;
    }

    /**
     * Get the type of the expression.
     *
     * @return {@link #BOOLEAN_TYPE}.
     */
    public int getType() {
        return BOOLEAN_TYPE;
    }

}
