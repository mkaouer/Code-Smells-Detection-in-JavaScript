/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2003 Andrew Leppard (aleppard@picknowl.com.au)

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

import java.lang.String;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.parser.Variable;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.Locale;

/**
 * A representation of a function parameter definition.
 */
public class DefineParameterExpression extends TerminalExpression {
    
    // The variable's name, type and constant status
    private String name;
    private int type;
    
    public DefineParameterExpression(String name, int type) {
	super();

        assert name != null && name.length() > 0;

        this.name = name;
        this.type = type;
    }
    
    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
        throws EvaluationException {

	/* If a variable with the same name as the parameter 
	   is already defined, replace it with the parameter so that
	   the parameter takes precedence. 

	   When evaluate is called, it is done in a function evaluation
	   context, so the previous defined variables will persist on return
	   from the function.
	*/	   
	if (!variables.contains(name)) {
	    variables.add(name, type, false);
	}
		
	//Retrieving the parameter value should happen in SetParameterExpression
	return 0.0;
    }

    public boolean equals(Object object) {
	if (!(object instanceof DefineParameterExpression)) {
	    return false;
	}

	DefineParameterExpression expression = 
	    (DefineParameterExpression)object;

	if (expression.getType() == type &&
	    expression.getName().equals(name)) {
	    return true;
	} else {
	    return false;
	}
    }

    public int hashCode() {
	return name.hashCode() ^ (type * 37);
    }

    public String toString() {
	String string = "";

	// const INT myVariable = 5
	switch(getType()) {
	case BOOLEAN_TYPE:
	    string = string.concat("boolean");
	    break;
	case INTEGER_TYPE:
	    string = string.concat("int");
	    break;
	default:
	    assert getType() == FLOAT_TYPE;
	    string = string.concat("float");
	}

	// const int MYVARIABLE = 5
	string = string.concat(" ");
	string = string.concat(name);
	return string;
    }

    /**
     * @return The name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type of the parameter
     */
    public int getType() {
        return type;
    }
   

    /**
     * @return The type of the parameter 
     */
    public int checkType() throws TypeMismatchException {
	return getType();	
    }

    /**
     * @return A clone of the DefineParameterExpression.
     */

    public Object clone() {
        return new DefineParameterExpression(getName(), getType()); 
    }
}
