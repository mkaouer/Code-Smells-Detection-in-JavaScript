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
     * Check the input arguments to the expression. They can be any type
     * except {@link #BOOLEAN_TYPE} and {@link #QUOTE_TYPE} and
     * they must match.
     *
     * @return	the type of the left expression
     */
    public int checkType() throws TypeMismatchException {
	// Types must be the same and not boolean or quote
	int leftType = getLeft().checkType();
	int rightType = getRight().checkType();

	if(equivelantTypes(leftType, rightType) && leftType != BOOLEAN_TYPE
	   && leftType != QUOTE_TYPE) 
	    return leftType;
	else
	    throw new TypeMismatchException();
    }

}
