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
 * Abstract base class for all expressions requiring three arguments.
 */
abstract public class TernaryExpression extends Expression {

    /**
     * Create a new ternary expression with the given three
     * arguments.
     *
     * @param	arg1	the first argument
     * @param	arg2	the second argument
     * @param	arg3	the third argument
     */
    public TernaryExpression(Expression arg1,
			     Expression arg2,
			     Expression arg3) {
	add(arg1);
	add(arg2);
	add(arg3);
    }

    /**
     * Return the number of children required in a ternary expression.
     * This will always evaluate to <code>3</code>.
     *
     * @return	<code>3</code>
     */
    public int getNeededChildren() {
	return 3;
    }

    /**
     * Return the given argument.
     *
     * @param	arg	the argument number
     * @return	the argument
     */
    protected Expression getArg(int arg) {
	return (Expression)getChildAt(arg);
    }
}
