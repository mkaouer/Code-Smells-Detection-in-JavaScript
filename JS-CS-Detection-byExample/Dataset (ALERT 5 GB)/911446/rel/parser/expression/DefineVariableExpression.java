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
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.Locale;

/**
 * A representation of a variable definition.
 */
public class DefineVariableExpression extends UnaryExpression {
    
    // The variable's name, type and constant status
    private String name;
    private int type;
    private boolean isConstant;
    
    public DefineVariableExpression(String name, int type, boolean isConstant, Expression value) {
	super(value);

        assert name != null && name.length() > 0;

        this.name = name;
        this.type = type;
	this.isConstant = isConstant;
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
        throws EvaluationException {

	double value = getChild(0).evaluate(variables, quoteBundle, symbol, day);

	// Check whether the variable exists *after* we've evaluated the expression
	// otherwise we might miss the silly case of a variable being defined in its
	// own definition. E.g.
	// int a = 5 + (int a = 6)
	if(!variables.contains(getName())) {
	    variables.add(getName(), getType(), isConstant(), value);
	    return value;
	}
	else
            throw new EvaluationException(Locale.getString("VARIABLE_DEFINED_ERROR", getName()));
    }

    public String toString() {
	// CONST int myVariable = 5
	String string = "";
	if(isConstant())
	    string = string.concat("const ");

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
	string = string.concat(" = ");
	string = string.concat(getChild(0).toString());
	return string;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
   
    public boolean isConstant() {
	return isConstant;
    }

    public int checkType() throws TypeMismatchException {
	if(getType() == getChild(0).checkType())
	    return getType();
	else {
	    throw new TypeMismatchException(this, 
					    getType(), 
					    getChild(0).getType());
	}
    }

    public boolean equals(Object object) {
        if(object instanceof DefineVariableExpression) {
            DefineVariableExpression expression = (DefineVariableExpression)object;

            if(expression.getName().equals(getName()) &&
               expression.getType() == getType() &&
	       expression.isConstant() == isConstant() &&
	       expression.getChild(0).equals(getChild(0)))
                return true;
        }

        return false;
    }

    public int hashCode() {
	return new Boolean(isConstant).hashCode() ^ getName().hashCode() ^ getType();
    }

    public Object clone() {
        return new DefineVariableExpression(getName(), getType(), isConstant(), 
					    (Expression)getChild(0).clone());
    }
}
