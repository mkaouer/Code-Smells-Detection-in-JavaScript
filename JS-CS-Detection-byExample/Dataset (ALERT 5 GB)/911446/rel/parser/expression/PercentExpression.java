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
 * An expression which returns the given percent of a value.
 */
public class PercentExpression extends BinaryExpression {

    /**
     * Create a new percent expression.
     *
     * @param	left	calculate the percent of this number
     * @param	right	the percent value to calculate
     */
    public PercentExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

        double value = getChild(0).evaluate(variables, quoteBundle, symbol, day);
        double percent = getChild(1).evaluate(variables, quoteBundle, symbol, day);
	return value * percent / 100;
    }

    public Expression simplify() {
        // First simplify all the child arguments
        Expression simplified = super.simplify();

        // If both the child arguments are constant we can precompute.
        if(simplified.getChild(0) instanceof NumberExpression &&
           simplified.getChild(1) instanceof NumberExpression) {
            try {
                return new NumberExpression(simplified.evaluate(null, null, null, 0),
                                            simplified.getType());
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

    public String toString() {
	String rv = "";

	String child1 = (getChild(0) != null) ? getChild(0).toString() : "(null)";
	String child2 = (getChild(1) != null) ? getChild(1).toString() : "(null)";
	
	return new String("percent(" + child1 + ", " + child2 + ")");

	    /*
	return new String("percent(" + getChild(0).toString() + ", " +
			  getChild(1).toString() + ")");
	    */
    }

    /** 
     * Either argument can be {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}.
     *
     * @return	the left argument type
     * @throws	TypeMismatchException if the expression has incorrect types
     */
    public int checkType() throws TypeMismatchException {
	// returned type is type of first arg
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

    /**
     * Get the type of the expression.
     *
     * @return {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
	int childCount = getChildCount();
	int type = UNDEFINED_TYPE;
	
	for (int i = 0; i < childCount; i++) {
	    if (getChild(i) != null) {
		type = getChild(i).getType();
		if (type == Expression.FLOAT_TYPE) {
		    return type;
		}
	    }
	}
        return type;
    }


    public Object clone() {
        return new PercentExpression((Expression)getChild(0).clone(), 
                                     (Expression)getChild(1).clone());
    }

}

