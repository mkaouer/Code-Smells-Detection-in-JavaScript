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

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

/**
 * An expression which represents the control flow of 
 * <code>if (x) y else z</code>.
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
    public IfExpression(Expression arg1, Expression arg2, Expression arg3) {
	super(arg1, arg2, arg3);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	// if(...) then
	if(getChild(0).evaluate(variables, quoteBundle, symbol, day) >= Expression.TRUE_LEVEL)
	    return getChild(1).evaluate(variables, quoteBundle, symbol, day);
	// else
	else
	    return getChild(2).evaluate(variables, quoteBundle, symbol, day);
    }

    public String toString() {
	String string = "if(" + getChild(0) + ")";
	string = string.concat(ClauseExpression.toString(getChild(1)));
	string = string.concat("else");	
	string = string.concat(ClauseExpression.toString(getChild(2)));
	return string;
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
	if(getChild(0).checkType() == BOOLEAN_TYPE &&
           getChild(1).checkType() == getChild(2).checkType() &&
           (getChild(1).getType() == FLOAT_TYPE || getChild(1).getType() == INTEGER_TYPE ||
            getChild(1).getType() == BOOLEAN_TYPE))
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
        assert getChild(1).getType() == getChild(2).getType();

        return getChild(1).getType();
    }

    public Expression simplify() {
        // First simplify all the child arguments
        super.simplify();

        NumberExpression first = (getChild(0) instanceof NumberExpression? 
                                  (NumberExpression)getChild(0) : null);

        // If the first argument is the constant TRUE then we simplify to the
        // second argument. Otherwise if the first argument is the constant FALSE
        // then we simplify to the third argument.
        if(first != null) {
            if(first.getValue() >= TRUE_LEVEL)
                return getChild(1);
            else
                return getChild(2);
        }

        // If the second and third arguments are the same then we can simplify
        // to the second argument.
        else if(getChild(1).equals(getChild(2)))
            return getChild(1);

        // If the first argument is not, create a new If expression that
        // reverses argument 1 and 2. I.e.
        // if(not(c)) {a} else {b}")) => if(c) {b} else {a}
        else if(getChild(0) instanceof NotExpression)
            return new IfExpression(getChild(0).getChild(0), getChild(2), getChild(1));

        else
            return this;
    }

    public Object clone() {
        return new IfExpression((Expression)getChild(0).clone(), 
                                (Expression)getChild(1).clone(),
                                (Expression)getChild(2).clone());
    }

}

