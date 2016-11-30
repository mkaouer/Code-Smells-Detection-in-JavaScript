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

import nz.org.venice.parser.Parser;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.util.Locale;

/**
 * An expression which defines a user function.
 *
 * @author Mark Hummel
 */
public class FunctionExpression extends BinaryExpression {

    private final String name;
    private final int type;

    /**
     * Create a new average expression for the given <code>quote</code> kind,
     * for <code>lag</code> days away.
     *
     * @param	name	the name of the function to define
     * @param	type	the variable type that the function returns
     * @param   parameterList List of DefineParameterExpressions 
     * @param   body    the Expression representing the funciton body 
     */
    public FunctionExpression(String name, int type, Expression parameterList, Expression body) {
        super(parameterList, body);
	this.name = name;
	this.type = type;
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {
	
	//This expression defines the expression only; evaluating the expression
	//occurs in EvalFunctionExpression when the function is called.
	return 0.0;
    }

    public boolean equals(Object object) {
	if (!(object instanceof FunctionExpression)) {
	    return false;
	}
	FunctionExpression expression = (FunctionExpression)object;

	if (!expression.getName().equals(name)) {
	    return false;
	}

	if (type != expression.getType()) {
	    return false;
	}

	if (!getChild(0).equals(expression.getChild(0)) ||
	    !getChild(1).equals(expression.getChild(1))) {
	    return false;
	} 

	return true;	
    }

    public int hashCode() {
	return name.hashCode() ^ (type * 37) ^ 
	    getChild(0).hashCode() ^ 
	    getChild(1).hashCode();
    }

    public String toString() {
	Expression parameterList = getChild(0);
	Expression body = getChild(1);

	return getType() + " " + getName() + "(" + parameterList.toString() + 
	    " " + ")" + "{" + body.toString() + "}";
    }

    public int checkType() throws TypeMismatchException {	
	Expression body = getChild(1);
	
	//This will catch errors of returning the wrong type for the definition
	//e.g. float function f { return true} 
	if (body.checkType() != getType()) {
	    String types = "" + body.getType();
	    String expectedTypes = "" + getType();
	    throw new TypeMismatchException(this, types, expectedTypes);
	} else {
	    return getType();
	}
    }

    /**
     * @return The name of the function
     */
    public String getName() {
	return name;
    }

    /**
     * @return The return type of the function.
     */
    public int getType() {
	return type;
    }

    /** 
     * @return a Clone of the FunctionExpression object
     */
    public Object clone() {
        return new FunctionExpression(getName(), 
				      getType(),
				      (Expression)getChild(0).clone(),
				      (Expression)getChild(1).clone());
    }

}
