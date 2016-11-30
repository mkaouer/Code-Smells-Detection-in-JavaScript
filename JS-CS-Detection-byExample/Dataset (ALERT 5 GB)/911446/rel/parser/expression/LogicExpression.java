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
 * Abstract base class for the boolean expressions:
 * <code>and, or</code>
 */
abstract public class LogicExpression extends BinaryExpression {

    /**
     * Create a new logic expression with the given left and
     * right arguments.
     */
    public LogicExpression(Expression left, Expression right) {
	super(left, right);
    }

    /**
     * Check the input arguments to the expression. They must both be
     * {@link #BOOLEAN_TYPE}.
     *
     * @return	{@link #BOOLEAN_TYPE}
     */
    public int checkType() throws TypeMismatchException {
	// both types must be boolean
	int leftType = getChild(0).checkType();
	int rightType = getChild(1).checkType();

	if(leftType == BOOLEAN_TYPE && rightType == BOOLEAN_TYPE)
	    return BOOLEAN_TYPE;
	else {
	    String types = 
		getChild(0).checkType() + " , " + 
		getChild(1).checkType();
	    
	    String expectedTypes = BOOLEAN_TYPE + " , " + BOOLEAN_TYPE;
		
	    throw new TypeMismatchException(this, types, expectedTypes);
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

    /**
     * Helper method to conver the given expression to a string.
     * Given an operator such as <code>and</code>, <code>or</code> etc
     * it will return <code>arg1 operator arg2</code>. It will insert
     * parentheses as needed.
     *
     * @param	operator	the binary operator
     * @return	the string representation
     */
    protected String toString(String operator) {
	String string = "";

	Expression c1 = getChild(0);
	Expression c2 = getChild(1);

	if (c1 != null) {
	    if (c1.getChildCount() < 2) {
		string += c1.toString();
	    } else {
		string += "(" + c1.toString() + ")";
	    }
	} else {
	    string += "(null)";
	}
	string += " " + operator + " ";
	
	if (c2 != null) {
	    if(c2.getChildCount() < 2) {
		string += getChild(1).toString();
	    } else {
		string += "(" + getChild(1).toString() + ")";
	    } 
	} else {
	    string += "(null)";
	}
	
	return string;
    }
}

