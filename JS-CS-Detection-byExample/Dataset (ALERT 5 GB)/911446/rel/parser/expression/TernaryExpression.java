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
 * Abstract base class for all expressions requiring three arguments.
 */
abstract public class TernaryExpression extends AbstractExpression {

    private Expression children[] = new Expression[3];

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
        super(new Expression[] {arg1, arg2, arg3});
        assert arg1 != null && arg2 != null && arg3 != null;
        //setChild(arg1, 0);
        //setChild(arg2, 1);
        //setChild(arg3, 2);
    }

    /**
     * Return the number of children required in a ternary expression.
     * This will always evaluate to <code>3</code>.
     *
     * @return	<code>3</code>
     */
    public int getChildCount() {
	return 3;
    }

}
