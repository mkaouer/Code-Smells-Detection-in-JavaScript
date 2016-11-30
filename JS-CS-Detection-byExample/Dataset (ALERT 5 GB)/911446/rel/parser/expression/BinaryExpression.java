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

import nz.org.venice.parser.Expression;

/**
 * Abstract base class for all expressions requiring two arguments.
 */
abstract public class BinaryExpression extends AbstractExpression {

    /**
     * Create a new binary expression with the given left and right
     * arguments.
     *
     * @param	left	the left argument
     * @param	right	the right argument
     */
    public BinaryExpression(Expression left,
			    Expression right) {	
	super(new Expression[] {left, right});
	assert left != null && right != null;

        //setChild(left, 0);
        //setChild(right, 1);
    }

    /**
     * Return the number of children required in a binary expression.
     * This will always evaluate to <code>2</code>.
     *
     * @return	<code>2</code>
     */
    public int getChildCount() {
	return 2;
    }

    /**
     * Helper method to conver the given expression to a string.
     * Given an operator such as <code>+</code>, <code>-</code> etc
     * it will return <code>arg1 operator arg2</code>. It will insert
     * parentheses as needed.
     *
     * @param	operator	the binary operator
     * @return	the string representation
     */
    protected String toString(String operator) {
	String string = "";
	
	if (getChild(0) != null) {
	    if(getChild(0).getChildCount() < 2) {
		string += getChild(0).toString();
	    } else {
		string += "(" + getChild(0).toString() + ")";
	    }	    	
	} else {
	    string += "(null)";
	}
	string += operator;
	
	if (getChild(1) != null) {
	    if(getChild(1).getChildCount() < 2) {
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
