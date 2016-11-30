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
 * An expression which represents the <code>for</code> command.
 * e.g. <pre>for(int i = 0; i < 10; i++) {
 *   a += i;
 *}</pre>
 */
public class ForExpression extends QuaternaryExpression {

    // For expression arguments
    private final static int INITIAL = 0;
    private final static int CONDITION = 1;
    private final static int LOOP = 2;
    private final static int COMMAND = 3;

    /**
     * Construct a <code>for</code> expression.
     * <pre>for(initial; condition; loop) {
     *   command
     *}</pre>
     * @param	initial	loop initialisation.
     * @param	condition loop condition test.
     * @param	loop the loop traversal function.
     * @param	command	the command to loop.
     */
    public ForExpression(Expression initial, Expression condition, Expression loop,
			 Expression command) {
	super(initial, condition, loop, command);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	double value = 0.0D;

	// Execute the initial
	getChild(INITIAL).evaluate(variables, quoteBundle, symbol, day);

	// Now loop running the command until the condition is no longer true
	do {
	    // Execute command
	    value = getChild(COMMAND).evaluate(variables, quoteBundle, symbol, day);

	    // Execute loop
	    getChild(LOOP).evaluate(variables, quoteBundle, symbol, day);

	} while(getChild(CONDITION).evaluate(variables, quoteBundle, symbol, day) >=
		Expression.TRUE_LEVEL);

	// Return the results of the last command
	return value;
    }

    public String toString() {
	String string = ("for(" + getChild(INITIAL) + ";" + getChild(CONDITION) + ";" +
			 getChild(LOOP) + ")");
	string = string.concat(ClauseExpression.toString(getChild(COMMAND)));
	return string;
    }

    /**
     * Check the input arguments to the expression. The arguments can be any
     * type except for the condition argument which must be {@link #BOOLEAN_TYPE}.
     *
     * @return	the type of the command argument.
     */
    public int checkType() throws TypeMismatchException {
	getChild(INITIAL).checkType();
	getChild(LOOP).checkType();
	getChild(COMMAND).checkType();

	if(getChild(CONDITION).checkType() != BOOLEAN_TYPE)
	    throw new TypeMismatchException();
	else
	    return getType();
    }

    /**
     * Get the type of the expression.
     *
     * @return the type of the command argument.
     */
    public int getType() {
        return getChild(COMMAND).getType();
    }

    public Object clone() {
        return new ForExpression((Expression)getChild(0).clone(), 
				 (Expression)getChild(1).clone(),
				 (Expression)getChild(2).clone(),
				 (Expression)getChild(3).clone());
    }

}

