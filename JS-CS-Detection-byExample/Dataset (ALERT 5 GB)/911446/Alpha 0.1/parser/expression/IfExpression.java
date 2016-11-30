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
import org.mov.quote.*;

/**
 * An expression which represents the control flow of 
 * <code>if (x) {y} else {z}</code>.
 */
public class IfExpression extends TernaryExpression {

    /**
     * Construct an <code>if</code> expression.
     *
     * @param	arg1	the expression to be tested
     * @param	arg2	the expression to be executed if the test was 
     *			{@link #TRUE}
     * @param	arg2	the expression to be executed if the test was 
     *			{@link #FALSE}
     */
    public IfExpression(Expression arg1, 
			Expression arg2,
			Expression arg3) {
	super(arg1, arg2, arg3);
    }

    public float evaluate(QuoteBundle quoteBundle, String symbol, int day) 
	throws EvaluationException {

	// if(...) then
	if(getArg(0).evaluate(quoteBundle, symbol, day) 
	   >= Expression.TRUE_LEVEL)
	    return getArg(1).evaluate(quoteBundle, symbol, day);
	// else
	else
	    return getArg(2).evaluate(quoteBundle, symbol, day);
    }

    public String toString() {
	return new String("if(" + getArg(0).toString() + ") {" +
			  getArg(1).toString() + "} else {" +
			  getArg(2).toString() + "} ");
    }

    /**
     * Check the input arguments to the expression. The first argument
     * must be {@link #BOOLEAN_TYPE}, the remaining arguments can be
     * any type but must be the same.      
     *
     * @return	the type of the second and third arguments
     */
    public int checkType() throws TypeMismatchException {
	// if(arg0) { arg1 } else { arg2} 
	// then type of arg1 should be the same of arg2
	// arg0 must be boolean
	int arg0type = getArg(0).checkType();
	int arg1type = getArg(1).checkType();
	int arg2type = getArg(2).checkType();

	if(arg0type == BOOLEAN_TYPE &&
	   arg1type == arg2type)
	    return arg1type;
	else
	    throw new TypeMismatchException();
    }
}

