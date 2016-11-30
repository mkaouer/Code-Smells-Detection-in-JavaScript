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

import java.sql.Timestamp;
import java.util.Date;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.Symbol;

import nz.org.venice.util.Locale;
import nz.org.venice.util.VeniceLog;

/**
 * An no op expression which when evaluated halts the processing of rules.
 * Use for when something seriously wrong happens and you don't care about
 * the rest of the days results.
 *
 * Will first display the alert message defined by the arguments.
 *
 * @author Mark Hummel
 */

public class LoggingExpression extends UnaryExpression{
   
    private Expression mandatoryArg;
    private Expression[] optionalArgs;
    private final int argCount;

    /**
     * Create a new logging expression.
     * 
     * @param arg The message expression 
     * @param optionalArgs Optional expressions (max 4) which are appended to
     * the message defined in arg.
     */    

    public LoggingExpression(Expression arg, Expression[] optionalArgs) {
	super(arg);
	this.mandatoryArg = arg;
	this.optionalArgs = optionalArgs;
	int count = 1;
	for (int i = 0; i < optionalArgs.length; i++) {
	    if (optionalArgs[i] != null) {
		count++;
		optionalArgs[i].setParent(this);
	    }
	}
	argCount = count;
    }

    /**
     * Evaluate the expression and subexpressions, and then display the 
     * resulting message.
     * 
     * @param variables The variables of the rule
     * @param quoteBundle The quote bundle containing the symbol data
     * @param symbol The implicit symbol of the rule
     * @param day The date offset used to evaluate the rule.
     * @return 0.0
     */

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {
	
	Date date = new Date();
	String message = new Timestamp(date.getTime()) + " " + symbol + " " + quoteBundle.offsetToDate(day) + " : ";
	
	message += appendMessage(getChild(0), 
				 variables, 
				 quoteBundle,
				 symbol, 
				 day);

	for (int i = 0; i < optionalArgs.length; i++) {
	    if (optionalArgs[i] != null) {
		message += appendMessage(optionalArgs[i], 
					 variables, 
					 quoteBundle, 
					 symbol, 
					 day);	 
	    }   
	}

	VeniceLog.getInstance().log(message);

	return 0.0;
    }
    
    private String appendMessage(Expression mesg, Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) throws EvaluationException {
	String rv = "";
	if (mesg instanceof StringExpression) {	    
	    rv += ((StringExpression)mesg).getText();
	} else {
	    rv += mesg.evaluate(variables,quoteBundle, symbol, day);
	}
	return rv;
    }

    /**
     * Check the argument to the expression. It can be an expression.
     *
     * @return the type of the expression
     */
    public int checkType() throws TypeMismatchException {
	return getType();
    }

        /**
     * Get the type of the expression
     * 
     * @return {@link #FLOAT_TYPE}
     */
    public int getType() {
	return FLOAT_TYPE;
    }

    /**
     * Return the number of children required in an alert expression.
     * This will be a minimum of <code>1</code> and a maximum of <code>4</code>.
     *
     * @return	<code>The number of non null arguments.</code>
     */
    public int getChildCount() {
	return argCount;
    }


    /**
     * Return the child of this node at the given index.
     *
     * @return child at given index.
     */
    public Expression getChild(int child) {
	assert child <= argCount;

	if (child == 0) {
	    return mandatoryArg;
	} else {
	    return optionalArgs[child-1];
	}
    }

    /**
     * @return A clone of the object.
     */

    public Object clone() {	
        return new HaltExpression(getChild(0), optionalArgs);
    }
}
