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

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.Symbol;

import nz.org.venice.util.Locale;

/**
 * An no op expression which when evaluated halts the processing of rules.
 * Use for when something seriously wrong happens and you don't care about
 * the rest of the days results.
 *
 * Will first display the alert message defined by the arguments.
 *
 * @author Mark Hummel
 */

public class HaltExpression extends AlertExpression {
   
    private Expression mandatoryArg;
    private Expression[] optionalArgs;

    /**
     * Create a new alert expression.
     * 
     * @param arg The message expression 
     * @param optionalArgs Optional expressions (max 4) which are appended to
     * the message defined in arg.
     */    

    public HaltExpression(Expression arg, Expression[] optionalArgs) {
	super(arg, optionalArgs);
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
	
	super.evaluate(variables, quoteBundle, symbol, day);

	throw EvaluationException.EVALUATION_HALTED_EXCEPTION;

    }
    /**
     * @return A clone of the object.
     */

    public Object clone() {	
        return new HaltExpression(getChild(0), optionalArgs);
    }
}
