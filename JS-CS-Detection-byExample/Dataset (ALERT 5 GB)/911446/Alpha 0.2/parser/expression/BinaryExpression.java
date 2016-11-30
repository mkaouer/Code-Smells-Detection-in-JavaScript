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
 * Abstract base class for all expressions requiring two arguments.
 */
abstract public class BinaryExpression extends Expression {

    /**
     * Create a new binary expression with the given left and right
     * arguments.
     *
     * @param	left	the left argument
     * @param	right	the right argument
     */
    public BinaryExpression(Expression left,
			    Expression right) {
        assert left != null && right != null;

	add(left);
	add(right);
    }

    /**
     * Return the number of children required in a binary expression.
     * This will always evaluate to <code>2</code>.
     *
     * @return	<code>2</code>
     */
    public int getNeededChildren() {
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

	if(getLeft().getNeededChildren() < 2)
	    string += getLeft().toString();
	else
	    string += "(" + getLeft().toString() + ")";
	
	string += operator;
	
	if(getRight().getNeededChildren() < 2)
	    string += getRight().toString();
	else
	    string += "(" + getRight().toString() + ")";

	return string;
    }

    /**
     * Return the left argument.
     *
     * @return	the left argument
     */
    protected Expression getLeft() {
	return get(0);
    }

    /**
     * Return the right argument.
     *
     * @return	the left argument
     */
    protected Expression getRight() {
	return get(1);
    }
}
