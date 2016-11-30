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
     * @param	arg3	the expression to be executed if the test was 
     *			{@link #FALSE}
     */
    public IfExpression(Expression arg1, 
			Expression arg2,
			Expression arg3) {
	super(arg1, arg2, arg3);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	// if(...) then
	if(get(0).evaluate(variables, quoteBundle, symbol, day) 
	   >= Expression.TRUE_LEVEL)
	    return get(1).evaluate(variables, quoteBundle, symbol, day);
	// else
	else
	    return get(2).evaluate(variables, quoteBundle, symbol, day);
    }

    public String toString() {
	return new String("if(" + get(0).toString() + ") {" +
			  get(1).toString() + "} else {" +
			  get(2).toString() + "}");
    }

    /**
     * Check the input arguments to the expression. The first argument
     * must be {@link #BOOLEAN_TYPE}, the remaining arguments can be
     * {@link #BOOLEAN_TYPE}, {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE} and 
     * must be the same.
     *
     * @return	the type of the second and third arguments
     */
    public int checkType() throws TypeMismatchException {
	if(get(0).getType() == BOOLEAN_TYPE &&
           get(1).getType() == get(2).getType() &&
           (get(1).getType() == FLOAT_TYPE || get(1).getType() == INTEGER_TYPE ||
            get(1).getType() == BOOLEAN_TYPE))
           return getType();
	else
	    throw new TypeMismatchException();
    }

    /**
     * Get the type of the expression.
     *
     * @return {@link #FLOAT_TYPE}, {@link #INTEGER_TYPE} or {@link #BOOLEAN_TYPE}.
     */
    public int getType() {
        assert get(1).getType() == get(2).getType();

        return get(1).getType();
    }

    public Expression simplify() {
        // First simplify all the child arguments
        super.simplify();

        NumberExpression first = (get(0) instanceof NumberExpression? 
                                  (NumberExpression)get(0) : null);

        // If the first argument is the constant TRUE then we simplify to the
        // second argument. Otherwise if the first argument is the constant FALSE
        // then we simplify to the third argument.
        if(first != null) {
            if(first.getValue() >= TRUE_LEVEL)
                return get(1);
            else
                return get(2);
        }

        // If the second and third arguments are the same then we can simplify
        // to the second argument.
        else if(get(1).equals(get(2)))
            return get(1);

        // If the first argument is not, create a new If expression that
        // reverses argument 1 and 2. I.e.
        // if(not(c)) {a} else {b}")) => if(c) {b} else {a}
        else if(get(0) instanceof NotExpression)
            return new IfExpression(get(0).get(0), get(2), get(1));

        else
            return this;
    }

    public Object clone() {
        return new IfExpression((Expression)get(0).clone(), 
                                (Expression)get(1).clone(),
                                (Expression)get(2).clone());
    }

}

