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
import org.mov.util.*;

/**
 * Abstract base class for all expressions requiring a single argument.
 */
abstract public class UnaryExpression extends Expression {

    /**
     * Create a new unary expression with the given argument.
     *
     * @param	sub	the sub argument
     */
    public UnaryExpression(Expression sub) {
	add(sub);
    }

    /**
     * Return the number of children required in a unary expression.
     * This will always evaluate to <code>1</code>.
     *
     * @return	<code>1</code>
     */
    public int getNeededChildren() {
	return 1;
    }

    /**
     * Return the sub expression.
     *
     * @return	the single argument
     */
    protected Expression getSub() {
	return (Expression)getChildAt(0);
    }
}
