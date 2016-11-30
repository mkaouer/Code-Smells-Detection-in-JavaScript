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
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.AnalyserGuard;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;

import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

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

    //Expression identifier - parent hashcode uses class hashcode
    private UUID id;

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
	id = UUIDGenerator.getInstance().generateRandomBasedUUID();
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	double value = 0.0D;

	UUID loopId = UUIDGenerator.getInstance().generateRandomBasedUUID();

	AnalyserGuard.getInstance().startLoop(this, loopId, symbol, day);

	// Execute the initial
	getChild(INITIAL).evaluate(variables, quoteBundle, symbol, day);
	
	/*Used to be a do while, but because that executes at least once
	  , that breaks for loops where the condition is false to begin with
	  
	*/
	// Now loop running the command until the condition is no longer true
	while (getChild(CONDITION).evaluate(variables, quoteBundle, symbol,
					    day) >= Expression.TRUE_LEVEL) {

	    //Don't want to run forever - if the limit is exceeded
	    //could be an infinite loop. 
	    if (AnalyserGuard.getInstance().
		evaluationTimeElapsed(this, loopId, symbol, day)) {
		throw EvaluationException.EVAL_TIME_TOO_LONG_EXCEPTION;
	    }
	    
	    // Execute command
	    value = getChild(COMMAND).evaluate(variables, quoteBundle, symbol, day);
	    
	    // Execute loop
	    getChild(LOOP).evaluate(variables, quoteBundle, symbol, day);
	    
	} 

	AnalyserGuard.getInstance().finishLoop(this, loopId, symbol, day);	
    
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

	if(getChild(CONDITION).checkType() != BOOLEAN_TYPE) {
	    int type = getChild(CONDITION).getType();
	    int expectedType = BOOLEAN_TYPE;
	    throw new TypeMismatchException(this, type, expectedType);
	}
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

